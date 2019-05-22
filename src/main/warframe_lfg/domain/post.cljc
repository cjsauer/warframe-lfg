(ns warframe-lfg.domain.post
  (:require [warframe-lfg.domain :as lfg]))

(defn- extract-hashtags
  "Extracts the hashtags contained within the given string, preserving order."
  [s]
  (re-seq hashtag-regex s))

(defn hashtags
  "Returns the set of hashtags contained within the given post's body."
  [post]
  (extract-hashtags (:post/body post)))

(defn expired?
  "Returns true if the given post's expiration date tests less than `now`."
  [now post]
  (< (:post/expiration-instant post) now))
