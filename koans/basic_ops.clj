;; ## Basic Operations
;;
;; The initial meditations build on some of the most basic Cascalog
;; concepts -- the operator and the filter. These problems make use of
;; Cascalog's "playground" dataset, located here:
;;
;; https://github.com/nathanmarz/cascalog/blob/master/src/clj/cascalog/playground.clj
;;
;; Open this page up in a browser to follow along as you begin to
;;encounter problems that deal with the playground.

(use 'cascalog.playground)

(defn first-char
  "Returns a string representation of the first character of the
  supplied string."
  [s]
  (str (first s)))

(meditations 
 "This query contains two predicates. The first is a familiar
 generator. The second produces a new output variable. What do the
 results of the query look like?"
 (let [src [[1]]]
   (?= __ (<- [?y]
              (src ?x)
              (+ ?x ?x :> ?y))))

 "What does this query generate when src produces more than one
 tuple?"
 (let [src [[1] [2] [3]]]
   (?= __ (<- [?x]
              (src ?x)
              (+ ?x ?x :> ?y))))

 "What if we include both ?x and ?y in the result vector?"
 (let [src [[1] [2] [3]]]
   (?= __ (<- [?x ?y]
              (src ?x)
              (+ ?x ?x :> ?y))))

 ;; "integer" comes from the playground and produces a sequence of
 ;; 1-tuples representing integers from -1 to 9.

 "Of all the 1-tuples produced by the integer dataset, which will make
 it past the following logical predicates? (That is, for which ?n does
 ?n equal its square?)"
 (?= __
     (<- [?n]
         (integer ?n)
         (* ?n ?n :> ?n)))

 "This query is similar, but not quite the same. Which numbers
  produced by the integer dataset make it past the predicates defined
  in the following query? (That is, for which ?n does ?n equal its cube?)"
 (?= __
     (<- [?n]
         (integer ?n)
         (* ?n ?n ?n :> ?n)))

 ;; the "age" generator comes from the playground; it contains a
 ;; sequence of 2-tuples of the form [person, age]. For example,
 ;; ["joe", 28].

 "Complete the missing predicate; we want a query that returns the
 names of people under 30 years of age."
 (?= [["alice"]
      ["david"]
      ["emily"]
      ["gary"]
      ["kumar"]]
     (<- [?person]
         (age ?person ?age)
         __
         ))

 "Now fill in the missing predicate so that the query returns all
 people under 28 along with their ages. (You'll have to fill in the
 result vector as well."
 (?= [["david" 25]
      ["emily" 25]
      ["kumar" 27]]
     (<- __
         (age ?person ?age)
         __
         ))

 "The next query is a bit trickier. What could possibly go in this
  spot that would constrain the output this way?"
 (?= [["alice"]
      ["gary"]]
     (<- [?person]
         (age ?person __)))

 "How about here? How do we get the only 31 year old?"
 (?= [[31]]
     (<- [?age]
         (age __ ?age)))

 "What if we want ages divisible by 10? How could we constrain this
  query?"
 (?= [["chris" 40]]
     (<- [?person ?age]
         (age ?person ?age)
         (mod ?age 10 :> __)))

 "Fill in the missing predicate so that the query only returns users
 starting with \"a\". (See the function defined at the top for some
 help!)"
 (?= [["alice"]]
     (<- [?person]
         (age ?person ?age)
         __
         ))

 ;; ## Inner Joins
 ;;
 ;; Filtering and operations with functions will only get you so far;
 ;; these predicates allow you to transform and filter functions from
 ;; a generator, but you can't really create anything new without the
 ;; ability to reference other datasets. Joins in Cascalog are
 ;; implicit, and occur when the same logic variable name is used
 ;; across multiple datasets.
 ;;
 ;; The "follows" and "gender" datasets are located in Cascalog's
 ;; playground.

 "Using the same logic variable name across two datasets produces some
 interesting behavior."
 (?= __
     (<- [?person]
         (follows "emily" ?person)
         (gender ?person "m")))

 "Write a query that produces ?age and ?gender for each person in the
 age and gender datasets."
 (?= [["emily" 25 "f"]
      ["david" 25 "m"]
      ["alice" 28 "f"]
      ["gary"  28 "m"]
      ["george "3 "m"]
      ["bob "3 "m"]
      ["luanne "3 "f"]
      ["chris" 40 "m"]]
     (<- [?person ?age ?gender]
         __
         __
         ))

 "Now write a query that produces ?age and ?gender for each person in
 the age and gender datasets with an even-numbered age."
 (?= [["alice" 28 "f"]
      ["gary"  28 "m"]
      ["chris" 40 "m"]]
     (<- [?person ?age ?gender]
         __
         __
         __
         ))
 
 "Write a query that returns every pair from the follows dataset where
  ?person1 is following a younger ?person2."
 (?= [["alice" "david"]
      ["alice" "emily"]
      ["bob" "david"]
      ["bob" "george"]
      ["george" "gary"]
      ["luanne" "gary"]]
     __)
 
 "Write the same query from the previous problem, but also generate
  the age delta."
 (?= [["bob" "david" -8]
      ["luanne" "gary" -8]
      ["alice" "david" -3]
      ["alice" "emily" -3]
      ["george" "gary" -3]
      ["bob" "george" -2]]
     __)
 )
