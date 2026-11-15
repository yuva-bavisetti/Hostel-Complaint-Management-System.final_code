@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out *.java GUI\*.java tests\ProjectSelfTest.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
if not exist test_workspace mkdir test_workspace
pushd test_workspace
java -cp ..\out ProjectSelfTest
set "RESULT=%errorlevel%"
popd
echo.
pause
exit /b %RESULT%
