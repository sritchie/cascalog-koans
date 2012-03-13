[["tuples" {"__" [
                  [["truth."]]
                  [[2]]
                  [[2]]
                  [[2]]
                  [[4]]
                  [[4] [5]]
                  ?x
                  ?x
                  ?y
                  ?name ?name ;; or any pair w/ ! or ?
                  [?x ?y]
                  [?y ?x]
                  [[1 "string"]]
                  ]}]

 ["basic_ops" {"__" [[[2]]
                     [[1] [2] [3]]
                     [[1 2] [2 3] [3 4]]
                     [[0] [1]]
                     [[-1] [0] [1]]

                     ;; people under 30
                     (< ?age 30)

                     ;; people under 28
                     [?person ?age]
                     (< ?age 28)

                     ;; produces alice and gary.
                     28

                     ;; produces 31.
                     "george"

                     ;; (mod ?age 10 :> 0)
                     0

                     ;; return users starting with \a
                     (first-char ?person :> "a")

                     ;; ## Inner Join Section

                     ;; "interesting behavior"
                     [["bob"] ["gary"] ["george"]]

                     ;; produces ?age and ?gender
                     (age ?person ?age)
                     (gender ?person ?gender)

                     ;; even ages and genders
                     (age ?person ?age)
                     (gender ?person ?gender)
                     (even? age)

                     ;; younger follows
                     (<- [?person1 ?person2] 
                         (age ?person1 ?age1)
                         (age ?person2 ?age2)
                         (follows ?person1 ?person2)
                         (< ?age2 ?age1))

                     ;; younger follows with delta
                     (<- [?person1 ?person2 ?delta] 
                         (age ?person1 ?age1)
                         (follows ?person1 ?person2)
                         (age ?person2 ?age2)
                         (- ?age2 ?age1 :> ?delta)
                         (< ?delta 0))
                     ]}]]
