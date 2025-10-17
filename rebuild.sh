#!/bin/bash

# RepeatWise - Rebuild Script
# This script rebuilds all Docker images and restarts services

set -e

echo "🔨 Rebuilding RepeatWise..."
echo ""

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose down

# Rebuild images
echo "🏗️  Rebuilding Docker images..."
docker-compose build --no-cache

# Start services
echo "🚀 Starting services..."
docker-compose up -d

echo ""
echo "✅ Rebuild complete!"
echo ""
echo "📊 View logs: docker-compose logs -f"
echo ""
