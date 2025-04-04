(ns me.contact-app-datastar-httpkit.repo.contact-repo
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as sql]
            [honey.sql :as hsql]
            [honey.sql.helpers :as hutil]))

(defn escape-value
  "Escapes and formats a parameter for safe SQL interpolation."
  [v]
  (cond
    (nil? v) "NULL"                                         ;; Convert nil to NULL
    (string? v) (str "'" (str/replace v "'" "''") "'")      ;; Escape single quotes in strings
    (boolean? v) (if v "TRUE" "FALSE")                      ;; Convert booleans to TRUE/FALSE
    :else (str v)))

(defn interpolate-sql
  "Replaces ? placeholders in an SQL query with actual parameter values safely."
  [query]
  (let [[sql & params] query]
    (reduce (fn [s p] (str/replace-first s "?" (str "'" (str/replace p "'" "''") "'"))) sql params)))

(defn find-contacts-keyset [txt sort-col sort-order limit last-on-page ds]
  (let [last-seen-cond (when last-on-page
                         [(get {:asc :> :desc :<} sort-order :>)
                          [:composite sort-col :id]
                          [:composite (first last-on-page) (second last-on-page)]])
        search-cond (when (seq txt)
                      [:or
                       [:like :first [:concat "%" txt "%"]]
                       [:like :last  [:concat "%" txt "%"]]
                       [:like :phone [:concat "%" txt "%"]]
                       [:like :email [:concat "%" txt "%"]]])
        query {:select   [:id
                          :first
                          :last
                          [[:concat :first " " :last] :full_name]
                          :phone
                          :email]
               :from     [:contact]
               :where    (let [conds (remove nil? [search-cond last-seen-cond])]
                           (if (seq conds)
                             (if (= 1 (count conds))
                               (first conds)
                               [:and (first conds) (second conds)])
                             []))
               :order-by [[sort-col sort-order] [:id sort-order]]
               :limit    [limit]}]
    (let [sql (hsql/format query {:builder-fn rs/as-unqualified-maps})]
      (log/debug "SQL: " (interpolate-sql sql))
      (jdbc/execute! ds sql {:builder-fn rs/as-unqualified-maps}))))






