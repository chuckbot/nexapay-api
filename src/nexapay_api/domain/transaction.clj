(ns nexapay-api.domain.transaction)

(defrecord Transaction [from_account_id to_account_id amount])

(defn new-transaction [from to amount]
  (assert (and (pos? amount) (not= from to)) "Invalid transaction parameters.")
  (map->Transaction {:from_account_id from
                     :to_account_id to
                     :amount amount}))