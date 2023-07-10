(ns omniward.handlers-test
  (:require [clojure.test :refer [is testing deftest use-fixtures] :as t]
            [test-common :refer [test-db-spec patient-data db-fixture]]
            [omniward.postgres.db :refer [insert-patient]]
            [omniward.handlers :as SUT]))

(deftest get-all-patients-test
  (testing "Fetching all patients"
    (insert-patient test-db-spec (patient-data))
    (let [args {:sys {:postgres test-db-spec}}
          res  (SUT/get-all-patients args)]
      (is (= 200 (:status res)))
      (is (not-empty (-> res :body :data))))))

(deftest new-patient!-test
  (let [args {:sys {:postgres test-db-spec}
              :parameters {:body (patient-data)}}]
    (testing "Inserting a new patient!"
      (let [res (SUT/new-patient! args)]
        (is (= 201 (:status res)))
        (is (int? (-> res :body :data :patient_id)))))
    (testing "Inserting an existing patient!"
      (let [res (SUT/new-patient! args)]
        (is (= 409 (:status res)))))))

(deftest get-patient-test
  (let [args {:sys {:postgres test-db-spec}
              :parameters {:path {:id "1"}}}]
    (insert-patient test-db-spec (patient-data))
    (testing "Requesting a patients info"
      (let [res (SUT/get-patient args)]
        (is (= 200 (:status res)))
        (is (= 1 (-> res :body :data :patient_id)))))
    (testing "Requesting a non-existent patients info"
      (let [args (assoc-in args [:parameters :path :id] "12")
            res  (SUT/get-patient args)]
        (is (= 404 (:status res)))))
    (testing "Requesting a patients info with invalid ID"
      (let [args (assoc-in args [:parameters :path :id] "id")
            res  (SUT/get-patient args)]
        (is (= 400 (:status res)))
        (is (= "Invalid Patient ID" (:body res)))))))

(deftest modify-patient!-test
  (let [args {:sys {:postgres test-db-spec}
              :path-params  {:id "1"}
              :query-params {"address" "102 Palm Ave, Vienna"}}]
    (insert-patient test-db-spec (patient-data))
    (testing "Modifying a patients record!"
      (let [res (SUT/modify-patient! args)]
        (is (= 200 (:status res)))
        (is (= "102 Palm Ave, Vienna" (-> res :body :data :address)))))
    (testing "Modifying a non-existent patients record!"
      (let [args (assoc-in args [:path-params :id] "12")
            res  (SUT/modify-patient! args)]
        (is (= 404 (:status res)))))
    (testing "Modifying a patients record with wrong params"
      (let [args (assoc args :query-params {"wrong" "params"})
            res  (SUT/modify-patient! args)]
        (is (= 400 (:status res)))
        (is (= "Missing or Invalid Parameters" (:body res)))))))

(deftest delete-patient!-test
  (insert-patient test-db-spec (patient-data))
  (let [args {:sys {:postgres test-db-spec}
              :parameters {:path {:id "1"}}}]
    (testing "Requesting deletion of a patients record"
      (let [res (SUT/delete-patient! args)]
        (is (= 200 (:status res)))))
    (testing "Requesting deletion of a non-existent record"
      (let [args (assoc-in args [:parameters :path :id] "18")
            res (SUT/delete-patient! args)]
        (is (= 404 (:status res)))))
    (testing "Requesting deletion with invalid ID"
      (let [args (assoc-in args [:parameters :path :id] "id")
            res  (SUT/get-patient args)]
        (is (= 400 (:status res)))
        (is (= "Invalid Patient ID" (:body res)))))))

(use-fixtures :each db-fixture)

(comment
  (t/run-tests *ns*))