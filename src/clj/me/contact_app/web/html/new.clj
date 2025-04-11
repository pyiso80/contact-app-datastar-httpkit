(ns me.contact-app.web.html.new
  (:require [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.html.layout :refer [layout]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [hiccup2.core :as hc]))


(defn main-content [contact verr csrf]
  [:div {:id "content"}
   [:input {:id    "__anti-forgery-token"
            :name  "__anti-forgery-token"
            :type  "hidden"
            :value csrf}]

   ;; Should display validation errors or not
   [:div {:data-signals "{v: {email: false,
                              first: false,
                              last: false,
                              phone: false}}"}]

   [:div {:data-signals "{ve: {email: '',
                               first: '',
                               last:  '',
                               phone: ''}}"}]

   ;; Email
   [:label {:for "email" :class sty/label-class} "Email"]
   [:input {:id           "email" :name "email" :type "text"
            :placeholder  "Email"
            :data-bind    "email"
            :data-on-blur "$v.email=true; @get('/contact/validate')"
            :class        sty/input-class
            :data-class   "{'border-red-500 ring-1 ring-red-500 focus:ring-red-500' : $v.email && $ve.email != ''}"}]
   [:span {:data-show "$v.email && $ve.email != ''"
           :class     sty/err-text-class
           :data-text "$ve.email"}]

   ;; First name
   [:label {:for "first" :class sty/label-class} "First name"]
   [:input {:id           "first" :name "first" :type "text"
            :placeholder  "First name"
            :data-bind    "first"
            :data-on-blur "$v.first=true; @get('/contact/validate')"
            :class        sty/input-class
            :data-class   "{'border-red-500 ring-1 ring-red-500 focus:ring-red-500' : $v.first && $ve.first != ''}"}]
   [:span {:data-show "$v.first && $ve.first != ''"
           :class     sty/err-text-class
           :data-text "$ve.first"}]

   ;; Last name
   [:label {:for "last" :class sty/label-class} "Last name"]
   [:input {:id           "last" :name "last" :type "text"
            :placeholder  "Last name"
            :data-bind    "last"
            :data-on-blur "$v.last=true; @get('/contact/validate')"
            :class        sty/input-class
            :data-class   "{'border-red-500 ring-1 ring-red-500 focus:ring-red-500' : $v.last && $ve.last != ''}"}]
   [:span {:data-show "$v.last && $ve.last != ''"
           :class     sty/err-text-class
           :data-text "$ve.last"}]

   ;; Phone
   [:label {:for "phone" :class sty/label-class} "Phone"]
   [:input {:id           "phone" :name "phone" :type "text"
            :placeholder  "Phone"
            :data-bind    "phone"
            :data-on-blur "$v.phone=true; @get('/contact/validate')"
            :class        sty/input-class
            :data-class   "{'border-red-500 ring-1 ring-red-500 focus:ring-red-500' : $v.phone && $ve.phone != ''}"}]
   [:p {:data-show "$v.phone && $ve.phone != ''"
           :class     sty/err-text-class
           :data-text "$ve.phone"}]

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