@echo off
REM run.bat — launch StudyAI GUI

REM Run the Jar (quotes protect the spaces)
java -jar "%~dp0target\StudyAI-1.0-SNAPSHOT.jar"

REM Keep the window open so you can read any messages
echo.
pause
