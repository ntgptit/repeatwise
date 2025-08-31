# C4 Model - Level 2: Container Diagram

## Tổng quan

Container Diagram mô tả các container chính trong hệ thống RepeatWise và cách chúng tương tác với nhau. Mỗi container đại diện cho một ứng dụng hoặc data store có thể được deploy độc lập.

## Container Diagram

```mermaid
graph TB
    %% Users
    Student[Student<br/>Người học]
    Admin[System Administrator<br/>Quản trị viên]
    
    %% Mobile Application
    MobileApp[Mobile Application<br/>Flutter App<br/>[Authentication, UI, State Management]]
    
    %% Web API
    WebAPI[Web API<br/>Spring Boot Application<br/>[REST API, Business Logic, Security]]
    
    %% Background Services
    BackgroundJobService[Background Job Service<br/>Spring Boot Application<br/>[Async Processing, Scheduling]]
    SchedulerService[Scheduler Service<br/>Spring Boot Application<br/>[Reminder Calculation, Cron Jobs]]
    
    %% Data Stores
    Database[(PostgreSQL Database<br/>[User Data, Sets, Cycles, Reviews])]
    Cache[(Redis Cache<br/>[Sessions, Statistics, Preferences])]
    
    %% External Services
    EmailService[Email Service<br/>SendGrid API]
    PushNotification[Push Notification<br/>Firebase FCM]
    Analytics[Analytics Service<br/>Google Analytics]
    StorageService[Storage Service<br/>AWS S3/Google Cloud Storage]
    CDN[Content Delivery Network<br/>CloudFlare/AWS CloudFront]
    MonitoringService[Monitoring Service<br/>Prometheus/Grafana]
    
    %% Relationships
    Student -->|HTTPS/JSON| MobileApp
    Admin -->|HTTPS/JSON| WebAPI
    
    MobileApp -->|HTTPS/JSON| WebAPI
    MobileApp -->|HTTPS/JSON| BackgroundJobService
    
    WebAPI -->|SQL| Database
    WebAPI -->|Redis Protocol| Cache
    WebAPI -->|HTTPS| EmailService
    WebAPI -->|HTTPS| PushNotification
    WebAPI -->|HTTPS| Analytics
    WebAPI -->|HTTPS| StorageService
    WebAPI -->|HTTPS| MonitoringService
    
    BackgroundJobService -->|SQL| Database
    BackgroundJobService -->|Redis Protocol| Cache
    BackgroundJobService -->|HTTPS| EmailService
    BackgroundJobService -->|HTTPS| PushNotification
    BackgroundJobService -->|HTTPS| StorageService
    BackgroundJobService -->|HTTPS| MonitoringService
    
    SchedulerService -->|SQL| Database
    SchedulerService -->|Redis Protocol| Cache
    SchedulerService -->|HTTPS| EmailService
    SchedulerService -->|HTTPS| PushNotification
    SchedulerService -->|HTTPS| MonitoringService
    
    StorageService -->|HTTPS| CDN
    
    %% Styling
    classDef user fill:#08427B,stroke:#073B6F,stroke-width:2px,color:#fff
    classDef container fill:#1168BD,stroke:#0E5DAD,stroke-width:2px,color:#fff
    classDef database fill:#FFD700,stroke:#FFC800,stroke-width:2px,color:#000
    classDef external fill:#999999,stroke:#8A8A8A,stroke-width:2px,color:#fff
    
    class Student,Admin user
    class MobileApp,WebAPI,BackgroundJobService,SchedulerService container
    class Database,Cache database
    class EmailService,PushNotification,Analytics,StorageService,CDN,MonitoringService external
```

## Mô tả chi tiết các Container

### 1. Mobile Application

#### Flutter App
- **Công nghệ**: Flutter 3.x, Dart
- **Trách nhiệm**:
  - Giao diện người dùng chính
  - State management
  - Local data caching
  - Offline support
  - Push notification handling

- **Chức năng chính**:
  - Authentication và authorization
  - Set management UI
  - Learning cycle interface
  - Statistics và analytics display
  - Settings và preferences
  - Data export/import interface

- **Deployment**: Mobile app stores (Google Play, App Store)
- **Scaling**: Horizontal scaling qua app stores

### 2. Web API

#### Spring Boot Application
- **Công nghệ**: Spring Boot 3.x, Java 17, Spring Security
- **Trách nhiệm**:
  - REST API endpoints
  - Business logic implementation
  - Authentication và authorization
  - Data validation
  - Error handling

- **Chức năng chính**:
  - User authentication (JWT)
  - Set CRUD operations
  - Learning cycle management
  - SRS algorithm implementation
  - Statistics calculation
  - Data export/import
  - Backup/restore operations

- **Deployment**: Docker containers trên cloud platform
- **Scaling**: Horizontal scaling với load balancer

### 3. Background Job Service

#### Spring Boot Application (Async Processing)
- **Công nghệ**: Spring Boot 3.x, Java 17, Spring Batch
- **Trách nhiệm**:
  - Xử lý các tác vụ nặng
  - Background job scheduling
  - File processing
  - Data export/import processing

