(ns analije.parens-test
  (:use clojure.test
        analije.parens))

(deftest indices-of-test
  (are [inner outer indices]
       (= (indices-of inner outer) indices)
       "a" ""  []
       "a" "a" [0]
       "a" "0a" [1]
       "a" "aa" [0 1]
       "a" "0a2a4a6a8a" [1 3 5 7 9]
       "ab" "0ab3ab6ab9" [1 4 7]))

(deftest paren-table-test
  (are [string res]
       (= (paren-table string) res)
       "" []
       "(" [[0 "("]]
       "()" [[0 "("]
             [1 ")"]]
       ")12(4((7)" [[0 ")"]
                    [3 "("]
                    [5 "("]
                    [6 "("]
                    [8 ")"]]))

(deftest scores-test
  (is (= (scores [[0 ")"]
                  [3 "("]
                  [5 "("]
                  [6 "("]
                  [8 ")"]])
         [-1 0 1 2 1])))

(deftest score-table-test
  (is (= (score-table "abf") []))
  (is (= (score-table ")12(4((7)")
         [[0 ")" -1]
          [3 "(" 0]
          [5 "(" 1]
          [6 "(" 2]
          [8 ")" 1]])))

(deftest all-paren-pairs-test
  (are [s pairs]
       (= pairs (all-paren-pairs s))
       "" []
       "0(2)4" [[1 3]]
       "(())" [[0 3] [1 2]]
       "((()))" [[0 5] [1 4] [2 3]]
       "(((())))" [[0 7] [1 6] [2 5] [3 4]]))

(deftest string-in-parens-test
  (are [s strings]
       (= strings
          (map #(string-in-parens s %)
               (all-paren-pairs s)))
       "s" []
       "()" ["()"]
       "a(b)c" ["(b)"]
       "a(b)(cd (ef) g)" ["(b)" "(cd (ef) g)" "(ef)"]))

(deftest string-after-parens-test
  (are [s string]
       (->> (first-paren-pair s)
            (string-after-parens s)
            (= string))
       "()" ""
       "()as" "as"
       "ab(fdjfdk)rest" "rest"))

(deftest ignore-ns-form-test
  (are [s res]
       (= res (ignore-ns-form s))
       "foo" "foo"
       "(ns foo)bar" "bar"
       "(ns clojure.core
          (:require [f.g :as h])
          (:use foo :only (x y z))) rest" " rest"))

(deftest all-calls-to-test
  (are [sym s res]
       (= res (all-calls-to sym s))
       '+ "" []
       '+ "a(+ 1 2)" ["(+ 1 2)"]
       '+ "a(+ 1 (+ 2 3))" ["(+ 1 (+ 2 3))"
                            "(+ 2 3)"]
       '. "((. foo bar) (.foo bar) (. f (. f g)))" ["(. foo bar)"
                                                    "(. f (. f g))"
                                                    "(. f g)"]))
