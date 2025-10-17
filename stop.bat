@echo off
REM RepeatWise - Docker Stop Script for Windows

echo 🛑 Stopping RepeatWise...
docker-compose down

echo.
echo ✅ All services stopped.
echo.
echo To remove volumes (delete database): docker-compose down -v
echo.

pause
