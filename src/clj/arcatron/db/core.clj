(ns arcatron.db.core
  (:require
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    [arcatron.config :refer [env]]))

(defstate ^:dynamic *db*
           :start (conman/connect! {:jdbc-url (env :database-url)})
           :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

