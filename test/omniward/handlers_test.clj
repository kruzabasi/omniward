(ns omniward.handlers-test
  (:require [clojure.test :refer [is testing deftest] :as t]
            [java-time.api :as jt]
            [test-common :refer [test-db-spec create-test-db drop-test-db]]
            [omniward.handlers :refer [new-patient!]]))

(deftest new-patient!-test
  (create-test-db)
  (let [patient-data {:p-name "Jane Smith"
                      :gender "Female"
                      :dob (jt/local-date (jt/sql-date 1995 6 21))
                      :address "456 Elm St"
                      :phone "555-5678"}
        args         {:sys {:postgres test-db-spec}
                      :parameters {:body patient-data}}]
    (testing "Inserting a new patient!"
      (let [res (new-patient! args)]
        (is (= 201 (:status res)))
        (is (int? (-> res :body :data :patient_id)))))
    (testing "Inserting an existing patient!"
      (let [res (new-patient! args)]
        (is (= 409 (:status res))))))
  (drop-test-db))

(comment
  (t/run-tests *ns*))