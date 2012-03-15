(ns cascalog.koans.tap
  (:use cascalog.api
        [cascalog.workflow :only (fields)]
        [cascalog.io :only (with-fs-tmp)])
  (:require [cascalog.tap :as tap]
            [cascalog.vars :as v])
  (:import [cascading.scheme.hadoop TextDelimited]
           [cascading.tuple Fields]))

(defn delimited
  [field-seq delim & {:keys [classes skip-header?]}]
  (let [skip-header? (boolean skip-header?)
        field-seq    (fields field-seq)
        field-seq    (if (and classes (not (.isDefined field-seq)))
                       (fields (v/gen-nullable-vars (count classes)))
                       field-seq)]
    (if classes
      (TextDelimited. field-seq skip-header? delim (into-array classes))
      (TextDelimited. field-seq skip-header? delim))))

(defn hfs-delimited
  "Creates a tap on HDFS using the TextDelimited scheme. Different
   filesystems can be selected by using different prefixes for `path`.

  Supports keyword option for `:outfields`, `:classes` and
  `:skip-header?`. See `cascalog.tap/hfs-tap` for more keyword
  arguments.

   See http://www.cascading.org/javadoc/cascading/tap/Hfs.html and
   http://www.cascading.org/javadoc/cascading/scheme/TextDelimited.html"
  [path & opts]
  (let [{:keys [outfields delimiter]} (apply array-map opts)
        scheme (apply delimited
                      (or outfields Fields/ALL)
                      (or delimiter "\t")
                      opts)]
    (apply tap/hfs-tap scheme path opts)))
