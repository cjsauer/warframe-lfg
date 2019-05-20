(ns warframe-lfg.domain
  (:require [clojure.string :as cstr]
            [clojure.spec.alpha :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Schema

(def attributes
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

   {:db/ident       :post/hashtag
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

(s/def :post/hashtag (s/keys :req [:hashtag/value]))
(s/def :post/hashtags (s/coll-of :post/hashtag :into #{}))
(def hashtag-regex #"\B#\w*[a-zA-Z]+\w*")
(s/def :hashtag/value (s/and string?
                             #(re-matches hashtag-regex %)
                             (s/conformer cstr/lower-case)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Transformations

(defn extract-hashtags
  "Extracts the hashtags contained within the given string, preserving order."
  [s]
  (re-seq hashtag-regex s))

(defn normalize-hashtag
  "Normalizes a hashtag. For example #WarFrame becomes #warframe."
  [htag]
  (s/conform :hashtag/value htag))

(defn normalize-all-hashtags
  "Maps `normalize-hashtag` over the given collection."
  [htag-coll]
  (map normalize-hashtag htag-coll))

(defn hashtag=
  "Equality for hashtags. Performs normalization before comparing."
  ([htag1] true)
  ([htag1 htag2]
   (= (normalize-hashtag htag1)
      (normalize-hashtag htag2)))
  ([htag1 htag2 & htags]
   (apply =
          (normalize-hashtag htag1)
          (normalize-hashtag htag2)
          (normalize-all-hashtags htags))))

(defn post-hashtags
  "Returns the set of hashtags contained within the given post's body."
  [post]
  (extract-hashtags (:post/body post)))

(defn now
  "Returns the current instant."
  []
  #?(:clj (java.util.Date.)
     :cljs (js/Date.)))

(defn post-expired?
  "Returns true if the given post's expiration date tests less than `now`."
  [now post]
  (< (:post/expiration-instant post) now))


(comment
  (let [body "Looking for 2 to #raid the #GoblinCamp, using #Oberon! #1234 I can #se33 clearly now"]
    (-> body
        extract-hashtags
        normalize-all-hashtags))

  (hashtag= "#oberon" "#Oberon" "#OBERON")

  (let [t (now)]
    (js/setTimeout
     #(js/alert (post-expired? {:post/expiration-instant t}))
     1000))

  )
