(ns nexapay-api.domain.transfer-service
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.java.jdbc :as j]
            [nexapay-api.db :refer [db-spec create-tables!]]))

;; --- Fixtures de prueba para un entorno limpio ---
(use-fixtures :once
  (fn [f]
    ;; Crea las tablas de la base de datos una única vez antes de todas las pruebas.
    (create-tables!)
    (f)))

(use-fixtures :each
  (fn [f]
    ;; Envuelve cada prueba en una transacción de base de datos.
    (j/with-db-transaction [_ db-spec]
      (f)
      ;; Los cambios se deshacen automáticamente al finalizar la transacción si no se hace commit.
      )))

;; --- Datos de prueba ---
(def sample-users [{:username "alice" :password_hash "dummy-hash-1"}
                   {:username "bob"   :password_hash "dummy-hash-2"}
                   {:username "charlie" :password_hash "dummy-hash-3"}])

;; --- Implementación de transfer-funds! ---
(defn transfer-funds!
  "Transfiere fondos entre dos cuentas de usuario."
  [from-user-id to-user-id amount]
  (j/with-db-transaction [tx db-spec]
    (when (= from-user-id to-user-id)
      (throw (ex-info "Cannot transfer to the same account" {:type :same-account})))
    (let [from-account (first (j/query tx ["SELECT * FROM accounts WHERE user_id = ?" from-user-id]))
          to-account   (first (j/query tx ["SELECT * FROM accounts WHERE user_id = ?" to-user-id]))]
      (when (nil? from-account)
        (throw (ex-info "Origin account does not exist" {:type :origin-not-found})))
      (when (nil? to-account)
        (throw (ex-info "Destination account does not exist" {:type :destination-not-found})))
      (when (< (:balance from-account) amount)
        (throw (ex-info "Insufficient funds" {:type :insufficient-funds})))
      ;; Actualiza balances
      (j/update! tx :accounts {:balance (- (:balance from-account) amount)} ["user_id = ?" from-user-id])
      (j/update! tx :accounts {:balance (+ (:balance to-account) amount)} ["user_id = ?" to-user-id])
      ;; Registra la transacción
      (j/insert! tx :transactions {:from_account_id from-user-id
                                   :to_account_id to-user-id
                                   :amount amount}))))

;; --- Casos de prueba para la función `transfer-funds!` ---
(deftest test-transfer-funds
  (testing "should successfully transfer funds between two accounts"
    ;; Setup
    (let [user-alice (j/insert! db-spec :users (first sample-users))
          user-bob (j/insert! db-spec :users (second sample-users))
          _        (j/insert! db-spec :accounts {:user_id (:id user-alice) :balance 100.0M})
          _        (j/insert! db-spec :accounts {:user_id (:id user-bob)   :balance 50.0M})]
      
      ;; Act
      (transfer-funds! (:id user-alice) (:id user-bob) 25.0M)
      
      ;; Assert
      (let [alice-balance (-> (j/query db-spec ["SELECT balance FROM accounts WHERE user_id = ?" (:id user-alice)]) first :balance)
            bob-balance (-> (j/query db-spec ["SELECT balance FROM accounts WHERE user_id = ?" (:id user-bob)]) first :balance)]
        (is (= alice-balance 75.0M) "Alice's balance should be 75.0M")
        (is (= bob-balance 75.0M) "Bob's balance should be 75.0M"))))

  (testing "should save a transaction record after a successful transfer"
    (let [user-alice (j/insert! db-spec :users (first sample-users))
          user-bob (j/insert! db-spec :users (second sample-users))
          _        (j/insert! db-spec :accounts {:user_id (:id user-alice) :balance 100.0M})
          _        (j/insert! db-spec :accounts {:user_id (:id user-bob)   :balance 50.0M})]
      
      (transfer-funds! (:id user-alice) (:id user-bob) 25.0M)
      
      (let [tx-record (first (j/query db-spec ["SELECT amount FROM transactions WHERE from_account_id = ?" (:id user-alice)]))]
        (is (= (:amount tx-record) 25.0M) "The transaction record amount should be 25.0M"))))

  (testing "should roll back if the origin account does not exist"
    (let [user-bob (j/insert! db-spec :users (second sample-users))
          _        (j/insert! db-spec :accounts {:user_id (:id user-bob)   :balance 50.0M})]
      
      (is (thrown? Exception (transfer-funds! 999 (:id user-bob) 10.0M))
          "An exception should be thrown for a non-existent origin account")))

  (testing "should roll back if the destination account does not exist"
    (let [user-alice (j/insert! db-spec :users (first sample-users))
          _        (j/insert! db-spec :accounts {:user_id (:id user-alice) :balance 100.0M})]
      
      (is (thrown? Exception (transfer-funds! (:id user-alice) 999 10.0M))
          "An exception should be thrown for a non-existent destination account")))

  (testing "should roll back if there are insufficient funds"
    (let [user-alice (j/insert! db-spec :users (first sample-users))
          user-bob (j/insert! db-spec :users (second sample-users))
          _        (j/insert! db-spec :accounts {:user_id (:id user-alice) :balance 10.0M})
          _        (j/insert! db-spec :accounts {:user_id (:id user-bob)   :balance 50.0M})]
      
      (is (thrown? Exception (transfer-funds! (:id user-alice) (:id user-bob) 25.0M))
          "An exception should be thrown for insufficient funds")
      
      (let [alice-balance (-> (j/query db-spec ["SELECT balance FROM accounts WHERE user_id = ?" (:id user-alice)]) first :balance)]
        (is (= alice-balance 10.0M) "Alice's balance should remain unchanged after a failed transaction"))))

  (testing "should not allow transfer to the same account"
    (let [user-charlie (j/insert! db-spec :users (get sample-users 2))
          _        (j/insert! db-spec :accounts {:user_id (:id user-charlie) :balance 100.0M})]
      
      (is (thrown? Exception (transfer-funds! (:id user-charlie) (:id user-charlie) 10.0M))
          "An exception should be thrown for transfer to the same account")
      
      (let [charlie-balance (-> (j/query db-spec ["SELECT balance FROM accounts WHERE user_id = ?" (:id user-charlie)]) first :balance)]
        (is (= charlie-balance 100.0M) "Charlie's balance should not change")))))