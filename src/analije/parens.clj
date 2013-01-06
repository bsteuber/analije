(ns analije.parens)

(defn indices-of [search s]
  (let [next-index (fn [prev]
                     (.indexOf s search (inc prev)))]
    (->> (iterate next-index -1)
         rest
         (take-while (complement neg?)))))

(defn one-table [search s]
  (->> (indices-of search s)
       (map #(vector % search))))

(defn index-table [searches s]
  (->> searches
       (map #(one-table % s))
       (apply concat)
       (sort-by first)))

(def paren-table
  (partial index-table ["(" ")"]))

(defn score-next [prev-score [index open-or-close-paren]]
  (condp = open-or-close-paren
    "(" (inc prev-score)
    ")" (dec prev-score)))

(defn scores [paren-table]
  (->> paren-table
       (reductions score-next 0)
       rest))

(defn score-table [s]
  (let [pt (paren-table s)
        s  (scores pt)]
    (map conj pt s)))

(defn matching-close-index [score-after-open]
  (fn [[idx _ score]]
    (when (= score (dec score-after-open))
      idx)))

(defn throw-unbalanced []
  (throw (RuntimeException. "unbalanced parentheses")))

(defn find-close-index [score-after-open table]
  (or (some (matching-close-index score-after-open)
            table)
      (throw-unbalanced)))

(defn rests [coll]
  (->> coll
       (iterate next)
       (take-while identity)))

(defn next-matching-pair [[[idx open-paren score] & table]]
  (when (= open-paren "(")
    [idx (find-close-index score table)]))

(defn check [[_ _ score :as row]]
  (when (neg? score)
    (throw-unbalanced))
  row)

(defn all-paren-pairs [s]
  (->> s
       score-table
       (map check)
       rests
       (map next-matching-pair)
       (remove nil?)))

(def first-paren-pair
  (comp first all-paren-pairs))

(defn string-in-parens [s [open-index close-index]]
  (.substring s open-index (inc close-index)))

(defn string-after-parens [s [_ close-index]]
  (.substring s (inc close-index)))

(defn ignore-ns-form [s]
  (let [first-pair (first-paren-pair s)]
    (if (and first-pair
             (re-find #"\( *ns "
                      (string-in-parens s first-pair)))
      (string-after-parens s first-pair)
      s)))

(defn all-calls-to [symbol s]
  (->> (all-paren-pairs s)
       (map #(string-in-parens s %))
       (filter #(.startsWith % (str "(" (name symbol) " ")))))
