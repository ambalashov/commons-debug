(ns loopme.debug.logger
  (:refer-clojure :exclude [replace])
  (:use [clojure.string :only [replace]
         [io.aviso ansi]])
  (:import [loopme.debug Logger]
           [java.util.regex Pattern]))

(def ^:private debug?
  (let [env-name "APP_DEBUG"]
    (-> (Pattern/compile "(y|yes|true|on)" Pattern/CASE_INSENSITIVE)
        (.matcher (or (System/getenv env-name) (System/getProperty env-name)))
        (.matches))))

(defn ^:private debug?)

(defn bg [text]
  (blue-bg (str bold-white-font " " text " " reset-font )))

(defn debug [data-name data-value]
  (if debug?
    (Logger/print
      (str
        (bg data-name)
        " => "
        (replace (str data-value) "nil" (bold-red " nil "))))))

(defn- lazy-seq-to-str [s]
  (reduce
    (fn [x y] (str x ", " y " (" (class y) ")"))
    s))

(defn debug-sql [data-name sql params]
  (if debug-sql?
    (debug data-name (str sql "[" (lazy-seq-to-str params) "]"))))

(def braker
  (bold-white-bg (str bold-white-font " " (apply str (repeat 150 "=")) " " reset-font)))

(defn debug-braker-start []
  (if debug?
    (do
      (Logger/print)
      (Logger/print braker))))

(defn debug-braker-end []
  (if debug?
    (do
      (Logger/print braker)
      (Logger/end))))

(defn debug-status []
  (if debug?
    (println (str (bg "DEBUG MODE:") " => " (bold-green-bg " ON ")))
    (println (str (bg "DEBUG MODE:") " => " (bold-red-bg " OFF ")))))

(defmacro mtime
  "Evaluates expr and prints the time it took.  Returns the value of expr."
  [msg expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr
         msg# ~msg]
     (prn (str msg# "=> " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
     ret#))
