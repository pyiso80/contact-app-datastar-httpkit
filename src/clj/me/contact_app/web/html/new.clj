(ns me.contact-app.web.html.new
  (:require [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup2.core :as hc]))

(defn verr-input [id class]
  (-> [:div {:id id :class class}]
      (hc/html)
      (str)))

(defn verr-msg [id class msg]
  (-> [:p {:id id :class class} msg]
      (hc/html)
      (str)))

(defn main-content [contact verr csrf]
  [:div {:id "content"}
   [:input {:id    "__anti-forgery-token"
            :name  "__anti-forgery-token"
            :type  "hidden"
            :value csrf}]

   ;; Email
   [:label {:for "email" :class sty/label-class} "Email"]
   [:input {:id           "email" :name "email" :type "text"
            :placeholder  "Email"
            :data-bind    "email"
            :data-on-blur "@get('/contact/validate?f=email')"
            :class        sty/input-class}]
   [:p {:id "verr-email"}]

   ;; First name
   [:label {:for "first" :class sty/label-class} "First name"]
   [:input {:id           "first" :name "first" :type "text"
            :placeholder  "First name"
            :data-bind    "first"
            :data-on-blur "@get('/contact/validate?f=first')"
            :class        sty/input-class}]
   [:p {:id "verr-first"}]

   ;; Last name
   [:label {:for "last" :class sty/label-class} "Last name"]
   [:input {:id           "last" :name "last" :type "text"
            :placeholder  "Last name"
            :data-bind    "last"
            :data-on-blur "@get('/contact/validate?f=last')"
            :class        sty/input-class}]
   [:p {:id "verr-last"}]

   ;; Phone
   [:label {:for "phone" :class sty/label-class} "Phone"]
   [:input {:id           "phone" :name "phone" :type "text"
            :placeholder  "Phone"
            :data-bind    "phone"
            :data-on-blur "@get('/contact/validate?f=phone')"
            :class        sty/input-class}]
   [:p {:id "verr-phone"}]

   ;; Submit button
   [:button {:id            "create-contact-btn"
             :data-on-click (-> "@post('/contact/create-new', {headers: {'x-csrf-token':'%s'}})"
                                (format csrf)
                                (hc/raw))
             :class         sty/btn-class}
    "Save"]])

(defn create-new-pg [contact verr csrf]
  (layout
    "New Contact"
    (main-content contact verr csrf)))

(defn contact-new-form [contact verr csrf]
  (main-content contact verr csrf))