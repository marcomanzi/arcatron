(ns arcatron.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [arcatron.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            [arcatron.models :as models]
            [arcatron.customers.table :as ct]
            [arcatron.customers.details :as cd]
            [arcatron.state :as db])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-dark.bg-primary
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "Arcatron"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Customers" :home collapsed?]
         [nav-link "#/prices" "Prices" :about collapsed?]
         [nav-link "#/customers" "New-Customers" :about collapsed?]]]])))

(defn prices-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src (str js/context "/img/warning_clojure.png")}]]]])

(defn home-page []
  [:div.container
   (when-let [docs (session/get :docs)]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])

(def pages
  {:home #'home-page
   :prices #'prices-page
   :customers #'arcatron.customers.table/customers-page
   :customer-details #'arcatron.customers.details/customer-detail-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/prices" []
  (session/put! :page :prices))

(secretary/defroute "/customers" []
  (session/put! :page :customers))

(secretary/defroute "/customers/:uuid" {:as params}
  (doall (session/put! :page :customer-details)
         (session/put! :uuid (:uuid params))))

(secretary/defroute "/customers/create" []
                    (session/put! :page :customer-details)
                    (session/remove! :uuid))


;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
            (db/set-windows-informations)
            (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
