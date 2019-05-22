(ns warframe-lfg.ui.main
  (:require [rum.core :as rum]
            [warframe-lfg.domain :as domain]
            [factui.api :as f :include-macros true]
            [factui.rum :as fr]
            [warframe-lfg.domain :as lfg]))

(f/rulebase rulebase)

(def initial-data
  [])

(rum/defc Root
  []
  [:div.container
   [:div.post-editor
    [:textarea {:id "new-post-body"}]
    [:button {:id "new-post!"} "Create post"]]
   [:div.post-list-container
    [:h2 "Latest Posts"]
    [:ul.post-list
     [:li.post-body "This is a post"]]]])

(defn ^:dev/after-load start!
  []
  (let [dom-root (.getElementById js/document "mount")
        mount (fn [app-state]
                (rum/mount ((deref #'Root) app-state)
                           dom-root))
        app-state (fr/initialize #'rulebase lfg/schema mount)]
    #_(fr/transact! app-state initial-data)))
