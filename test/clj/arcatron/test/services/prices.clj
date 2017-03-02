(ns arcatron.test.services.prices
  (:require [arcatron.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [arcatron.config :refer [env]]
            [mount.core :as mount]
            [arcatron.models :as models]
            [arcatron.services.prices :as p]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'arcatron.config/env
      #'arcatron.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(def test-prices (atom []))

(defn create-price [destination prefix price_per_minute]
  (let [price (models/generate-price destination prefix price_per_minute)
        _ (swap! test-prices conj price)
        _ (p/create-price! price)]
    price))

(deftest price-tests
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [_ (p/delete-all!)
          italy-price (create-price "Italy" "06" "0.01")]
      (is (= "Italy" (:destination (p/get-price-by-prefix "06"))))
      (let [italy-tim-price (create-price "Italy Tim" "0651" "0.05")]
        (is (= "Italy" (:destination (p/get-price-by-prefix "06"))))
        (is (= "Italy Tim" (:destination (p/get-price-by-prefix "0651")))))
      (map p/delete! @test-prices))))
