(defproject loopme/debug "0.1.0"
            :description "Loopme debug library."
            :license {:name "MIT license"
                      :url "http://opensource.org/licenses/MIT"}
            :source-paths ["src"]
            :java-source-paths ["src"]
            :test-paths ["test"]
            :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
            :dependencies [[org.clojure/clojure "1.5.1"]
                           [io.aviso/pretty "0.1.8"]]
            :plugins [[s3-wagon-private "1.1.2"]]
            :repositories [["loopme" {:url           "s3p://lm-artifacts/releases/"
                                      :username :env :passphrase :env
                                      :sign-releases false}]])
