(ns omniward.util.integrant-config
  (:require
    [clojure.java.io :as io]
    [integrant.core :as ig]))

(defn config []
  (let [config (->
                "config.edn"
                io/resource
                slurp
                ig/read-string)]
    (ig/load-namespaces config)
    config))