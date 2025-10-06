# Environment Configuration

## 1. Environment Overview

RepeatWise sử dụng 3 môi trường chính: Development, Staging, và Production. Mỗi môi trường có cấu hình riêng biệt để đảm bảo tính bảo mật và hiệu suất.

## 2. Environment Types

### 2.1 Development Environment
**Purpose**: Phát triển và test tính năng mới
**Access**: Development team only
**Data**: Mock data và test data

**Configuration**:
```yaml
# application-dev.yml
spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:postgresql://localhost:5432/repeatwise_dev
    username: repeatwise_dev
    password: dev_password
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
  
  security:
    jwt:
      secret: dev_jwt_secret_key_very_long_for_development_only
      expiration: 86400000 # 24 hours
  
logging:
  level:
    com.repeatwise: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

notification:
  email:
    enabled: false
    smtp:
      host: localhost
      port: 1025
  push:
    enabled: false
    fcm:
      project-id: repeatwise-dev
      private-key: dev_private_key

srs:
  algorithm:
    base-delay: 7
    min-delay: 7
    max-delay: 90
    penalty-factor: 0.5
    scaling-factor: 0.1
  
  overload:
    max-daily-sets: 3
    reschedule-limit: 2
    priority-strategy: OVERDUE_FIRST

analytics:
  enabled: true
  retention-days: 30
  batch-size: 1000

cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:8080
    - http://localhost:4200
```

### 2.2 Staging Environment
**Purpose**: Test tích hợp và UAT
**Access**: QA team và stakeholders
**Data**: Production-like data (anonymized)

**Configuration**:
```yaml
# application-staging.yml
spring:
  profiles:
    active: staging
  
  datasource:
    url: jdbc:postgresql://staging-db:5432/repeatwise_staging
    username: repeatwise_staging
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: 3600000 # 1 hour
  
logging:
  level:
    com.repeatwise: INFO
    org.springframework.security: WARN
  file:
    name: /var/log/repeatwise/staging.log
    max-size: 100MB
    max-history: 30

notification:
  email:
    enabled: true
    smtp:
      host: smtp.gmail.com
      port: 587
      username: ${SMTP_USERNAME}
      password: ${SMTP_PASSWORD}
      properties:
        mail.smtp.auth: true
        mail.smtp.starttls.enable: true
  push:
    enabled: true
    fcm:
      project-id: repeatwise-staging
      private-key: ${FCM_PRIVATE_KEY}

srs:
  algorithm:
    base-delay: 7
    min-delay: 7
    max-delay: 90
    penalty-factor: 0.5
    scaling-factor: 0.1
  
  overload:
    max-daily-sets: 3
    reschedule-limit: 2
    priority-strategy: OVERDUE_FIRST

analytics:
  enabled: true
  retention-days: 90
  batch-size: 5000

cors:
  allowed-origins:
    - https://staging.repeatwise.com
    - https://staging-app.repeatwise.com

monitoring:
  prometheus:
    enabled: true
    endpoint: /actuator/prometheus
  health:
    endpoint: /actuator/health
    show-details: when-authorized
```

### 2.3 Production Environment
**Purpose**: Môi trường live cho end users
**Access**: End users và admin team
**Data**: Real user data