- **Chức năng chính**:
  - Large data export jobs
  - Data import processing
  - Backup creation
  - Email queue processing
  - Analytics data processing
  - Cleanup jobs

- **Deployment**: Docker containers với job queue
- **Scaling**: Horizontal scaling với job distribution

### 4. Scheduler Service

#### Spring Boot Application (Cron Jobs)
- **Công nghệ**: Spring Boot 3.x, Java 17, Spring Scheduler
- **Trách nhiệm**:
  - Reminder calculation
  - Scheduled tasks
  - System maintenance
  - Data cleanup

- **Chức năng chính**:
  - Daily reminder calculation
  - Overdue review detection
  - Statistics aggregation
  - Cache cleanup
  - Backup scheduling
  - System health checks

- **Deployment**: Docker containers với cron scheduling
- **Scaling**: Single instance với high availability

### 5. Data Stores

#### PostgreSQL Database
- **Công nghệ**: PostgreSQL 15+
- **Trách nhiệm**:
  - Persistent data storage
  - ACID transactions
  - Data integrity
  - Backup và recovery

- **Chức năng chính**:
  - User data storage
  - Set và cycle data
  - Review history
  - Statistics data
  - Backup records
  - Job history

- **Deployment**: Managed database service hoặc self-hosted
- **Scaling**: Vertical scaling + read replicas

#### Redis Cache
- **Công nghệ**: Redis 7.x
- **Trách nhiệm**:
  - Session storage
  - Data caching
  - Rate limiting
  - Job queue

- **Chức năng chính**:
  - User sessions
  - Statistics cache
  - Preferences cache
  - Rate limiting data
  - Background job queue
  - Temporary data storage

- **Deployment**: Managed Redis service hoặc self-hosted
- **Scaling**: Redis Cluster cho high availability

## Luồng tương tác chính

### 1. User Authentication Flow
```
Student → MobileApp → WebAPI → Database
MobileApp ← WebAPI ← Cache (JWT)
```

### 2. Learning Cycle Flow
```
Student → MobileApp → WebAPI → Database
WebAPI → SchedulerService → Database (Calculate reminders)
WebAPI → EmailService/PushNotification (Send reminders)
```

### 3. Data Export Flow
```
Student → MobileApp → WebAPI → BackgroundJobService
BackgroundJobService → Database → StorageService
BackgroundJobService → EmailService (Send download link)
```

### 4. Statistics Flow
```
Student → MobileApp → WebAPI → Cache (Check cached)
WebAPI → Database (If not cached)
WebAPI → Analytics (Send usage data)
```

### 5. Background Processing Flow
```
SchedulerService → Database (Find due reminders)
SchedulerService → EmailService/PushNotification (Send notifications)
BackgroundJobService → Database (Process large exports)
BackgroundJobService → StorageService (Store files)
```

## Ràng buộc kỹ thuật

### 1. Bảo mật
- Tất cả container giao tiếp qua HTTPS/TLS
- JWT authentication cho API access
- Database encryption at rest
- Network isolation với VPC

### 2. Hiệu suất
- API response time < 2 giây
- Cache hit ratio > 80%
- Database connection pooling
- Background job timeout < 30 phút

### 3. Khả năng mở rộng
- Horizontal scaling cho Web API
- Database read replicas
- Redis cluster cho high availability
- Auto-scaling cho background jobs

### 4. Monitoring
- Health checks cho tất cả containers
- Metrics collection với Prometheus
- Log aggregation với ELK stack
- Alert system cho critical issues

## Deployment Strategy

### 1. Container Orchestration
- **Platform**: Kubernetes hoặc Docker Swarm
- **Service Discovery**: Kubernetes services
- **Load Balancing**: Ingress controller
- **Secrets Management**: Kubernetes secrets

### 2. Database Strategy
- **Primary**: PostgreSQL với high availability
- **Read Replicas**: 2-3 replicas cho read scaling
- **Backup**: Automated daily backups
- **Disaster Recovery**: Cross-region replication

### 3. Caching Strategy
- **Session Storage**: Redis cluster
- **Data Cache**: Redis với TTL
- **CDN**: Static content delivery
- **Application Cache**: In-memory caching

### 4. Monitoring Strategy
- **Infrastructure**: Prometheus + Grafana
- **Application**: Application metrics
- **Logging**: Centralized logging
- **Alerting**: PagerDuty hoặc Slack

## Technology Stack

### Frontend
- **Framework**: Flutter 3.x
- **Language**: Dart
- **State Management**: Provider/Riverpod
- **UI**: Material Design 3
- **HTTP Client**: Dio
- **Local Storage**: Hive/SQLite

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **Database**: Spring Data JPA
- **Caching**: Spring Cache + Redis
- **Scheduling**: Spring Scheduler
- **Batch Processing**: Spring Batch

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack
- **Storage**: AWS S3/Google Cloud Storage
- **CDN**: CloudFlare/AWS CloudFront
