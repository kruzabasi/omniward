(ns omniward.middleware)

(def wrap-sys
  {:name ::sys
   :description "Middleware for injecting sys-data into requests"
  ;; runs once - imporant for performance reasons
   :compile (fn [{:keys [sys]} _route-options]
              (fn [handler]
                (fn [request]
                  (handler (assoc request :sys sys)))))})