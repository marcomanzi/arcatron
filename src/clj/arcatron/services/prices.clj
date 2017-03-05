(ns arcatron.services.prices
  (:require [arcatron.db.core :refer [*db*] :as db]
            [arcatron.models :as m]))

(defn get
  "Retrieve customer by uuid"
  [uuid]
  (m/map->Price (db/get-price {:uuid uuid})))

(defn get-paginated
  "Retrieve customers by page and size"
  [page size]
  (into [] (map #(m/map->Price %) (db/get-prices {:limit size :offset (* page size)}))))

(defn get-price-by-prefix
  "Retrieve customers by page and size"
  [prefix]
  (let [prices-by-prefix (db/get-price-by-prefix {:prefix prefix})
        prefix-count (fn [price] (count (:prefix price)))]
    (first (sort #(compare (prefix-count %1) (prefix-count %2)) prices-by-prefix))))

(defn create-price!
  [price]
  (db/create-price! price))

(defn save-prices-in-file
  [file]
  (letfn [(line-to-price [line]
            (apply m/generate-price (clojure.string/split line #"/")))]
    (let [file-input  (slurp file)
          file-lines  (clojure.string/split file-input #"\n")
          prices      (map line-to-price file-lines)]
      (map create-price! prices))))

(defn delete!
  "Delete the customer in input"
  [price]
  (db/delete-price! price))

(defn delete-all!
  "Delete all customers from the database"
  []
  (db/delete-all-prices!))
