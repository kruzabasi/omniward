(ns user
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :refer [system]]))

(ig-repl/set-prep!
 (fn []
   (let [config (-> "config.edn"
                    io/resource
                    slurp
                    ig/read-string)]
     (ig/load-namespaces config)
     config)))

(defn start-server
  []
  (ig-repl/go))

(defn stop-server
  []
  (ig-repl/halt))

(defn get-db []
  (:omniward.postgres.db/pg-db system))

(defn restart-server
  []
  (stop-server)
  (start-server))