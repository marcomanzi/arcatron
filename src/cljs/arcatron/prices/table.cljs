(ns arcatron.prices.table
  (:require [arcatron.utilities :refer [forward]]
            [arcatron.services.prices :as service]
            [arcatron.state :refer [price-page table-max-rows]]
            [arcatron.reagent.table :refer [paginated-table table-header]]
            [reagent.core :as r]))

(def upload-file-label (r/atom "Select File with Prices"))

(defn ^:export on-price-file-click
  [event]
  (if-let [_ ((complement clojure.string/blank?) (str (-> event .-target .-value)))]
    (do
      (.preventDefault event)
      (js/alert (str "File Selected")))
    (js/alert "No File Selected")))

(defn ^:export on-price-file-selection
  [event]
  (let [full-file-path (str (-> event .-target .-value))
        file-name (last (clojure.string/split full-file-path #"\\" ))]
    (reset! upload-file-label (str "Upload " file-name))))

(def input-html "<input type='file' style='display: none;' onclick='arcatron.prices.table.on_price_file_click(event)' onchange='arcatron.prices.table.on_price_file_selection(event)'></input>")

(defn page []
  [:div.container
   [paginated-table {:id "price-table"
                     :headers [(table-header "Destination" true)
                               (table-header "Prefix" true)
                               (table-header "Price per Minute")]
                     :element-key :uuid
                     :on-element-click-url #(str "#/prices/" (:uuid %))
                     :elements @(service/prices @(price-page))
                     :element-provider (partial service/prices)
                     :count-provider (partial service/count-all)
                     :row-fields [:destination :prefix :price_per_minute]}]
   [:form.btn-toolbar
    [:button.btn.btn-primary {:on-click #(forward "#/prices/create")} "Add Price"]
    [:label.btn.btn-primary.file-input.col-span-1
     {:dangerouslySetInnerHTML
      {:__html (str
                 @upload-file-label
                 input-html)}}]
    [:button.btn.btn-primary {:on-click #(forward "#/prices/create")} "Remove all Prices"]]])

