(ns me.contact-app-datastar-httpkit.web.controllers.hello-world
  (:require [me.contact-app-datastar-httpkit.web.pages.layout :as layout]
            [starfederation.datastar.clojure.api :as d*]
            [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open]]
            [dev.onionpancakes.chassis.compiler :as hc]
            [dev.onionpancakes.chassis.core :as h]
            [charred.api :as charred]))


(defn hello-world-home [_ req]
  (layout/render req "hello-world.html"))


(def message "Hello, world!")


(def msg-count  (count message))


(defn ->frag [i]
  (h/html
    (hc/compile
      [:div {:id "message"}
       (subs message 0 (inc i))])))



(def ^:private bufSize 1024)
(def read-json (charred/parse-json-fn {:async? false :bufsize bufSize}))

(defn get-signals [req]
  (-> req d*/get-signals read-json))

(defn hello-world [_ request]
  (let [d (-> request get-signals (get "delay") int)]
    (->sse-response request
                    {on-open
                     (fn [sse]
                       (d*/with-open-sse sse
                                         (dotimes [i msg-count]
                                           (d*/merge-fragment! sse (->frag i))
                                           (Thread/sleep d))))})))
