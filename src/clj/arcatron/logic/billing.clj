(ns arcatron.logic.billing
  (:require [arcatron.db.core :as db]
            [arcatron.services.customers :as c]
            [arcatron.services.prices :as p]))

(defn evalute-caller
  [cdr]
  (let [caller (c/find-by-phone-number (:caller cdr))]
    (assoc cdr :caller_uuid (:uuid caller))))

(defn evaluate-price
  "Evaluate the price for the cdr"
  [cdr]
  (let [receiver (:receiver cdr)
        price (p/get-price-by-prefix (subs receiver 0 5))]
    (assoc cdr :price_uuid (:uuid price))))

(defn load-cdrs
  "Load cdrs in the system retrieving the caller, the price, marking them with errors and saving in the database"
  [cdrs-non-valuated]
  (let [cdrs-with-customers (map evalute-caller cdrs-non-valuated)
        cdrs-with-prices (map evaluate-price cdrs-with-customers)]
    (doall (map db/create-cdr! cdrs-with-prices))))
