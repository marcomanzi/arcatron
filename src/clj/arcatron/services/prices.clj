(ns arcatron.services.prices
  (:require [arcatron.db.core :refer [*db*] :as db]
            [arcatron.models :as m]))

(defn get
  "Retrieve customer by uuid"
  [uuid]
  (let [values (db/get-price {:uuid uuid})]
    (if values
      (m/map->Price values)
      nil)))

(defn count-prices
  "Max number of prices in the system"
  []
  (let [count (db/count-prices)]
    {:count (:c count)}))

(defn get-paginated
  "Retrieve customers by page and size"
  [page size]
  (into [] (map #(m/map->Price %) (db/get-prices {:limit size :offset (* page size)}))))

(defn get-price-by-prefix
  "Retrieve prices by prefix"
  [prefix]
  (let [prices-by-prefix (db/get-price-by-prefix {:prefix prefix})
        prefix-count (fn [price] (count (:prefix price)))]
    (first (sort #(compare (prefix-count %1) (prefix-count %2)) prices-by-prefix))))

(defn create-price!
  [{:keys [uuid destination prefix price_per_minute created_on]}]
  (if-let [price-to-update (get uuid)]
    (db/update-price! (m/update-price price-to-update destination prefix price_per_minute))
    (let [generated-price (m/generate-price destination prefix price_per_minute)
          price-to-create (if uuid (assoc generated-price :uuid uuid) generated-price)]
      (db/create-price! price-to-create))))

(defn save-prices-in-file
  [file]
  (letfn [(line-to-price [line]
            (apply m/generate-price (clojure.string/split line #"/")))]
    (let [file-input  (slurp file)
          file-lines  (clojure.string/split file-input #"\n")
          prices      (map line-to-price file-lines)
          _           (doall (map create-price! prices))]
      (count prices))))

(defn delete!
  "Delete the customer in input"
  [price]
  (db/delete-price! price))

(defn delete-all!
  "Delete all customers from the database"
  []
  (db/delete-all-prices!))
