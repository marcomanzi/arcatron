(ns arcatron.services.customers
  (:require [arcatron.db.core :refer [*db*] :as db]
            [arcatron.models :as m]))

(defn get
  "Retrieve customer by uuid"
  [uuid]
  (m/map->Customer (db/get-customer {:uuid uuid})))

(defn get-paginated
  "Retrieve customers by page and size"
  [page size]
  (into [] (map #(m/map->Customer %) (db/get-customers {:limit size :offset (* page size)}))))

(defn create-customer!
  [customer]
  (db/create-customer! customer))

(defn delete!
  "Delete the customer in input"
  [customer]
  (db/delete-customer! customer))

(defn delete-all!
  "Delete all customers from the database"
  []
  (db/delete-all-customers!))
