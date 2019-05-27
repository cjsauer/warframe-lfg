(ns warframe-lfg.domain.hashtag
  (:refer-clojure :exclude [=])
  (:require [clojure.string :as cstr]))

(defn normalize
  "Normalizes a hashtag. For example #WarFrame becomes #warframe."
  [htag]
  (cstr/lower-case htag))

(defn normalize-all
  "Maps `normalize` over the given hashtag collection."
  [htag-coll]
  (map normalize htag-coll))

(defn normalized-set
  "Returns the set of normalized hashtags from the given collection"
  [htag-coll]
  (into #{} (map normalize) htag-coll))

(defn =
  "Equality for hashtags. Performs normalization before comparing."
  ([htag1] true)
  ([htag1 htag2]
   (clojure.core/= (normalize htag1)
      (normalize htag2)))
  ([htag1 htag2 & htags]
   (apply clojure.core/=
          (normalize htag1)
          (normalize htag2)
          (normalize-all htags))))
