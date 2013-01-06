(ns analije.props
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [analije.parens :as par]
            [analije.generate-externs :as gen]
            [analije.strings :as st]))

(defn list-files [root-dir]
  (->> root-dir
       io/file
       file-seq))

(defn source-file? [file]
  (re-find #"\.cljs?$"
           (str file)))

(defn list-source-files [root-dir]
  (->> root-dir
       list-files
       (filter source-file?)))

(defn dbg [msg x]
  (prn msg)
  x)

(defn remover [prefix coll]
  (remove #(.startsWith % prefix)
          coll))

(defn find-property-accesses [content]
  (doseq [unparsed (par/all-calls-to "." content)]
    (println "Warning: dot syntax not parsed:" unparsed))
  (->> content
       par/ignore-ns-form
       st/without-strings
       (re-seq #":?\w*(\.-?[a-zA-Z]\w*)+")
       (map first)
       (remover ":")
       (remover "goog.")
       (remover "String.")
       (remover "Math.")
       distinct))

(defn gen-for-file [file]
  (let [props (find-property-accesses (slurp file))]
    (when-not (empty? props)
      (let [comm (->> props
                      (list* (str file) "")
                      gen/comments)
            funs (->> props
                      (mapcat #(re-seq #".\b(\w+)" %))
                      (map second)
                      distinct
                      gen/functions)]
        (str comm "\n\n" funs ",")))))

(defn gen-for-folder [root-dir obj-name out-file]
  (->> root-dir
       list-source-files
       (map gen-for-file)
       (remove nil?)
       (str/join "\n\n")
       (#(str "\n" % "\n\n"(gen/function "lazyBenjaminsEndFn")))
       (gen/object obj-name)
       (spit out-file)))
