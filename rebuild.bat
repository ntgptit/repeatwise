@echo off
REM RepeatWise - Rebuild Script for Windows
REM This script rebuilds all Docker images and restarts services

echo 🔨 Rebuilding RepeatWise...
echo.

REM Stop existing containers
echo 🛑 Stopping existing containers...
docker-compose down

REM Rebuild images
echo 🏗️  Rebuilding Docker images...
docker-compose build --no-cache

REM Start services
echo 🚀 Starting services...
docker-compose up -d

echo.
echo ✅ Rebuild complete!
echo.
echo 📊 View logs: docker-compose logs -f
echo.

pause
