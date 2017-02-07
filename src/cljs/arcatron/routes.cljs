(ns arcatron.routes
  (:require [secretary.core :as secretary :include-macros true]
            [reagent.session :as session]
            [arcatron.customers.table :as ct]
            [arcatron.customers.details :as cd]
            [arcatron.prices.table :as pt]
            [arcatron.prices.details :as pd]))

(def pages
  {:customers        #'arcatron.customers.table/page
   :customer-details #'arcatron.customers.details/page
   :prices           #'arcatron.prices.table/page
   :price-details    #'arcatron.prices.details/page})


(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :customers))

;; Customers
(secretary/defroute "/customers" []
                    (session/put! :page :customers))

(secretary/defroute "/customers/:uuid" {:as params}
                    (doall (session/put! :page :customer-details)
                           (session/put! :uuid (:uuid params))))

(secretary/defroute "/customers/create" []
                    (session/put! :page :customer-details)
                    (session/remove! :uuid))

;; Prices
(secretary/defroute "/prices" []
                    (session/put! :page :prices))

(secretary/defroute "/prices/:uuid" {:as params}
                    (doall (session/put! :page :price-details)
                           (session/put! :uuid (:uuid params))))

(secretary/defroute "/prices/create" []
                    (session/put! :page :price-details)
                    (session/remove! :uuid))
