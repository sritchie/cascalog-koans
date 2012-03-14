(ns cascalog.koans.solutions.aggregator
  (:use [cascalog api playground])
  (:require [clojure.string :as s]))

;; ## Problem 1 Solutions

;; Top 5 with a defaggregateop:

(defaggregateop top-5-agg
  ([] [])          
  ([res val]
     (if (< (count res) 5)
       (conj res val)
       res))
  ([res] res))

;; Implemented with a parameter:

(defaggregateop [first-n-agg [n]]
  ([] [])
  ([res val]
     (if (< (count res) n)
       (conj res val)
       res))
  ([res] res))

;; Top 5 with a buffer:

(defbufferop top-5-buf
  [tuples]
  (take 5 tuples))

;; Top 5 buffer with a parameter:

(defbufferop [first-n-buf [n]] [tuples]
  (take n tuples))

(defn top-n-query [n op]
  (<- [?val]
      (integer ?n)
      (:sort ?n)
      (:reverse true)
      (op [n] ?n :> ?val)))

(defn bottom-n-query [n op]
  (<- [?val]
      (integer ?n)
      (:sort ?n)
      (op [n] ?n :> ?val)))

;; With keyword args:

(defn extreme-n-query
  [n op & {:keys [reverse?]}]
  (<- [?val]
      (integer ?n)
      (:sort ?n)
      (:reverse (boolean reverse?))
      (op [n] ?n :> ?val)))

;; ## Problem 2 Solutions

(defbufferop str-append-buffer
  "Returns a 1-tuple containing a comma-separated list of follower
  names."
  [tuples]
  [(s/join "," (map (comp str first) tuples))])

(defaggregateop str-append-agg
  ([] nil)
  ([curr val] (if curr (str curr "," val) val))
  ([curr] [curr]))

(defn str-append-combine [val1 val2]
  (str val1 "," val2))

(defparallelagg str-append-pagg
  "Returns a single field containing a comma-separated list of
   follower names."
  :init-var    #'str
  :combine-var #'str-append-combine)

(defn all-followers
  "Accepts an aggregator meant to process a sequence of followers and
  returns a query that generates a sequence of 2-tuples of the form
  <user, processed-follower-list>"
  [op]
  (<- [?person ?list]
      (follows ?follower ?person)
      (op ?follower :> ?list)))

(comment
  "Execute each in parallel with the following:"
  (??- (all-followers str-append-buffer)
       (all-followers str-append-agg)
       (all-followers str-append-pagg)))

;; ## Problem 3 Solutions

(defn keep-max-val [amap]
  (if (empty? amap)
    amap
    (into {} [(first (sort-by #(* -1 (second %)) amap))])))

(defaggregateop most-frequent-val
  ([] {})
  ([state val] 
     (let [curr-val (state val)]
       (if curr-val
         (update-in state [val] inc)
         (assoc (keep-max-val state) val 1))))
  ([state] [((comp first first) (keep-max-val state))]))

(def gender-query
  (<- [?person ?gender]
      (gender-fuzzy ?person ?g _)
      (most-frequent-val ?g :> ?gender)
      (:sort ?g)))

;; Implemented with a parallel aggregator:

(defn identity-tuple [& tuple] tuple)

(defn choose-recent-val
  [val1 time1 val2 time2]
  (if (> time2 time1)
    [val2 time2]
    [val1 time1]))

;; Note that init-var returns 2 fields, so combine-var accepts 4 (and
;; returns 2).

(defparallelagg most-recent-val
  :init-var    #'identity-tuple
  :combine-var #'choose-recent-val)

(comment
  (def parallel-gender-query
    (<- [?person ?gender]
        (gender-fuzzy ?person ?g ?t)
        (most-recent-val ?g ?t :> ?gender _)))

  (def limit-gender-query
    (<- [?person ?gender]
        (gender-fuzzy ?person ?g ?t)
        (:sort ?t)
        (:reverse true)
        (c/limit [1] ?g :> ?gender))))



