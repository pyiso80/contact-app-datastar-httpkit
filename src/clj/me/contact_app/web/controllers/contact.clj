(ns me.contact-app.web.controllers.contact
  (:require [clojure.set :as set]
            [clojure.tools.logging :as log]
            [dev.onionpancakes.chassis.core :as h]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.pages.layout :as layout]
            [ring.util.http-response :as http-response]
            [ring.util.response :as response]
            [starfederation.datastar.clojure.api :as d*]
            [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [dev.onionpancakes.chassis.compiler :as oc-compiler]
            [dev.onionpancakes.chassis.core :as oc-core]
            [hiccup2.core :as hc-core]
            [charred.api :as ch]
            [me.contact-app.web.html.new :refer [create-new-pg contact-new-form verr-input verr-msg]]
            [me.contact-app.web.html.home :refer [home-pg]]
            [me.contact-app.web.html.list :refer [contact-list]]
            [me.contact-app.web.html.edit :refer [edit-contact-pg]]
            [me.contact-app.web.html.view :refer [contact-view-pg]]
            [clojure.walk :refer [keywordize-keys]]))


(defn send-sse [req fn data]
  (->sse-response
    req
    {on-open #(d*/with-open-sse % (fn % data))}))

(defn send-sse2 [req myfn]
  (->sse-response
    req
    {on-open #(d*/with-open-sse % (myfn %))}))


(def col-heads {:first "First" :last "Last" :phone "Phone" :email "Email"})


(defn render-contact-new [contact verr]
  (-> (create-new-pg contact verr *anti-forgery-token*)
      (oc-compiler/compile)
      (oc-core/html)
      (response/response)
      (response/content-type "text/html")))


(defn render-contact-new-form [contact verr]
  (-> (contact-new-form contact verr *anti-forgery-token*)
      (oc-compiler/compile)
      (oc-core/html)))


(defn render-contact-list [contacts]
  (-> (contact-list contacts)
      (oc-compiler/compile)
      (oc-core/html)
      (response/response)
      (response/content-type "text/html")))


(defn home [_ req]
  (-> home-pg
      (oc-compiler/compile)
      (oc-core/html)
      (response/response)
      (response/content-type "text/html")))


(defn render-edit-contact-pg [contact verr csrf]
  (-> (hc-core/raw "<!DOCTYPE html>")
      (hc-core/html (edit-contact-pg contact verr csrf))
      (str)
      (response/response)
      (response/content-type "text/html")))

(defn render-contact-view-pg [contact]
  (-> (hc-core/raw "<!DOCTYPE html>")
      (hc-core/html (contact-view-pg contact))
      (str)
      (response/response)
      (response/content-type "text/html")))


(def ^:private bufSize 1024)
(def read-json (ch/parse-json-fn {:async? false :bufsize bufSize}))


(defn get-signals [req]
  (-> req d*/get-signals read-json))


(defn to-create-new [_ req]
  (render-contact-new
    {:email "" :first "" :last "" :phone ""}
    {}))


(defn validate-contact [contact]
  (cond-> {:first "" :last "" :phone "" :email ""}
          (empty? (:first contact))
          (assoc-in [:first] "First name is required")
          (empty? (:last contact))
          (assoc-in [:last] "Last name is required")
          (empty? (:phone contact))
          (assoc-in [:phone] "Phone number is required")
          (empty? (:email contact))
          (assoc-in [:email] "Email is required")))


(defn handle-verr [req id verr]
  (if (seq (id verr))
    (send-sse2 req
               (fn [sse]
                 (d*/merge-fragment!
                   sse
                   (verr-input (name id) (str sty/input-class " " sty/err-input-class))
                   {d*/merge-mode d*/mm-upsert-attributes})
                 (d*/merge-fragment!
                   sse
                   (verr-msg (str "verr-" (name id)) sty/err-text-class (id verr)))))
    (send-sse2 req
               (fn [sse]
                 (d*/merge-fragment!
                   sse
                   (verr-input (name id) sty/input-class)
                   {d*/merge-mode d*/mm-upsert-attributes})
                 (d*/merge-fragment!
                   sse
                   (verr-msg (str "verr-" (name id)) "hidden" ""))))))


(defn validate-form [_ req]
  (let [contact (-> req
                    (get-signals)
                    (keywordize-keys)
                    (select-keys [:first :last :phone :email]))
        {{:strs [f]} :query-params} req
        fkw (keyword f)
        verr (validate-contact contact)]
    (handle-verr req fkw verr)))


(defn create-new! [{:keys [query-fn]} req]
  (let [contact (-> req
                    (get-signals)
                    (keywordize-keys)
                    (select-keys [:first :last :phone :email]))
        verr (validate-contact contact)]
    (log/debug verr)
    (if (not-empty verr)
      (send-sse req d*/merge-fragment! (render-contact-new-form contact verr))
      (do
        (query-fn :save-contact! contact)
        (http-response/found "/")))))


(defn to-edit [{:keys [query-fn]} req]
  (let [{{:keys [id]} :path-params} req
        contact (query-fn :find-contact-by-id {:id (some-> id Integer/parseInt)})]
    (log/debug contact)
    (render-edit-contact-pg contact {} *anti-forgery-token*)))


(defn edit! [{:keys [query-fn]} req]
  (let [{{:strs [email first last phone]} :form-params} req
        {{:keys [id]} :path-params} req
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
    (log/debug id)
    (if (not-empty verr)
      (render-edit-contact-pg contact {} verr)
      (do
        (query-fn :update-contact! (assoc contact :id (some-> id Integer/parseInt)))
        (http-response/found "/")))))


(defn delete! [{:keys [query-fn]} req]
  (log/debug "about to delete")
  (let [{{:keys [id]} :path-params} req
        data "<div id='content'><h1 class='text-3xl font-bold'>Just Deleted</h1></div>"]
    (do
      (query-fn :delete-contact! {:id (some-> id Integer/parseInt)})
      (send-sse req d*/merge-fragment! data))))


(defn view [{:keys [query-fn]} req]
  (let [{{:keys [id]} :path-params} req
        contact (query-fn :find-contact-by-id {:id (some-> id Integer/parseInt)})]
    (log/debug id)
    (render-contact-view-pg contact)))


(defn view-all [{:keys [query-fn]} req]
  (try
    (let [contacts (query-fn :get-all-contacts {})]
      (layout/render req "index.html" {:contacts contacts}))
    (catch Exception e
      (log/error e "failed to save message!"))))


(defn delete-by-ids! [_ req]
  (layout/render req "new.html"))


(defn search [{:keys [query-fn]} req]
  (let [{{:strs [q]} :query-params} req
        contacts (query-fn :find-contacts {:text q})]
    (render-contact-list contacts)))