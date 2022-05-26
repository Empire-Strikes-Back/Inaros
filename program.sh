#!/bin/bash


install(){
  npm i --no-package-lock
  mkdir -p out/jar/ui/
  mkdir -p out/jar/src/Inaros/
  cp src/Inaros/index.html out/jar/ui/index.html
  cp src/Inaros/style.css out/jar/ui/style.css
  cp src/Inaros/schema.edn out/jar/src/Inaros/schema.edn
  cp package.json out/jar/package.json
}

Moana(){
  clj -A:Moana:main:ui -M -m shadow.cljs.devtools.cli "$@"
}

repl(){
  install
  Moana clj-repl
  # (shadow/watch :main)
  # (shadow/watch :ui)
  # (shadow/repl :main)
  # :repl/quit
}

jar(){
  rm -rf out
  install
  Moana release :main :ui
  COMMIT_HASH=$(git rev-parse --short HEAD)
  COMMIT_COUNT=$(git rev-list --count HEAD)
  echo Inaros-$COMMIT_COUNT-$COMMIT_HASH.zip
  cd out/jar
  zip -r ../Inaros-$COMMIT_COUNT-$COMMIT_HASH.zip ./ && \
  cd ../../
}

release(){
  jar
}


tag(){
  COMMIT_HASH=$(git rev-parse --short HEAD)
  COMMIT_COUNT=$(git rev-list --count HEAD)
  TAG="$COMMIT_COUNT-$COMMIT_HASH"
  git tag $TAG $COMMIT_HASH
  echo $COMMIT_HASH
  echo $TAG
}

"$@"