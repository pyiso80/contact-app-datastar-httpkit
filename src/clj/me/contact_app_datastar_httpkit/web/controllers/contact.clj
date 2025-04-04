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
            [charred.api :as charred]))


(def col-heads {:first "First" :last "Last" :phone "Phone" :email "Email"})


(defn index [_ req]
  (layout/render req "index.html"
                 {:col-heads col-heads}))


(def ^:private bufSize 1024)
(def read-json (charred/parse-json-fn {:async? false :bufsize bufSize}))


(defn get-signals [req]
  (-> req d*/get-signals read-json))

(def new-html "<div id=\"content\">\n<div id=\"contact-new\" data-signals=\"{\n                          first: '',\n                          last: '',\n                          email: '',\n                          phone: ''\n                        }\"\n>\n</div>\n<div>\n    <label for=\"email\">Email</label>\n    <input id=\"email\" name=\"email\" type=\"email\" data-bind=\"email\" placeholder=\"Email\"/>\n</div>\n<div>\n    <label for=\"first\">First Name</label>\n    <input id=\"first\" name=\"first\" type=\"text\" data-bind=\"first\" placeholder=\"First Name\"/>\n</div>\n<div>\n    <label for=\"last\">Last Name</label>\n    <input id=\"last\" name=\"last\" type=\"text\" data-bind=\"last\" placeholder=\"Last Name\"/>\n</div>\n<div>\n    <label for=\"phone\">Phone</label>\n    <input id=\"phone\" name=\"phone\" type=\"email\" data-bind=\"phone\" placeholder=\"Phone\"/>\n</div>\n<div>\n    <button data-on-click=\"@post('/contact/create-new', {\nheaders: {\n    'x-csrf-token':'%s'\n}})\">\n        Save\n    </button>\n</div>\n</div>")
(defn to-create-new [_ req]
  (->sse-response req
                  {on-open
                   (fn [sse]
                     (d*/with-open-sse sse (d*/merge-fragment! sse (format new-html *anti-forgery-token*))))}))


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