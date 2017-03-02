(ns arcatron.test.services.call-data-records
  (:require [arcatron.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [arcatron.config :refer [env]]
            [mount.core :as mount]
            [arcatron.models :as models]
            [arcatron.services.call-data-records :as cdrs])
  (:import (java.io File)))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'arcatron.config/env
      #'arcatron.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(def test-call-data-records (atom []))

#_(defn create-customer []
  (let [customer (models/generate-customer)
        _ (swap! test-customers conj customer)
        _ (c/create-customer! customer)]
    customer))

(deftest customer-tests
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [_ (cdrs/delete-all!)
          cdrs (cdrs/read-from-file "test/resources/test-cdrs.txt" )]
      (is (= "" (:nv (second cdrs))))
      (is (= "39159" (:receiver (second cdrs))))
      (is (= "908646-0617954" (:cid (second cdrs))))
      (is (= 1 (cdrs/create-cdr! (first cdrs)))))))
