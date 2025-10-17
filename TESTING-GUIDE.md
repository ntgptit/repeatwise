# 🧪 RepeatWise - Docker Testing Guide

Complete guide for testing the Docker setup when Docker is installed.

## 📋 Prerequisites Checklist

Before testing, ensure you have:

- [ ] **Docker Desktop installed** ([Download here](https://www.docker.com/products/docker-desktop))
- [ ] Docker Desktop is **running**
- [ ] At least **4GB RAM** allocated to Docker
- [ ] Ports **3000, 8080, 5432** are free
- [ ] **Git Bash** or PowerShell (Windows) / Terminal (Mac/Linux)

### Verify Docker Installation

```bash
# Check Docker is installed and running
docker --version
docker-compose --version

# Should output something like:
# Docker version 24.x.x
# Docker Compose version 2.x.x
```

---

## 🧪 Test Plan

### Phase 1: Pre-Flight Checks ✅

#### 1.1 Verify File Structure

```bash
# Navigate to project root
cd d:\workspace\repeatwise

# Check all Docker files exist
ls -la docker-compose.yml
ls -la backend-api/Dockerfile
ls -la frontend-web/Dockerfile
ls -la frontend-web/nginx.conf
ls -la .env.example
```

**Expected**: All files should exist

#### 1.2 Create Environment File

```bash
# Copy environment template
cp .env.example .env

# Verify .env exists
cat .env
```

**Expected**: `.env` file created with default values

#### 1.3 Validate Docker Compose Syntax

```bash
# Validate docker-compose.yml syntax
docker-compose config

# If successful, it will output the resolved configuration
```

**Expected**: No syntax errors

---

### Phase 2: Individual Service Builds 🏗️

#### 2.1 Build Backend Image

```bash
# Build backend Docker image
cd backend-api
docker build -t repeatwise-backend:test .

# Expected output:
# [+] Building XXXs
# => [build 1/5] FROM maven:3.9-eclipse-temurin-17-alpine
# => [build 2/5] WORKDIR /app
# => [build 3/5] COPY pom.xml .
# => [build 4/5] RUN mvn dependency:go-offline -B
# => [build 5/5] COPY src ./src
# => [build 6/5] RUN mvn clean package -DskipTests -B
# => [stage-1 1/6] FROM eclipse-temurin:17-jre-alpine
# => [stage-1 2/6] WORKDIR /app
# => [stage-1 3/6] RUN apk add --no-cache wget
# => [stage-1 4/6] RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring
# => [stage-1 5/6] COPY --from=build /app/target/*.jar app.jar
# => [stage-1 6/6] RUN chown -R spring:spring /app
# => exporting to image
# => => naming to docker.io/library/repeatwise-backend:test
```

**Success Criteria**:
- ✅ Build completes without errors
- ✅ Final image is created (~250MB)
- ✅ Maven build succeeds
- ✅ JAR file is packaged

**Common Issues**:
- ❌ Maven dependency download fails → Check internet connection
- ❌ Compilation errors → Check Java source code
- ❌ Out of memory → Increase Docker memory allocation

#### 2.2 Build Frontend Web Image

```bash
# Build frontend web Docker image
cd ../frontend-web
docker build -t repeatwise-web:test .

# Expected output:
# [+] Building XXXs
# => [build 1/5] FROM node:20-alpine
# => [build 2/5] WORKDIR /app
# => [build 3/5] COPY package*.json ./
# => [build 4/5] RUN npm ci --only=production=false
# => [build 5/5] COPY . .
# => [build 6/5] RUN npm run build
# => [stage-1 1/5] FROM nginx:alpine
# => [stage-1 2/5] WORKDIR /usr/share/nginx/html
# => [stage-1 3/5] RUN rm -rf ./*
# => [stage-1 4/5] COPY --from=build /app/dist .
# => [stage-1 5/5] COPY nginx.conf /etc/nginx/conf.d/default.conf
# => exporting to image
# => => naming to docker.io/library/repeatwise-web:test
```

**Success Criteria**:
- ✅ Build completes without errors
- ✅ Final image is created (~50MB)
- ✅ NPM install succeeds
- ✅ Vite build succeeds
- ✅ dist/ folder is created and copied

**Common Issues**:
- ❌ NPM install fails → Check package.json
- ❌ Build errors → Check TypeScript/React code
- ❌ Missing dist/ → Check Vite build output

#### 2.3 Verify Built Images

```bash
# List Docker images
docker images | grep repeatwise

# Expected output:
# repeatwise-backend   test   XXXXXXXXXX   X minutes ago   ~250MB
# repeatwise-web       test   XXXXXXXXXX   X minutes ago   ~50MB
```

---

### Phase 3: Full Stack Test 🚀

#### 3.1 Start All Services

```bash
# Navigate back to project root
cd d:\workspace\repeatwise

# Method 1: Using script (Recommended)
# Windows:
start.bat

# Linux/Mac:
chmod +x start.sh
./start.sh

# Method 2: Direct docker-compose
docker-compose up -d
```

**Expected Output**:
```
Creating network "repeatwise_repeatwise-network" ... done
Creating volume "repeatwise_postgres_data" ... done
Creating repeatwise-db ... done
Creating repeatwise-backend ... done
Creating repeatwise-web ... done
```

#### 3.2 Monitor Service Startup

```bash
# Watch all logs
docker-compose logs -f

# Watch specific service
docker-compose logs -f backend
docker-compose logs -f web
docker-compose logs -f db
```

**What to Look For**:

**Database (db)**:
```
repeatwise-db | PostgreSQL init process complete; ready for start up.
repeatwise-db | LOG:  database system is ready to accept connections
```

**Backend (backend)**:
```
repeatwise-backend | Started RepeatWiseApplication in X.XXX seconds
repeatwise-backend | Tomcat started on port(s): 8080 (http)
```

**Web (web)**:
```
repeatwise-web | /docker-entrypoint.sh: Configuration complete; ready for start up
```

#### 3.3 Check Service Health

```bash
# Check all containers are running
docker-compose ps

# Expected output:
# NAME                  COMMAND                  SERVICE   STATUS          PORTS
# repeatwise-backend    "java -jar app.jar"      backend   Up (healthy)    0.0.0.0:8080->8080/tcp
# repeatwise-db         "docker-entrypoint.s…"   db        Up (healthy)    0.0.0.0:5432->5432/tcp
# repeatwise-web        "/docker-entrypoint.…"   web       Up (healthy)    0.0.0.0:3000->80/tcp
```

**Success Criteria**:
- ✅ All 3 containers show "Up"
- ✅ All 3 containers show "(healthy)"
- ✅ Ports are correctly mapped

**Common Issues**:
- ❌ Container exits immediately → Check logs: `docker-compose logs <service>`
- ❌ "unhealthy" status → Check health check endpoint
- ❌ Port conflicts → Change ports in docker-compose.yml

---

### Phase 4: Connectivity Tests 🔗

#### 4.1 Test Database Connection

```bash
# Connect to PostgreSQL
docker-compose exec db psql -U postgres -d repeatwise

# Should open psql prompt:
# psql (16.x)
# Type "help" for help.
# repeatwise=#

# Run test query
\dt

# List databases
\l

# Exit
\q
```

**Success Criteria**:
- ✅ Can connect to database
- ✅ Database "repeatwise" exists
- ✅ Tables are created (if Flyway ran)

#### 4.2 Test Backend API

```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}

# Or with formatting:
curl http://localhost:8080/actuator/health | json_pp
```

**Test in Browser**:
- Open: http://localhost:8080/actuator/health
- Open: http://localhost:8080/swagger-ui.html

**Success Criteria**:
- ✅ Health endpoint returns `{"status":"UP"}`
- ✅ Swagger UI loads
- ✅ API documentation is visible

#### 4.3 Test Frontend Web

```bash
# Test nginx is serving
curl http://localhost:3000

# Should return HTML content starting with:
# <!DOCTYPE html>
# <html lang="en">
```

**Test in Browser**:
- Open: http://localhost:3000
- Should see React app

**Success Criteria**:
- ✅ Web page loads
- ✅ No console errors (F12 → Console)
- ✅ React app renders

#### 4.4 Test API Proxy (Nginx → Backend)

```bash
# Test API through nginx proxy
curl http://localhost:3000/api/actuator/health

# Should return same as direct backend:
# {"status":"UP"}
```

**Success Criteria**:
- ✅ Nginx proxies /api requests to backend
- ✅ Same response as direct backend call

---

### Phase 5: Integration Tests 🎯

#### 5.1 Test Network Communication

```bash
# From web container, ping backend
docker-compose exec web ping -c 3 backend

# From backend container, ping db
docker-compose exec backend ping -c 3 db
```

**Success Criteria**:
- ✅ Web can reach backend
- ✅ Backend can reach db
- ✅ All pings succeed

#### 5.2 Test Data Persistence

```bash
# Create test data in database
docker-compose exec db psql -U postgres -d repeatwise -c "CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(100));"

# Insert data
docker-compose exec db psql -U postgres -d repeatwise -c "INSERT INTO test_table (name) VALUES ('test');"

# Query data
docker-compose exec db psql -U postgres -d repeatwise -c "SELECT * FROM test_table;"

# Restart database
docker-compose restart db

# Wait for restart
sleep 10

# Check data still exists
docker-compose exec db psql -U postgres -d repeatwise -c "SELECT * FROM test_table;"
```

**Success Criteria**:
- ✅ Data persists after restart
- ✅ Volume is working correctly

#### 5.3 Test Service Dependencies

```bash
# Restart backend (should wait for db)
docker-compose restart backend

# Watch logs
docker-compose logs -f backend

# Should see:
# Waiting for database...
# Database is ready
# Starting application...
```

**Success Criteria**:
- ✅ Backend waits for db health check
- ✅ Backend starts after db is ready

---

### Phase 6: Performance Tests ⚡

#### 6.1 Check Resource Usage

```bash
# Check container resource usage
docker stats

# Output shows:
# CONTAINER           CPU %   MEM USAGE / LIMIT     MEM %   NET I/O
# repeatwise-backend  0.5%    512MiB / 4GiB        12.5%   1.2kB / 850B
# repeatwise-web      0.1%    50MiB / 4GiB         1.25%   850B / 1.2kB
# repeatwise-db       0.3%    100MiB / 4GiB        2.5%    2kB / 1.5kB
```

**Success Criteria**:
- ✅ Backend uses < 1GB RAM
- ✅ Web uses < 100MB RAM
- ✅ Database uses < 500MB RAM
- ✅ Total < 2GB RAM

#### 6.2 Test Response Times

```bash
# Backend response time
time curl http://localhost:8080/actuator/health

# Web response time
time curl http://localhost:3000

# API proxy response time
time curl http://localhost:3000/api/actuator/health
```

**Success Criteria**:
- ✅ Health check < 100ms
- ✅ Web page < 200ms
- ✅ API proxy < 150ms

---

### Phase 7: Rebuild & Cleanup Tests 🧹

#### 7.1 Test Rebuild

```bash
# Rebuild all services
# Windows:
rebuild.bat

# Linux/Mac:
./rebuild.sh

# Or manually:
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

**Success Criteria**:
- ✅ All images rebuild successfully
- ✅ Services start correctly
- ✅ No errors in logs

#### 7.2 Test Stop/Start

```bash
# Stop all services
# Windows:
stop.bat

# Linux/Mac:
./stop.sh

# Or manually:
docker-compose down

# Check all stopped
docker-compose ps

# Start again
start.bat  # or ./start.sh
```

**Success Criteria**:
- ✅ All services stop cleanly
- ✅ All services restart successfully
- ✅ Data persists

#### 7.3 Test Complete Cleanup

```bash
# Stop and remove volumes (WARNING: deletes database)
docker-compose down -v

# Remove images
docker-compose down --rmi all

# Clean up Docker system
docker system prune -a

# Verify everything is removed
docker images | grep repeatwise
docker volume ls | grep repeatwise
docker network ls | grep repeatwise
```

**Success Criteria**:
- ✅ All containers removed
- ✅ All volumes removed
- ✅ All images removed
- ✅ Networks cleaned up

---

## 📱 Mobile App Connection Test

### Setup Mobile Environment

1. **Copy environment file**:
   ```bash
   cp frontend-mobile/.env.example frontend-mobile/.env
   ```

2. **Update API URL** based on platform:

   **Android Emulator**:
   ```env
   API_BASE_URL=http://10.0.2.2:8080/api
   ```

   **iOS Simulator**:
   ```env
   API_BASE_URL=http://localhost:8080/api
   ```

   **Physical Device**:
   ```bash
   # Find your IP first
   ipconfig  # Windows
   ifconfig  # Mac/Linux

   # Then use in .env:
   API_BASE_URL=http://192.168.1.100:8080/api
   ```

3. **Start mobile app**:
   ```bash
   cd frontend-mobile
   npm start

   # In another terminal:
   npm run android  # or npm run ios
   ```

4. **Test connection**:
   - Open mobile app
   - Should connect to backend
   - Check network requests in logs

**Success Criteria**:
- ✅ Mobile app connects to backend
- ✅ API calls succeed
- ✅ Data loads correctly

---

## 📊 Test Results Checklist

### ✅ Pre-Flight
- [ ] Docker installed and running
- [ ] All files exist
- [ ] .env file created
- [ ] docker-compose.yml valid

### ✅ Build Tests
- [ ] Backend image builds successfully
- [ ] Web image builds successfully
- [ ] Image sizes reasonable (<300MB total)

### ✅ Stack Tests
- [ ] All 3 services start
- [ ] All services show "healthy"
- [ ] No errors in logs
- [ ] Ports correctly mapped

### ✅ Connectivity Tests
- [ ] Database accessible
- [ ] Backend API responds
- [ ] Web page loads
- [ ] API proxy works
- [ ] Swagger UI accessible

### ✅ Integration Tests
- [ ] Network communication works
- [ ] Data persists across restarts
- [ ] Dependencies work correctly

### ✅ Performance Tests
- [ ] Resource usage acceptable
- [ ] Response times good
- [ ] No memory leaks

### ✅ Cleanup Tests
- [ ] Rebuild works
- [ ] Stop/start works
- [ ] Complete cleanup works

### ✅ Mobile Tests (if applicable)
- [ ] Mobile connects to backend
- [ ] API calls work
- [ ] Data syncs

---

## 🐛 Troubleshooting

### Common Issues & Solutions

#### Port Already in Use
```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID <pid> /F

# Linux/Mac
lsof -ti:3000 | xargs kill -9
```

#### Container Won't Start
```bash
# Check logs
docker-compose logs <service>

# Check with detailed output
docker-compose up <service>
```

#### Database Connection Failed
```bash
# Check DB is running
docker-compose ps db

# Test connection
docker-compose exec db pg_isready -U postgres

# Check backend environment
docker-compose exec backend env | grep DATABASE
```

#### Out of Memory
```bash
# Check Docker settings
docker system info | grep Memory

# Increase in Docker Desktop:
# Settings → Resources → Memory → 4GB+
```

---

## 📈 Expected Test Duration

| Phase | Duration | Description |
|-------|----------|-------------|
| Pre-Flight | 2 min | File checks, validation |
| Build Tests | 10-15 min | First build (downloads deps) |
| Stack Tests | 2-3 min | Start all services |
| Connectivity | 5 min | Test all endpoints |
| Integration | 5 min | Network, persistence tests |
| Performance | 2 min | Resource checks |
| Cleanup | 2 min | Rebuild, cleanup tests |
| **Total** | **25-35 min** | Complete test suite |

*Subsequent builds will be faster due to Docker layer caching*

---

## ✅ Success Criteria Summary

All tests pass if:

1. ✅ All Docker images build without errors
2. ✅ All 3 services start and show "healthy"
3. ✅ Database is accessible and data persists
4. ✅ Backend API responds on port 8080
5. ✅ Web app loads on port 3000
6. ✅ Nginx proxies /api to backend correctly
7. ✅ Swagger UI is accessible
8. ✅ Resource usage is reasonable
9. ✅ No errors in any service logs
10. ✅ Mobile app can connect (if tested)

---

**Next Steps After Testing**:

1. If all tests pass → Start developing features! 🚀
2. If issues found → Check [DOCKER-SETUP.md](DOCKER-SETUP.md) troubleshooting
3. Document any issues → Create GitHub issues

---

**Happy Testing!** 🎉
