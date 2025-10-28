# System Context Diagram - RepeatWise

## Tổng quan

Sơ đồ ngữ cảnh hệ thống mô tả RepeatWise và các tương tác với actors bên ngoài.

## C4 Model - Context Level

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│                    RepeatWise System                            │
│                                                                 │
│   Ứng dụng học tập sử dụng Spaced Repetition System           │
│   Giúp người dùng ghi nhớ kiến thức hiệu quả                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
          ▲                    ▲                    ▲
          │                    │                    │
          │                    │                    │
    ┌─────┴─────┐        ┌────┴────┐         ┌────┴────┐
    │           │        │         │         │         │
    │  Web      │        │ Mobile  │         │ Admin   │
    │  Browser  │        │   App   │         │  Panel  │
    │           │        │         │         │ (Future)│
    └───────────┘        └─────────┘         └─────────┘
         │                     │                   │
         │                     │                   │
         └─────────┬───────────┴───────────────────┘
                   │
              ┌────▼────┐
              │         │
              │  User   │
              │         │
              └─────────┘
```

## Actors

### 1. User (End User)

**Vai trò**: Người dùng cuối của ứng dụng

**Mục đích**:
- Tạo và quản lý flashcards
- Tổ chức kiến thức theo folders/decks
- Học và ôn tập theo SRS schedule
- Theo dõi progress và statistics

**Interactions**:
- Đăng ký, đăng nhập hệ thống
- Tạo/sửa/xóa folders, decks, cards
- Import/Export flashcards từ CSV/Excel
- Review cards theo multiple modes
- Xem statistics và progress
- Cấu hình settings (theme, language, SRS preferences)

### 2. Web Browser

**Vai trò**: Client interface trên desktop/laptop

**Công nghệ**:
- React TypeScript
- Modern browsers (Chrome, Firefox, Safari, Edge)

**Features**:
- Full-featured UI với Tailwind CSS + Shadcn/ui
- Responsive design
- Progressive Web App (PWA) capabilities
- Dark/Light theme

### 3. Mobile App

**Vai trò**: Client interface trên smartphone/tablet

**Công nghệ**:
- React Native
- iOS & Android platforms

**Features**:
- Native performance
- Push notifications
- Offline capabilities (future)
- Touch-optimized UI

### 4. Admin Panel (Future)

**Vai trò**: Quản trị hệ thống

**Features**:
- User management
- System monitoring
- Analytics dashboard
- Content moderation

## External Systems (Future)

### 1. Email Service
- **Purpose**: Send notifications, password reset
- **Provider**: SendGrid / AWS SES
- **Integration**: SMTP / API

### 2. Cloud Storage
- **Purpose**: Store images, audio files
- **Provider**: AWS S3 / Google Cloud Storage
- **Integration**: SDK

### 3. Analytics Service
- **Purpose**: Track user behavior, app performance
- **Provider**: Google Analytics / Mixpanel
- **Integration**: JavaScript SDK

### 4. Push Notification Service
- **Purpose**: Send study reminders
- **Provider**: Firebase Cloud Messaging
- **Integration**: Admin SDK

## System Boundaries

### In Scope (MVP)
- User authentication (JWT + Refresh Token)
- Folder/Deck/Card management
- SRS algorithm implementation
- Import/Export CSV/Excel
- Multiple study modes
- Basic statistics
- Web + Mobile apps

### Out of Scope (MVP)
- Social features (share, community)
- Rich media (images, audio)
- AI-generated content
- Advanced analytics
- Team collaboration
- Third-party integrations

## Data Flow Overview

```
┌──────────┐         HTTPS          ┌──────────────┐
│          │────────────────────────>│              │
│  Client  │    REST API (JSON)      │   Backend    │
│ (Web/App)│<────────────────────────│  (Spring)    │
│          │                         │              │
└──────────┘                         └──────┬───────┘
                                            │
                                            │ JDBC
                                            │
                                      ┌─────▼──────┐
                                      │            │
                                      │ PostgreSQL │
                                      │            │
                                      └────────────┘
```

### Authentication Flow

```
Client                 Backend              Database
  │                      │                     │
  │─── POST /auth/login ─>│                    │
  │                      │─── Query user ─────>│
  │                      │<─── User data ──────│
  │                      │                     │
  │<── Access + Refresh ─│                     │
  │    Tokens            │                     │
  │                      │                     │
  │─── API with Access ─>│                     │
  │    Token             │                     │
  │                      │─── Verify JWT ───> │
  │                      │                     │
  │<── Response ─────────│                     │
```

### Review Flow

```
Client                 Backend              Database
  │                      │                     │
  │─── GET /review/due ─>│                     │
  │                      │─── Query due ──────>│
  │                      │    cards            │
  │                      │<─── Cards ──────────│
  │<── Due cards ────────│                     │
  │                      │                     │
  │─── POST rating ─────>│                     │
  │                      │─── Update box ─────>│
  │                      │    position         │
  │                      │─── Insert log ─────>│
  │<── Success ──────────│                     │
```

## Technology Stack Summary

### Frontend
- **Web**: React + TypeScript + Tailwind CSS
- **Mobile**: React Native + TypeScript
- **State**: TanStack Query + Context API + Zustand

### Backend
- **Framework**: Spring Boot 3 (Java 17)
- **Database**: PostgreSQL 15+
- **ORM**: Spring Data JPA
- **Authentication**: JWT + Refresh Token

### Infrastructure (MVP)
- **Hosting**: Local / Docker containers
- **Database**: PostgreSQL standalone
- **File Storage**: Local filesystem

### Infrastructure (Production - Future)
- **Hosting**: AWS / Google Cloud
- **Database**: PostgreSQL RDS / Cloud SQL
- **Load Balancer**: Nginx / AWS ALB
- **File Storage**: S3 / Cloud Storage
- **CDN**: CloudFront / Cloud CDN

## Security Considerations

### MVP
- HTTPS for production
- JWT authentication
- Password hashing (bcrypt)
- Input validation
- SQL injection prevention (JPA)
- XSS prevention
- CORS configuration

### Production (Future)
- Rate limiting (Redis)
- DDoS protection
- Security headers
- Regular security audits
- Penetration testing
- Backup encryption
- Audit logging

## Deployment Architecture

### Development
```
Developer ─> Git Push ─> GitHub ─> Local Testing
```

### Production (Future)
```
Developer ─> Git Push ─> GitHub ─> CI/CD ─> Staging ─> Production
                                    (Actions)  (Test)   (Deploy)
```

## Monitoring & Logging (Future)

- **Application Logs**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Metrics**: Prometheus + Grafana
- **Error Tracking**: Sentry
- **Uptime Monitoring**: Pingdom / UptimeRobot
- **APM**: New Relic / Datadog
