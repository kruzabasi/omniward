(ns omniward.postgres.db-test
  (:require
   [clojure.test :refer [is testing deftest] :as t]
   [omniward.postgres.db :refer [insert-patient]]
   [java-time.api :as jt]
   [test-common :refer [test-db-spec create-test-db drop-test-db]]))

(deftest insert-patient-test
  (create-test-db)
  (let [patient-data {:p-name "Jane Smith"
                      :gender "Female"
                      :dob (jt/local-date (jt/sql-date 1995 6 21))
                      :address "456 Elm St"
                      :phone "555-5678"}]
    (testing "Inserting a new patients record to db"
      (is (->
           (insert-patient test-db-spec patient-data)
           first
           :patient_id
           int?))))
  (drop-test-db))

(comment
  (t/run-tests *ns*))