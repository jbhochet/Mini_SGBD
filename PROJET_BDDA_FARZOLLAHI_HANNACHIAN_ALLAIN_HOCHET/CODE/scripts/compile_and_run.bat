@echo off
:: The script directory
setlocal enabledelayedexpansion
for %%i in (%~dp0) do set "SCRIPT_DIR=%%~fi"

:: Configuration
set "JAVA_VERSION=17"
set "SRC_DIR=..\src"
set "BIN_DIR=..\bin"
set "MAIN_CLASS=main.Main"

:: Additional Argument
set "ARGUMENT=../DB"

:: Clear bin directory
rmdir /s /q "%BIN_DIR%"
mkdir "%BIN_DIR%"

:: Compile
javac --release !JAVA_VERSION! --source-path "%SRC_DIR%" -d "%BIN_DIR%" "%SRC_DIR%\main\Main.java"

:: Run with Argument
java -cp "%BIN_DIR%" %MAIN_CLASS% "%ARGUMENT%"
