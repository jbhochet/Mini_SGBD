
@echo off
setlocal

rem Configuration
set JAVA_VERSION=17
set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib
set MAIN_CLASS=main.Main
set DB_PATH=%~dp1

rem The script must work every time
cd /d "%~dp0\.."

rem Clear bin directory
rmdir /s /q "%BIN_DIR%" 2>nul
mkdir "%BIN_DIR%"

rem Compile
javac --release %JAVA_VERSION% --source-path %SRC_DIR% -cp "%LIB_DIR%\*" -d %BIN_DIR% %SRC_DIR%\*.java

rem Run tests
java -jar "%LIB_DIR%\junit-platform-console-standalone-1.9.3.jar" -cp %BIN_DIR% --scan-classpath

if %errorlevel% neq 0 (
    set /p continue=Unit test failure! Continue? (press enter for yes, C-c to exit)
)

rem Run
java -cp %BIN_DIR% %MAIN_CLASS% %DB_PATH%

endlocal
