(def tuple-src [[2]])

(meditations
 "Each of the following koans requires you to fill in the blank,
  represented by a double underscore (__). Move on to the next koan by
  creating an equality."
 (= __ [["truth."]])
 
 "A cascalog query generates a sequence of result tuples. What does
  this query generate?"
 (?= __ (<- [?x]
            (tuple-src ?x)))

 "Any sequence of tuples can act as a generator, not just sequences
  bound to vars. What do we need to bind to src in order to produce
  the supplied result sequence?"
 (let [src __]
   (?= [[2]]
       (<- [?x] (src ?x))))

 "Lists are valid generators as well."
 (let [src (list [2])]
   (?= __ (<- [?x]
              (src ?x))))

 "What generator might produce the following result?"
 (?= [[4]] (<- [?x]
               (__ ?x)))

 "Generators can produce many tuples."
 (?= [[4] [5]] (<- [?x]
                   (__ ?x)))

 "To execute properly, each of the variables in the output vector must
  show up somewhere in the query's predicates."
 (?= tuple-src (<- [?x]
                   (tuple-src __)))

 "To produce results, the output vector must contain variables that
  show up somewhere in the query's predicates."
 (?= tuple-src (<- [__]
                   (tuple-src ?y)))

 "The logic variables' exact names don't matter; the only rule is that
  output variables must show up in the query's predicates."
 (?= tuple-src (<- [__]
                   (tuple-src __)))

 "Generators can produce tuples with multiple fields. What does the
  result vector for this query look like?"
 (?= [[1 2]] (<- __
                 ([[1 2]] ?x ?y)))

 "How about the result vector for this query?"
 (?= [[2 1]] (<- __
                 ([[1 2]] ?x ?y)))

 "Tuple fields can contain items of almost any type. What does this
  blank field need to contain?"
 (?= [[1 "string"]] (<- [?x ?str]
                        (__ ?x ?str))))
