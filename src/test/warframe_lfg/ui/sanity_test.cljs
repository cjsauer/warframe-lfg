(ns warframe-lfg.ui.sanity-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest sanity
  (is (= 2 (+ 1 1))))
