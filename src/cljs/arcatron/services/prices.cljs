(ns arcatron.services.prices
  (:require [arcatron.models :as models]
            [reagent.core :as r]
            [arcatron.state :as db]))

(def init
  [(models/generate-price "Italy 191" "39191" "0.01")
   (models/generate-price "Italy 3" "39139" "0.01")
   (models/generate-price "Italy 187" "39187" "0.01")
   (models/generate-price "USA" "13" "10")
   (models/generate-price "Italy" "39159" "0.48")
   (models/generate-price "Italy" "39895" "1.5")])

(def ^{:private true} db (r/atom init))

(defn count-all []
  (count @db))

(defn get-price [uuid]
  (let [customer (first (filter #(= (get % :uuid) (int  uuid)) @db))]
    customer))

(defn prices [page]
  (let [size (:table-max-rows @db/app-state)
        max-customers (count-all)
        max-start (- max-customers 1)
        start (let [page-start (* page size)]
                (cond 
                  (< page-start 0) 0
                  (> page-start max-start) (- max-customers size)
                  :else page-start))
        end (let [end (+ start size)]
              (if (> end max-customers) max-customers end))]
    (r/atom (subvec (vec @db) start end))))
