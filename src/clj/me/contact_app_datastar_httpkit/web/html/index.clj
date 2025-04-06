(ns me.contact-app-datastar-httpkit.web.html.index
  (:require [me.contact-app-datastar-httpkit.web.html.styles :as sty]
            [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app-datastar-httpkit.web.html.layout :refer [base-layout]]))

(def index-page [:div {:id "content"}

                 ;; Search Form
                 [:form {:action "/contacts"
                         :method "get"
                         :class  "flex flex-col sm:flex-row items-start sm:items-end gap-4 mb-6"}
                  [:div
                   [:label {:for   "search"
                            :class sty/label-class} "Search Term"]
                   [:input {:id          "search"
                            :type        "search"
                            :name        "q"
                            :value       ""
                            :placeholder "Search contacts..."
                            :class       sty/input-class}]]
                  [:input {:type  "submit"
                           :value "Search"
                           :class sty/btn-class}]]

                 ;; Table
                 [:table {:class "w-full text-left text-sm text-gray-300 border border-gray-700 mb-6"}
                  [:thead {:class "bg-gray-800 text-gray-400 uppercase tracking-wider"}
                   [:tr
                    [:th {:class sty/table-header-class} "First"]
                    [:th {:class sty/table-header-class} "Last"]
                    [:th {:class sty/table-header-class} "Phone"]
                    [:th {:class sty/table-header-class} "Email"]
                    [:th {:class sty/table-header-class} ""]]]
                  [:tbody {}]]                              ;; Empty for now – add rows dynamically later

                 [:button
                  {:data-on-click    (cc/raw "@get('/contact/create-new'); history.pushState('/contact/create-new', '', '/contact/create-new');")
                   :class            sty/btn-class} "Add Contact"]])


(defn home-page []
  (base-layout
    "Home"
    index-page))