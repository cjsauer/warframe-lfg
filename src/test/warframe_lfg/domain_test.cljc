(ns warframe-lfg.domain-test
  (:require [clojure.test :refer [deftest is testing]]
            [warframe-lfg.domain :as domain]
            [warframe-lfg.domain.post :as post]
            [warframe-lfg.domain.hashtag :as htag]))

(def sample-post
  {:post/body "Looking for 2 more to complete #Ragnarok #Lvl25 need a #Warlok"})

(deftest posts
  (is (= (post/hashtag-entities-from-body (:post/body sample-post))
         '({:hashtag/value "#Ragnarok"}
           {:hashtag/value "#Lvl25"}
           {:hashtag/value "#Warlok"}))))

(deftest hashtags
  (testing "extraction"
    (is (= (domain/extract-hashtags (:post/body sample-post))
           '("#Ragnarok" "#Lvl25" "#Warlok"))))

  (testing "normalization"
    (is (= (htag/normalize-all ["#MyTag" "#YourTag12" "#thistag"])
           '("#mytag" "#yourtag12" "#thistag")))
    (is (= (htag/normalized-set ["#MyTag" "#mytag" "#MYTAG"])
           #{"#mytag"})))

  (testing "equality"
    (is (htag/= "#Ragnarok" "#ragnarok" "#raGnaRok")))
  )
