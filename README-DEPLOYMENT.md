# RepeatWise Horizontal Deployment Guide

This guide provides comprehensive instructions for deploying the RepeatWise application with horizontal scaling capabilities using both Docker Compose and Kubernetes.

## 🏗️ Architecture Overview

The RepeatWise application is designed for horizontal scaling with the following components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │    │   Load Balancer │    │   Load Balancer │
│   (Nginx)       │    │   (Kubernetes)  │    │   (Cloud)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Frontend Pods  │    │  Frontend Pods  │    │  Frontend Pods  │
│  (Flutter Web)  │    │  (Flutter Web)  │    │  (Flutter Web)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Backend Pods    │    │ Backend Pods    │    │ Backend Pods    │
│ (Spring Boot)   │    │ (Spring Boot)   │    │ (Spring Boot)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Cache Layer   │    │   Cache Layer   │    │   Cache Layer   │
│   (Redis)       │    │   (Redis)       │    │   (Redis)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Database Layer  │    │ Database Layer  │    │ Database Layer  │
│ (PostgreSQL)    │    │ (PostgreSQL)    │    │ (PostgreSQL)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📋 Prerequisites

### For Docker Compose Deployment
- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB RAM
- 10GB free disk space

### For Kubernetes Deployment
- Kubernetes cluster 1.24+
- kubectl configured
- Helm 3.0+ (optional)
- At least 8GB RAM
- 20GB free disk space

## 🚀 Quick Start

### Option 1: Docker Compose Deployment

1. **Clone and navigate to the project:**
   ```bash
   cd repeatwise
   ```

2. **Deploy the entire stack:**
   ```bash
   # Using the deployment script
   ./scripts/deploy.sh docker-compose
   
   # Or manually
   docker-compose up -d --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Grafana: http://localhost:3001 (admin/admin123)
   - Prometheus: http://localhost:9090

### Option 2: Kubernetes Deployment

1. **Build Docker images:**
   ```bash
   docker build -t repeatwise-backend:latest ./repeatwise-server
   docker build -t repeatwise-frontend:latest ./repeatwise-ui
   ```

2. **Deploy to Kubernetes:**
   ```bash
   # Using the deployment script
   ./scripts/deploy.sh kubernetes
   
   # Or manually
   kubectl apply -f kubernetes/
   ```

3. **Access the application:**
   ```bash
   # Add to /etc/hosts (Linux/Mac) or C:\Windows\System32\drivers\etc\hosts (Windows)
   echo "127.0.0.1 repeatwise.local" >> /etc/hosts
   
   # Access at: http://repeatwise.local
   ```

## 🔧 Configuration

### Environment Variables

The application can be configured using environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `docker` |
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:postgresql://postgres:5432/repeatwise_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `giapnt` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `abcd1234` |
| `SPRING_REDIS_HOST` | Redis host | `redis` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |
| `SERVER_PORT` | Backend port | `8080` |

### Scaling Configuration

#### Docker Compose Scaling
Edit `docker-compose.yml`:
```yaml
services:
  repeatwise-server:
    deploy:
      replicas: 3  # Number of backend instances
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
  
  repeatwise-ui:
    deploy:
      replicas: 2  # Number of frontend instances
      resources:
        limits:
          memory: 512M
          cpus: '0.25'
```

#### Kubernetes Scaling
```bash
# Manual scaling
kubectl scale deployment repeatwise-backend --replicas=5 -n repeatwise
kubectl scale deployment repeatwise-frontend --replicas=3 -n repeatwise

