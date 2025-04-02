(ns me.contact-app-datastar-httpkit.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[contact-app-datastar-httpkit starting]=-"))
   :start      (fn []
                 (log/info "\n-=[contact-app-datastar-httpkit started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[contact-app-datastar-httpkit has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
