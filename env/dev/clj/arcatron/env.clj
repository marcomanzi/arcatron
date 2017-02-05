(ns arcatron.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [arcatron.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[arcatron started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[arcatron has shut down successfully]=-"))
   :middleware wrap-dev})