**Configuration**:
```yaml
# application-prod.yml
spring:
  profiles:
    active: prod
  
  datasource:
    url: jdbc:postgresql://prod-db-cluster:5432/repeatwise_prod
    username: repeatwise_prod
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
  
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: 1800000 # 30 minutes
  
  cache:
    type: redis
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

logging:
  level:
    com.repeatwise: WARN
    org.springframework.security: ERROR
  file:
    name: /var/log/repeatwise/production.log
    max-size: 500MB
    max-history: 90
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

notification:
  email:
    enabled: true
    smtp:
      host: smtp.sendgrid.net
      port: 587
      username: apikey
      password: ${SENDGRID_API_KEY}
      properties:
        mail.smtp.auth: true
        mail.smtp.starttls.enable: true
  push:
    enabled: true
    fcm:
      project-id: repeatwise-prod
      private-key: ${FCM_PRIVATE_KEY}

srs:
  algorithm:
    base-delay: 7
    min-delay: 7
    max-delay: 90
    penalty-factor: 0.5
    scaling-factor: 0.1
  
  overload:
    max-daily-sets: 3
    reschedule-limit: 2
    priority-strategy: OVERDUE_FIRST

analytics:
  enabled: true
  retention-days: 730 # 2 years
  batch-size: 10000

cors:
  allowed-origins:
    - https://repeatwise.com
    - https://app.repeatwise.com
    - https://api.repeatwise.com

monitoring:
  prometheus:
    enabled: true
    endpoint: /actuator/prometheus
  health:
    endpoint: /actuator/health
    show-details: never
  metrics:
    export:
      prometheus:
        enabled: true

security:
  rate-limiting:
    enabled: true
    requests-per-minute: 100
    burst-capacity: 200
  cors:
    max-age: 3600
  headers:
    hsts: true
    content-security-policy: "default-src 'self'"

performance:
  async:
    core-pool-size: 10
    max-pool-size: 50
    queue-capacity: 1000
  cache:
    ttl:
      user-profile: 3600
      set-details: 1800
      statistics: 300
```

## 3. Environment Variables

### 3.1 Required Environment Variables
```bash
# Database
DB_PASSWORD=secure_password_here
DB_HOST=your_db_host
DB_PORT=5432

# JWT Security
JWT_SECRET=your_super_secure_jwt_secret_key_here_minimum_256_bits

# Email Configuration
SMTP_USERNAME=your_smtp_username
SMTP_PASSWORD=your_smtp_password
SENDGRID_API_KEY=your_sendgrid_api_key

# Push Notifications
FCM_PRIVATE_KEY=your_fcm_private_key_json_content

# Redis (Production)
REDIS_HOST=your_redis_host
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# External Services
GOOGLE_ANALYTICS_ID=GA_MEASUREMENT_ID
SENTRY_DSN=your_sentry_dsn

# Feature Flags
FEATURE_MASTERED_STATUS=true
FEATURE_ADVANCED_ANALYTICS=false
FEATURE_SOCIAL_FEATURES=false
```

### 3.2 Environment-Specific Variables
```bash
# Development
NODE_ENV=development
LOG_LEVEL=debug
ENABLE_SWAGGER=true

# Staging
NODE_ENV=staging
LOG_LEVEL=info
ENABLE_SWAGGER=false

# Production
NODE_ENV=production
LOG_LEVEL=warn
ENABLE_SWAGGER=false
ENABLE_METRICS=true
```

## 4. Database Configuration

### 4.1 Connection Pool Settings
```yaml
# Development
hikari:
  maximum-pool-size: 5
  minimum-idle: 2
  connection-timeout: 20000
  idle-timeout: 300000
  max-lifetime: 1200000

# Staging
hikari:
  maximum-pool-size: 10
  minimum-idle: 5
  connection-timeout: 30000
  idle-timeout: 600000
  max-lifetime: 1800000

# Production
hikari:
  maximum-pool-size: 50
  minimum-idle: 10
  connection-timeout: 30000
  idle-timeout: 600000
  max-lifetime: 1800000
  leak-detection-threshold: 60000
```

### 4.2 Database Schema Versions
```sql
-- Development: Auto create/drop
spring.jpa.hibernate.ddl-auto=create-drop

-- Staging/Production: Validate only
spring.jpa.hibernate.ddl-auto=validate
```

## 5. Security Configuration

### 5.1 JWT Configuration
```yaml
# Development
jwt:
  secret: dev_secret_key_very_long_for_development_only
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days

# Staging
jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000 # 1 hour
  refresh-expiration: 604800000 # 7 days

# Production
jwt:
  secret: ${JWT_SECRET}
  expiration: 1800000 # 30 minutes
  refresh-expiration: 604800000 # 7 days
```

