(ns arcatron.test.services.customers
  (:require [arcatron.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [arcatron.config :refer [env]]
            [mount.core :as mount]
            [arcatron.models :as models]
            [arcatron.services.customers :as c]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'arcatron.config/env
      #'arcatron.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(def test-customers (atom []))

(defn create-customer []
  (let [customer (models/generate-customer)
        _ (swap! test-customers conj customer)
        _ (c/create-customer! customer)]
    customer))

(deftest customer-tests
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [customer (create-customer)]
      (is ((complement nil?) customer))
      (is (= customer (c/get (:uuid customer))))
      (is (= 1 (count (c/get-paginated 0 10))))
      (let [customer (create-customer)]
        (is (= 2 (count (c/get-paginated 0 10))))
        (is (= 1 (count (c/get-paginated 0 1))))
        (is (= 1 (count (c/get-paginated 1 1))))
        (c/delete! customer)
        (is (= 1 (count (c/get-paginated 0 10)))))
      (println @test-customers)
      (doall (map #(c/delete! %) @test-customers))
      )))
