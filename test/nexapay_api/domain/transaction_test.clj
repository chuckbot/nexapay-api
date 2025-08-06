(ns nexapay-api.domain.transaction-test 
  (:require [clojure.test :refer [deftest is testing]]
            [nexapay-api.domain.transaction :as transaction]))

(deftest test-new-transaction
  (testing "should create a new transaction with valid data"
    (let [tx (transaction/new-transaction 1 2 10.0)]
      (is (= (:from_account_id tx) 1))
      (is (= (:to_account_id tx) 2))
      (is (= (:amount tx) 10.0))))
  
  (testing "should throw an error for a negative amount"
    (is (thrown? AssertionError (transaction/new-transaction 1 2 -5.0))))

  (testing "should throw an error for a zero amount"
    (is (thrown? AssertionError (transaction/new-transaction 1 2 0))))

  (testing "should throw an error for the same origin and destination account"
    (is (thrown? AssertionError (transaction/new-transaction 1 1 5.0)))))