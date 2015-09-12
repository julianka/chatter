(defproject chatter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://pure-tundra-5297.herokuapp.com/"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [garden "1.3.0-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.1.2"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler chatter.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
