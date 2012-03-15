(ns cascalog.koans.datastore
  (:use cascalog.api
        cascalog.koans.tap
        [cascalog.koans.util :only (dev-path)]))

;; ## Playground datasets
;;
;; ##  Moving Average stock data -- yahoo_stock:
;;
;; The following tap produces stock price data from Yahoo!

(def stock-tap
  (hfs-delimited (dev-path "yahoo_stock_AA_32_mini.csv")
                 :delimiter ","
                 :outfields ["?exchange" "?stock-sym" "?date" "?open"
                             "?high" "?low" "?close" "?volume" "?adj"]
                 :classes [String String String Float Float Float Float Integer Float]
                 :skip-header? true))

;; ## Log Data
;;
;; The following tap produces Apache log data.

(def log-regex
  #"^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] \"(.+?)\" (\S+) (\S+) \"([^\"]*)\" \"([^\"]*)\"")

(defn parse-log-str
  [log-str]
  (rest (re-find log-regex log-str)))

(def small-log-tap
  (let [src (hfs-textline (dev-path "logdata/access_log_1"))
        log-fields ["?remote-addr" "?remote-logname" "?user" "?time"
                    "?request" "?status" "?bytes_string"
                    "?referrer" "?browser"]]
    (<- log-fields
        (src ?line)
        (parse-log-str ?line :>> log-fields)
        (:distinct false))))

;; ## Timeseries dataset

(def timeseries-src
  (into [] (for [region ["IDN" "IND" "BRA" "USA"]
                 period (range 20)
                 :let [val (rand)]]
             [region period val])))
