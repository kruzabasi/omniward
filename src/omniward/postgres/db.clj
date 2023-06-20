(ns omniward.postgres.db
  (:require [integrant.core :as ig]
            [integrant.repl.state :refer [system]]))

(defmethod ig/init-key ::pg-db
  [_ config]
  config)

(defn get-db []
  (:omniward.postgres.db/pg-db system))