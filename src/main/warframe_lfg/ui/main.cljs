(ns warframe-lfg.ui.main
  (:require [rum.core :as rum]
            [warframe-lfg.domain :as domain]
            [factui.api :as f :include-macros true]
            [factui.rum :as fr :refer [*results*]]
            [warframe-lfg.domain :as lfg]
            [warframe-lfg.domain.post :as post]
            [warframe-lfg.domain.hashtag :as htag]
            [clojure.spec.alpha :as s]))

(defn make-hashtag
  [ht]
  {:hashtag/value ht})

(f/defrule extract-new-post-hashtags-rule
  "Extract and transact hashtags whenever a new post is transacted"
  [_ :global/posts ?p]
  [?p :post/body ?body]
  =>
  (when-let [hashtags (seq (map make-hashtag (post/extract-hashtags ?body)))]
    (f/transact! [{:db/id ?p
                   :post/hashtags hashtags}])))

(f/defquery all-posts-q
  [:find ?uuid ?body
   :where
   [_ :global/posts ?p]
   [?p :post/uuid ?uuid]
   [?p :post/body ?body]])

(f/defquery all-hashtags-q
  [:find [?htvalue ...]
   :where
   [_ :global/posts ?p]
   [?p :post/hashtags ?ht]
   [?ht :hashtag/value ?htvalue]])

(f/defquery wip-post-q
  [:find ?post ?body
   :where
   [_ :global/wip-post ?post]
   [?post :post/body ?body]])

(defn make-post
  [body]
  {:post/uuid (random-uuid)
   :post/body body})

(defn make-wip-post
  []
  (make-post ""))

(def full-schema
  (concat lfg/schema
          [{:db/ident       :global/posts
            :db/valueType   :db.type/ref
            :db/cardinality :db.cardinality/many}
           {:db/ident       :global/wip-post
            :db/valueType   :db.type/ref
            :db/cardinality :db.cardinality/one
            :db/doc         "The new post currently being edited"}]))

(f/rulebase rulebase warframe-lfg.ui.main)

(def sample-posts
  [(make-post "This is a post with a #hashtag")
   (make-post "Need two more to #raid #Ragnarok")
   (make-post "Hashtags can contain #Numbers123")
   (make-post "This #HashTag is not unique")])

(def initial-data
  [{:db/ident        :global
    :global/wip-post (make-wip-post)
    :global/posts    sample-posts}])

(rum/defc PostEditor < rum/static
                       (fr/mixin wip-post-q)
  [app-state]
  (let [[?post ?body] (first *results*)
        change #(fr/transact! app-state [{:db/id ?post
                                          :post/body (-> % .-target .-value)}])
        save #(when (s/valid? :post/body ?body)
                (fr/transact! app-state [{:db/ident :global
                                          :global/posts ?post
                                          :global/wip-post (make-wip-post)}]))]
    [:div.post-editor
     [:textarea {:id "wip-post-body"
                 :value (or ?body "")
                 :on-change change}]
     [:button {:id "new-post!"
               :on-click #(do (save))}
      "Post!"]]))

(rum/defc PostList < rum/static
                     (fr/mixin all-posts-q)
  [app-state]
  (let [posts *results*]
    [:div.post-list-container
     [:h2 "Latest Posts"]
     [:ul.post-list
      (for [[uuid body] posts]
        [:li.post-body {:key uuid} body])]]))

(rum/defc HashtagList < rum/static
                        (fr/mixin all-hashtags-q)
  [app-state]
  (let [hashtags (htag/normalized-set *results*)]
    [:div.hashtag-list-container
     [:h2 "Hashtags Used"]
     [:ul.hashtag-list
      (for [ht hashtags]
        [:li.hashtag {:key ht} ht])]]))

(rum/defc Root < rum/static
  [app-state]
  [:div.container
   (PostEditor app-state)
   (PostList app-state)
   (HashtagList app-state)])

(defn ^:dev/after-load refresh
  []
  (fr/refresh))

(defn start!
  []
  (let [dom-root (.getElementById js/document "mount")
        mount (fn [app-state]
                (rum/mount ((deref #'Root) app-state)
                           dom-root))
        app-state (fr/initialize #'rulebase full-schema mount)]
    (fr/transact! app-state initial-data)))
