(ns omniward.postgres.db-test
  (:require
    [clojure.java.jdbc :as j]
    [clojure.test :refer [is testing deftest] :as t]
    [omniward.postgres.db :refer [insert-patient update-patient get-patient-info]]
    [test-common :refer [test-db-spec patient-data create-test-db drop-test-db]]))

(deftest get-patient-info-test
  (create-test-db)
  (testing "Fetching a patients info from db"
    (insert-patient test-db-spec patient-data)
    (let [patient-info (first (get-patient-info test-db-spec 1))
          non-patient  (first (get-patient-info test-db-spec 100))]
      (is (not-empty  patient-info))
      (is (empty? non-patient))))
  (drop-test-db))

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
          patient-update {:patient-id patient-id
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