(ns omniward.handlers-test
  (:require [clojure.test :refer [is testing deftest] :as t]
            [test-common :refer [test-db-spec patient-data create-test-db drop-test-db]]
            [omniward.postgres.db :refer [insert-patient]]
            [omniward.handlers :refer [new-patient! modify-patient!]]))

(deftest new-patient!-test
  (create-test-db)
  (let [args {:sys {:postgres test-db-spec}
              :parameters {:body patient-data}}]
    (testing "Inserting a new patient!"
      (let [res (new-patient! args)]
        (is (= 201 (:status res)))
        (is (int? (-> res :body :data :patient_id)))))
    (testing "Inserting an existing patient!"
      (let [res (new-patient! args)]
        (is (= 409 (:status res))))))
  (drop-test-db))

(deftest modify-patient!-test
  (create-test-db)
  (let [args {:sys {:postgres test-db-spec}
              :path-params  {:id "1"}
              :query-params {"address" "102 Palm Ave, Vienna"}}]
    (insert-patient test-db-spec patient-data)
    (testing "Modifying a patients record!"
      (let [res (modify-patient! args)]
        (is (= 200 (:status res)))
        (is (= "102 Palm Ave, Vienna" (-> res :body :data :address)))))
    (testing "Modifying a non-existent patients record!"
      (let [args (assoc-in args [:path-params :id] "12")
            res  (modify-patient! args)]
        (is (= 404 (:status res)))))
    (testing "Modifying a patients record with wrong params"
      (let [args (assoc args :query-params {"wrong" "params"})
            res  (modify-patient! args)]
        (is (= 400 (:status res)))
        (is (= "Missing or Invalid Parameters" (:body res))))))
  (drop-test-db))

(comment
  (t/run-tests *ns*))