(ns test-common
  (:require
   [clojure.java.jdbc :as j]
   [omniward.specs.patient :as specs]))

(def test-db-spec
  {:dbtype "postgresql"
   :port 5432
   :user "admin"
   :dbname "omniward_test"
   :password "admin"})

(def test-patient-ddl
  (j/create-table-ddl
   :patient
   [[:patient_id :serial "PRIMARY KEY"]
    [:name "VARCHAR(255)"]
    [:gender "VARCHAR(10)"]
    [:dob "DATE"]
    [:address "VARCHAR(255)"]
    [:phone "VARCHAR(20)"]
    [:CONSTRAINT :unique_test_patient "UNIQUE(name, dob)"]]))

(defn create-test-db []
  (j/execute! test-db-spec test-patient-ddl))

(defn drop-test-db []
  (j/execute! test-db-spec "DROP TABLE IF EXISTS patient"))

(defn db-fixture
  [test-fn]
  (try
    (create-test-db)
    (test-fn)
    (finally (drop-test-db))))

(defn patient-data
  []
  (specs/create ::specs/patient-data))