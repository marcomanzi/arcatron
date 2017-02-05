(ns arcatron.state
  (:require [reagent.core :as r]))

(declare customer-page table-max-rows)

(def app-state (r/atom {:window-width        (.-width js/window)
                        :window-height       (.-height js/window)
                        :prev-table-max-rows 15
                        :table-max-rows      15
                        :customer-page       0}))

(defn set-table-max-rows []
  (let [h (:window-height @app-state)
        right-h (if (< h 350) 350 h)]
    (swap! app-state assoc :prev-table-max-rows (:table-max-rows @app-state))
    (swap! app-state assoc :table-max-rows
           (quot (- right-h 300) 50))))

(defn set-customer-page
  "Set the right customer page if the table max row as changed (if it was the 6th page with less or more rows in the table it can be the 5th or the 7th"
  []
  (swap! app-state assoc :customer-page 
         (let [customer-page @(customer-page)
               prev-table-max-rows (:prev-table-max-rows @app-state)
               num-of-first-customer-in-table (* customer-page prev-table-max-rows)]
           (quot num-of-first-customer-in-table @(table-max-rows))))) 

(defn set-windows-informations
  ([] (let [w (.-innerWidth js/window)
            h (.-innerHeight js/window)]
        (do
          (swap! app-state assoc :window-width w)
          (swap! app-state assoc :window-height h)
          (set-table-max-rows)
          (set-customer-page))))
  ([event] (set-windows-informations)))

(defn customer-page
  ([] (r/atom (:customer-page @app-state)))
  ([current-page] (swap! app-state assoc :customer-page current-page)))

(defn table-max-rows
  [] (r/atom (:table-max-rows @app-state)))

(.addEventListener js/window "resize" set-windows-informations)
