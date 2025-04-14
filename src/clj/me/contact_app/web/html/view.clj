(ns me.contact-app.web.html.view
  (:require [hiccup2.core :as hc-core]
            [hiccup2.core :as hc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout-hc :refer [layout]]
            [ring.util.response :as response]))


(defn main-content [contact]
  [:div
   [:h1 {:class "text-3xl font-bold"} (str (:first contact) " " (:last contact))]
   [:div
    [:div "Phone: " (:phone contact)]
    [:div "Email: " (:email contact)]]
   [:p [:a {:href (format "/contact/%s/edit" (:id contact))
            :class "text-accent hover:underline mr-1"} "Edit"]]])

(defn contact-view [contact]
  (layout
    "Contact Info"
    (main-content contact)))

(defn contact-view-html [contact]
  (-> (hc-core/raw "<!DOCTYPE html>")
      (hc-core/html (contact-view contact))
      (str)
      (response/response)
      (response/content-type "text/html")))


