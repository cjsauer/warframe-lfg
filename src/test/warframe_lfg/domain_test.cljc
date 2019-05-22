(ns warframe-lfg.domain-test
  (:require #?@(:clj [[clojure.test :refer [deftest is testing]]]
                :cljs [[clojure.test :refer-macros [deftest is testing]]])
            [warframe-lfg.domain :as wf]))

(def sample-post
  {:post/body "Looking for 2 more to complete #Ragnarok #Lvl25 need a #Warlok"})

(deftest hashtags
  (testing "extraction"
    (is (= (wf/get-hashtags sample-post)
           '("#Ragnarok" "#Lvl25" "#Warlok"))))

  (testing "normalization"
    (is (= (wf/normalize-all-hashtags ["#MyTag" "#YourTag12" "#thistag"])
           '("#mytag" "#yourtag12" "#thistag"))))

  (testing "equality"
    (is (wf/hashtag= "#Ragnarok" "#ragnarok" "#raGnaRok")))

  (testing "round-trip"
    (let [htags         (wf/get-hashtags sample-post)
          normalized    (wf/normalize-all-hashtags htags)
          norm-equality (map wf/hashtag= htags normalized)]
      (is (every? true? norm-equality)))))
