(ns omniward.handlers
  (:require [omniward.postgres.db :as db]))

(defn get-patient
  [{:keys [parameters sys]}]
  (try
    (let [db-spec      (-> sys :postgres)
          patient-id   (Integer/parseInt (-> parameters :path :id))
          patient-info (first (db/get-patient-info db-spec patient-id))]
      (if patient-info
        {:status 200
         :body {:data patient-info}}
        {:status 404
         :body (str "Patient with id: " patient-id " does not exist")}))
    (catch NumberFormatException _
      {:status 400
       :body "Invalid Patient ID"})))

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

(defn modify-patient!
  [{:keys [query-params path-params sys]}]
  (let [db-spec (-> sys :postgres)
        {:strs [p-name dob gender address phone]} query-params]
    (try
      (let [patient-id   (Integer/parseInt (:id path-params))
            update-val   (cond-> {}
                           p-name (assoc :name p-name)
                           dob (assoc :dob dob)
                           gender (assoc :gender gender)
                           phone (assoc :phone phone)
                           address (assoc :address address))]
        (if (empty? update-val)
          {:status 400
           :body  "Missing or Invalid Parameters"}
          (let [db-res (first (db/update-patient
                               db-spec
                               {:update-val update-val
                                :patient-id patient-id}))]
            (cond
              (= 1 db-res) {:status 200
                            :body {:data update-val}}
              (= 0 db-res) {:status 404
                            :body (str "Patient with id: " patient-id " does not exist")}))))
      (catch NumberFormatException _
        {:status 400
         :body "Invalid Patient ID"}))))