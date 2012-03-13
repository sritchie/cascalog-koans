(use 'cascalog.api
     '[midje sweet cascalog]
     '[cascalog.koans.util :only (dev-path)]
     '[cascalog.testing :only (test?-)]
     '[jackknife.def :only (defalias)])

(defalias ?= test?-)
