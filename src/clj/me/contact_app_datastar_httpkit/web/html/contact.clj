(ns me.contact-app-datastar-httpkit.web.html.contact
  (:require [dev.onionpancakes.chassis.core :as cc]
            [me.contact-app-datastar-httpkit.web.html.styles :as sty]))


(def frag-new
  [:div {:id "content" :class "space-y-4"}
   [:div {:id "contact-new"
          :data-signals (cc/raw "{
                          first: '',
                          last: '',
                          email: '',
                          phone: ''
                        }")}]

   ;; for url
   ;; [:div {:data-replace-url "'/contact/create-new'"}]

   ;; Email
   [:div {:class sty/form-group-class}
    [:label {:for "email" :class sty/label-class} "Email"]
    [:input {:id "email" :name "email" :type "text"
             :data-bind "email"
             :placeholder "Email"
             :class sty/input-class}]]

   ;; First name
   [:div {:class sty/form-group-class}
    [:label {:for "first" :class sty/label-class} "First name"]
    [:input {:id "first" :name "first" :type "text"
             :data-bind "first"
             :placeholder "First name"
             :class sty/input-class}]]

   ;; Last name
   [:div {:class sty/form-group-class}
    [:label {:for "last" :class sty/label-class} "Last name"]
    [:input {:id "last" :name "last" :type "text"
             :data-bind "last"
             :placeholder "Last name"
             :class sty/input-class}]]

   ;; Phone
   [:div {:class sty/form-group-class}
    [:label {:for "phone" :class sty/label-class} "Phone"]
    [:input {:id "phone" :name "phone" :type "text"
             :data-bind "phone"
             :placeholder "Phone"
             :class sty/input-class}]]

   ;; Submit button
   [:div {:class "text-right"}
    [:button {:id "create-contact-btn"
              :class sty/btn-class
              :data-on-click (cc/raw "@post('/contact/create-new', {headers: {'x-csrf-token':'%s'}})")}
     "Save"]]])