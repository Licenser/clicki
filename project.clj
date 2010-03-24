(defproject clicki "1.0.0-SNAPSHOT" 
  :description "Clojure coding wiki" 
  :namespaces [clicki api]
  :dependencies [
  [compojure "0.3.2"]
  [clj-sandbox "0.2.8-SNAPSHOT"]
  [org.clojure/clojure "1.1.0"]
  [org.clojure/clojure-contrib "1.1.0"]]
  :main clicki
  :dev-dependencies [
  [swank-clojure/swank-clojure "1.1.0"]])
