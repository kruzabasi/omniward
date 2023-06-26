(ns omniward.router
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.spec :as rs]
            [omniward.middleware :as mw]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            
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
                       parameters/parameters-middleware
                       mw/wrap-sys]}})

(defn api-routes []
  ["/api"
   ["/patients"
    [""
     {:get  {:summary    "Fetches all patients"
             :responses  {200 {:body map?}}
             :handler    handler/get-all-patients}
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
     {:get {:summary    "Fetches info of a particular patient"
            :responses  {200 {:body map?}
                         400 {:body string?}
                         404 {:body string?}}
            :parameters {:path {:id string?}}
            :handler    handler/get-patient}
      :put {:summary    "Updates an existing patients record"
            :responses  {200 {:body map?}
                         400 {:body string?}
                         404 {:body string?}}
            :handler    handler/modify-patient!}}]]])

(defn routes
  [sys]
  (ring/ring-handler
   (ring/router
    (api-routes)
    (router-config sys))
   (ring/create-default-handler)))