(ns arcatron.reagent.table
  (:require [arcatron.reagent.utilities :as u]
            [arcatron.reagent.state :as s]))

(defn- th-header [header]
  (let [label (:label header)]
    (if (:with-input header)
      [u/th-with-input label]
      [:th label])))

(defn- customer-table-head [headers]
  [:thead
   [:tr
    (for [header headers]
      ^{:key header} [th-header header])]])

(defn- row
  [element row-key row-fields on-element-click-url]
  [:tr {:class    "hover-pointer"
        :on-click #(let [url (on-element-click-url element)]
                     (u/forward url))}
   (for [field row-fields]
     ^{:key (str (row-key element) field)} [:td (str (field element))])])

(defn- table-header
  ([label with-input]
   {:label label
    :with-input with-input})
  ([label]
   (table-header label false)))

(defn- page-item [label page table-id] [:li.page-item [:a.page-link {:on-click #(s/table-page table-id page)} label]])

(defn- pagination [table-id count-provider]
  [:ul.pagination.offset-md-5
   (if (not (s/first-page? table-id)) [page-item "<" (- @(s/table-page table-id) 1) table-id])
   [page-item @(s/table-page table-id) @(s/table-page table-id) table-id]
   (if (not (s/last-page? table-id count-provider)) [page-item ">" (+ @(s/table-page table-id) 1) table-id])])

(defn paginated-table
  [{:keys [id headers elements element-provider count-provider element-key row-fields on-element-click-url] :as config}]
  (let [elements @(element-provider @(s/table-page id))]
    [:span
     [:table {:class "table table-hover table-bordered"}
      [customer-table-head headers]
      [:tbody
       (for [element elements]
         ^{:key (element-key element)} [row element element-key row-fields on-element-click-url])]]
     [pagination id count-provider]]))

