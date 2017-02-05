(ns arcatron.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [arcatron.core-test]))

(doo-tests 'arcatron.core-test)

