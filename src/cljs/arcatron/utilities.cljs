(ns arcatron.utilities
  (:require [secretary.core :as secretary :include-macros true]))

;; ------------------------
;; Utilities

(defn forward
  "Forward to another page and set the URL"
  [url]
  (set! js/window.location.href url)
  (secretary/dispatch! url))
