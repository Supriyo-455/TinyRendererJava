#!/bin/bash
# ==== Compile all Java source files ====
mkdir -p build

javac -d build src/Main.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit $?
fi

# ==== Run the program ====
java -cp build src.Main
