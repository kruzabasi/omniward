(ns omniward.handlers
  (:require [omniward.postgres.db :as db]))

(defn new-patient!
  [{:keys [parameters sys]}]
  (let [patient-data (-> parameters :body)
        db-spec      (-> sys :postgres)]
    (try
      (let [new-patient (db/insert-patient db-spec patient-data)]
        {:status 201
         :body   {:data (first new-patient)}})
      (catch java.sql.SQLException e
        (let [patient-name (:p-name patient-data)
              error-code   (.getSQLState e)
              unique-constraint-error "23505"]
          (if (= error-code unique-constraint-error)
            {:status 409
             :body (str "Patient: " patient-name ", with the same date-of-birth already exists")}
            (throw e)))))))