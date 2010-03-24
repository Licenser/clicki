(ns clicki
  (:use config)
  (:use compojure)
  (:use net.licenser.sandbox)
  (:use net.licenser.sandbox.matchers)
  (:require  [clojure.contrib.str-utils2 :as su])
  (:use clojure.contrib.duck-streams)
  (:require api)
  (:gen-class))
  
  


(defn parital-namespace-matcher
  "Creates a tester that whitelists all functions within a namespace."
  [& namespaces]
  (fn [form]
    (cond
     (= (type form) clojure.lang.Var)
     (let [ns (str (ns-name (:ns (meta form))))]
       (map #(zero? (.indexOf ns (str %))) namespaces))
     (= (type form) java.lang.Class)
     (let [ns (second (re-find #"^class (.*)\.\w+$" (str form)))]
       (map #(zero? (.indexOf ns (str %))) namespaces))
     true
      '())))

(def *tester* 
  (extend-tester 
    secure-tester 
      (whitelist 
        (function-matcher 'def 'print 'println 'apply) 
        (namespace-matcher 'api)
        (parital-namespace-matcher (symbol *base-name*)))))

(defn page
  [& body]
  (html [:title "Clicki! Bunti!"] (vec (concat [:body] body))))

(defn uri-to-ns
  [uri]
  (symbol (su/replace (su/drop uri 1) \/ \.)))

(defn exec-file
  [file ns uri params]
  (let [sb (new-sandbox-compiler :namespace ns :tester *tester* :timeout 500)
        out (java.io.StringWriter.)
        eof (gensym "eof")]
    (with-open [r (java.io.PushbackReader. (reader file)) 
                out (java.io.StringWriter.)] 
      (loop [exp (read r false eof) res nil]
          (if (not= exp eof)
            (recur (read r false eof) ((sb exp 'uri 'params) {'*out* out} uri params))
            (str (if res (.append out res) out)))))))

(defn run-file
  [ns file uri params]
  (let [ ns (symbol (str *base-name* ns))]
    (try
     (let [res (exec-file file ns uri params)]
      res)
     (catch Exception e
       (page e [:p [:a {:href (str uri "?edit=") } "edit"]])))))

(defn clicly-handler [request]
  (html))
  
(defn uri-to-file-name
  [uri]
  (str *data-dir*  uri ".clj"))


(defroutes my-app
  (GET "/"
       (redirect-to "/index"))
  (GET "*"
       (let [uri (:uri request)
	     file-name (uri-to-file-name uri)
	     file (java.io.File. file-name)]
	 (if (or (:edit params) (not (.exists file)))
	   (page [:h2 (str file-name " not found.")] [:form {:action (:uri request) :method :post}
		  [:textarea {:name "code" :cols 80 :rows 40} (if (.exists file) (slurp file-name))] [:br] [:input  {:type "submit" :value "Save"}]])
	   (run-file (uri-to-ns uri) file uri params))))
  (POST "*"
	(let [uri (:uri request)
	      file-name (uri-to-file-name uri)
	      file (java.io.File. file-name)
	      code (:code (:form-params request))]
	  (try
	   (read-string code)
	   (.mkdirs (.getParentFile file))
	   (spit file-name code)
	   (run-file (uri-to-ns uri) file uri params)
	   (catch Exception e (page [:h1 "Ohhh no! The code could not be read!"] [:pre code] e))))))



(defn first-update []
  (let [files (filter #(.isFile %) (file-seq (java.io.File. *data-dir*)))]
    (loop [c (* (count files) (count files))]
      (if 
	  (try
	   (dorun
	    (map #(apply exec-file %)
		 (sort-by (fn [& _] (rand-int 42)) (map 
						    (fn [file] (vector file (symbol (str *base-name* (su/replace (second (re-find #"^\./data/(.*)\.clj$" (str file))) \/ \.))) "" {}))
						    files))))
	   true
	   (catch Exception e false))
	true (if (> c 0) (recur (dec c)))))))

(defn -main []
  (first-update)
  (run-server {:port 8080}
	      "/*" (servlet my-app)))
