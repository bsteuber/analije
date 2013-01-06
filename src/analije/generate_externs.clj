(ns analije.generate-externs
  (:require [clojure.java.io :as io]
            [clojure.string  :as str]))

(defn object [name props-str]
  (str "var " name " = {\n" props-str "\n}"))

(defn function [name]
  (str "  \"" name "\"" " : function () {}"))

(defn functions [names]
  (->> names
       (map function)
       (str/join ",\n")))

(defn externs [obj-name fn-names]
  (->> fn-names
       functions
       (object obj-name)))

(defn comments [lines]
  (->> lines
       (str/join "\n")
       (#(str "/*\n" % "\n*/"))))

(defn generate [file name props]
  (spit file (externs name props)))
