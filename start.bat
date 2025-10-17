@echo off
REM RepeatWise - Docker Start Script for Windows
REM This script starts all services using docker-compose

echo 🚀 Starting RepeatWise...
echo.

REM Check if .env exists, if not copy from example
if not exist .env (
    echo 📋 Creating .env file from .env.example...
    copy .env.example .env
    echo ⚠️  Please update .env with your configuration!
    echo.
)

REM Start services
echo 🐳 Starting Docker containers...
docker-compose up -d

echo.
echo ⏳ Waiting for services to be healthy...
timeout /t 30 /nobreak >nul

echo.
echo ✅ All services started successfully!
echo.
echo 📱 Services URLs:
echo    - Web App:    http://localhost:3000
echo    - Backend API: http://localhost:8080
echo    - Swagger UI:  http://localhost:8080/swagger-ui.html
echo    - PostgreSQL:  localhost:5432
echo.
echo 📊 View logs: docker-compose logs -f
echo 🛑 Stop all:  docker-compose down
echo.

pause
