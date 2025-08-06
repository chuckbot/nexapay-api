(ns nexapay-api.adapter.persistence.user-repository
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [nexapay-api.db :refer [db-spec]]))

(defn save-user [user]
  (j/with-db-connection [conn db-spec]
    ;; j/insert! devuelve un vector de mapas con los datos insertados, incluyendo el ID.
    ;; Tomamos el primer elemento del vector (el único usuario insertado).
    (first (j/insert! conn :users user))))

(defn find-user-by-username [username]
  (j/with-db-connection [conn db-spec]
    (first (j/query conn ["SELECT * FROM users WHERE username = ?" username]))))

(defn create-account-for-user [user-id initial-balance]
  (j/with-db-connection [conn db-spec]
    (j/insert! conn :accounts {:user_id user-id :balance initial-balance})))