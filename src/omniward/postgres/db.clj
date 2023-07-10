(ns omniward.postgres.db
  (:require [integrant.core :as ig]
            [clojure.java.jdbc :as j]
            [java-time.api :as jt]))

(defmethod ig/init-key ::pg-db
  [_ config]
  config)

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

(defn create-patients-table
  [db]
  (let [db-spec db]
    (j/execute! db-spec patients-sql)))

(defn next-cond
  "Returns an initial or subsequent SQL where clause without parameters.\n
   Examples:\n
   user=> (next-cond '' 'age')\n
   ' age = ?'\n
   user=> (next-cond ' age = ?' 'gender')\n
   ' age = ? AND gender = ?'"
  [where-clause next-clause]
  (str where-clause
       (when-not (empty? where-clause) " AND")
       " " next-clause
       " = ?"))

(defn build-query
  "Builds SQL query with optional WHERE clause and pagination.
   Examples:\n
   user=> (build-query ' age = ? AND gender = ?' [22 'female'])\n
   ['select * from patient WHERE age = ? and gender = ?' 22 'female']"
  ([where params]
   (build-query where params nil))
  ([where params {:keys [offset limit]}]
   (let [select       "select * from patient"
         select+where (str
                       select
                       (when (not-empty where) " where ")
                       where)
         sw+pages     (cond->
                       (str select+where
                            (str " limit " limit))
                        offset (str " offset " offset))
         swp+params   (if (empty? params)
                        [sw+pages]
                        (flatten (conj [sw+pages] params)))]
     (vec swp+params))))

(defn get-patient-info
  [db-spec patient-id]
  (j/query
   db-spec
   ["select * from patient where patient_id = ?" patient-id]))

(defn get-patients
  ([db-spec]
   (get-patients db-spec {:offset nil :limit 100}))

  ([db-spec {:keys [offset limit params]}]
   (let [{:keys [p-name gender dob address phone]} params
         limit  (or limit 100)
         params (cond-> []
                  p-name  (conj p-name)
                  gender  (conj gender)
                  dob     (conj dob)
                  address (conj address)
                  phone   (conj phone))
         where  (cond-> ""
                  p-name  (next-cond "name")
                  gender  (next-cond "gender")
                  dob     (next-cond "dob")
                  address (next-cond "address")
                  phone   (next-cond "phone"))
         query-vec (build-query
                    where
                    params
                    {:offset offset :limit limit})]
     (j/query
      db-spec
      query-vec))))

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

(defn update-patient
  [db-spec patient]
  (let [{:keys [update-val patient-id]} patient]
    (j/update!
     db-spec
     :patient
     update-val
     ["patient_id=?" patient-id])))

(defn delete-patient
  [db-spec patient-id]
  (j/delete! db-spec :patient ["patient_id=?" patient-id]))