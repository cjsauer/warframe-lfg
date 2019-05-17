(ns warframe-lfg.app.sanity-test
  (:require [clojure.test :refer [deftest is testing run-tests]]))

(deftest sanity
  (is (= 2 (+ 1 1))))
