#!/bin/bash

mkdir -p build

javac -d build src/Main.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit $?
fi

java -cp build src.Main
