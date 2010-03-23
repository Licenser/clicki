(ns clicky
  (:use compojure)
  (:use net.licenser.sandbox)
  (:require  [clojure.contrib.str-utils2 :as su])
  (:use clojure.contrib.duck-streams))
  
  
(def *data-dir* "./data")

(defn page
  [& body]
  (html [:title "Clicky! Bunti!"] (vec (concat [:body] body))))

(defn uri-to-ns
  [uri]
  (symbol (su/replace (su/drop uri 1) \/ \.)))


(defn run-file
  [ns file]
  (let [sb (new-sandbox :namespace ns)
	ns (create-ns ns)]
    (try
     (let [res (with-open [r (java.io.PushbackReader. (reader file))] (sb (read r)))]
      (page [:body res]))
     (catch Exception e
       (page [:body e])))))

(defn clicly-handler [request]
  (html))
  
(defn uri-to-file-name
  [uri]
  (str *data-dir*  uri ".clj"))


(defroutes my-app
  (GET "/"
    (html [:h1 "Hello World"]))
  (GET "*"
    (let [file-name (uri-to-file-name  (:uri request))
          file (java.io.File. file-name)]
      (if (or (:edit params) (not (.exists file)))
	(page [:form {:action (:uri request) :method :post}
	       [:textarea {:name "code"} (read-lines file-name)] :br [:input  {:type "submit" :value "Save"}]])
	(run-file (uri-to-ns file-name) file))))
  (POST "*"
	(let [file-name (uri-to-file-name  (:uri request))
	      code (:code (:form-params request))]
	  (try
	   (read-string code)
	   (spit file-name code)
	   (run-file (uri-to-ns file-name) (java.io.File. file-name))
	   (catch Exception e (page [:h1 "Ohhh no! The code could not be read!"] [:pre code]))))))

(run-server {:port 8080}
  "/*" (servlet my-app))