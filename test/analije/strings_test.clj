(ns analije.strings-test
    (:use clojure.test
          analije.strings))

(def s str)

(def b "\\")
(def e "\"")
(def esc (s b e))

(deftest backslashes-before-test
  (are [string idx cnt]
       (= cnt (backslashes-before string idx))
       (s e) 0 0
       (s "foo" e "bar") 3 0
       (s b e) 1 1
       (s "foo" b e "bar") 4 1
       (s b b e) 2 2
       (s "foo" b b e "bar") 5 2
       (s b b b e) 3 3
       (s "foo" b b b e "bar") 6 3))

(deftest escaped-test
  (are [string idx esc?]
       (= esc? (escaped? string idx))
       (s e) 0 false
       (s "foo" e "bar") 3 false
       (s b e) 1 true
       (s "foo" b e "bar") 4 true
       (s b b e) 2 false
       (s "foo" b b e "bar") 5 false
       (s b b b e) 3 true
       (s "foo" b b b e "bar") 6 true))

(defn q [& args]
  (s e (apply s args) e))

(deftest without-strings-test
  (are [before after]
       (let [res (without-strings before)]
         ;; (println :bef before)
         ;; (println :res res)
         ;; (println :aft after)
         (= after res))
       "ab"
       "ab"

       (q "foo")
       " "

       (s "a" (q "foo") "b")
       "a b"

       (s "a" esc "b")
       "a\\\"b"

       (s "a" (q "fo" esc "o") "b")
       "a b"

       (s "a" (q "foo") "b" (q "ba" esc "r"))
       "a b "

       (s esc "foo" (q "bar") "b")
       "\\\"foo b"

       (s "a" esc (q "bar" esc esc) "b" esc)
       "a\\\" b\\\""))
