(ns me.contact-app.web.html.list
  (:require [dev.onionpancakes.chassis.compiler :as oc-compiler]
            [dev.onionpancakes.chassis.core :as oc-core]
            [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ring.util.response :as response]))

(defn contact-rows [contacts]
  ;; Rows
  (for [c contacts]
    [:tr {:class "odd:bg-gray-900 even:bg-gray-800 hover:bg-gray-700 transition"}
     [:td {:class "px-4 py-3 border-b border-gray-700"} (:first c)]
     [:td {:class "px-4 py-3 border-b border-gray-700"} (:last c)]
     [:td {:class "px-4 py-3 border-b border-gray-700"} (:phone c)]
     [:td {:class "px-4 py-3 border-b border-gray-700"} (:email c)]
     [:td {:class "px-4 py-3 border-b border-gray-700 text-right"}
      [:a {:href  (str "/contact/" (:id c) "/edit")
           :class "text-accent hover:underline mr-1"} "Edit"]
      [:a {:href  (str "/contact/" (:id c) "/view")
           :class "text-accent hover:underline"} "View"]]]))

(defn contact-table [contacts]
  ;; Table
  [:div {:id "contact-table"}
   [:table {:class "w-full text-left text-sm text-gray-300 border border-gray-700 mb-6"}
    [:thead {:class "bg-gray-800 text-gray-400 uppercase tracking-wider"}
     [:tr
      [:th {:class sty/table-header-class} "First"]
      [:th {:class sty/table-header-class} "Last"]
      [:th {:class sty/table-header-class} "Phone"]
      [:th {:class sty/table-header-class} "Email"]
      [:th {:class sty/table-header-class} ""]]]
    [:tbody {:id "cotact-table-body"} (contact-rows contacts)]]
   [:div {:id "load-more"}]])

(defn contact-table-html [contacts]
  (-> (contact-table contacts)
      (oc-compiler/compile)
      (oc-core/html)))


(defn contact-rows-html [contacts]
  (-> (contact-rows contacts)
      (oc-compiler/compile)
      (oc-core/html)))