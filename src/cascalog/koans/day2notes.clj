(ns cascalog.koans.problems.day2notes
  (:use [cascalog vars api playground]))

(defbufferop first-five
  [tuples]
  (take 5 tuples))

(comment
  (?<- (stdout)
       [?x]
       (integer ?n)
       (first-five ?n :> ?x)))

(defn initial [x]
  x
  ) ;; emits n fields

(defbufferop first-five
  [tuples]
  [
   [1] [2] [3] [4] [5]
   ])



(defmapcatop split [s]
  (.split s " "))

(comment
  (let [src [["this word"]]]
    (?<- (stdout)
         [?index ?word]
         (src ?line)
         (split ?line :> ?index ?word))))

(defn square [x]
  [1 (* x x)])

(defn square [x] (* x x))

(defmapop square [x]
  (* x x))

(defaggregateop sum
  ([] 0)
  ([sum val] (+ val sum))
  ([sum] [sum]))

(defn square [x] (* x x))

(defmapop square
  ([] nil)
  ([state val] (* val val))
  ([state] nil))

(defn mk-query [op]
  (let [src [[1] [2] [3]]]
    (<- [?x !out]
        (src ?x)
        (op ?x :> !out)
        (:distinct false))))

(defn process-file
  [val]
  (dump-to-path path val)
  (read-data path))

;; stateful example

(defmapop [duplicate [n]]
  {:stateful true}
  ([] (open-database url)))

(deffilterop [from-small-src? [small-dataset]]
  [domain]
  (contains? small-dataset domain))

(defn mk-query [small-dataset]
  (<- []
      (src ?click ?domain)
      (from-small-src? [small-dataset] ?domain)))

(comment
  (?<- (stdout)
       [?dup]
       (integer ?n)
       (duplicate [3] ?n :> ?dup ?dup2)
       (:distinct false)))

([-1] [0] [1] [2] [3] [4] [5] [6] [7] [8] [9])

([-1] [-1] [-1] [-1] [-1] [0] [0][0] [0] [0] ...)

(defn mk-query [field-names]
  (let [c-str "?c"]
    (<- field-names
        (integer ?x)
        (add-fields ?x :> "?a" (str "?" "b") c-str))))

(<- [?x ?square]
    (integer ?x)
    (square ?x :> "?square"))

(<- [?x ?square]
    (integer ?x)
    (square ?x :>> ["?square" "?second"]))

(<- [?x ?square]
    (integer ?x)
    (square ?x :> ?square))

(<- [?x ?square]
    (integer ?x)
    (square ?x :>> [?square]))

(defn add-fields [x]
  [x (inc x) (+ x 2)])

(defn mk-query [n]
  (let [out-vec (gen-nullable-vars n)
        [a-str b-str c-str] out-vec]
    (<- out-vec
        (integer ?x)
        (add-fields ?x :>> out-vec))))

(mk-query 1)

(<- [?a]
    (integer ?x)
    (add-fields ?x :> ?a ?b ?c))

(mk-query 2)

(<- [?a ?b]
    (integer ?x)
    (add-fields ?x :> ?a ?b ?c))

(mk-query 3)

(<- [?a ?b ?c]
    (integer ?x)
    (add-fields ?x :> ?a ?b ?c))

(defn no-follow-emily []
  (<- [?person]
      (person ?person)
      (follows ?person "emily" :> true)
      (:distinct false)))

(defmapcatop [duplicate-fn [n]]
  [x]
  (repeat n x))

;; [[mc fc] gender]

(defn duplicate [n]
  (<- [?x :> ?dup]
      (duplicate-fn [n] ?x :> ?dup)))

(let [op (duplicate n)]
  (<- [?dup]
      (src ?x)
      (duplicate ?x :> ?dup)))

(defn emily-male-follows []
  (let [person-var "?person"]
    (<- [person-var]
        (follows "emily" person-var)
        (gender person-var "m"))))

(defn emily-male-follows2 []
  (let [out-vars (vec (v/gen-nullable-vars 1))]
    (<- out-vars
        (follows :>> (cons "emily" out-vars))
        (gender :>> (conj out-vars "m")))))

(defn global-sort [sq fields]
  (let [out-fields (get-out-fields sq)
        new-out-fields (v/gen-nullable-vars (count out-fields))]
    (<- new-out-fields
        (sq :>> out-fields)
        (:sort :<< fields)
        ((IdentityBuffer.) :<< out-fields :>> new-out-fields))))

(let [double-square (<- [?x :> ?square2]
                        (square ?x :> ?square)
                        (square ?square :> ?square2)
                        (square ?square2 :> ?square3))]
  (<- [?square2]
      (src ?x)
      (double-square ?x :> ?square2)))

(def square #(* % %))

(defn transform
  "Accepts a generator of one variable, returns the same generator
  with all tuples transformed by op."
  [op]
  (<- [?y]
      ([[10]] ?x)
      (op ?x :> ?y)))

(defn mk-op [return-count]
  (construct ["?x" :> (gen-nullable-vars return-count)]
             [[#'square "?x" :> "?square"]
              [#'square "?square" :> "?y"]]))

(def square #(* % %))

(def parallel-sum (each #'sum))

(parallel-sum ?a ?b ?c :> ?d ?e ?f)

(defn each [op]
  (predmacro [invars outvars]
             (map (fn [invar outvar]
                    [op invar :> outvar])
                  invars
                  outvars)))

(<- [?x :> ?y]
    (square ?x :> ?y))

(<- [?y]
    ([[2]] ?x)
    (my-op ?x :> ?y)) ;; invars = ["?x"], outvars = ["?y"]

(<- [?y]
    ([[2]] ?x)
    (my-op ?x ?b :> ?y ?c))

;;expands to
(<- [?x ?b :> ?y ?c]
    (square ?x :> ?y)
    (square ?b :> ?c))

;; invars = ["?x" "?b"], outvars = ["?y" "?c"]
(gen-nullable-vars 4)


(our-comp #'square #'square #'square)
(<- [?x :> ?result]
    (square ?x :> ?temp1)
          (square ?temp1 :> ?temp2)
                    (square ?temp2 :> ?result))

(our-comp #'inc #'dec)
(<- [?x :> ?result]
    (dec ?x :> ?temp1)
    (inc ?temp1 :> ?result))

(construct ["?x" :> "?result"]
           [[#'dec "?x" :> "?temp1"]
            [#'inc "?temp1" :> "?result"]])


(our-comp #'inc #'dec)

(our-comp #'inc #'square #'dec)
(<- [?x :> ?result]
    (dec ?x :> ?temp1)
    (square ?temp1 :> ?temp2)
    (inc ?temp2 :> ?result))

(defn our-comp [& ops]
  (let [opvars (gen-nullable-vars (inc (count ops)))]
    (construct [(first opvars) :> (last opvars)]
               (map (fn [op [invar outvar]]
                      [op invar :> outvar])
                    (reverse ops)
                    (partition 2 1 opvars)))))

(def inc-inc (our-comp #'inc #'inc))

(comment
  (?<- (stdout)
       [?y]
       (integer ?x)
       (inc-inc ?x :> ?y)))

(defn our-comp [& ops]
  (let [[invar :as allvars] (v/gen-nullable-vars (inc (count ops)))]
    (construct [invar :> (last more)]
               (map (fn [o [in out]]
                      [o in :> out])
                    (reverse ops)
                    (partition 2 1 allvars)))))

(defn run-op [op]
  (<- [?y]
      ([[8]] ?x)
      (op ?x :> ?y)))
