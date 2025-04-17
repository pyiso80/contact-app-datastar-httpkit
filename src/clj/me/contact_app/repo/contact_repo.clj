(ns me.contact-app.repo.contact-repo
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

(defn find-contacts-keyset [q sort-by sort-order last-key last-id limit ds]
  ;(log/debug "find-contacts-keyset" last-key)
  (let [last-seen-cond (when (int? last-id)
                         [(get {:asc :> :desc :<} sort-order :>)
                          [:composite (keyword sort-by) :id]
                          [:composite last-key last-id]])
        search-cond (when (seq q)
                      [:or
                       [:like :first [:concat "%" q "%"]]
                       [:like :last  [:concat "%" q "%"]]
                       [:like :phone [:concat "%" q "%"]]
                       [:like :email [:concat "%" q "%"]]])
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
               :order-by [[sort-by sort-order] [:id sort-order]]
               :limit    [limit]}]
    (let [sql (hsql/format query {:builder-fn rs/as-unqualified-maps})]
      ;(log/debug "SQL: " (interpolate-sql sql))
      (jdbc/execute! ds sql {:builder-fn rs/as-unqualified-maps}))))






