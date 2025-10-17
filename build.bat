@echo off
rem ==== Compile all Java source files ====
javac -d build src\Main.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)

rem ==== Run the program ====
java -cp build src.Main