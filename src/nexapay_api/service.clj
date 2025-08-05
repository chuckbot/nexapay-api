(ns nexapay-api.service (:require [io.pedestal.http :as http]
            [mi-fintech-mvp.db :as db]
            [mi-fintech-mvp.logic :as logic]))

(defn home-page [request]
  {:status 200
   :body "Welcome to NEXAPAY!"})

(def routes
  #{["/" :get `home-page]
    ["/api/v1/register" :post `logic/register-handler]
    ["/api/v1/login" :post `logic/login-handler]
    ["/api/v1/transfer" :post `logic/transfer-handler]})

(def service
  {:env :dev
   ::http/routes routes
   ::http/type :jetty
   ::http/port 8080})