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
            [me.contact-app-datastar-httpkit.web.html.contact :refer [create-new-pg]]
            [me.contact-app-datastar-httpkit.web.html.home :refer [home-pg]]))


(def col-heads {:first "First" :last "Last" :phone "Phone" :email "Email"})


(defn render-contact-new [contact verr]
  (-> (create-new-pg contact verr *anti-forgery-token*)
      (hc/compile)
      (h/html)
      (response/response)
      (response/content-type "text/html")))


(defn home [_ req]
  (-> home-pg
      (hc/compile)
      (h/html)
      (response/response)
      (response/content-type "text/html")))


(def ^:private bufSize 1024)
(def read-json (charred/parse-json-fn {:async? false :bufsize bufSize}))


(defn get-signals [req]
  (-> req d*/get-signals read-json))


(defn to-create-new [_ req]
  (render-contact-new
    {:email "" :first "" :last "" :phone ""}
    {}))


(defn create-new! [{:keys [query-fn]} req]
  (let [{{:strs [email first last phone]} :form-params} req
        contact {:email email :first first :last last :phone phone}
        verr (if (some #(empty? %) [email first last phone])
               (-> {}
                   (cond->
                     (empty? first)
                     (assoc-in [:first] "First name is required")
                     (empty? last)
                     (assoc-in [:last] "Last name is required")
                     (empty? phone)
                     (assoc-in [:phone] "Phone number is required")
                     (empty? email)
                     (assoc-in [:email] "Email is required")))
               {})]
    (if (not-empty verr)
      (render-contact-new contact verr)
      (do
        (query-fn :save-contact! contact)
        (http-response/found "/")))))


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