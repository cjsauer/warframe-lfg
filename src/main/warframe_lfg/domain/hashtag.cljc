(ns warframe-lfg.domain.hashtag
  (:require [clojure.string :as cstr])
  (:refer-clojure :exclude [=]))

(defn normalize
  "Normalizes a hashtag. For example #WarFrame becomes #warframe."
  [htag]
  (cstr/lower-case htag))

(defn normalize-all
  "Maps `normalize` over the given collection."
  [htag-coll]
  (map normalize htag-coll))

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
