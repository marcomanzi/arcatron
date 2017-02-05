(ns user
  (:require [mount.core :as mount]
            [arcatron.figwheel :refer [start-fw stop-fw cljs]]
            arcatron.core))

(defn start []
  (mount/start-without #'arcatron.core/http-server
                       #'arcatron.core/repl-server))

(defn stop []
  (mount/stop-except #'arcatron.core/http-server
                     #'arcatron.core/repl-server))

(defn restart []
  (stop)
  (start))


