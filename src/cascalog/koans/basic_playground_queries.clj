(ns cascalog.koans.basic-playground-queries
  (:use cascalog.api
        cascalog.playground)
  (:require [cascalog.ops :as c]))

(defn to-stdout
  "Executes the supplied query, printing results to stdout."
  [q]
  (?- (stdout) q))

(def people-under-30
  (<- [?person]
      (age ?person ?age) ;; <-- generator
      (< ?age 30)))      ;; <-- filter

(def under-30-with-age
  (<- [?person ?age]
      (age ?person ?age) ;; <-- generator
      (< ?age 30)))      ;; <-- filter

(def square-equals-self
  (<- [?n]
      (integer ?n)      ;; <-- generator
      (* ?n ?n :> ?n))) ;; <-- operation

(def cubed-equals-self
  (<- [?n]
      (integer ?n)         ;; <-- generator
      (* ?n ?n ?n :> ?n))) ;; <-- operation

(def follows-younger
  (<- [?person1 ?person2]
      (age ?person1 ?age1)        ;; <-- generator
      (follows ?person1 ?person2) ;; <-- generator
      (age ?person2 ?age2)        ;; <-- generator
      (< ?age2 ?age1)))           ;; <-- filter

(def people-under-30-count
  (<- [?count]
      (age _ ?age)       ;; <-- generator
      (< ?age 30)        ;; <-- operation
      (c/count ?count))) ;; <-- aggregator

(def follows-count
  (<- [?person ?count]
      (follows ?person _) ;; <-- generator
      (c/count ?count)))  ;; <-- aggregator

(defmapcatop split [sentence]
  (.split sentence "\\s+"))

(def wordcount-query
  (<- [?word ?count]
      (sentence ?sentence)       ;; <-- generator
      (split ?sentence :> ?word) ;; <-- operation
      (c/count ?count)))         ;; <-- aggregator

(def a-follows-b
  (let [many-follows (<- [?person]
                         (follows ?person _) ;; <-- generator
                         (c/count ?count)    ;; <-- aggregator
                         (> ?count 2))]      ;; <-- filter
    (<- [?person1 ?person2]
        (many-follows ?person1)        ;; <-- generator
        (many-follows ?person2)        ;; <-- generator
        (follows ?person1 ?person2)))) ;; <-- generator

(def inner-join
  (<- [?person ?age ?gender]
      (age ?person ?age)         ;; <-- generator
      (gender ?person ?gender))) ;; <-- generator

;; very similar to outer-join:

(def outer-join
  (<- [?person !!age !!gender]
      (age ?person !!age)         ;; <-- generator
      (gender ?person !!gender))) ;; <-- generator
