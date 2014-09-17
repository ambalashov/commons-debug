(ns loopme.debug.logger
  (:refer-clojure :exclude [replace])
  (:require [io.aviso.ansi :refer :all])
  (:use [clojure.string :only [replace]])
  (:import [loopme.debug Logger]
           [java.util.regex Pattern]))

(defn ^:private parse-env [env-name]
  (let [env (or (System/getenv env-name) (System/getProperty env-name) "")]
    (-> (Pattern/compile "(y|yes|true|on)" Pattern/CASE_INSENSITIVE)
        (.matcher env)
        (.matches))))

(def ^:private debug? (parse-env "APP_DEBUG"))
(def ^:private debug-sql? (parse-env "APP_DEBUG_SQL"))

(defn ^:private background [text]
  (blue-bg (str bold-white-font " " text " " reset-font )))

(defn ^:private lazy-seq-to-str [s]
  (reduce (fn [x y] (str x ", " y " (" (class y) ")")) nil s))

(def ^:private braker
  (bold-white-bg (str bold-white-font " " (apply str (repeat 150 "=")) " " reset-font)))

; =============================================================================
; API's
; =============================================================================

(defn debug [data-name data-value]
  (if debug? (do (Logger/print (background data-name))
                 (Logger/print " => ")
                 (Logger/println (replace (str data-value) "nil" (bold-red " nil "))))))

(defn debug-sql [data-name sql params]
  (if debug-sql?
    (debug data-name (str sql "[" (lazy-seq-to-str params) "]"))))

(defn debug-braker-start []
  (if debug? (do (Logger/println)
                 (Logger/println braker))))

(defn debug-braker-end []
  (if debug? (do (Logger/println braker)
                 (Logger/end))))

(defn debug-status []
  (println (str (background "DEBUG MODE:")
                " => "
                (if debug? (bold-green-bg "ON") (bold-red-bg "OFF")))))

(defmacro mtime
  "Evaluates expr and prints the time it took.  Returns the value of expr."
  [msg expr]
  `(let [start# (System/nanoTime)
         ret# ~expr
         msg# ~msg]
     (prn (str msg# "=> " (/ (double (- (System/nanoTime) start#)) 1000000.0) " msecs"))
     ret#))
