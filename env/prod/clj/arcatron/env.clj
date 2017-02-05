(ns arcatron.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[arcatron started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[arcatron has shut down successfully]=-"))
   :middleware identity})
