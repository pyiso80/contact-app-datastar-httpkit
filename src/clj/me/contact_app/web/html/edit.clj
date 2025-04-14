(ns me.contact-app.web.html.edit
  (:require [hiccup2.core :as hc-core]
            [hiccup2.core :as hc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout-hc :refer [layout]]
            [ring.util.response :as response]))


(defn main-content [contact verr csrf]
  [:div {:id "content"}
   [:form {:action (format "/contact/%s/edit" (:id contact))
           :method "post"}
    [:fieldset
     [:input {:id    "__anti-forgery-token"
              :name  "__anti-forgery-token"
              :type  "hidden"
              :value csrf}]

     ;; Email
     [:label {:for "email" :class sty/label-class} "Email"]
     [:input {:id          "email" :name "email" :type "text"
              :placeholder "Email"
              :value       (:email contact)
              :class       sty/input-class}]

     ;; First name
     [:label {:for "first" :class sty/label-class} "First name"]
     [:input {:id          "first" :name "first" :type "text"
              :placeholder "First name"
              :value       (:first contact)
              :class       sty/input-class}]

     ;; Last name
     [:label {:for "last" :class sty/label-class} "Last name"]
     [:input {:id          "last" :name "last" :type "text"
              :placeholder "Last name"
              :value       (:last contact)
              :class       sty/input-class}]

     ;; Phone
     [:label {:for "phone" :class sty/label-class} "Phone"]
     [:input {:id          "phone" :name "phone" :type "text"
              :placeholder "Phone"
              :value       (:phone contact)
              :class       sty/input-class}]
     ;; Submit button
     [:button.mr-2 {:id    "save-contact-btn"
                    :class sty/btn-class}
      "Save"]]]

   [:form {:action (format "/contact/%s/delete" (:id contact))
           :method "post"}
    [:fieldset
     [:input {:id    "__anti-forgery-token"
              :name  "__anti-forgery-token"
              :type  "hidden"
              :value csrf}]]
    [:button {:id    "delete-contact-btn"
              :class sty/btn-class}
     "Delete"]]
   [:a {:id            "delete-contact"
        :class         "text-accent hover:underline"
        :href          "#"
        :data-on-click (-> "@delete('/contact/%s/delete', {headers: {'x-csrf-token':'%s'}})"
                           (format (:id contact) csrf)
                           (hc/raw))}
    "Delete"]])

(defn contact-edit-page [contact verr csrf]
  (layout
    "New Contact"
    (main-content contact verr csrf)))

(defn contact-edit-page-html [contact verr csrf]
  (-> (hc-core/raw "<!DOCTYPE html>")
      (hc-core/html (contact-edit-page contact verr csrf))
      (str)
      (response/response)
      (response/content-type "text/html")))
