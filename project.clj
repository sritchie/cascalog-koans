(defproject cascalog-koans "1.0.0"
  :description "Koans for Cascalog."
  :dev-resources "dev"
  :koan {:koan-root "koans"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [cascalog "1.8.6"]
                 [simpledb "0.1.4"]
                 [clj-time "0.3.7"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]
                     [lein-koan "0.1.0"]])
