#!/usr/bin/env bash

SRC_DIR=`dirname "$0"`
if [ "$SRC_DIR" != "." ]; then
  pushd "$SRC_DIR"
fi

mvn package -DdockerRegistry=
mvn dockerfile:push@latest -DdockerRegistry= -DdockerUser= -DdockerPassword=
mvn dockerfile:push@current -DdockerRegistry= -DdockerUser= -DdockerPassword=

if [ "$SRC_DIR" != "." ]; then
  popd
fi