# Using the deployment script
./scripts/deploy.sh scale 5 3
```

### Horizontal Pod Autoscaler (Kubernetes)

The application includes HPA configurations that automatically scale based on CPU and memory usage:

- **Backend HPA**: Scales between 2-10 replicas based on 70% CPU and 80% memory
- **Frontend HPA**: Scales between 2-8 replicas based on 70% CPU and 80% memory

## 📊 Monitoring & Observability

### Prometheus Metrics

The application exposes metrics at:
- Backend: `/actuator/prometheus`
- Frontend: `/metrics`

### Grafana Dashboards

Pre-configured dashboards for:
- Application performance
- Database metrics
- Cache performance
- Infrastructure monitoring

### Health Checks

All services include health checks:
- **Backend**: `/actuator/health`
- **Frontend**: `/health`
- **Database**: PostgreSQL readiness probe
- **Cache**: Redis ping

## 🔒 Security

### Network Security
- All services run in isolated networks
- Internal communication only
- External access through load balancer only

### Security Headers
- X-Frame-Options: SAMEORIGIN
- X-XSS-Protection: 1; mode=block
- X-Content-Type-Options: nosniff
- Content-Security-Policy headers

### Rate Limiting
- API endpoints: 10 requests/second
- Web endpoints: 30 requests/second
- Burst allowance for legitimate traffic

## 🛠️ Management Commands

### Using the Deployment Script

```bash
# Deploy with Docker Compose
./scripts/deploy.sh docker-compose

# Deploy with Kubernetes
./scripts/deploy.sh kubernetes

# Scale services
./scripts/deploy.sh scale 5 3

# Check status
./scripts/deploy.sh status

# View logs
./scripts/deploy.sh logs repeatwise-backend

# Clean up
./scripts/deploy.sh cleanup
```

### Manual Commands

#### Docker Compose
```bash
# Start services
docker-compose up -d

# Scale services
docker-compose up -d --scale repeatwise-server=3 --scale repeatwise-ui=2

# View logs
docker-compose logs -f repeatwise-server

# Stop services
docker-compose down
```

#### Kubernetes
```bash
# Apply all configurations
kubectl apply -f kubernetes/

# Check pod status
kubectl get pods -n repeatwise

# View logs
kubectl logs -f deployment/repeatwise-backend -n repeatwise

# Scale deployments
kubectl scale deployment repeatwise-backend --replicas=5 -n repeatwise

# Delete namespace
kubectl delete namespace repeatwise
```

## 🔍 Troubleshooting

### Common Issues

1. **Database Connection Issues**
   ```bash
   # Check database status
   kubectl logs deployment/repeatwise-postgres -n repeatwise
   # or
   docker-compose logs postgres
   ```

2. **Backend Not Starting**
   ```bash
   # Check backend logs
   kubectl logs deployment/repeatwise-backend -n repeatwise
   # or
   docker-compose logs repeatwise-server
   ```

3. **Frontend Not Loading**
   ```bash
   # Check frontend logs
   kubectl logs deployment/repeatwise-frontend -n repeatwise
   # or
   docker-compose logs repeatwise-ui
   ```

4. **High Memory Usage**
   ```bash
   # Check resource usage
   kubectl top pods -n repeatwise
   # or
   docker stats
   ```

### Performance Tuning

1. **Database Optimization**
   - Increase connection pool size
   - Optimize query performance
   - Add database indexes

2. **Cache Optimization**
   - Increase Redis memory
   - Optimize cache keys
   - Implement cache warming

3. **Application Optimization**
   - Enable JVM optimizations
   - Configure thread pools
   - Implement connection pooling

## 📈 Scaling Strategies

### Vertical Scaling
- Increase CPU and memory limits
- Optimize application performance
- Use more powerful hardware

### Horizontal Scaling
- Add more application instances
- Use load balancers
- Implement auto-scaling

### Database Scaling
- Read replicas for read-heavy workloads
- Connection pooling
- Query optimization

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
name: Deploy RepeatWise
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Build and push images
      run: |
        docker build -t repeatwise-backend:${{ github.sha }} ./repeatwise-server
        docker build -t repeatwise-frontend:${{ github.sha }} ./repeatwise-ui
    
    - name: Deploy to Kubernetes
      run: |
        kubectl set image deployment/repeatwise-backend backend=repeatwise-backend:${{ github.sha }} -n repeatwise
        kubectl set image deployment/repeatwise-frontend frontend=repeatwise-frontend:${{ github.sha }} -n repeatwise
```

## 📚 Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Prometheus Monitoring](https://prometheus.io/docs/)
- [Grafana Dashboards](https://grafana.com/docs/)

## 🤝 Support

For issues and questions:
1. Check the troubleshooting section
2. Review application logs
3. Check system resources
4. Consult the documentation
5. Create an issue in the repository
