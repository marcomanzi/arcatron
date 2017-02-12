(ns arcatron.customers.table
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [arcatron.models :as models]
            [secretary.core :as secretary :include-macros true]
            [clojure.set :as set]
            [arcatron.services.customers :as service]
            [arcatron.utilities :refer [forward]]
            [arcatron.state :refer [customer-page table-max-rows]]
            [arcatron.reagent.table :refer [paginated-table table-header]]))

(defn page []
  [:div.container
   [paginated-table {:id "customer-table"
                     :headers [(table-header "Name" true)
                               (table-header "Surname" true)
                               (table-header "Fiscal Code")
                               (table-header "Telephon Number")
                               (table-header "Invoices Payed")]
                     :element-key :uuid
                     :on-element-click-url #(str "#/customer/" (:uuid %))
                     :elements @(service/customers @(customer-page))
                     :element-provider (partial service/customers)
                     :count-provider (partial service/count-customers)
                     :row-fields [:name :surname :fiscal_code :phone_number :invoices_payed]}]
   [:div [:button.btn.btn-primary {:on-click #(forward "#/customers/create")} "Add Customer"]]])


