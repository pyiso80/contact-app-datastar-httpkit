(ns me.contact-app-datastar-httpkit.core
  (:require
   [clojure.tools.logging :as log]
   [integrant.core :as ig]
   [me.contact-app-datastar-httpkit.config :as config]
   [me.contact-app-datastar-httpkit.env :refer [defaults]]

    ;; Edges
   [kit.edge.server.http-kit]
   [me.contact-app-datastar-httpkit.web.handler]

    ;; Routes
   [me.contact-app-datastar-httpkit.web.routes.api]
    [me.contact-app-datastar-httpkit.web.routes.pages])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (fn [thread ex]
   (log/error {:what :uncaught-exception
               :exception ex
               :where (str "Uncaught exception on" (.getName thread))})))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!)))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/expand)
       (ig/init)
       (reset! system)))

(defn -main [& _]
  (start-app)
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (stop-app) (shutdown-agents)))))
