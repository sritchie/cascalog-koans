(ns cascalog.koans.solutions.chains
  (:use cascalog.api)
  (:require [cascalog.vars :as v]))

(defn chained-pairs-simple
  [pairs chain-length]
  {:pre [(>= chain-length 2)]}
  (let [out-vars  (v/gen-nullable-vars chain-length)
        var-pairs (partition 2 1 out-vars)]
    (construct out-vars
               (concat
                (for [var-pair var-pairs]
                  [pairs :>> var-pair])
                [[:distinct false]]))))

;; For those of you who get this far, there exists a more efficient
;; solution involving subchains of increasing length that are linked
;; together.
