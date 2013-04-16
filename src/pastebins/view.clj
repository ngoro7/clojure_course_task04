(ns pastebins.view
  (:require [me.raynes.laser :as l]
            [noir.session :as session]
            [clojure.java.io :refer [file]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Main page. Here we will inject fragments
(def main-html-parsed
  (l/parse
   (slurp (clojure.java.io/resource "public/main.html"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Templates
;;
;; list of pastebins
(def list-html
;  (l/parse-fragment
    (slurp (clojure.java.io/resource "public/template/list.html")))

;; one of pastebin on list
(def list-item-html
;  (l/parse-fragment
    (slurp (clojure.java.io/resource "public/template/list-item.html")))


;; Edit pastebin page
(def pastebin-edit-html
  (l/parse-fragment
    (slurp (clojure.java.io/resource "public/template/pastebin.html"))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fragments

;(defragment main-content-frag [content]
;  (l/element= :content) (l/content content)


;(def pastebin-item (l/fragment list-html (l/id= "pastebin-item")))

(def max-preview-lines 5)

(l/defragment pastebin-item-frag list-item-html [{:keys [id title description ts content]}]
  ; shorten content text to 5 lines
  [content-lines (clojure.string/split-lines content)
   content (if (< max-preview-lines (count content-lines))
             (->> content-lines
               (take max-preview-lines)
               (clojure.string/join "\n"))
             content)
   codestr (str "/code/" id)]

  (l/class= "item-title") (comp  (l/attr :href codestr) (l/content title))
  (l/class= "item-link") (l/attr :href codestr)
  (l/class= "itemnum") (l/content (str "Code paste #" id))
  (l/class= "code") (l/content content))

(l/defragment pastebin-list-frag list-html [pageno items]
  (l/id= "items") (l/content
    (for [item items]
      (pastebin-item-frag item)))
  (l/class= :pageno) (l/attr :href (str "/list?page=" (+ pageno 1))))


(def pastebin-edit-form (l/select pastebin-edit-html (l/id= "codeedit")))

(l/defragment pastebin-edit-item-frag pastebin-edit-form
  [{:keys [id title description code action]}]
    (l/attr= :name "title") (l/attr :value title)
    (l/attr= :name "description") (l/content description)
    (l/attr= :name "code") (l/content code)
    (l/element= :form) (l/attr :action (if-not (nil? id) (str "/code/" id) ""))
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pages

;; Список всех последних pastebins
(defn show-pastebins-list [pageno pastebins-list]
  (l/document main-html-parsed
    (l/element= :title) (l/content "Recent codes")
    (l/id= :content) (l/content (pastebin-list-frag pageno pastebins-list))))

;; Добавление/Редактирования куска кода
(defn show-pastebin-edit-form
  ; Добавление
  ([userid]
    (println "userid=" userid)
    (l/document pastebin-edit-html
      (l/element= :title) (l/content (str "Add new code"))
      (l/class= :flash) (l/content (session/flash-get "paste-edit-form-flash"))
      (l/id= :content) (l/content (pastebin-edit-item-frag {:title "" :description "" :code ""}))
      ))
  ;; Редактирование
  ([userid {:keys [id title description ts content]}]
    (println "userid=" userid)
    (l/document pastebin-edit-html
      (l/element= :title) (l/content (str "Edit code " id))
      (l/class= :flash) (l/content (session/flash-get "paste-edit-form-flash"))
      (l/id= :content) (l/content (pastebin-edit-item-frag {:id id :title title :description description :code content}))
      ))
  )