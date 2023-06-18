(ns omniward.server
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]))

(defmethod ig/init-key ::server
  [_ {:keys [handler port]}]
  (println (str "\nStarting omniward server..."))

  (println (str "\nServer running on port " port))

  (jetty/run-jetty
   handler
   {:port port
    :join? false}))

(defmethod ig/halt-key! ::server
  [_ jetty]
  (.stop jetty))