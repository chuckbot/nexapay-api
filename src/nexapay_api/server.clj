(ns nexapay-api.server
  (:require [nexapay-api.service :as service]
            [nexapay-api.db :as db]
            [io.pedestal.http :as http])
  (:gen-class)) ; ¡Importante para que Leiningen pueda encontrar el -main!

(defn run-dev
  "The entry point for 'lein run-dev'. Starts the development server."
  [& args]
  (println "\nCreating database tables...")
  (db/create-tables!)
  (println "\nStarting server...")
  (-> service/service
      (http/create-server)
      (http/start)))

(defn -main
  "Main entry point for the application. Called by `lein run`."
  [& args]
  (run-dev args)) ; Llama a tu función de inicio de desarrollo