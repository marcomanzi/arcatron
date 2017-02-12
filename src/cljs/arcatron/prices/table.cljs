(ns arcatron.prices.table
  (:require [arcatron.utilities :refer [forward]]
            [arcatron.services.prices :as service]
            [arcatron.state :refer [price-page table-max-rows]]
            [arcatron.reagent.table :refer [paginated-table table-header]]))

(defn page []
  [:div.container
   [paginated-table {:id "price-table"
                     :headers [(table-header "Destination" true)
                               (table-header "Prefix" true)
                               (table-header "Price per Minute")]
                     :element-key :uuid
                     :on-element-click-url #(str "#/prices/" (:uuid %))
                     :element-provider (partial service/prices)
                     :count-provider (partial service/count-all)
                     :row-fields [:destination :prefix :price-per-minute]}]
   [:div [:button.btn.btn-primary {:on-click #(forward "#/prices/create")} "Add Price"]]])

