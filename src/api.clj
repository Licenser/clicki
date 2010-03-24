(ns api
  (:use config)
  (:require [compojure.html :as ch]))


(defn list-articles
  []
  (map #(second (re-find #"^\./data/(.*)\.clj$" (str %))) (filter #(.isFile %) (file-seq (java.io.File. *data-dir*)))))

(def html ch/html)
