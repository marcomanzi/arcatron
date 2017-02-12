(ns arcatron.prices.table
  (:require [arcatron.utilities :refer [forward]]
            [arcatron.services.prices :as service]
            [arcatron.state :refer [price-page table-max-rows]]
            [arcatron.reagent.table :refer [paginated-table table-header]]))

(defn th-with-input [label]
  [:th label [:input.table-search]])

(defn row
  [{:keys [uuid destination prefix price-per-minute] :as price}]
  [:tr {:class    "hover-pointer"
        :on-click #(let [url (str "#/prices/" uuid)]
                     (forward url))}
   [:td destination]
   [:td prefix]
   [:td price-per-minute]])

(defn customer-table-head []
  [:thead
   [:tr
    [th-with-input "Destination "]
    [th-with-input "Prefix "]
    [:th "Price per Minute"]]])

(defn page-item [label page] [:li.page-item [:a.page-link {:on-click #(price-page page)} label]])

(defn pagination []
  [:ul.pagination.offset-md-5
   (if (not (service/first-page?)) [page-item "<" (- @(price-page) 1)])
   [page-item @(price-page) @(price-page)]
   (if (not (service/last-page?)) [page-item ">" (+ @(price-page) 1)])])

(defn table []
  [:table {:class "table table-hover table-bordered"}
   [customer-table-head]
   [:tbody
    (for [price @(service/prices @(price-page))]
      ^{:key (:uuid price)} [row price])]])

(defn page []
  [:div.container
   (comment [table]
            [pagination])
   [paginated-table {:id "price-table"
                     :headers [(table-header "Destination" true)
                               (table-header "Prefix" true)
                               (table-header "Price per Minute")]
                     :element-key :uuid
                     :on-element-click-url #(str "#/prices/" (:uuid %))
                     :elements @(service/prices @(price-page))
                     :row-fields [:destination :prefix :price-per-minute]}]
   [:div [:button.btn.btn-primary {:on-click #(forward "#/prices/create")} "Add Price"]]])

