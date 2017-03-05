(ns arcatron.services.prices
  (:require [arcatron.models :as models]
            [reagent.core :as r]
            [arcatron.state :as db]
            [ajax.core :refer [GET]]))

(declare db count-prices)

(def init
  [(models/generate-price "Italy 191" "39191" "0.01")
   (models/generate-price "Italy 3" "39139" "0.01")
   (models/generate-price "Italy 187" "39187" "0.01")
   (models/generate-price "USA" "13" "10")
   (models/generate-price "Italy" "39159" "0.48")
   (models/generate-price "Italy" "39895" "1.5")])

(defn refresh-count
  []
  (GET "/prices/count"
       {:handler #(reset! count-prices %)})
  @count-prices)

(defn get-prices
  [page size]
  (GET "/prices/get-paginated"
       {:params {:page page :size size}
        :handler #(reset! db %)})
  @db)

(def ^{:private true} db (r/atom init))

(def ^{:private true} count-prices (r/atom 5))

(defn count-all []
  (count @db))

(defn get-price [uuid]
  (let [customer (first (filter #(= (get % :uuid) (int  uuid)) @db))]
    customer))

(defn prices [page]
  (let [size (:table-max-rows @db/app-state)
        max-prices @count-prices
        max-start (- max-prices 1)
        start (let [page-start (* page size)]
                (cond 
                  (< page-start 0) 0
                  (> page-start max-start) (- max-prices size)
                  :else page-start))
        end (let [end (+ start size)]
              (if (> end max-prices) max-prices end))
        _ (println start " " end)]
    (r/atom (vec @db))))

(get-prices 0 (:table-max-rows @db/app-state))
(refresh-count)
