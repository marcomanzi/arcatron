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

(def test-caller-number "390699335285")

(def test-receiver-prefix-1 39362)
(def test-receiver-prefix-2 39159)

(def test-customers (atom []))

(defn create-customer [customer-number]
  (let [customer (assoc (models/generate-customer) :phone_number customer-number)
        _ (swap! test-customers conj customer)
        _ (c/create-customer! customer)]
    customer))

(def test-prices (atom []))

(defn create-price [destination prefix price_per_minute]
  (let [price (models/generate-price destination prefix price_per_minute)
        _ (swap! test-prices conj price)
        _ (p/create-price! price)]
    price))

(deftest billing-tests
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [_             (cdrs/delete-all!)
          caller        (create-customer test-caller-number)
          test-price-1  (create-price "Italy" test-receiver-prefix-1 "0.01")
          test-price-2  (create-price "Italy" test-receiver-prefix-2 "0.05")
          cdrs          (cdrs/read-from-file "test/resources/test-cdrs.txt" )
          _             (b/load-cdrs cdrs)
          loaded-cdrs   (cdrs/get-all)]
      (is (= 5 (count loaded-cdrs)))
      (is (= (:uuid caller) (:caller_uuid (first loaded-cdrs))))
      (is (= (:uuid test-price-1) (:price_uuid (first loaded-cdrs))))
      (doall (map p/delete! @test-prices))
      (doall (map c/delete! @test-customers)))))
