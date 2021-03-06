;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial
  (:gen-class)
  (:require [ganelon.tutorial.pages.routes]
            [ganelon.tutorial.middleware]
            [ring.middleware.stacktrace]
            [ring.middleware.reload]
            [ring.adapter.jetty :as jetty]
            [ganelon.web.middleware :as middleware]
            [ganelon.web.app :as webapp]
            [noir.session :as sess]
            [somnium.congomongo :as db]))

(defonce SERVER (atom nil))

(defn get-mongo-url []
  (or (get (System/getenv) "MONGOHQ_URL")
    (System/getProperty "MONGOHQ_URL")
    "mongodb://localhost/meetups"))

(defn initialize[]
  (db/set-connection! (db/make-connection (get-mongo-url))))

(def handler
  (->
    (ganelon.web.app/app-handler
      (ganelon.web.app/javascript-actions-route))
    middleware/wrap-x-forwarded-for
    (ring.middleware.stacktrace/wrap-stacktrace)
    (ring.middleware.reload/wrap-reload {:dirs ["src/ganelon/tutorial/pages"]})))

(defn start-demo [port]
  (jetty/run-jetty handler {:port port :join? false}))

(defn -main [& m]
  (initialize)
  (let [port (Integer. (or (first m) (get (System/getenv) "PORT" "8096")))]
    (swap! SERVER (fn [s] (when s (.stop s)) (start-demo port)))))