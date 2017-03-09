(ns arcatron.prices.table
  (:require [arcatron.utilities :refer [forward]]
            [arcatron.services.prices :as service]
            [arcatron.state :refer [price-page table-max-rows]]
            [arcatron.reagent.table :refer [paginated-table table-header]]
            [reagent.core :as r]))

(def initial-upload-label "Select File with Prices")

(defn ^:export on-price-file-click
  [event]
  (if (> (count (array-seq (-> event .-target .-files))) 0)
    (do
      (.preventDefault event)
      (service/upload-file (aget (-> event .-target .-files) 0)))))

(defn ^:export on-price-file-selection
  [event]
  (let [full-file-path (str (-> event .-target .-value))
        file-name (last (clojure.string/split full-file-path #"\\" ))]
    (set! (.-innerHTML (.getElementById js/document "upload-label")) (str "Upload " file-name))
    event))

(def input-html "<input id='upload-file' name='updload-file' type='file' style='display: none;' onclick='arcatron.prices.table.on_price_file_click(event)' onchange='arcatron.prices.table.on_price_file_selection(event);'></input>")

(defn label-input [label] (str "<span id='upload-label'>" label "</span>"))

(defn remove-all-prices []
  (service/remove-all-prices)
  (forward "#/prices"))

(defn page []
  [:div.container
   [paginated-table {:id "price-table"
                     :headers [(table-header "Destination" true)
                               (table-header "Prefix" true)
                               (table-header "Price per Minute")]
                     :element-key :uuid
                     :on-element-click-url #(str "#/prices/" (:uuid %))
                     :element-provider (partial service/get-prices)
                     :count-provider (partial service/prices-count)
                     :row-fields [:destination :prefix :price_per_minute]}]
   [:form.btn-toolbar
    [:button.btn.btn-primary {:on-click #(forward "#/prices/create")} "Add Price"]
    [:label.btn.btn-primary.file-input.col-span-1
     {:dangerouslySetInnerHTML
      {:__html (str (label-input "Select File with Prices") input-html)}}]
    [:button.btn.btn-primary {:on-click #(remove-all-prices)} "Remove all Prices"]]])

