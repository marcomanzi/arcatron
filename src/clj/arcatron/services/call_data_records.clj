(ns arcatron.services.call-data-records
  (:require [arcatron.models :as models]
            [arcatron.db.core :as db]
            [arcatron.models :as m]))

(defn create-cdr!
  [cdr]
  (db/create-cdr! cdr))

(defn delete-all!
  "Remove the call data record in input"
  []
  (db/delete-all-cdrs!))

(defn- from-line-to-values
  "Convert a cdr line in a sequence of values"
  [str]
  (clojure.string/split str #"\t"))

(defn read-from-file
  "Read cdrs from a file removing the first line with description"
  [file-path]
  (let [file-content (slurp file-path)
        cdr-lines (rest (clojure.string/split file-content #"\n"))
        cdrs (map models/non-evaluated-call-data-record (map from-line-to-values cdr-lines))]
    cdrs))

(defn get-all
  "Retrieve all Cdrs from the database"
  []
  (->> (db/get-all-cdrs)
       (map m/map->call-data-record)))
