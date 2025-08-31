# C4 Model - Level 1: System Context Diagram

## Tổng quan

System Context Diagram mô tả RepeatWise trong bối cảnh của các hệ thống bên ngoài và người dùng. Đây là cấp độ cao nhất của C4 model, tập trung vào ranh giới hệ thống và luồng dữ liệu tổng thể.

## Context Diagram

```mermaid
graph TB
    %% Users
    Student[Student<br/>Người học]
    Admin[System Administrator<br/>Quản trị viên]
    
    %% External Systems
    EmailService[Email Service<br/>Gmail/SendGrid]
    PushNotification[Push Notification<br/>Firebase Cloud Messaging]
    Analytics[Analytics Service<br/>Google Analytics]
    StorageService[Storage Service<br/>AWS S3/Google Cloud Storage]
    CDN[Content Delivery Network<br/>CloudFlare/AWS CloudFront]
    MonitoringService[Monitoring Service<br/>Prometheus/Grafana]
    
    %% Main System
    RepeatWise[RepeatWise<br/>Spaced Learning System<br/>[Mobile App + Backend API]]
    
    %% Relationships
    Student -->|Sử dụng mobile app<br/>Quản lý set học tập<br/>Thực hiện ôn tập| RepeatWise
    Admin -->|Quản trị hệ thống<br/>Monitor performance| RepeatWise
    
    RepeatWise -->|Gửi email reminder| EmailService
    RepeatWise -->|Gửi push notification| PushNotification
    RepeatWise -->|Gửi analytics data| Analytics
    RepeatWise -->|Lưu trữ file export/backup| StorageService
    RepeatWise -->|Phục vụ static content| CDN
    RepeatWise -->|Gửi metrics/logs| MonitoringService
    
    %% Styling
    classDef user fill:#08427B,stroke:#073B6F,stroke-width:2px,color:#fff
    classDef system fill:#1168BD,stroke:#0E5DAD,stroke-width:2px,color:#fff
    classDef external fill:#999999,stroke:#8A8A8A,stroke-width:2px,color:#fff
    
    class Student,Admin user
    class RepeatWise system
    class EmailService,PushNotification,Analytics,StorageService,CDN,MonitoringService external
```

## Mô tả chi tiết

### 1. Người dùng (Users)

#### Student (Người học)
- **Mô tả**: Người dùng chính sử dụng ứng dụng để học tập
- **Tương tác**:
  - Sử dụng mobile app để quản lý set học tập
  - Thực hiện ôn tập theo lịch SRS
  - Nhập điểm số sau mỗi lần ôn
  - Xem thống kê tiến trình
  - Reschedule reminder khi cần

#### System Administrator (Quản trị viên)
- **Mô tả**: Quản trị viên hệ thống
- **Tương tác**:
  - Quản lý user accounts
  - Monitor system performance
  - Backup và restore database
  - Cấu hình system parameters

### 2. Hệ thống bên ngoài (External Systems)

#### Email Service
- **Mô tả**: Dịch vụ gửi email reminder
- **Công nghệ**: Gmail API hoặc SendGrid
- **Chức năng**:
  - Gửi email nhắc nhở ôn tập
  - Gửi email xác thực tài khoản
  - Gửi email reset password

#### Push Notification Service
- **Mô tả**: Dịch vụ gửi push notification
- **Công nghệ**: Firebase Cloud Messaging (FCM)
- **Chức năng**:
  - Gửi push notification nhắc nhở
  - Gửi notification về tiến trình học tập
  - Gửi notification về set mới

#### Analytics Service
- **Mô tả**: Dịch vụ phân tích dữ liệu
- **Công nghệ**: Google Analytics hoặc Mixpanel
- **Chức năng**:
  - Thu thập dữ liệu sử dụng
  - Phân tích hành vi người dùng
  - Báo cáo hiệu suất ứng dụng

#### Storage Service
- **Mô tả**: Dịch vụ lưu trữ file và backup
- **Công nghệ**: AWS S3 hoặc Google Cloud Storage
- **Chức năng**:
  - Lưu trữ file export dữ liệu
  - Backup và restore database
  - File compression và encryption
  - Version control cho backup

#### Content Delivery Network (CDN)
- **Mô tả**: Mạng phân phối nội dung
- **Công nghệ**: CloudFlare hoặc AWS CloudFront
- **Chức năng**:
  - Phục vụ static content nhanh
  - Caching cho performance
  - DDoS protection
  - Global content distribution

#### Monitoring Service
- **Mô tả**: Dịch vụ giám sát và logging
- **Công nghệ**: Prometheus + Grafana hoặc DataDog
- **Chức năng**:
  - Thu thập metrics hệ thống
  - Monitoring performance
  - Alert và notification
  - Log aggregation và analysis

### 3. Hệ thống chính (Main System)

#### RepeatWise
- **Mô tả**: Hệ thống học tập thông minh với thuật toán Spaced Repetition
- **Thành phần**:
  - Mobile Application (Flutter)
  - Backend API (Spring Boot)
  - Database (PostgreSQL)
- **Chức năng chính**:
  - Quản lý set học tập
  - Thuật toán SRS tính toán lịch ôn
  - Hệ thống reminder thông minh
  - Thống kê và phân tích tiến trình

## Luồng dữ liệu chính

### 1. Luồng học tập
```
Student → RepeatWise → Analytics
```
- Người dùng thực hiện ôn tập
- Hệ thống ghi nhận dữ liệu
- Gửi analytics để phân tích

### 2. Luồng nhắc nhở
```
RepeatWise → Email Service → Student
RepeatWise → Push Notification → Student
```
- Hệ thống tính toán thời gian nhắc nhở
- Gửi email và push notification
- Người dùng nhận được nhắc nhở

### 3. Luồng quản trị
```
Admin → RepeatWise → Analytics
```
- Quản trị viên monitor hệ thống
- Thu thập dữ liệu performance
- Phân tích và báo cáo

### 4. Luồng export/backup
```
RepeatWise → Storage Service → CDN
```
- Hệ thống tạo file export/backup
- Lưu trữ trên cloud storage
- Phân phối qua CDN

### 5. Luồng monitoring
```
RepeatWise → Monitoring Service → Admin
```
- Hệ thống gửi metrics/logs
- Monitoring service phân tích
- Admin nhận alerts và reports

## Ràng buộc kỹ thuật

### 1. Bảo mật
- Tất cả giao tiếp phải được mã hóa (HTTPS/TLS)
- Xác thực người dùng qua JWT
- Bảo vệ dữ liệu cá nhân theo GDPR

### 2. Hiệu suất
- Response time < 2 giây cho mobile app
- Uptime > 99.5%
- Hỗ trợ 1000+ concurrent users

### 3. Khả năng mở rộng
- Kiến trúc microservices-ready
- Database có thể scale horizontally
- Cache layer cho performance

## Công nghệ sử dụng

### Frontend
- **Framework**: Flutter
- **State Management**: Provider/Riverpod
- **UI Components**: Material Design 3

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL 15+
- **Cache**: Redis

### Infrastructure
- **Container**: Docker
- **Orchestration**: Kubernetes (tương lai)
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus + Grafana
- **Storage**: AWS S3/Google Cloud Storage
- **CDN**: CloudFlare/AWS CloudFront
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana) 
