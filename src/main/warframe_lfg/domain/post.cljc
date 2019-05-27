(ns warframe-lfg.domain.post
  (:require [warframe-lfg.domain :as domain]
            [warframe-lfg.util :as util]))

(defn extract-hashtags
  "Extracts the hashtags contained within the given string, preserving order."
  [s]
  (re-seq domain/hashtag-regex s))

(defn hashtags
  "Returns the set of hashtags contained within the given post's body."
  [post]
  (extract-hashtags (:post/body post)))

(defn expired?
  "Returns true if the given post's expiration date tests less than `now`."
  [now post]
  (< (:post/expiration-instant post) now))

(defn make-post
  "Create a new post (with qualified keys) from an unqualified map."
  [{:keys [body]}]
  {:post/uuid (util/rand-uuid)
   :post/body body})
