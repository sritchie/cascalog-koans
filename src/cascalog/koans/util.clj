(ns cascalog.koans.util
  (:use [hadoop-util.core :only (path)]))

(defn parse-str [^String s]
  (seq (.split s "\t")))

(defn longify-all [& strs]
  (map #(Long/parseLong %) strs))

(defn log2 [val]
  (/ (double (Math/log val)) (Math/log 2)))

(defn compact [coll]
  (filter (complement nil?) coll))

(defn binary-rep
  "
2 -> [1 0]
3 -> [1 1]
4 -> [1 0 0]
5 -> [1 0 1]
"
  [anum]
  (for [char (Integer/toBinaryString anum)]
    (if (= char \1) 1 0)))

;; ## Test Dataset Helpers

(def dev-resources-subdir "dev")

(defn get-current-directory
  "Returns the current project directory."
  []
  (.getCanonicalPath (java.io.File. ".")))

(defn project-path
  "Accepts a sub-path within the current project structure and returns
  a fully qualified system path. For example:

    (project-path \"/dev/file.txt\")
    ;=> \"/home/sritchie/myproject/dev/file.txt\""
  ([] (project-path ""))
  ([sub-path]
     (str (path (get-current-directory) sub-path))))

(defn dev-path
  ([] (project-path dev-resources-subdir))
  ([sub-path]
     (project-path (str (path dev-resources-subdir sub-path)))))