### 5.2 CORS Configuration
```yaml
# Development
cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:8080
    - http://localhost:4200
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - "*"
  allow-credentials: true

# Staging
cors:
  allowed-origins:
    - https://staging.repeatwise.com
    - https://staging-app.repeatwise.com
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - "Authorization"
    - "Content-Type"
    - "X-Requested-With"
  allow-credentials: true
  max-age: 3600

# Production
cors:
  allowed-origins:
    - https://repeatwise.com
    - https://app.repeatwise.com
    - https://api.repeatwise.com
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - "Authorization"
    - "Content-Type"
    - "X-Requested-With"
  allow-credentials: true
  max-age: 3600
```

## 6. Monitoring and Logging

### 6.1 Logging Configuration
```yaml
# Development
logging:
  level:
    com.repeatwise: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# Staging
logging:
  level:
    com.repeatwise: INFO
    org.springframework.security: WARN
  file:
    name: /var/log/repeatwise/staging.log
    max-size: 100MB
    max-history: 30
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# Production
logging:
  level:
    com.repeatwise: WARN
    org.springframework.security: ERROR
  file:
    name: /var/log/repeatwise/production.log
    max-size: 500MB
    max-history: 90
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

### 6.2 Health Checks
```yaml
# All environments
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
  health:
    db:
      enabled: true
    redis:
      enabled: true
    disk:
      enabled: true
```

## 7. Deployment Configuration

### 7.1 Docker Configuration
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/repeatwise-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 7.2 Docker Compose (Development)
```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_HOST=postgres
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
    volumes:
      - ./logs:/var/log/repeatwise

  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: repeatwise_dev
      POSTGRES_USER: repeatwise_dev
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  mailhog:
    image: mailhog/mailhog
    ports:
      - "1025:1025"
      - "8025:8025"

volumes:
  postgres_data:
  redis_data:
```

### 7.3 Kubernetes Configuration (Production)
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: repeatwise-api
  namespace: repeatwise
spec:
  replicas: 3
  selector:
    matchLabels:
      app: repeatwise-api
  template:
    metadata:
      labels:
        app: repeatwise-api
    spec:
      containers:
      - name: repeatwise-api
        image: repeatwise/api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: repeatwise-secrets
              key: db-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: repeatwise-secrets
              key: jwt-secret
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

## 8. Environment-Specific Features

### 8.1 Feature Flags
```yaml
# Development
features:
  mastered-status: true
  advanced-analytics: true
  social-features: false
  experimental-srs: true
  debug-mode: true

# Staging
features:
  mastered-status: true
  advanced-analytics: true
  social-features: false
  experimental-srs: false
  debug-mode: false

# Production
features:
  mastered-status: true
  advanced-analytics: false
  social-features: false
  experimental-srs: false
  debug-mode: false
```

### 8.2 External Service Configuration
```yaml
# Development
external:
  google-analytics:
    enabled: false
    tracking-id: ""
  sentry:
    enabled: false
    dsn: ""
  firebase:
    enabled: false
    project-id: ""

# Staging
external:
  google-analytics:
    enabled: true
    tracking-id: "G-XXXXXXXXXX"
  sentry:
    enabled: true
    dsn: "https://xxx@sentry.io/xxx"
  firebase:
    enabled: true
    project-id: "repeatwise-staging"

# Production
external:
  google-analytics:
    enabled: true
    tracking-id: "G-XXXXXXXXXX"
  sentry:
    enabled: true
    dsn: "https://xxx@sentry.io/xxx"
  firebase:
    enabled: true
    project-id: "repeatwise-prod"
```

## 9. Environment Migration Guide

### 9.1 Development to Staging
1. Update environment variables
2. Change database connection
3. Enable email notifications
4. Configure monitoring
5. Disable debug features

### 9.2 Staging to Production
1. Update domain names
2. Configure production database
3. Set up SSL certificates
4. Configure CDN
5. Set up backup strategies
6. Configure load balancer

### 9.3 Rollback Procedures
1. Database rollback scripts
2. Application version rollback
3. Configuration rollback
4. Monitoring and alerting
