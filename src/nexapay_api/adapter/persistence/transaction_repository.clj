(ns nexapay-api.adapter.persistence.transaction-repository
  (:require [clojure.java.jdbc :as j]
            [nexapay-api.db :refer [db-spec]]))

(defn save-transaction [transaction]
  (j/with-db-connection [conn db-spec]
    (j/insert! conn :transactions transaction)))