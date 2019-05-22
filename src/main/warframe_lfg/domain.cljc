(ns warframe-lfg.domain
  (:require [clojure.string :as cstr]
            [clojure.spec.alpha :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Schema

(def schema
  [{:db/ident       :post/uuid
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident       :post/body
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Body of a LFG posting. Contains #hashtags."}

   {:db/ident       :post/deleted?
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc         "True if this post has been soft deleted by its author"}

   {:db/ident       :post/expiration-instant
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc         "The instant in time after which this post is considered expired"}

   {:db/ident       :post/hashtags
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc         "One-to-many relationship between a post and the hashtags it contains"}

   {:db/ident       :hashtag/value
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         (str "Value of a hashtag is just the hashtag itself, e.g. \"#warframe\"."
                         "Note that hashtags are NOT case sensitive, and are normalized "
                         "by way of lower casing.")}

   ])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Specs

(s/def ::non-empty-string (s/and string? not-empty))

(s/def :post/uuid uuid?)
(s/def :post/body ::non-empty-string)
(s/def :post/deleted? boolean?)
(s/def :post/expiration-instant inst?)

(def hashtag-regex #"\B#\w*[a-zA-Z]+\w*")
(s/def :post/hashtags (s/keys :req [:hashtag/value]))
(s/def :post/hashtagss (s/coll-of :post/hashtags :into #{}))
(s/def :hashtag/value (s/and string? #(re-matches hashtag-regex %)))
