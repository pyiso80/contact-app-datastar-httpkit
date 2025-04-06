(ns me.contact-app-datastar-httpkit.web.controllers.contact
  (:require [clojure.tools.logging :as log]
            [me.contact-app-datastar-httpkit.web.pages.layout :as layout]
            [ring.util.http-response :as http-response]
            [ring.util.response :as response]
            [starfederation.datastar.clojure.api :as d*]
            [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [dev.onionpancakes.chassis.compiler :as hc]
            [dev.onionpancakes.chassis.core :as h]
            [charred.api :as charred]
            [me.contact-app-datastar-httpkit.web.html.contact :as hfrags]
            [me.contact-app-datastar-httpkit.web.html.index :as index]))


(def col-heads {:first "First" :last "Last" :phone "Phone" :email "Email"})


(defn index [_ req]
  (-> (response/response (h/html (hc/compile index/home-page)))
      (response/content-type "text/html")))


(def ^:private bufSize 1024)
(def read-json (charred/parse-json-fn {:async? false :bufsize bufSize}))


(defn get-signals [req]
  (-> req d*/get-signals read-json))


(def new-html (h/html (hc/compile hfrags/frag-new)))
(defn to-create-new [_ req]
  (->sse-response req
                  {on-open
                   (fn [sse]
                     (d*/with-open-sse
                       sse
                       ;;(d*/execute-script! sse "history.pushState('/contact/create-new', '', '/contact/create-new')")
                       (d*/merge-fragment!
                         sse
                         (format new-html *anti-forgery-token*))))}))


(defn create-new! [_ req]
  (log/debug (:d*-signals req))
  (-> (response/response "<h1>Hello, World!</h1>")
      (response/content-type "text/html")))


(defn to-edit [_ req]
  (-> (response/response "<h1>Contact Edit Form!</h1>")
      (response/content-type "text/html")))


(defn edit! [_ req]
  (layout/render req "new.html"))


(defn delete! [_ req]
  (layout/render req "new.html"))


(defn view [_ req]
  (layout/render req "new.html"))


(defn view-all [{:keys [query-fn]} req]
  (try
    (let [contacts (query-fn :get-all-contacts {})]
      (layout/render req "index.html" {:contacts contacts}))
    (catch Exception e
      (log/error e "failed to save message!"))))


(defn delete-by-ids! [_ req]
  (layout/render req "new.html"))


(defn search [_ req]
  (layout/render req "new.html"))