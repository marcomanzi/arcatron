(ns arcatron.test.logic.billing
  (:require [arcatron.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [arcatron.config :refer [env]]
            [mount.core :as mount]
            [arcatron.models :as models]
            [arcatron.services.call-data-records :as cdrs]
            [arcatron.services.customers :as c]
            [arcatron.services.prices :as p]
            [arcatron.logic.billing :as b]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'arcatron.config/env
      #'arcatron.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(defn create-customer [customer-number]
  (let [customer (assoc (models/generate-customer) :phone_number customer-number)
        _ (c/create-customer! customer)]
    customer))

(defn create-price [destination prefix price_per_minute]
  (let [price (models/generate-price destination prefix price_per_minute)
        _ (p/create-price! price)]
    price))

(deftest billing-tests
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [_             (cdrs/delete-all!)
          _             (c/delete-all!)
          _             (p/delete-all!)
          caller        (create-customer "390699335285")
          test-price-1  (create-price "Italy" 39362 "0.01")
          test-price-2  (create-price "Italy" 39159 "0.05")
          test-price-3  (create-price "London" 44 "0.05")
          cdrs          (cdrs/read-from-file "test/resources/test-cdrs.txt" )
          _             (b/load-cdrs cdrs)
          loaded-cdrs   (cdrs/get-all)]
      (testing "Loaded cdrs are saved"
        (is (= 8 (count loaded-cdrs))))
      (testing "Caller Resolution"
        (is (= (:uuid caller) (:caller_uuid (first loaded-cdrs))))
        (is (= (:uuid caller) (:caller_uuid (nth loaded-cdrs 4)))))
      ;Price evaluation Testing
      (testing "Price Resolution"
        (is (= (:uuid test-price-1) (:price_uuid (first loaded-cdrs))))
        (is (= (:uuid test-price-2) (:price_uuid (second loaded-cdrs))))
        (is (= (:uuid test-price-1) (:price_uuid (nth loaded-cdrs 2))))
        (is (= (:uuid test-price-3) (:price_uuid (nth loaded-cdrs 3))))
        (is (= (:uuid test-price-1) (:price_uuid (nth loaded-cdrs 4)))))
      (testing "Error for missing caller"
        (let [error-cdr (nth loaded-cdrs 5)]
          (is (nil? (:caller_uuid error-cdr)))
          (is (= :CUSTOMER_NOT_FOUND (first (:errors error-cdr))))))
      (testing "Error for missing price"
        (let [error-cdr (nth loaded-cdrs 6)]
          (is (nil? (:price_uuid error-cdr)))
          (is (= :PRICE_NOT_FOUND (first (:errors error-cdr))))))
      (testing "Errors for price and customer missing"
        (let [error-cdr (nth loaded-cdrs 7)]
          (is (nil? (:price_uuid error-cdr)))
          (is (nil? (:caller_uuid error-cdr)))
          (is (= [:CUSTOMER_NOT_FOUND :PRICE_NOT_FOUND] (:errors error-cdr))))))))
