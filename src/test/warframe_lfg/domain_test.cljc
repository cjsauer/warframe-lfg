(ns warframe-lfg.domain-test
  (:require #?@(:clj [[clojure.test :refer [deftest is testing]]]
                :cljs [[clojure.test :refer-macros [deftest is testing]]])
            [warframe-lfg.domain :as wf]
            [warframe-lfg.domain.post :as post]
            [warframe-lfg.domain.hashtag :as htag]))

(def sample-post
  {:post/body "Looking for 2 more to complete #Ragnarok #Lvl25 need a #Warlok"})

(deftest hashtags
  (testing "extraction"
    (is (= (post/hashtags sample-post)
           '("#Ragnarok" "#Lvl25" "#Warlok"))))

  (testing "normalization"
    (is (= (htag/normalize-all ["#MyTag" "#YourTag12" "#thistag"])
           '("#mytag" "#yourtag12" "#thistag")))
    (is (= (htag/normalized-set ["#MyTag" "#mytag" "#MYTAG"])
           #{"#mytag"})))

  (testing "equality"
    (is (htag/= "#Ragnarok" "#ragnarok" "#raGnaRok")))

  )
