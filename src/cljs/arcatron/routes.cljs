(ns arcatron.routes
  (:require [secretary.core :as secretary]))

(defn forward
  "Forward to another page and set the URL"
  [url]
  (set! js/window.location.href url)
  (secretary/dispatch! url))
