(ns arcatron.services.prices
  (:require [arcatron.models :as models]
            [reagent.core :as r]
            [arcatron.state :as db]
            [ajax.core :refer [GET POST]]))

(declare db count-prices)

(defn prices-count
  []
  (GET "/prices/count"
       {:handler #(if (not= % @count-prices) (reset! count-prices %))})
  @count-prices)

(defn get-prices
  [page size]
  (let [_ (js/console.log (str "Price for page:" page " size:" size))]
    (GET "/prices/get-paginated"
         {:params  {:page page :size size}
          :handler #(if (not= @db %)
                      (reset! db %))}))
  db)

(defn save-price [price]
  (POST "/prices/create"
        {:params {:price price}
         :handler #(prices-count)}))

(defn remove-price [price]
  (POST "/prices/remove"
        {:params {:uuid (:uuid price)}
         :handler #(prices-count)}))

(defn remove-all-prices []
  (POST "/prices/remove-all"
        {:handler #(prices-count)}))

(defn upload-file [file]
  (letfn [(handle-response-ok [] (do
                                   (get-prices 0 db/table-max-rows)
                                   (prices-count)))
          (handle-response-error [] (js/alert "There were problems during the upload"))]
    (let [form-data (doto
                      (js/FormData.)
                      (.append "file" file))]
      (POST "/prices/upload"
            {:body form-data
             :response-format :json
             :keywords? true
             :handler handle-response-ok
             :error-handler handle-response-error}))))

(def ^{:private true} db (r/atom []))

(def ^{:private true} count-prices (r/atom {:count 5}))

(defn get-price [uuid]
  (let [price (first (filter #(= (str (get % :uuid)) uuid) @db))]
    price))

(prices-count)
