(ns cascalog.koans.problems.chains
  (:use cascalog.api
        cascalog.playground))

;; ## Challenge:
;;
;; Write a dynamically generated query that produces "chains" within
;; the playground's follows dataset. The follows dataset is,
;; essentially, a set of chains of length two. Taking the following
;; dataset as an example:

(def example-src
  [["alice" "john"]
   ["john" "carrie"]])

;; The only chain of length three would be

["alice" "john" "carrie"]

;; Since alice follows john, who follows carrie.
;;
;; "construct" is your friend, here.
