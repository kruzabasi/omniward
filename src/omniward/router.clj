(ns omniward.router
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.spec :as rs]
            [ring.util.response :as rr]
            [omniward.middleware :as mw]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            
            [omniward.handlers :as handler]))

(defn router-config
  [sys]
  {:validate rs/validate
  ;; :reitit.middleware/transform dev/print-request-diffs
   :exception pretty/exception
   :data {:sys sys
          :coercion coercion-spec/coercion
          :muuntaja m/instance
          :middleware [muuntaja/format-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware
                       mw/wrap-sys]}})

(defn test-handler[_]
  (rr/response "Hello"))

(defn api-routes []
  ["/api"
   ["/patients"
    [""
     {:get  {:handler    test-handler
             :responses  {200 {:body string?}}
             :parameters {}
             :summary    "Test"}
      :post {:summary    "Creates a new patient record"
             :parameters {:body {:p-name string?
                                 :gender string?
                                 :dob string?
                                 :address string?
                                 :phone string?}}
             :responses  {201 {:body map?}
                          409 {:body string?}}
             :handler handler/new-patient!}}]
    ["/:id"
     {:get {:handler    test-handler
            :responses  {200 {:body string?}}
            :parameters {}
            :summary    "Test"}}]]])

(defn routes
  [sys]
  (ring/ring-handler
   (ring/router
    (api-routes)
    (router-config sys))))