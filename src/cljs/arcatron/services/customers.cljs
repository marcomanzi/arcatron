(ns arcatron.services.customers
  (:require [arcatron.models :as models]
            [reagent.core :as r]
            [arcatron.state :as db]))

(def initial-customers
  (map (fn [x] (models/generate-customer)) (range 0 50)))

(def ^{:private true} customers-db (r/atom initial-customers ))

(defn count-customers []
  (count @customers-db))

(defn get-customer [uuid]
  (let [customer (first  (filter #(= (get % :uuid) (int  uuid)) @customers-db))]
    customer))

(defn customers [page]
  (let [size (:table-max-rows @db/app-state)
        max-customers (count-customers)
        max-start (- max-customers 1)
        start (let [page-start (* page size)]
                (cond 
                  (< page-start 0) 0
                  (> page-start max-start) (- max-customers size)
                  :else page-start))
        end (let [end (+ start size)]
              (if (> end max-customers) max-customers end))]
    (r/atom (subvec (vec @customers-db) start end))))
