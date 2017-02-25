(ns arcatron.customers.details
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [arcatron.models :as models]
            [secretary.core :as secretary :include-macros true]
            [clojure.set :as set]
            [arcatron.services.customers :as service]
            [arcatron.utilities :refer [forward]]
            [ajax.core :refer [POST]]))

(defn atom-input [atom-value label field]
  [:div.form-group.col-md-6
   [:label.col-md-12.col-form-label label]
   [:div.col-md-12
    [:input.form-control {:type      "text"
                          :value     (get @atom-value field)
                          :on-change #(swap! atom-value (fn [atom]
                                                          (let [new-value (-> % .-target .-value)]
                                                            (assoc atom field new-value))))}]]])


(defn customer-detail-title [customer]
  (if (:uuid @customer)
    [:h1 (str "Details for " (:name @customer) " " (:surname @customer))]
    [:h1 "New Customer"]))

(defn save-customer []
  (POST "/customers/create"
        {:headers {}
         :params {"customer" (into {} (models/generate-customer))}
         :handler #(js/alert (into {} %))}))

(defn page []
  (let [uuid (session/get :uuid)
        customer (r/atom (if uuid
                           (service/get-customer uuid)
                           models/empty-customer))]
    (fn [] [:form.container
            [customer-detail-title customer]
            [:fieldset.form-group
             [:legend "Base Informations"]
             [atom-input customer "Name" :name]
             [atom-input customer "Surname" :surname]
             [atom-input customer "Fiscal Code/Partita IVA" :fiscal_code]
             [atom-input customer "Phone Number" :phone_number]
             [atom-input customer "Address" :address]
             [atom-input customer "City" :city]]
            [:fieldset.form-group
             [:legend "Billing Informations"]
             [atom-input customer "Margin" :profit-margin]]
            [:button.btn.btn-primary.col-md-2 {:on-click #(.log js/console (clj->js @state))} "Save"]
            [:button.btn.btn-primary.col-md-2.offset-md-1 {:on-click #(forward "#/customers")} "Cancel"]])))
