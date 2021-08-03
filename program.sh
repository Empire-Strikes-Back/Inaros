#!/bin/bash 

repl(){
  clj \
    -X:repl deps-repl.core/process \
    :main-ns surveyhub.main \
    :port 7788 \
    :host '"0.0.0.0"' \
    :repl? true \
    :nrepl? false
}

main(){
  clojure \
    -J-Dclojure.core.async.pool-size=1 \
    -J-Dclojure.compiler.direct-linking=false \
    -M -m surveyhub.main
}

uberjar(){
  clj \
    -X:uberjar genie.core/process \
    :uberjar-name out/surveyhub.standalone.jar \
    :main-ns surveyhub.main
  mkdir -p out/jpackage-input
  mv out/surveyhub.standalone.jar out/jpackage-input/
}

"$@"