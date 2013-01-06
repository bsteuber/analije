(ns analije.props-test
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:use clojure.test
        analije.props))

(deftest source-file?-test
  (are [filename res]
       (-> filename
           io/file
           source-file?
           boolean
           (= res))
       "bas.clj" true
       "foo/bar/bas.cljs" true
       "foo/bar/bas.cl" false
       "foo/bar/bas.clja" false
       "foo/bar/bas.cljsa" false))

(deftest find-property-accesses-test
  (are [content dotforms]
       (let [msg (->> dotforms
                      (map #(str "Warning: dot syntax not parsed: " % "\n"))
                      str/join)
             gen-msg (->> content
                          find-property-accesses
                          with-out-str)]
         (= msg gen-msg))
       "hdfjshdjshdsj (. foo bar) dksdjsk (. foo -baz)" ["(. foo bar)"
                                                         "(. foo -baz)"]
       "abc(. foo (bar (. baz ban)))def " ["(. foo (bar (. baz ban)))"
                                           "(. baz ban)"])
  (are [content props]
       (= (map name props)
          (find-property-accesses content))
       "(new Foo.Bar.Baz)" '[Foo.Bar.Baz]
       "(.getMethod Foo.Bar)" '[.getMethod Foo.Bar]
       "xx(.-bar this)yy\n\n .foo" '[.-bar .foo]
       "ab \".foo\"" []
       "fo (:bar.baz f) o" []
       "(ns foo.bar
          (:require [clojure.string :as str])) (.blub x)" '[.blub]
          ))
