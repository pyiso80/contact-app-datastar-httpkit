(ns me.contact-app-datastar-httpkit.dev-middleware
  (:require [clojure.tools.logging :as log]
            [starfederation.datastar.clojure.api :as d*]
            [charred.api :as charred]))


(def ^:private bufSize 1024)
(def read-json (charred/parse-json-fn {:async? false :bufsize bufSize :key-fn keyword}))


(defn get-signals [req]
  (-> req d*/get-signals read-json))


(defn wrap-json-body
  [handler]
  (fn [request]
    (let [signals (d*/get-signals request)]
      (if signals
        (let [mod-req (assoc request :d*-signals (get-signals request))]
          (handler mod-req))
        (handler request)))))


(defn wrap-dev [handler _opts]
  (-> handler
      wrap-json-body))
