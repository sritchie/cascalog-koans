(ns cascalog.koans.problems.stocks
  (:use cascalog.api
        [cascalog.koans.datastore :only (stock-tap)])
  (:require [cascalog.ops :as c]
            [clj-time.core :as time]
            [clj-time.format :as format]))

;; ## Stock Calculations

(def formatter
  (format/formatter "yyyy-MM-dd"))

(defn parse-date
  "Accepts a date string of the format yyyy-MM-dd and returns a Long
  representing the number of milliseconds since the epoch."
  [s]
  (when s
    (format/parse formatter s)))

(defn subtract-days
  "Adds `n` days to the supplied DateTime object."
  ([n dt] (time/minus dt (time/days n))))

;; Challenge:
;;
;; write a query that calculates the change in stock price over each 5
;; day period.
;;
;; The result vector should be [?sym ?date ?trailing-drop]
;;
;; Challenge 2: Calculate the maximum drop over that period.
