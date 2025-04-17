(ns me.contact-app.web.html.home
  (:require [dev.onionpancakes.chassis.compiler :as oc-compiler]
            [dev.onionpancakes.chassis.core :as oc-core]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]
            [me.contact-app.web.html.list :refer [contact-table]]
            [ring.util.response :as response]))

(defn main-content [contact-list]
  [:div {:id "content"}

   ;; Search Form
   [:div {:class "flex flex-col sm:flex-row items-start sm:items-end gap-4"}
    [:div {:class "flex items-center gap-x-4"}
     [:input {:id          "search"
              :type        "search"
              :data-bind   "q"
              :placeholder "Search contacts..."
              :class       sty/input-class}]
     [:button {:id            "search-contact-btn"
               :data-on-click "@get('/contact/search')"
               :class         sty/btn-class}
      "Search"]]]

   ;; Table
   [:div {:id           "contact-table-div"
          :data-signals "{sort_by: 'first', sort_order: 'asc', last_id: null, last_key: '' , limit: 10 }"}
    (contact-table {})]

   [:a
    {:href  "/contact/create-new"
     :class sty/btn-class} "Add Contact"]])


(defn home-page []
  (layout
    "Home"
    (main-content nil)))


(defn home-page-html []
  (-> home-page
      (oc-compiler/compile)
      (oc-core/html)
      (response/response)
      (response/content-type "text/html")))