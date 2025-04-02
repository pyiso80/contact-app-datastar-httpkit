(ns me.contact-app-datastar-httpkit.env
  (:require
    [clojure.tools.logging :as log]
    [me.contact-app-datastar-httpkit.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[contact-app-datastar-httpkit starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[contact-app-datastar-httpkit started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[contact-app-datastar-httpkit has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev}})
