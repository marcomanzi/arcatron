(ns arcatron.customers.routes
  (:require [secretary.core :as secretary :include-macros true]
            [reagent.session :as session]))

(secretary/defroute "/customers" []
                    (session/put! :page :customers))

(secretary/defroute "/customers/:uuid" {:as params}
                    (doall (session/put! :page :customer-details)
                           (session/put! :uuid (:uuid params))))

(secretary/defroute "/customers/create" []
                    (session/put! :page :customer-details)
                    (session/remove! :uuid))
