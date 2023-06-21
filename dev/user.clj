(ns user
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [integrant.repl :as ig-repl]))

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

(defn restart-server
  []
  (stop-server)
  (start-server))