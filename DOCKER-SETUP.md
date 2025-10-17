# 🐳 RepeatWise Docker Setup Guide

Complete guide for running RepeatWise using Docker.

## 📋 Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop) (includes Docker & Docker Compose)
- At least 4GB RAM available for Docker
- Ports 3000, 8080, 5432 should be free

## 🚀 Quick Start

### Windows
```bash
# Start all services
start.bat

# Stop all services
stop.bat

# Rebuild from scratch
rebuild.bat
```

### Linux/Mac
```bash
# Make scripts executable (first time only)
chmod +x start.sh stop.sh rebuild.sh

# Start all services
./start.sh

# Stop all services
./stop.sh

# Rebuild from scratch
./rebuild.sh
```

## 📦 What Gets Started

The `start` script launches 3 services:

1. **PostgreSQL Database** (`db`)
   - Port: `5432`
   - Database: `repeatwise`
   - User: `postgres`
   - Password: `postgres`

2. **Backend API** (`backend`)
   - Port: `8080`
   - Spring Boot application
   - Connects to PostgreSQL
   - Auto-runs Flyway migrations

3. **Frontend Web** (`web`)
   - Port: `3000` (mapped to container port 80)
   - React + Vite app served by Nginx
   - Proxies `/api` requests to backend

## 🌐 Access URLs

After starting:

- **Web Application**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## ⚙️ Configuration

### Environment Variables

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and update values:
   ```env
   JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-32-chars
   DB_PASSWORD=your-strong-password
   ```

### Custom Ports

Edit `docker-compose.yml` to change ports:

```yaml
services:
  web:
    ports:
      - "3001:80"  # Change 3000 to 3001
  backend:
    ports:
      - "8081:8080"  # Change 8080 to 8081
```

## 🔧 Development Commands

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f web
docker-compose logs -f db
```

### Check Service Status

```bash
docker-compose ps
```

### Restart a Service

```bash
# Restart backend only
docker-compose restart backend

# Restart web only
docker-compose restart web
```

### Execute Commands Inside Container

```bash
# Access backend container
docker-compose exec backend sh

# Access database
docker-compose exec db psql -U postgres -d repeatwise

# Check backend logs
docker-compose exec backend cat /var/log/spring.log
```

## 🗄️ Database Management

### Access PostgreSQL CLI

```bash
docker-compose exec db psql -U postgres -d repeatwise
```

### Backup Database

```bash
docker-compose exec db pg_dump -U postgres repeatwise > backup.sql
```

### Restore Database

```bash
docker-compose exec -T db psql -U postgres repeatwise < backup.sql
```

### Reset Database (Delete All Data)

```bash
docker-compose down -v  # -v removes volumes
docker-compose up -d
```

## 🏗️ Building Images

### Build All Services

```bash
docker-compose build
```

### Build Specific Service

```bash
docker-compose build backend
docker-compose build web
```

### Build Without Cache (Clean Build)

```bash
docker-compose build --no-cache
```

## 🧹 Cleanup

### Stop and Remove Containers

```bash
docker-compose down
```

### Remove Containers + Volumes (Delete Database)

```bash
docker-compose down -v
```

### Remove Containers + Images

```bash
docker-compose down --rmi all
```

### Complete Cleanup

```bash
# Remove everything
docker-compose down -v --rmi all --remove-orphans

# Remove unused Docker resources
docker system prune -a
```

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Windows - Find process using port 3000
netstat -ano | findstr :3000
taskkill /PID <process_id> /F

# Linux/Mac - Find and kill process
lsof -ti:3000 | xargs kill -9
```

### Backend Won't Start

1. Check logs:
   ```bash
   docker-compose logs backend
   ```

2. Verify database is healthy:
   ```bash
   docker-compose ps db
   ```

3. Restart backend:
   ```bash
   docker-compose restart backend
   ```

### Web App Shows "Cannot connect to backend"

1. Check backend is running:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. Check nginx config:
   ```bash
   docker-compose exec web cat /etc/nginx/conf.d/default.conf
   ```

### Database Connection Failed

1. Check PostgreSQL is running:
   ```bash
   docker-compose ps db
   ```

2. Test connection:
   ```bash
   docker-compose exec db pg_isready -U postgres
   ```

3. Check credentials in `docker-compose.yml`

### Out of Memory

Increase Docker Desktop memory:
- Docker Desktop → Settings → Resources → Memory
- Increase to at least 4GB

## 📱 Mobile App Connection

For React Native mobile app to connect to backend:

### Android Emulator
```env
API_BASE_URL=http://10.0.2.2:8080/api
```

### iOS Simulator
```env
API_BASE_URL=http://localhost:8080/api
```

### Physical Device
```env
# Use your computer's IP address
API_BASE_URL=http://192.168.1.100:8080/api
```

Find your IP:
```bash
# Windows
ipconfig

# Linux/Mac
ifconfig
```

## 🔐 Security Notes

### Production Deployment

⚠️ **Before deploying to production:**

1. Change JWT secret in `.env`:
   ```env
   JWT_SECRET=use-a-strong-random-32-character-secret-here
   ```

2. Change database password:
   ```env
   DB_PASSWORD=use-a-strong-password-here
   ```

3. Update `docker-compose.yml`:
   ```yaml
   environment:
     SPRING_PROFILES_ACTIVE: prod
   ```

4. Enable HTTPS (use nginx with SSL or reverse proxy)

5. Don't expose database port externally:
   ```yaml
   db:
     # Remove or comment out ports
     # ports:
     #   - "5432:5432"
   ```

## 📊 Performance Tips

### Speed Up Builds

Use Docker BuildKit:

```bash
# Windows
set DOCKER_BUILDKIT=1
set COMPOSE_DOCKER_CLI_BUILD=1

# Linux/Mac
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1
```

### Reduce Image Size

Images are already optimized with:
- Multi-stage builds
- Alpine Linux base images
- .dockerignore files

## 🎯 Next Steps

1. ✅ Start services: `start.bat` or `./start.sh`
2. ✅ Access web app: http://localhost:3000
3. ✅ Check API docs: http://localhost:8080/swagger-ui.html
4. 🚀 Start developing!

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker)
- [Nginx Configuration](https://nginx.org/en/docs/)

---

**Questions or issues?** Check the logs with `docker-compose logs -f`
