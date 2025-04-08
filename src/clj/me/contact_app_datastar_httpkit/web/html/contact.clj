(ns me.contact-app-datastar-httpkit.web.html.contact
  (:require [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app-datastar-httpkit.web.html.styles :as sty]
            [me.contact-app-datastar-httpkit.web.html.layout :refer [layout]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))


(defn main-content [contact verr csrf]
  [:form {:action "/contact/create-new"
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
             :phone       (:phone contact)
             :class       sty/input-class}]
    ;; Submit button
    [:button {:id    "create-contact-btn"
              :class sty/btn-class}
     "Save"]]])

(defn create-new-pg [contact verr csrf]
  (layout
    "New Contact"
    (main-content contact verr csrf)))