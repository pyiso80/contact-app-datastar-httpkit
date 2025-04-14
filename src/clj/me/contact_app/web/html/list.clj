(ns me.contact-app.web.html.list
  (:require [dev.onionpancakes.chassis.compiler :as oc-compiler]
            [dev.onionpancakes.chassis.core :as oc-core]
            [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [me.contact-app.web.html.home :as home]
            [ring.util.response :as response]))


(defn contact-rows [contacts]
  [:tbody
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
            :class "text-accent hover:underline"} "View"]]])])

(defn contact-list [contacts]
  (layout
    "Contacts"
    (home/main-content (contact-rows contacts))))

(defn contact-list-html [contacts]
  (-> (contact-list contacts)
      (oc-compiler/compile)
      (oc-core/html)
      (response/response)
      (response/content-type "text/html")))