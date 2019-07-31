(defproject piaojuocr "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories [["vendredi" "https://repository.hellonico.info/repository/hellonico/"]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.baidu.aip/java-sdk "4.11.3"] ; baidu ocr sdk
                 [origami "4.0.1-1" :exclusions [opencv/opencv-native hellonico/gorilla-repl]]
                 [seesaw "1.5.0"] ; swing GUI
                 [camel-snake-kebab/camel-snake-kebab "0.4.0"]
                 [com.taoensso/timbre "4.10.0"] ; logging
                                        ; uncomment to use only the binary for your platform
                                        ;[origami "4.0.0-1" :exclusions [opencv/opencv-native]]
                                        ;[opencv/opencv-native "4.0.0-1" :classifier "osx_64"]
                 [opencv/opencv-native "4.0.1-0" :classifier "linux_64"]
                 [opencv/opencv-native "4.0.1-0" :classifier "windows_64"]
                 ]
  :omit-source true
  :main ^:skip-aot piaojuocr.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
