(ns cascalog.koans.problems.aggregator
  (:use [cascalog api playground]
        [clojure.string :only (join)])
  (:require [clojure.string :as s]))

;; ### Challenge 1
;;
;; Implement two queries that process the integer dataset and return
;; (respectively):
;;
;; * The greatest 5 values
;; * The least 5 values

;; ### Challenge 2
;;
;; Using the `follows` data source from `cascalog.playground`,
;;generate a sequence of 2-tuples of the form
;;
;; ["username", "follower1,follower2,follower3"]
;;
;; The second field should be a comma-separated string listing each of
;; the user's followers.
;;
;; If you finish early, try to write your aggregation all three ways:
;;
;; * defbufferop
;; * defaggregateop
;; * defparallelagg

;; ### Challenge 3
;;
;; This problem uses the `gender-fuzzy` dataset in
;; cascalog.playground. The goal is to figure out the user's gender by
;; choosing the most frequent value out of all entered genders.
;;
;; Implement a query that returns a sequence of tuples of the form:
;;
;; ["username" "gender"] ;; gender is "m" or "f"
;;
;; Implement the aggregation using defaggregateop and defparallelagg.
