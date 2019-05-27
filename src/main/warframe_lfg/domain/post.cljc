(ns warframe-lfg.domain.post
  (:require [cljs.spec.alpha :as s]
            [warframe-lfg.domain :as domain]
            [warframe-lfg.util :as util]))

(defn expired?
  "Returns true if the given post's expiration date tests less than `now`."
  [now post]
  (< (:post/expiration-instant post) now))

(defn make-hashtag-entity
  "Creates a new hashtag map with the given string value."
  [htag-str]
  {:pre [(s/valid? :hashtag/value htag-str)]}
  {:hashtag/value htag-str})

(defn hashtag-entities-from-body
  "Given a post body string, returns the seq of hashtag entities contained within,
  preserving order. Returns nil if no hashtags are present in the given body."
  [body]
  (->> body
       domain/extract-hashtags
       (map make-hashtag-entity)
       seq))

(defn make-post
  "Create a new post (with qualified keys) from the given unqualified map."
  [{:keys [uuid body]}]
  {:post/uuid (or uuid (util/rand-uuid))
   :post/body body})
