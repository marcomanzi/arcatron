(ns arcatron.prices.details
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [arcatron.models :as models]
            [secretary.core :as secretary :include-macros true]
            [clojure.set :as set]
            [arcatron.services.prices :as service]
            [arcatron.utilities :refer [forward]]))

(defn atom-input [atom-value label field]
  [:div.form-group.col-md-6
   [:label.col-md-12.col-form-label label]
   [:div.col-md-12
    [:input.form-control {:type      "text"
                          :value     (get @atom-value field)
                          :on-change #(swap! atom-value (fn [atom]
                                                          (let [new-value (-> % .-target .-value)]
                                                            (assoc atom field new-value))))}]]])

(defn detail-title [price]
  (if (:uuid @price)
    [:h1 (str "Details for destination " (:destination @price))]
    [:h1 "New Price"]))

(defn save-and-forward [price]
  (service/save-price price)
  (forward "#/prices"))

(defn remove-and-forward [price]
  (service/remove-price price)
  (forward "#/prices"))

(defn page []
  (let [uuid (session/get :uuid)
        price (r/atom (if uuid
                        (service/get-price uuid)
                        models/empty-price))]
    (fn [] [:div.container
            [detail-title price]
            [:fieldset.form-group
             [atom-input price "Destination" :destination]
             [atom-input price "Prefix" :prefix]
             [atom-input price "Price Per minute" :price_per_minute]]
            [:button.btn.btn-primary.col-md-2 {:on-click #(save-and-forward @price)} "Save"]
            (if uuid [:button.btn.btn-primary.col-md-2.offset-md-1 {:on-click #(remove-and-forward @price)} "Remove"])
            [:button.btn.btn-primary.col-md-2.offset-md-1 {:on-click #(forward "#/prices")} "Cancel"]])))
