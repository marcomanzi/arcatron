(ns arcatron.logic.billing
  (:require [arcatron.db.core :as db]
            [arcatron.services.customers :as c]
            [arcatron.services.prices :as p]))

(defn load-cdrs
  "Load cdrs in the system retrieving the caller, the price, marking them with errors and saving in the database"
  [cdrs-non-valuated]
  (letfn [(save-cdr! [{:keys [errors] :as cdr}]
            (db/create-cdr! (assoc cdr :errors (str errors))))
          (evalute-caller [cdr]
            (let [caller (c/find-by-phone-number (:caller cdr))]
              (assoc cdr :caller_uuid (:uuid caller))))
          (evaluate-price [cdr]
            (letfn [(retrieve-price-by-prefix [receiver]
                      (let [price (p/get-price-by-prefix receiver)]
                        (if (and (nil? price) (> (count receiver) 0))
                          (retrieve-price-by-prefix (subs receiver 0 (dec (count receiver))))
                          price)))]
              (assoc cdr :price_uuid (:uuid (retrieve-price-by-prefix (:receiver cdr))))))
          (null-field-check-cdr [field error {:keys [errors] :as cdr}]
            (if (nil? (field cdr))
              (assoc cdr :errors (conj errors error))
              cdr))
          (check-if-has-customer [cdr]
            (null-field-check-cdr :caller_uuid :CUSTOMER_NOT_FOUND cdr))
          (check-if-has-price [cdr]
            (null-field-check-cdr :price_uuid :PRICE_NOT_FOUND cdr))]
    (let [evaluated-cdrs (map (comp check-if-has-price check-if-has-customer evaluate-price evalute-caller) cdrs-non-valuated)]
      (doall (map save-cdr! evaluated-cdrs)))))
