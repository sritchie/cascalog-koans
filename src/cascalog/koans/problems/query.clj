(ns cascalog.koans.problems.query
  (:use cascalog.api))

;; A common pattern in Cascalog is a function that accepts a query and
;; returns a new, transformed query. This shows up in Cascalog's API;
;; select-fields and first-n are great examples. In this set of
;; exercises, we're going to recreate a few of those functions to get
;; a feel for the pattern and to understand how to implement our own
;; query building functions.

;; ## Exercise 1
;;
;; Write a function that accepts a query and returns the same query
;; with a distinct performed.

;; ## Exercise 2
;;
;; Write a function that accepts a query and returns a new query with
;; ONLY the specified fields (the same as select-fields, essentially).

;; ## Exercise 3
;;
;; Write a function that accepts a query and returns a new query with
;; new names.

;; ## Exercise 4:
;;
;; This one's a bit trickier. Write a function that accepts a query
;; and a sequence of pairs of old field position to new name:

[[1 "?x"]
 [3 "?y"]]

;; The resulting query should take each field at the supplied position
;; in the old query and assign it to the paired variable name in the
;; new query. (Side question -- why can't we just use a map, instead
;; of these pairs?)

;; ## Exercise 5: Global Sort
;;
;; Write a function that accepts a query and returns a NEW query with
;;global sorting. Here's the function signature:

(defn global-sort
  [query sort-fields]
  ,,,)

;; I want to note that you can sort on multiple fields with
;;
;; (:sort ?x ?y)
;;
;; You can also write the above as:
;;
;; (:sort :<< ["?x" "?y"])
