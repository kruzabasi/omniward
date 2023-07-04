(ns omniward.core
  (:require [integrant.core :as ig]
            [omniward.util.integrant-config :as igc]
            [omniward.router :as router])
  (:gen-class))

(defn app
  [sys]
  (router/routes sys))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarting app..")
  (app config))

(defonce system (atom nil))

(defn start-system! []
  (let [s (ig/init (igc/config) [:omniward.server/server])]
    (reset! system s)))

(defn stop-system! []
  (ig/halt! @system))

(defn -main [& args]
  (start-system!)
  (.addShutdownHook (Runtime/getRuntime) (Thread. #(stop-system!))))