# Architecture Documentation

## Tổng quan

Thư mục này chứa tài liệu kiến trúc hệ thống RepeatWise theo mô hình C4. Các tài liệu này mô tả kiến trúc hệ thống từ góc nhìn tổng quan đến chi tiết implementation.

## Cấu trúc tài liệu

### 1. C4 Model Diagrams

#### [C4 Context Diagram](c4-context.md)
- **Mục đích**: Mô tả hệ thống RepeatWise trong bối cảnh của các hệ thống bên ngoài và người dùng
- **Nội dung**:
  - System boundaries
  - External systems (Email, Push Notification, Analytics)
  - User interactions
  - Data flows
  - Technology stack overview

#### [C4 Container Diagram](c4-container.md)
- **Mục đích**: Mô tả các container chính trong hệ thống
- **Nội dung**:
  - Mobile Application (Flutter)
  - Web API (Spring Boot)
  - Scheduler Service (Spring Boot)
  - Database (PostgreSQL)
  - Cache (Redis)
  - External services integration

#### [C4 Component Diagram](c4-component.md)
- **Mục đích**: Mô tả các component trong Web API container
- **Nội dung**:
  - Controller layer (Auth, Set, Cycle, Reminder, Statistics)
  - Service layer (Business logic)
  - Repository layer (Data access)
  - API Gateway (Security, Rate limiting)

### 2. Sequence Diagrams

#### [Sample Sequence Diagrams](sequence-diagrams/sample-sequence.md)
- **Mục đích**: Mô tả luồng tương tác giữa các component
- **Nội dung**:
  - User Authentication Flow
  - Set Management Flow
  - Learning Cycle Flow
  - Reminder Management Flow
  - Statistics Flow
  - Error Handling Flow

## Kiến trúc tổng thể

### 1. Layered Architecture

```
┌─────────────────────────────────────┐
│           Mobile App                │
│         (Flutter/Dart)              │
└─────────────────┬───────────────────┘
                  │ HTTPS/JSON
┌─────────────────▼───────────────────┐
│           API Gateway               │
│        (Spring Security)            │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│         Controller Layer            │
│   (Auth, Set, Cycle, Reminder)      │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│          Service Layer              │
│      (Business Logic)               │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│        Repository Layer             │
│       (Data Access)                 │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│           Data Layer                │
│    (PostgreSQL + Redis)             │
└─────────────────────────────────────┘
```

### 2. Technology Stack

#### Frontend
- **Framework**: Flutter 3.x
- **Language**: Dart 3.x
- **State Management**: Provider/Riverpod
- **HTTP Client**: Dio
- **Local Storage**: SharedPreferences
- **Push Notifications**: Firebase Cloud Messaging

#### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **Data Access**: Spring Data JPA
- **Validation**: Spring Validation
- **Scheduling**: Spring Scheduler

#### Database
- **Primary**: PostgreSQL 15+
- **Cache**: Redis 7.x
- **Migration**: Flyway

#### Infrastructure
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus + Grafana
- **Logging**: Logback

## Design Patterns

### 1. Layered Architecture
- **Separation of Concerns**: Mỗi layer có trách nhiệm riêng biệt
- **Dependency Direction**: Chỉ phụ thuộc vào layer bên dưới
- **Testability**: Dễ dàng test từng layer độc lập

### 2. Repository Pattern
- **Data Access Abstraction**: Ẩn chi tiết database
- **Consistent Interface**: Interface thống nhất cho data access
- **Testability**: Mock repository cho unit testing

### 3. Service Pattern
- **Business Logic Encapsulation**: Logic nghiệp vụ được đóng gói
- **Transaction Management**: Quản lý transaction ở service layer
- **Cross-cutting Concerns**: Xử lý các vấn đề chung

### 4. Dependency Injection
- **Constructor Injection**: Inject dependencies qua constructor
- **Loose Coupling**: Giảm sự phụ thuộc giữa các component
- **Testability**: Dễ dàng mock dependencies

