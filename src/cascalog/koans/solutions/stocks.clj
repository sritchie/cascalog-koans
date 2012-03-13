(ns cascalog.koans.stocks
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

(defn trailing-drop-query
  "Returns a query that calculates the trailing change in stock price
  over the last `day-spread` days"
  [day-spread]
  ;; select-fields works here because the tap has fields declared.
  (let [stock-src (select-fields stock-tap ["?stock-sym" "?date" "?adj"])]
    (<- [?sym ?date ?trailing-drop]
        (stock-src ?sym ?date-str ?adj-close)
        (stock-src ?sym ?lag-date-str ?lag-adj-close)
        (parse-date ?lag-date-str ?date-str :> ?lag-date ?date)
        (subtract-days day-spread ?date :> ?lag-date)
        (- ?adj-close ?lag-adj-close :> ?trailing-drop)
        (:distinct false))))

(defn max-drop-query
  "Returns the maximum drop!"
  [day-spread]
  (let [src (trailing-drop-query day-spread)]
    (<- [?sym ?max-drop]
        (src ?sym ?date ?drop)
        (c/max ?drop :> ?max-drop))))

;; More Depth with Aggregations

(defn average [coll]
  (/ (reduce + coll) (count coll)))

(defbufferop [moving-average-sort [window]]
  [coll]
  (let [coll (sort-by first coll)]
    [[(map (fn [xs]
             (let [date   (first (last xs))
                   prices (average (map second xs))]
               [date prices]))
           (partition window 1 coll))]]))

;; Can also do ths with a sliding window: http://www.learningclojure.com/2010/03/moving-average-of-list.html

(defmapcatop transpose-coll [xs] xs)

;; This is the naive way to do it, since we're sorting inside.

(def moving-avg-query-sort
  (<- [?sym ?end-date ?avg]
      (stock-tap
       ?exchange ?sym ?date-str ?open ?high ?low ?close ?volume ?adj)
      (parse-date ?date-str :> ?date)
      (moving-average-sort [30] ?date ?adj :> ?averages)
      (transpose-coll ?averages :> ?end-date ?avg)))

;; Let's try something a bit better:

(defbufferop [moving-average [window]]
  [coll]
  [[(map (fn [xs]
           (let [date   (first (last xs))
                 prices (average (map second xs))]
             [date prices]))
         (partition window 1 coll))]])

;; This query handles the sorting by itself!

(def moving-avg-query
  (<- [?sym ?end-date ?avg]
      (stock-tap _ ?sym ?date-str ?open ?high ?low ?close ?volume ?adj)
      (parse-date ?date-str :> ?date)
      (moving-average [30] ?date ?adj :> ?averages)
      (:sort ?date)
      (transpose-coll ?averages :> ?end-date ?avg)))

;; Separate the logic out into a predicate macro:

(defn moving-avg [window]
  (<- [?date ?val :> ?end-date ?avg]
      (:sort ?date)
      (moving-average [window] ?date ?val :> ?averages)
      (transpose-coll ?averages :> ?end-date ?avg)))

(def moving-avg-query
  (let [avg-op (moving-avg 30)]
    (<- [?sym ?end-date ?avg]
        (stock-tap _ ?sym ?date-str ?open ?high ?low ?close ?volume ?adj)
        (parse-date ?date-str :> ?date)
        (avg-op ?date ?adj :> ?end-date ?avg))))

;; We can calculate all sorts of arbitrary stats using Cascalog

(def stock-stats-query
  (<- [?sym ?average ?highest ?lowest ?max-spread]
      (stock-tap _ ?sym ?date-str ?open ?high ?low ?close ?volume ?adj)
      (parse-date ?date-str :> ?date)
      (- ?high ?low :> ?spread)
      (c/max ?spread :> ?max-spread)
      (c/max ?high :> ?highest)
      (c/min ?low :> ?lowest)
      (c/avg ?adj :> ?average)))
