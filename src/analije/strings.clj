(ns analije.strings
  (:require [clojure.string :as str]
            [analije.parens :as par]))

(defn backslashes-before [s index]
  (loop [cnt 0
         idx (dec index)]
    (if (and (not (neg? idx))
             (= (get s idx) \\))
      (recur (inc cnt) (dec idx))
      cnt)))

(defn escaped? [s index]
  (odd? (backslashes-before s index)))

(defn all-strings [s]
  (->> (par/indices-of "\"" s)
       (remove #(escaped? s %))
       (partition 2)))

(defn without-pairs [s pairs]
  (if (empty? pairs)
    s
    (let [end (second (last pairs))
          last-part (.substring s (inc end))
          use-pairs (->> pairs
                         (apply concat)
                         (list* -1)
                         butlast
                         (partition 2))
          add-last #(concat % [last-part])]
      (->> use-pairs
           (map (fn [[close open]]
                  (.substring s (inc close) open)))
           add-last
           (str/join " ")))))

(defn without-strings [s]
  (without-pairs s (all-strings s)))
