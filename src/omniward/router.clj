(ns omniward.router
  (:require [reitit.ring :as ring]
            [reitit.ring.spec :as rs]
            [ring.util.response :as rr]
            [reitit.dev.pretty :as pretty]))

(defn router-config
  [sys]
  {:validate  rs/validate
   :exception pretty/exception
   :data {:sys sys}})

(defn test-handler[_]
  (rr/response "Hello"))

(defn routes
  [sys]
  (ring/ring-handler
   (ring/router
    ["/"
     {:get {:handler    test-handler
            :responses  {201 {:body nil?}}
            :parameters {}
            :summary    "Test"}}]
   (router-config sys))))