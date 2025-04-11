(ns me.contact-app.web.html.layout-hc
  (:require [dev.onionpancakes.chassis.core :as cc]
            [hiccup2.core :as hc-core]))

(defn layout [title content]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:title title]
    ;; Optional: Tailwind CDN only for dev/small apps
    [:script {:src "https://cdn.tailwindcss.com"}]
    [:script {:type "module"
              :src  "https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.0-beta.11/bundles/datastar.js"}]
    ;; Tailwind theme override
    [:script
     (hc-core/raw "tailwind.config = {
        theme: {
          extend: {
            colors: {
              background: '#0f0f0f',
              surface: '#1c1c1c',
              accent: '#3B82F6',
              'accent-hover': '#2563EB'
            }
          }
        }
      };")]]

   [:body {:class "bg-background text-white min-h-screen flex items-center justify-center"}
    [:main {:class "w-full max-w-3xl p-6 bg-surface rounded-xl shadow-lg space-y-6"}

     ;; Header
     [:header {:class "flex items-center justify-between mb-6"}
      [:div
       [:h1 {:class "text-3xl font-bold uppercase tracking-widest text-accent"} "contacts.app"]
       [:p {:class "text-lg text-gray-400"} "A Demo Contacts Application"]
       [:p {:class "text-sm italic text-gray-400"} "Built with Datastar and Clojure's Kit!"]]
      [:img {:src    "https://data-star.dev/static/images/rocket.png"
             :alt    "Rocket"
             :width  "64"
             :height "64"
             :class  "ml-4"}]]

     ;; Main content
     content]]])
