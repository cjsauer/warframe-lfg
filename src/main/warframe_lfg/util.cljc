(ns warframe-lfg.util)

(defn now
  "Returns the current instant."
  []
  #?(:clj (java.util.Date.)
     :cljs (js/Date.)))
