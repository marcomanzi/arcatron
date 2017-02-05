(ns arcatron.state
  (:require [reagent.core :as r]))

(def app-state (r/atom {:window-width (.-width js/window)
                        :window-height (.-height js/window)
                        :table-max-rows 15
                        :customer-page 0}))

(defn set-table-max-rows []
  (let [h (:window-height @app-state)]
    (swap! app-state assoc :table-max-rows
           (quot (- h 300) 50))))

(defn set-windows-informations
  ([] (let [w (.-innerWidth js/window)
            h (.-innerHeight js/window)]
        (do
          (swap! app-state assoc :window-width w)
          (swap! app-state assoc :window-height h)
          (set-table-max-rows))))
  ([event] (set-windows-informations)))

(defn customer-page
  ([] (r/atom (:customer-page @app-state)))
  ([current-page] (swap! app-state assoc :customer-page current-page)))

(defn table-max-rows
  [] (r/atom (:table-max-rows @app-state)))

(.addEventListener js/window "resize" set-windows-informations)
