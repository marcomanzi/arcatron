(ns arcatron.customers.table
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [arcatron.models :as models]
            [secretary.core :as secretary :include-macros true]
            [clojure.set :as set]
            [arcatron.services.customers :as service]
            [arcatron.routes :as routes]
            [arcatron.state :refer [customer-page table-max-rows]]))

(defn th-with-input [label]
  [:th label [:input.table-search]])

(defn customer-row
  [{:keys [uuid name surname fiscal_code phone_number invoices_payed] :as customer}]
  [:tr {:class    "hover-pointer"
        :on-click #(let [url (str "#/customers/" uuid)]
                     (routes/forward url))}
   [:td name]
   [:td surname]
   [:td fiscal_code]
   [:td phone_number]
   [:td (str invoices_payed)]])

(defn customer-table-head []
  [:thead
   [:tr
    [th-with-input "Name "]
    [th-with-input "Surname "]
    [:th "Fiscal Code"]
    [:th "Telephon Number"]
    [:th "Invoices Payed"]]])

(defn page-item [label page] [:li.page-item [:a.page-link {:on-click #(customer-page page)} label]])

(defn pagination []
  [:ul.pagination.offset-md-5
   (if (> @(customer-page) 0) [page-item "<" (- @(customer-page) 1)])
   [page-item @(customer-page) @(customer-page)]
   [page-item ">" (+ @(customer-page) 1)]])

(defn customer-table []
  [:table {:class "table table-hover table-bordered"}
   [customer-table-head]
   [:tbody
    (for [customer @(service/customers @(customer-page))]
      ^{:key (:uuid customer)} [customer-row customer])]])

(defn customers-page []
  [:div.container
   [customer-table]
   [pagination] [:div (str @(customer-page) @(table-max-rows))]
   [:div [:button.btn.btn-primary {:on-click #(routes/forward "#/customers/create")} "Add Customer"]]])


