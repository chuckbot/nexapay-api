(ns nexapay-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [nexapay-api.adapter.persistence.user-repository :as user-repo]
            [nexapay-api.domain.user :as user-domain]
            [nexapay-api.domain.transfer-service :as transfer-service]
            [buddy.auth.backends.token :as token]
            [buddy.auth.middleware :as buddy-middleware]))

(def auth-backend (token/token-backend {:authfn (fn [request token]
                                                 (if-let [user (user-repo/find-user-by-username token)]
                                                   (assoc request :identity user)
                                                   nil))}))

(defn home-page
  [request]
  {:status 200 :body "¡Bienvenido a mi Fintech MVP!"})

(defn register-handler
  [request]
  (let [{{:keys [username password]} :json-params} request
        new-user-data (user-domain/new-user username password)]
    (if (user-repo/find-user-by-username username)
      {:status 409 :body "El nombre de usuario ya existe."}
      (try
        (let [saved-user (user-repo/save-user new-user-data)
              user-id (:id saved-user)]
          (user-repo/create-account-for-user user-id 0.0M)
          {:status 201 :body "Usuario registrado exitosamente."})
        (catch Exception e
          (println (str "Error durante el registro: " (.getMessage e)))
          {:status 500 :body (str "Error interno al registrar usuario: " (.getMessage e))})))))

(defn login-handler
  [request]
  (let [{{:keys [username password]} :json-params} request
        found-user (user-repo/find-user-by-username username)]
    (if (and found-user (user-domain/password-matches? found-user password))
      {:status 200 :body {:token username}}
      {:status 401 :body "Credenciales inválidas."})))

(defn transfer-handler
  [request]
  (let [{{:keys [from-account to-account amount]} :json-params} request]
    (try
      (transfer-service/transfer-funds! from-account to-account amount)
      {:status 200 :body "Transferencia exitosa."}
      (catch Exception e
        {:status 400 :body (str "Error: " (.getMessage e))}))))

;; Definición de los interceptores comunes
(def common-interceptors [(buddy-middleware/wrap-authentication auth-backend)
                          body-params/body-params
                          http/json-body])

;; ¡La corrección clave está aquí!
;; Usamos `vec (concat ...)` para crear una nueva secuencia de interceptores
;; para cada ruta, asegurando que Pedestal las vea como únicas.
(def routes
  #{["/" :get (vec (concat common-interceptors [`home-page]))]
    ["/api/v1/register" :post (vec (concat common-interceptors [`register-handler]))]
    ["/api/v1/login" :post (vec (concat common-interceptors [`login-handler]))]
    ["/api/v1/transfer" :post (vec (concat common-interceptors [`transfer-handler]))]})

(def service
  {:env :dev
   ::http/routes routes
   ::http/type :jetty
   ::http/port 8080})