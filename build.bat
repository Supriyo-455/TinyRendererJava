@echo off

javac -d build src\Main.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)

if not exist rendered (
    mkdir rendered
)

java -cp build src.Main