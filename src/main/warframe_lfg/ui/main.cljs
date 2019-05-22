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
  [:h1 "Warframe LFG coming"])

(defn ^:dev/after-load start!
  []
  (let [dom-root (.getElementById js/document "mount")
        mount (fn [app-state]
                (rum/mount ((deref #'Root) app-state)
                           dom-root))
        app-state (fr/initialize #'rulebase lfg/schema mount)]
    #_(fr/transact! app-state initial-data)))
