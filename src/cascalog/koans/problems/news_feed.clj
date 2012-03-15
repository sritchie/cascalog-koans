(ns cascalog.koans.problems.news-feed
  (:use cascalog.api
        [cascalog.koans.util :only (dev-path)])
  (:require [cascalog.elephantdb.keyval :as kv]
            [cascalog.vars :as v])
  (:import [elephantdb.persistence JavaBerkDB KeyValPersistence]
           [elephantdb.partition HashModScheme]
           [elephantdb.document KeyValDocument]))

;; ## News Feed Creation!
;;
;; In this exercise you're going to write a news feed that aggregates
;; lists of social data. You'll need to make use of two data
;;sources. The first is a "follows" relationship, just like the
;;follows dataset from the playground:

(def example-follows-src
  [["nathan bob"]
   ["chris mike"]
   ["mike chris"]
   ["michelle nathan"]])

;; The second data source is a list of "actions". An action is a
;; 3-tuple of the form <username, action, timestamp>. The timestamp is
;; in milliseconds, just like the timestamp returned by

(defn now []
  (System/currentTimeMillis))

;; Here's an example:

(def example-actions-src
  [["nathan status=good 1273094927000"]
   ["nathan birthday  1273026922000"]])

;; The goal is to generate, for each user, a list of the top-ranked
;; events from that person's timeline. The final query will produce
;; something like this:

(def example-results
  [["mike"  "((\"alice\" \"status=good\" 1273091927000) (\"alice\" \"rock-climbing\" 1273084927000))"]
   ["jai"   "((\"danielle\" \"married\" 1273095924348) (\"danielle\" \"cooking-a-storm\" 1273094927000) (\"mike\" \"tennis\" 1273084927000))"]
   ["david" "((\"nathan\" \"status=great\" 1273096922000) (\"nathan\" \"status=good\" 1273094927000) (\"vijay\" \"inventing\" 1273099927000))"]])

;; Events should show up in descending order (by time).
;;
;; Good luck! To get started, work with the following textline taps:

(def action-src
  (hfs-textline (dev-path "actions")))

(def follows-src
  (hfs-textline (dev-path "follows")))
