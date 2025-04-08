(ns me.contact-app-datastar-httpkit.web.routes.pages
  (:require
    [me.contact-app-datastar-httpkit.web.middleware.exception :as exception]
    [me.contact-app-datastar-httpkit.web.pages.layout :as layout]
    [integrant.core :as ig]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [me.contact-app-datastar-httpkit.web.controllers.contact :as contact]
    [me.contact-app-datastar-httpkit.web.controllers.hello-world :as hds]))

(defn wrap-page-defaults []
  (let [error-page (layout/error-page
                     {:status 403
                      :title  "Invalid anti-forgery token"})]
    #(wrap-anti-forgery % {:error-response error-page})))


;; Routes
(defn page-routes [_opts]
  [["/" {:get (partial contact/home _opts)}]
   ;; ["/hello-world-home" {:get (partial hds/hello-world-home _opts)}]
   ;; ["/hello-world" {:get (partial hds/hello-world _opts)}]
   ["/contact/search" {:get (partial contact/search _opts)}]
   ["/contact/view-all" {:get (partial contact/view-all _opts)}]
   ["/contact/create-new" {:get  (partial contact/to-create-new _opts)
                           :post (partial contact/create-new! _opts)}]
   ["/contacts/:id"
    ["/edit" {:get  (partial contact/to-edit _opts)
              :post (partial contact/edit! _opts)}]
    ["/delete" {:delete (partial contact/delete! _opts)}]
    ["/view" {:get (partial contact/view _opts)}]]])

(def route-data
  {:middleware
   [;; Default middleware for pages
    (wrap-page-defaults)
    ;; query-params & form-params
    parameters/parameters-middleware
    ;; encoding response body
    muuntaja/format-response-middleware
    ;; exception handling
    exception/wrap-exception]})

(derive :reitit.routes/pages :reitit/routes)

(defmethod ig/init-key :reitit.routes/pages
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (layout/init-selmer! opts)
  (fn [] [base-path route-data (page-routes opts)]))

