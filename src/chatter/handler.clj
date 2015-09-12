(ns chatter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [garden.core :refer [css]]
            [hiccup.page :as page]
            [hiccup.form :as form]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.anti-forgery :as anti-forgery])
  (:gen-class))

(def styles
  (css [:h1 {:text-align "center", :color "purple"}]
       [:p {:font-family "Georgia", :color "#333"}
          [:.special {:font-weight "bold"}]]))

(declare message-form message-table)

(defn init []
  (println "Chatter is starting"))

(defn destroy []
  (println "Chatter is shutting down"))

(defn generate-message-view
  "This generates the HTML for displaying messages"
  [messages]
  (page/html5
   [:head
    [:title "Chatter"]
    (page/include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css"
                      "/style.css")
    (page/include-js "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")]
   [:body
    [:h1 "My Chat App"]
    message-form
    [:p "This is some "
      [:span.special "special text"]
      "!"]
    (message-table messages)]))

(defn message-table
  [messages]
  [:table#messages.table.table-striped
   [:tr
      [:th "Name"]
      [:th "Message"]]
   (map (fn [m]
          [:tr
           [:td (:name m)]
           [:td (:message m)]])
        messages)])

(def message-form
  (form/form-to
       [:post "/"]
       "Name: " (form/text-field "name")
       "Message: " (form/text-field "msg")
       (form/submit-button "Submit")))

(def chat-messages
    (atom []))

(defn update-messages!
  "This will update a message list atom"
  [messages name new-message]
  (swap! messages conj {:name name :message new-message}))

(defroutes app-routes
  (GET "/" [] (generate-message-view @chat-messages))
  (GET "/style.css" [] {:content-type "text/css" :body styles})
  (POST "/" {params :params}
        (let [name-param (get params "name")
              msg-param (get params "msg")
              new-messages (update-messages! chat-messages name-param msg-param)]
          (generate-message-view new-messages)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (wrap-params app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty #'app {:port port :join? false})))
