(ns warframe-lfg.util)

(defn now
  "Returns the current instant."
  []
  #?(:clj (java.util.Date.)
     :cljs (js/Date.)))

(defn rand-uuid
  []
  #?(:clj (java.util.UUID/randomUUID)
     :cljs (random-uuid)))
