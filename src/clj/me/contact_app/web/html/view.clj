(ns me.contact-app.web.html.view
  (:require [hiccup2.core :as hc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout-hc :refer [layout]]))


(defn main-content [contact]
  [:div
   [:h1 {:class "text-3xl font-bold"} (str (:first contact) " " (:last contact))]
   [:div
    [:div "Phone: " (:phone contact)]
    [:div "Email: " (:email contact)]]
   [:p [:a {:href (format "/contact/%s/edit" (:id contact))
            :class "text-accent hover:underline mr-1"} "Edit"]]])

(defn contact-view-pg [contact]
  (layout
    "Contact Info"
    (main-content contact)))


