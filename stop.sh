#!/bin/bash

# RepeatWise - Docker Stop Script

set -e

echo "🛑 Stopping RepeatWise..."
docker-compose down

echo ""
echo "✅ All services stopped."
echo ""
echo "To remove volumes (delete database): docker-compose down -v"
echo ""
