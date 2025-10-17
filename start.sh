#!/bin/bash

# RepeatWise - Docker Start Script
# This script starts all services using docker-compose

set -e

echo "🚀 Starting RepeatWise..."
echo ""

# Check if .env exists, if not copy from example
if [ ! -f .env ]; then
    echo "📋 Creating .env file from .env.example..."
    cp .env.example .env
    echo "⚠️  Please update .env with your configuration!"
    echo ""
fi

# Start services
echo "🐳 Starting Docker containers..."
docker-compose up -d

echo ""
echo "⏳ Waiting for services to be healthy..."
echo ""

# Wait for backend to be healthy
echo "Waiting for backend..."
timeout=60
counter=0
until docker-compose ps | grep repeatwise-backend | grep -q "healthy" || [ $counter -eq $timeout ]; do
    printf "."
    sleep 2
    counter=$((counter + 2))
done

if [ $counter -eq $timeout ]; then
    echo ""
    echo "❌ Backend failed to start. Check logs with: docker-compose logs backend"
    exit 1
fi

echo ""
echo "✅ All services started successfully!"
echo ""
echo "📱 Services URLs:"
echo "   - Web App:    http://localhost:3000"
echo "   - Backend API: http://localhost:8080"
echo "   - Swagger UI:  http://localhost:8080/swagger-ui.html"
echo "   - PostgreSQL:  localhost:5432"
echo ""
echo "📊 View logs: docker-compose logs -f"
echo "🛑 Stop all:  docker-compose down"
echo ""
