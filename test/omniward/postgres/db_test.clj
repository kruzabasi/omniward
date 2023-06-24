(ns omniward.postgres.db-test
  (:require
    [clojure.java.jdbc :as j]
    [clojure.test :refer [is testing deftest] :as t]
    [omniward.postgres.db :refer [insert-patient update-patient]]
    [java-time.api :as jt]
    [test-common :refer [test-db-spec create-test-db drop-test-db]]))

(def patient-data
  {:p-name "Jane Smith"
   :gender "Female"
   :dob (jt/local-date (jt/sql-date 1995 6 21))
   :address "456 Elm St"
   :phone "555-5678"})

(deftest insert-patient-test
  (create-test-db)
    (testing "Inserting a new patients record to db"
      (is (-> (insert-patient test-db-spec patient-data)
              first
              :patient_id
              int?)))
  (drop-test-db))

(deftest update-patient-test
  (create-test-db)
  (testing "Updating details of a patients record"
    (insert-patient test-db-spec patient-data)
    (let [patient-id     (-> (j/query
                              test-db-spec
                              ["select patient_id from patient where name = ?" "Jane Smith"])
                             first
                             :patient_id)
          patient-update {:update-where ["patient_id=?" patient-id]
                          :update-val   {:gender "Male"}}]
      (is (= 1 (first (update-patient test-db-spec patient-update))))
      (let [updated-patient (j/query 
                             test-db-spec 
                             ["select * from patient where patient_id = ?" patient-id])]
        (is (= "Male" (-> updated-patient first :gender)))
        (is (= "Jane Smith" (-> updated-patient first :name))))))
  (drop-test-db))

(comment
  (t/run-tests *ns*))