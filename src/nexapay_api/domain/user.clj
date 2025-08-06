(ns nexapay-api.domain.user (:require [buddy.hashers :as hashers]))

(defrecord User [id username password_hash])

(defn new-user [username password]
  (map->User {:username username
              :password_hash (hashers/derive password {:alg :bcrypt})}))

(defn password-matches? [user password]
  (hashers/check password (:password_hash user)))