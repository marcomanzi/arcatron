(ns arcatron.services.customers
  (:require [arcatron.models :as models]
            [reagent.core :as r]
            [arcatron.state :as db]))

(def initial-customers
  (map (fn [x] (models/generate-customer)) (range 0 50)))

(def ^{:private true} customers-db (r/atom initial-customers ))

(defn customers [page]
  (let [size (:table-max-rows @db/app-state)
        start (* page size)
        end (+ start size)]
    (r/atom (subvec (vec @customers-db) start end))))

(defn get-customer [uuid]
  (let [customer (first  (filter #(= (get % :uuid) (int  uuid)) @customers-db))]
    customer))
