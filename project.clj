(defproject nexapay-api "0.1.0-SNAPSHOT"
  :description "NEXAPAY: MVP de Fintech con Clojure"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [ch.qos.logback/logback-classic "1.4.11"]
                 [org.slf4j/slf4j-api "2.0.7"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [com.h2database/h2 "2.2.224"]
                 [honeysql "1.0.444"]
                 [buddy/buddy-auth "3.0.0"]
                 [buddy/buddy-hashers "2.0.167"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :main nexapay-api.server
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
