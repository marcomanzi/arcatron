(ns arcatron.routes.services
  (:require [ring.util.http-response :refer :all]
            [ring.swagger.upload :as upload]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [compojure.api.meta :refer [restructure-param]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [arcatron.models :as m]
            [arcatron.services.customers :as c]
            [arcatron.services.prices :as p])
  (:import (java.util UUID)))

(defn access-error [_ _]
  (unauthorized {:error "unauthorized"}))

(defn wrap-restricted [handler rule]
  (restrict handler {:handler  rule
                     :on-error access-error}))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-restricted rule]))

(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}
  
  (GET "/authenticated" []
       :auth-rules authenticated?
       :current-user user
       (ok {:user user}))
  (context "/customers" []
    :tags ["Customers"]

    (GET "/get" []
      :query-params [uuid :- String]
      :summary      "Return the customer with the uuid in input"
      (ok (into {} (c/get uuid))))
    (GET "/get-paginated" []
      :query-params [page :- Long, size :- Long]
      :summary      "Return the customer's page"
      (ok (into {} (c/get-paginated page size))))
    ;Example customer for test {"customer": {"uuid" : "53227", "name" : "Enrico", "surname" : "Summer", "fiscal_code" : "QHJMDO99G63O678M", "phone_number" : "382119121", "invoices_payed" false, "profit_margin" : "1.0", "address" : "Strange Street", "city" : "Test"}}
    (POST "/create" []
      :return      Long
      :body-params [customer]
      :summary     "Create the customer"
      (ok (c/create-customer! customer)))
    )

  (context "/prices" []
    :tags ["Prices"]

    (GET "/get" []
      :query-params [uuid :- String]
      :summary      "Return the price with the uuid in input"
      (ok (into {} (p/get uuid))))
    (GET "/count" []
      :summary      "Return the price's count"
      (ok (p/count-prices)))
    (GET "/get-paginated" []
      :query-params [page :- Long, size :- Long]
      :summary      "Return the prices page"
      (ok (p/get-paginated page size)))
    ;Example price for test {"price": {"destination" : "Roma", "prefix" : "06", "price_per_minute" : "0.01"}}
    (POST "/create" []
      :return      Long
      :body-params [price]
      :summary     "Create or update the price"
      (ok (p/create-price! price)))
    (POST "/remove" []
      :body-params [uuid]
      :summary     "Remove the price"
      (if (p/delete! {:uuid uuid})
        (accepted)
        (forbidden)))
    (POST "/remove-all" []
      :summary     "Remove the price"
      (if (p/delete-all!)
        (accepted)
        (forbidden)))
    (POST "/upload" []
      :multipart-params [file :- upload/TempFileUpload]
      :middleware       [upload/wrap-multipart-params]
      (ok (let [{:keys [filename tempfile]} file ]
            (p/save-prices-in-file tempfile)
            {:success true}))))

  (context "/api" []
    :tags ["thingie"]

    (GET "/plus" []
      :return       Long
      :query-params [x :- Long, {y :- Long 1}]
      :summary      "x+y with query-parameters. y defaults to 1."
      (ok (+ x y)))

    (POST "/minus" []
      :return      Long
      :body-params [x :- Long, y :- Long]
      :summary     "x-y with body-parameters."
      (ok (- x y)))

    (GET "/times/:x/:y" []
      :return      Long
      :path-params [x :- Long, y :- Long]
      :summary     "x*y with path-parameters"
      (ok (* x y)))

    (POST "/divide" []
      :return      Double
      :form-params [x :- Long, y :- Long]
      :summary     "x/y with form-parameters"
      (ok (/ x y)))

    (GET "/power" []
      :return      Long
      :header-params [x :- Long, y :- Long]
      :summary     "x^y with header-parameters"
      (ok (long (Math/pow x y))))))
