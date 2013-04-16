(ns pastebins.model
  (:use [korma db core]))


(def default-conn {:classname "com.mysql.jdbc.Driver"
                   :subprotocol "mysql"
                   :user "pb"
                   :password "pbkey"
                   :subname "//127.0.0.1:3306/pastebins?useUnicode=true&characterEncoding=utf8"
                   :delimiters "`"})

(defdb korma-db default-conn)


(defentity author)

(defentity post
  (has-one author)
  )

;;;;;;;;;;
(def default-page-items 2)

(defn select-posts [page]
  (select post
    (order :id :desc)
    (limit default-page-items)
    (offset (inc (* (dec page) default-page-items)))
    ))

(defn select-post [id]
  (first (select post (where {:id id}))))

(defn create-post [item]
  (insert post (values item)))

(defn update-post [item]
  (update post
    (set-fields item)
    (where {:id (:id item)})))