## Security Architecture

### 1. Authentication
- **JWT-based**: Stateless authentication
- **Token Refresh**: Automatic token renewal
- **Session Management**: Redis-based session storage

### 2. Authorization
- **Role-based Access Control (RBAC)**: Phân quyền theo vai trò
- **API Protection**: Bảo vệ endpoints theo role
- **Data Access Control**: Kiểm soát truy cập dữ liệu

### 3. Data Protection
- **HTTPS/TLS**: Mã hóa giao tiếp
- **Database Encryption**: Mã hóa dữ liệu ở rest
- **Input Validation**: Validate tất cả input

## Performance Considerations

### 1. Caching Strategy
- **Redis Cache**: Cache frequently accessed data
- **Cache Invalidation**: Tự động invalidate khi data thay đổi
- **TTL Management**: Time-to-live cho cached data

### 2. Database Optimization
- **Indexing**: Tối ưu indexes cho queries
- **Query Optimization**: Tối ưu SQL queries
- **Connection Pooling**: Quản lý database connections

### 3. API Performance
- **Response Caching**: Cache API responses
- **Rate Limiting**: Giới hạn request rate
- **Async Processing**: Xử lý bất đồng bộ cho heavy operations

## Scalability

### 1. Horizontal Scaling
- **Load Balancing**: Cân bằng tải cho multiple instances
- **Database Scaling**: Read replicas cho read operations
- **Cache Scaling**: Redis cluster cho high availability

### 2. Microservices Ready
- **Service Boundaries**: Định nghĩa rõ ràng service boundaries
- **API Gateway**: Centralized API management
- **Service Discovery**: Dynamic service discovery

## Monitoring & Observability

### 1. Logging
- **Structured Logging**: JSON format logs
- **Log Levels**: Appropriate log levels
- **Log Aggregation**: Centralized log collection

### 2. Metrics
- **Application Metrics**: Business metrics
- **Infrastructure Metrics**: System metrics
- **Custom Metrics**: Domain-specific metrics

### 3. Tracing
- **Distributed Tracing**: Track requests across services
- **Performance Monitoring**: Monitor response times
- **Error Tracking**: Track and alert on errors

## Deployment Architecture

### 1. Containerization
- **Docker**: Containerized applications
- **Docker Compose**: Local development
- **Kubernetes**: Production orchestration (future)

### 2. Environment Management
- **Development**: Local development environment
- **Staging**: Pre-production testing
- **Production**: Live production environment

### 3. CI/CD Pipeline
- **Automated Testing**: Unit, integration, e2e tests
- **Automated Deployment**: Automated deployment process
- **Rollback Strategy**: Quick rollback capability

## Cách sử dụng tài liệu

### 1. Cho Developers
- Đọc C4 diagrams để hiểu kiến trúc tổng thể
- Tham khảo sequence diagrams để hiểu luồng xử lý
- Tuân thủ design patterns đã định nghĩa

### 2. Cho Architects
- Review và update architecture khi có thay đổi
- Đảm bảo consistency across all diagrams
- Validate implementation against design

### 3. Cho DevOps
- Hiểu deployment architecture
- Setup monitoring và logging
- Configure CI/CD pipeline

## Cập nhật tài liệu

### 1. Khi nào cập nhật
- Thay đổi kiến trúc hệ thống
- Thêm/sửa/xóa components
- Thay đổi technology stack
- Thay đổi deployment strategy

### 2. Quy trình cập nhật
- Review changes với team
- Update relevant diagrams
- Update sequence diagrams nếu cần
- Update README nếu cần
- Commit changes với descriptive messages

## Liên kết

- [Business Requirements](../01-business/brd.md)
- [System Specification](../02-system-analysis/system-spec.md)
- [API Documentation](../api/openapi.yaml)
- [Database Schema](../database/schema.md)
- [Security Model](../security/authn-authz-model.md)
