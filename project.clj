(defproject cascalog-koans "1.0.0"
  :description "Koans for Cascalog."
  :dev-resources "dev"
  :koan {:koan-root "koans"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [cascalog "1.9.0-wip7"]
                 [simpledb "0.1.4"]
                 [clj-time "0.3.7"]
                 [elephantdb "0.2.0-wip1"
                  :exclusions [org.slf4j/slf4j-api]]
                 [cascalog-elephantdb "0.3.1"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]
                     [lein-koan "0.1.0"]])
