(ns arcatron.reagent.state
  (:require [reagent.core :as r]))

(def internal-state (r/atom {:prev-table-max-rows 15
                             :table-max-rows      15}))

(defn- table-page-key
  [table-id]
  (keyword (str table-id "-page")))

(defn table-page
  ([table-id] (r/atom (get @internal-state (table-page-key table-id) 0)))
  ([table-id current-page]
   (swap! internal-state assoc (table-page-key table-id) current-page)))

(defn first-page? [table-id] (= 0 @(table-page table-id)))

(defn last-page? [table-id count-elements-provider]
  (= @(table-page table-id) (quot (- (count-elements-provider) 1) (:table-max-rows @internal-state)) ))


(defn set-table-max-rows []
  (let [h (:window-height @internal-state)
        right-h (if (< h 350) 350 h)]
    (swap! internal-state assoc :prev-table-max-rows (:table-max-rows @internal-state))
    (swap! internal-state assoc :table-max-rows
           (quot (- right-h 300) 50))))

(defn set-windows-informations
  ([] (let [w (.-innerWidth js/window)
            h (.-innerHeight js/window)]
        (do
          (swap! internal-state assoc :window-width w)
          (swap! internal-state assoc :window-height h)
          (set-table-max-rows))))
  ([event] (set-windows-informations)))

(.addEventListener js/window "resize" set-windows-informations)
(set-windows-informations)
