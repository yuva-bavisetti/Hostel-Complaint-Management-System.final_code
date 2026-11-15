@echo off
setlocal
cd /d "%~dp0"
echo ===========================================
echo  HOSTEL COMPLAINT MANAGEMENT - LOCAL DEMO
echo ===========================================
echo.
where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java Development Kit not found. Install a JDK and check PATH.
    pause
    exit /b 1
)
if not exist out mkdir out
echo Compiling the Java source files...
javac -encoding UTF-8 -d out *.java GUI\*.java
if errorlevel 1 (
    echo.
    echo COMPILATION FAILED. Copy or screenshot the errors above.
    pause
    exit /b 1
)
echo.
echo Opening the login screen...
java -cp out complaint_main
if errorlevel 1 (
    echo.
    echo The app closed with an error. See the message above.
    pause
    exit /b 1
)
endlocal
