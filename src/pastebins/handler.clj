(ns pastebins.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [pastebins.model :as model]
            [pastebins.view :as view]
            [ring.util.response :as resp]
            [noir.util.middleware :as noir]
            [noir.session :as session]
            ))


(defn show-list [pageno]
	(view/show-pastebins-list pageno (model/select-posts pageno)))

(defn show-code-new []
  (let [userid (session/get :userid 1)]
    (println userid)
    (view/show-pastebin-edit-form userid)))

(defn edit-code [id]
  (let [userid (session/get :userid 1)
        post (model/select-post id)]
    (view/show-pastebin-edit-form userid post)))

(defn- flds-to-post [flds]
  (->
    flds
    (clojure.set/rename-keys  {:code :content})
    (merge {:f_author (session/get :userid 1)})
    )
  )

(defn create-pastebin [flds]
  (println "creating post " flds)
  (model/create-post (flds-to-post flds))
  (resp/redirect "/list"))

(defn update-post [flds]
  (let [userid (session/get :userid 1)
        post (flds-to-post flds)]
    ;; TODO: security check userid == f_author
    (println "updating " post)
    (model/update-post post)
    (session/flash-put! "paste-edit-form-flash" (str "Code #" (:id post) " saved!"))
    (view/show-pastebin-edit-form userid post)))


(defroutes app-routes
  
  (GET "/" [] (resp/redirect "/list?page=1"))
  
  ;; Show pastebins list
  (GET "/list" req (show-list  (if-let [page (get-in req [:params :page])] (Integer. page) 1) ))

  (GET "/code/new" [] (show-code-new))

  ;; Create new pastebin
  (POST "/code/new" req (create-pastebin (:params req)))

  ;; Get pastebin edit form
  (GET  "/code/:id" [id]
    (edit-code id))

  ;; Update pastebin edit form
  (POST "/code/:id" req (update-post (:params req)))

  (route/resources "/")
  (route/not-found (slurp (clojure.java.io/resource "public/404.html")))

  )


(def app
  (->
    [(handler/site app-routes)]
    noir/app-handler
    noir/war-handler
    ))