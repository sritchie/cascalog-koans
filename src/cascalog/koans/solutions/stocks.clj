(ns cascalog.koans.solutions.stocks
  (:use cascalog.api
        [cascalog.koans.datastore :only (stock-tap)])
  (:require [cascalog.ops :as c]
            [clj-time.core :as time]
            [clj-time.format :as format]))

(def formatter
  (format/formatter "yyyy-MM-dd"))

(defn parse-date*
  "Accepts a date string of the format yyyy-MM-dd and returns a Long
  representing the number of milliseconds since the epoch."
  [s]
  (when s
    (format/parse formatter s)))

(def parse-date
  (c/each #'parse-date*))

(defn subtract-days
  "Adds `n` days to the supplied DateTime object."
  ([n dt] (time/minus dt (time/days n))))

;; ### Challenge 1 Solution

(defn trailing-drop-query
  "Returns a query that calculates the trailing change in stock price
  over the last `day-spread` days"
  [day-spread]
  (let [stock-src (select-fields stock-tap ["?stock-sym" "?date" "?adj"])]
    (<- [?sym ?date ?trailing-drop]
        (stock-src ?sym ?date-str ?adj-close)
        (stock-src ?sym ?lag-date-str ?lag-adj-close)
        (parse-date ?lag-date-str ?date-str :> ?lag-date ?date)
        (subtract-days day-spread ?date :> ?lag-date)
        (- ?adj-close ?lag-adj-close :> ?trailing-drop)
        (:distinct false))))

;; ### Challenge 2 Solution
;;
;; Note how grouping occurs, and that we needed to break off into
;;another subquery.

(defn max-drop-query
  "Returns a query that generates the maximum drop over all
  periods (for each `day-spread` length window) for each stock
  symbol."
  [day-spread]
  (let [src (trailing-drop-query day-spread)]
    (<- [?sym ?max-drop]
        (src ?sym ?date ?drop)
        (c/max ?drop :> ?max-drop))))

;; ## Challenge 3 Solution: More Depth with Aggregations

(defn average [coll]
  (/ (reduce + coll)
     (count coll)))

;; This is a first try at `moving-average`, this time with an internal
;; sort.

(defbufferop [moving-average-sort [window]]
  [coll]
  (let [coll (sort-by first coll)]
    [[(map (fn [xs]
             (let [date   (first (last xs))
                   prices (average (map second xs))]
               [date prices]))
           (partition window 1 coll))]]))

;; You can also implement the moving average using a sliding window:
;; http://www.learningclojure.com/2010/03/moving-average-of-list.html
;;
;; Now, you might think that you need the following defmapcatop to
;; transpose the result sequence:

(defmapcatop transpose-coll [xs] xs)

(def moving-avg-query-sort
  (let [stock-src (select-fields stock-tap ["?stock-sym" "?date" "?adj"])]
    (<- [?sym ?end-date ?avg]
        (stock-src ?sym ?date-str ?adj)
        (parse-date ?date-str :> ?date)
        (moving-average-sort [30] ?date ?adj :> ?averages)
        (transpose-coll ?averages :> ?end-date ?avg))))

;; And indeed this does work fine. `defbufferop` has some interesting
;; behavior, however; it functions a bit like a defmapcatop with its
;; results in a way that `defaggregateop` can't. The following
;; implementation drops the outer two vectors:

(defbufferop [moving-average-sort [window]]
  [coll]
  (let [coll (sort-by first coll)]
    (map (fn [xs]
           (let [date   (first (last xs))
                 prices (average (map second xs))]
             [date prices]))
         (partition window 1 coll))))

;; Allowing the query to drop the defmapcatop:

(def moving-avg-query-sort
  (let [stock-src (select-fields stock-tap ["?stock-sym" "?date" "?adj"])]
    (<- [?sym ?end-date ?avg]
        (stock-src ?sym ?date-str ?adj)
        (parse-date ?date-str :> ?date)
        (moving-average-sort [30] ?date ?adj :> ?end-date ?avg))))

;; Let's further optimize this query by allowing Hadoop to do the
;; sorting for us:

(defbufferop [moving-average [window]]
  [coll]
  (map (fn [xs]
         (let [date   (first (last xs))
               prices (average (map second xs))]
           [date prices]))
       (partition window 1 coll)))

;; Notice the `:sort` option predicate:

(def moving-avg-query
  (let [src (select-fields stock-tap ["?stock-sym" "?date" "?adj"])]
    (<- [?sym ?end-date ?avg]
        (stock-src ?sym ?date-str ?adj)
        (parse-date ?date-str :> ?date)
        (moving-average [30] ?date ?adj :> ?end-date ?avg)
        (:sort ?date))))

;; Since the sort is always necessary, we can pull the
;; `moving-average` function and the `:sort` clause out into a
;; predicate macro:

(defn moving-avg [window]
  (<- [?date ?val :> ?end-date ?avg]
      (:sort ?date)
      (moving-average [window] ?date ?val :> ?end-date ?avg)))

;; This simplifies the logic of the actual query even further.

(def moving-avg-query
  (let [stock-src  (select-fields stock-tap ["?stock-sym" "?date" "?adj"])
        avg-op     (moving-avg 30)]
    (<- [?sym ?end-date ?avg]
        (stock-src ?sym ?date-str ?adj)
        (parse-date ?date-str :> ?date)
        (avg-op ?date ?adj :> ?end-date ?avg))))

;; Note that we can use `avg-op` directly, even though it was
;; dynamically generated. This is fine with predicate macros.
