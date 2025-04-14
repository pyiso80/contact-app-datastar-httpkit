(ns me.contact-app.web.html.new
  (:require [dev.onionpancakes.chassis.compiler :as oc-compiler]
            [dev.onionpancakes.chassis.core :as oc-core]
            [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup2.core :as hc]
            [ring.util.response :as response]))

(defn new-form [contact verr csrf]
  [:div {:id "content"}
   [:input {:id    "__anti-forgery-token"
            :name  "__anti-forgery-token"
            :type  "hidden"
            :value csrf}]

   ;; Email
   [:label {:for "email" :class sty/label-class} "Email"]
   [:input {:id                             "email" :name "email" :type "text"
            :placeholder                    "Email"
            :data-bind                      "email"
            :data-on-keyup__debounce.250ms  "@get('/contact/validate?f=email')"
            :data-on-blur                   "@get('/contact/validate?f=email')"
            :class                          sty/input-class}]
   [:p {:id "verr-email" :class "hidden"}]

   ;; First name
   [:label {:for "first" :class sty/label-class} "First name"]
   [:input {:id           "first" :name "first" :type "text"
            :placeholder  "First name"
            :data-bind    "first"
            :data-on-blur "@get('/contact/validate?f=first')"
            :class        sty/input-class}]
   [:p {:id "verr-first" :class "hidden"}]

   ;; Last name
   [:label {:for "last" :class sty/label-class} "Last name"]
   [:input {:id           "last" :name "last" :type "text"
            :placeholder  "Last name"
            :data-bind    "last"
            :data-on-blur "@get('/contact/validate?f=last')"
            :class        sty/input-class}]
   [:p {:id "verr-last" :class "hidden"}]

   ;; Phone
   [:label {:for "phone" :class sty/label-class} "Phone"]
   [:input {:id           "phone" :name "phone" :type "text"
            :placeholder  "Phone"
            :data-bind    "phone"
            :data-on-blur "@get('/contact/validate?f=phone')"
            :class        sty/input-class}]
   [:p {:id "verr-phone" :class "hidden"}]

   ;; Submit button
   [:button {:id            "create-contact-btn"
             :data-on-click (-> "@post('/contact/create-new', {headers: {'x-csrf-token':'%s'}})"
                                (format csrf)
                                (hc/raw))
             :class         sty/btn-class}
    "Save"]])

(defn new-page [contact verr csrf]
  (layout
    "New Contact"
    (new-form contact verr csrf)))

(defn new-html [contact verr]
  (-> (new-page contact verr *anti-forgery-token*)
      (oc-compiler/compile)
      (oc-core/html)
      (response/response)
      (response/content-type "text/html")))

(defn new-form-html [contact verr]
  (-> (new-form contact verr *anti-forgery-token*)
      (oc-compiler/compile)
      (oc-core/html)))

(defn handle-verr-textbox [id class]
  (-> [:div {:id id :class class}]
      (hc/html)
      (str)))

(defn handle-verr-msg [id class msg]
  (-> [:p {:id id :class class} msg]
      (hc/html)
      (str)))