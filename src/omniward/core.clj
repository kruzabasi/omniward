(ns omniward.core
  (:require [integrant.core :as ig]
            [omniward.router :as router]))

(defn app
  [sys]
  (router/routes sys))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarting app..")
  (app config))