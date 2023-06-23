(ns omniward.postgres.db
  (:require [integrant.core :as ig]
            [integrant.repl.state :refer [system]]
            [clojure.java.jdbc :as j]
            [java-time.api :as jt]))

(defmethod ig/init-key ::pg-db
  [_ config]
  config)

(defn get-db []
  (:omniward.postgres.db/pg-db system))

(def patients-sql
  (j/create-table-ddl
   :patient 
   [[:patient_id :serial "PRIMARY KEY"]
    [:name "VARCHAR(255)"]
    [:gender "VARCHAR(10)"]
    [:dob "DATE"]
    [:address "VARCHAR(255)"]
    [:phone "VARCHAR(20)"]
    [:CONSTRAINT :unique_patient "UNIQUE(name, dob)"]]))

(defn create-patients-table []
  (let [db-spec (get-db)]
    (j/execute! db-spec patients-sql)))

(defn insert-patient
  [db-spec patient]
  (let [{:keys [p-name gender dob address phone]} patient]
    (j/insert!
     db-spec
     :patient {:name    p-name
               :gender  gender
               :dob     (jt/local-date (jt/sql-date dob))
               :address address
               :phone   phone})))