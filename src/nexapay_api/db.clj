(ns nexapay-api.db 
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as sql]))

(def db-spec {:class "org.h2.Driver"
              :subprotocol "h2"
              :subname "./db/nexapay-api"})

(defn create-tables! []
  (j/with-db-connection [conn db-spec]
      (j/execute! conn ["
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL
);"])
      (j/execute! conn ["
CREATE TABLE IF NOT EXISTS accounts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNIQUE NOT NULL,
  balance DECIMAL(19, 4) NOT NULL DEFAULT 0.00,
  FOREIGN KEY (user_id) REFERENCES users(id)
);"])
    (j/execute! conn ["
CREATE TABLE IF NOT EXISTS transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  from_account_id INT NOT NULL,
  to_account_id INT NOT NULL,
  amount DECIMAL(19, 4) NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (from_account_id) REFERENCES accounts(id),
  FOREIGN KEY (to_account_id) REFERENCES accounts(id)
);"])))