(ns chatter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.page :as page]
            [hiccup.form :as form]))

(declare message-form message-table)

(defn generate-message-view
  "This generates the HTML for displaying messages"
  [messages]
  (page/html5
   [:head
    [:title "Chatter"]
    (page/include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
    (page/include-js "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")]
   [:body
    [:h1 "Our Chat App"]
    message-form
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
  (POST "/" {params :params}
        (let [name-param (get params "name")
              msg-param (get params "msg")
              new-messages (update-messages! chat-messages name-param msg-param)]
          (generate-message-view new-messages)))
  (route/not-found "Not Found"))

(def app (wrap-params app-routes))
