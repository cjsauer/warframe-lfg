(ns warframe-lfg.ui.main
  (:require [clojure.spec.alpha :as s]
            [factui.api :as f :include-macros true]
            [factui.rum :as fr :refer [*results*]]
            [rum.core :as rum]
            [warframe-lfg.domain :as lfg]
            [warframe-lfg.domain.hashtag :as htag]
            [warframe-lfg.domain.post :as post]
            [datascript.core :as d]))

(f/defrule transact-new-post-hashtags-rule
  "On new post creation, transact the hashtags contained within the post body
  as :post/hashtags."
  [_ :global/posts ?p]
  [?p :post/body ?body]
  =>
  (when-let [htag-ents (post/hashtag-entities-from-body ?body)]
    (f/transact! [{:db/id ?p
                   :post/hashtags htag-ents}])))

(f/defquery all-post-eids-q
  [:find [?p ...]
   :where
   [_ :global/posts ?p]])

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

(defn make-wip-post
  []
  (post/make-post {:body ""}))

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
  [(post/make-post {:body "This is a post with a #hashtag" })
   (post/make-post {:body "Need two more to #raid #Ragnarok"})
   (post/make-post {:body "Hashtags can contain #Numbers123"})
   (post/make-post {:body "This #HashTag is not unique"})])

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
     [:button {:id "new-post-btn"
               :on-click save}
      "Post!"]]))

(rum/defc Post < rum/static
                 {:key-fn :post/uuid}
  [post]
  (let [{:post/keys [body]} post]
    [:li.post-list-item body]))

(rum/defc PostList < rum/static
                     (fr/mixin all-post-eids-q)
  [app-state]
  (let [posts (d/pull-many (f/db @app-state) '[*] *results*)]
    [:div.post-list-container
     [:h2 "Latest Posts"]
     [:ul.post-list
      (for [post posts]
        (Post post))]]))

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
