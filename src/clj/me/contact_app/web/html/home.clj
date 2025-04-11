(ns me.contact-app.web.html.home
  (:require [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]))

(defn main-content [contact-list]
  [:div {:id "content"}

   ;; Search Form
   [:form {:action "/contact/search"
           :method "get"
           :class  "flex flex-col sm:flex-row items-start sm:items-end gap-4"}
    [:div {:class "flex items-center gap-x-4"}
     [:input {:id          "search"
              :type        "search"
              :name        "q"
              :value       ""
              :placeholder "Search contacts..."
              :class       sty/input-class}]
     [:input {:type  "submit"
              :value "Search"
              :class sty/btn-class}]]]

   ;; Table
   [:table {:class "w-full text-left text-sm text-gray-300 border border-gray-700 mb-6"}
    [:thead {:class "bg-gray-800 text-gray-400 uppercase tracking-wider"}
     [:tr
      [:th {:class sty/table-header-class} "First"]
      [:th {:class sty/table-header-class} "Last"]
      [:th {:class sty/table-header-class} "Phone"]
      [:th {:class sty/table-header-class} "Email"]
      [:th {:class sty/table-header-class} ""]]]
    contact-list]                                           ;; Empty for now – add rows dynamically later

   [:a
    {:href  "/contact/create-new"
     :class sty/btn-class} "Add Contact"]])


(defn home-pg []
  (layout
    "Home"
    (main-content nil)))