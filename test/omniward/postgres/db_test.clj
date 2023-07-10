(ns omniward.postgres.db-test
  (:require
    [clojure.java.jdbc :as j]
    [clojure.test :refer [is testing deftest use-fixtures] :as t]
    [omniward.postgres.db :refer [insert-patient] :as SUT]
    [test-common :refer [test-db-spec patient-data db-fixture]]))

(deftest get-patient-info-test
  (testing "Querying a patients info from db"
    (insert-patient test-db-spec (patient-data))
    (let [patient-info (first (SUT/get-patient-info test-db-spec 1))
          non-patient  (first (SUT/get-patient-info test-db-spec 100))]
      (is (not-empty  patient-info))
      (is (empty? non-patient)))))

(deftest get-patients-test
  (testing "Querying for all patients"
    (is (empty? (SUT/get-patients test-db-spec)))
    (insert-patient test-db-spec (patient-data))
    (is (not-empty (SUT/get-patients test-db-spec)))))

(deftest insert-patient-test
    (testing "Inserting a new patients record to db"
      (is (-> (SUT/insert-patient test-db-spec (patient-data))
              first
              :patient_id
              int?))))

(deftest update-patient-test
  (testing "Updating details of a patients record"
    (let [patient        (patient-data)
          patient-name   (str (:p-name patient))
          patient-id     (-> (insert-patient test-db-spec patient)
                             first
                             :patient_id)
          patient-update {:patient-id patient-id
                          :update-val {:gender     "male"
                                       :address "102 Palm Ave, Vienna"}}]
      (is (= 1 (first (SUT/update-patient test-db-spec patient-update))))
      (let [updated-patient (j/query 
                             test-db-spec 
                             ["select * from patient where patient_id = ?" patient-id])]
        (is (= "male" (-> updated-patient first :gender)))
        (is (= "102 Palm Ave, Vienna" (-> updated-patient first :address)))
        (is (= patient-name (-> updated-patient first :name)))))))

(deftest delete-patient-test
  (testing "Deleting a patients record"
    (insert-patient test-db-spec (patient-data))
    (is (not-empty (SUT/get-patients test-db-spec)))
    (SUT/delete-patient test-db-spec 1)
    (is (empty? (SUT/get-patients test-db-spec)))))

(use-fixtures :each db-fixture)

(comment
  (t/run-tests *ns*))