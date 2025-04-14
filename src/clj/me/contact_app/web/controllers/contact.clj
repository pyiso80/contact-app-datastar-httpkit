(ns me.contact-app.web.controllers.contact
  (:require [clojure.tools.logging :as log]
            [me.contact-app.web.html.styles :as sty]
            [me.contact-app.web.pages.layout :as layout]
            [ring.util.http-response :as http-response]
            [starfederation.datastar.clojure.api :as d*]
            [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [charred.api :as ch]
            [me.contact-app.web.html.new :refer [new-html new-form-html handle-verr-textbox handle-verr-msg]]
            [me.contact-app.web.html.home :refer [home-page-html]]
            [me.contact-app.web.html.list :refer [contact-list-html]]
            [me.contact-app.web.html.edit :refer [contact-edit-page-html]]
            [me.contact-app.web.html.view :refer [contact-view-html]]
            [clojure.walk :refer [keywordize-keys]]))


(defn send-sse2 [req myfn]
  (->sse-response
    req
    {on-open #(d*/with-open-sse % (myfn %))}))


(def ^:private bufSize 1024)
(def read-json (ch/parse-json-fn {:async? false :bufsize bufSize}))


(defn get-signals [req]
  (-> req d*/get-signals read-json))


(defn to-create-new [_ req]
  (new-html
    {:email "" :first "" :last "" :phone ""}
    {}))


(defn validate-contact [contact]
  (cond-> {}
          (empty? (:first contact))
          (assoc :first "First name is required")
          (empty? (:last contact))
          (assoc :last "Last name is required")
          (empty? (:phone contact))
          (assoc :phone "Phone number is required")))


(defn handle-verr-inline [sse id verr]
  (d*/with-open-sse
    sse
    (let [css-input sty/input-class
          css-input-err (str sty/input-class " " sty/err-input-class)
          css-msg-err sty/err-text-class
          msg-id (str "verr-" (name id))]
      (if (empty? verr)
        (do
          (d*/merge-fragment!
            sse
            (handle-verr-textbox id css-input)
            {d*/merge-mode d*/mm-upsert-attributes})
          (d*/merge-fragment!
            sse
            (handle-verr-msg msg-id "hidden" "")))
        (do
          (d*/merge-fragment!
            sse
            (handle-verr-textbox id css-input-err)
            {d*/merge-mode d*/mm-upsert-attributes})
          (d*/merge-fragment!
            sse
            (handle-verr-msg msg-id css-msg-err (id verr))))))))


(defn handle-verr-all [sse verr]
  (d*/with-open-sse
    sse
    (doseq [e verr
            :let [css-input (str sty/input-class " " sty/err-input-class)
                  css-msg sty/err-text-class
                  input-id (name (first e))
                  msg-id (str "verr-" input-id)
                  msg (second e)]]
      (do
        (d*/merge-fragment!
          sse
          (handle-verr-textbox input-id css-input)
          {d*/merge-mode d*/mm-upsert-attributes})
        (d*/merge-fragment!
          sse
          (handle-verr-msg msg-id css-msg msg))))))


(defn validate-email [email query-fn]
  (let [existing (query-fn :find-email {:email email})]
    (cond (empty? email)
          {:email "Email is required"}
          existing
          {:email "Email already exists."})))


(defn validate-inline [{:keys [query-fn]} req]
  (let [contact (-> req
                    (get-signals)
                    (keywordize-keys)
                    (select-keys [:first :last :phone :email]))
        {{:strs [f]} :query-params} req
        verr (if (= :email (keyword f))
               (validate-email (:email contact) query-fn)
               (select-keys (validate-contact contact) [(keyword f)]))]
    (log/debug "contact: " contact " " "verr: " verr)
    (send-sse2 req #(handle-verr-inline % (keyword f) verr))))


(defn create-new! [{:keys [query-fn]} req]
  (log/debug "create-new!")
  (let [contact (-> req
                    (get-signals)
                    (keywordize-keys)
                    (select-keys [:first :last :phone :email]))
        email-verr (validate-email (:email contact) query-fn)
        other-verr (validate-contact contact)
        verr (merge email-verr other-verr)]
    (log/debug verr)
    (if (not-empty verr)
      (send-sse2 req #(handle-verr-all % verr))
      (do
        (query-fn :save-contact! contact)
        (http-response/found "/")))))


(defn to-edit [{:keys [query-fn]} req]
  (let [{{:keys [id]} :path-params} req
        contact (query-fn :find-contact-by-id {:id (some-> id Integer/parseInt)})]
    (log/debug contact)
    (contact-edit-page-html contact {} *anti-forgery-token*)))


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
      (contact-edit-page-html contact {} verr)
      (do
        (query-fn :update-contact! (assoc contact :id (some-> id Integer/parseInt)))
        (http-response/found "/")))))


(defn delete! [{:keys [query-fn]} req]
  (log/debug "about to delete")
  (let [{{:keys [id]} :path-params} req
        data "<div id='content'><h1 class='text-3xl font-bold'>Just Deleted</h1></div>"]
    (do
      (query-fn :delete-contact! {:id (some-> id Integer/parseInt)})
      (send-sse2 req (fn [sse] (d*/merge-fragment! sse data))))))


(defn view [{:keys [query-fn]} req]
  (let [{{:keys [id]} :path-params} req
        contact (query-fn :find-contact-by-id {:id (some-> id Integer/parseInt)})]
    (log/debug id)
    (contact-view-html contact)))


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
    (contact-list-html contacts)))

(defn home [_ req]
  (home-page-html))