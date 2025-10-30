# System Context Diagram - RepeatWise

Tài liệu này mô tả biên giới hệ thống RepeatWise và các tác nhân chính tương tác với ứng dụng. Sử dụng C4 Model - Context Level để hiển thị high-level view của hệ thống.

## Tổng quan

RepeatWise là một hệ thống học tập cá nhân sử dụng Spaced Repetition. Người dùng tương tác qua Web và Mobile interfaces để quản lý flashcards, tổ chức kiến thức, và ôn tập theo lịch trình tự động.

### Mô tả ngắn gọn

- **Người dùng** tương tác qua giao diện Web (React) và Mobile (React Native)
- **Frontend** gọi Backend API (REST/JSON) để thực hiện các operations
- **Backend** (Spring Boot) kết nối PostgreSQL để lưu trữ dữ liệu
- **File Processing**: Async operations cho Import/Export CSV/XLSX và Copy operations
- **Notifications**: In-app và push notifications cho mobile

## C4 Model - Context Level

### System Context Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                                                                    │
│                      RepeatWise System                             │
│                                                                    │
│   Ứng dụng học tập sử dụng Spaced Repetition System              │
│   Giúp người dùng ghi nhớ kiến thức hiệu quả và lâu dài           │
│                                                                    │
│   • Folder/Deck Management với tree structure                     │
│   • SRS 7-box algorithm                                           │
│   • Import/Export CSV/Excel                                       │
│   • Multi-mode study                                              │
│   • Statistics & Analytics                                        │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
           ▲                       ▲                       ▲
           │                       │                       │
           │                       │                       │
     ┌─────┴─────┐          ┌─────┴─────┐         ┌──────┴──────┐
     │           │          │           │         │             │
     │    Web    │          │  Mobile   │         │   Admin     │
     │  Browser  │          │    App    │         │   Panel     │
     │           │          │           │         │  (Future)   │
     └───────────┘          └───────────┘         └─────────────┘
          │                       │                      │
          │                       │                      │
          └───────────────────────┴──────────────────────┘
                                  │
                             ┌────▼────┐
                             │         │
                             │  User   │
                             │         │
                             └─────────┘
```

## Actors (Người dùng)

### 1. End User - Người dùng cuối

**Vai trò**: Người sử dụng chính của ứng dụng

**Đặc điểm**:

- Học sinh, sinh viên
- Lập trình viên
- Người học ngoại ngữ
- Bất kỳ ai muốn ghi nhớ kiến thức hiệu quả

**Mục đích**:

- Tạo và quản lý flashcards
- Tổ chức kiến thức theo folders/decks
- Học và ôn tập theo SRS schedule
- Theo dõi progress và statistics
- Import/Export flashcards từ CSV/Excel

**Interactions với hệ thống**:

- **Authentication**: Đăng ký, đăng nhập với email + password
- **Folder Management**: Tạo/sửa/xóa folders, di chuyển/sao chép folders
- **Deck Management**: Tạo/sửa/xóa decks, di chuyển/sao chép decks
- **Card Management**: CRUD flashcards, import/export
- **Review Session**: Ôn tập cards theo SRS/Cram/Random mode
- **Settings**: Cấu hình SRS settings, notification, theme, language
- **Statistics**: Xem streak, box distribution, progress

## Client Interfaces

### 2. Web Browser

**Vai trò**: Client interface trên desktop/laptop

**Công nghệ**:

- **Framework**: React + TypeScript
- **UI**: Tailwind CSS + Shadcn/ui
- **State Management**: TanStack Query + Context API + Zustand
- **Build Tool**: Vite
- **Browsers**: Chrome, Firefox, Safari, Edge (modern browsers)

**Features**:

- Full-featured UI với rich components
- Responsive design (desktop, tablet)
- Dark/Light mode với smooth transition
- Progressive Web App (PWA) capabilities (future)
- Keyboard shortcuts (future)

**Communication**:

- REST API calls qua HTTPS
- JWT authentication trong Authorization header
- Refresh token trong HTTP-only cookie
- JSON payload format

### 3. Mobile App

**Vai trò**: Client interface trên smartphone/tablet

**Công nghệ**:

- **Framework**: React Native + TypeScript
- **UI**: React Native Paper (Material Design)
- **State Management**: TanStack Query + Context API
- **Platforms**: iOS (14+), Android (8+)

**Features**:

- Native performance
- Touch-optimized UI
- Push notifications (Firebase Cloud Messaging)
- Offline capabilities (future)
- Biometric authentication (future)
- Camera for OCR (future)

**Communication**:

- REST API calls qua HTTPS
- Same authentication mechanism as Web
- Background sync for notifications

### 4. Admin Panel (Future - Phase 3)

**Vai trò**: Quản trị hệ thống

**Features**:

- User management
- System monitoring
- Analytics dashboard
- Content moderation
- Audit logs

## System Components

### RepeatWise Backend

**Technology Stack**:

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL 15+
- **ORM**: Spring Data JPA (Hibernate)
- **Authentication**: JWT + Refresh Token
- **File Processing**: Apache POI (Excel), OpenCSV
- **Async Processing**: Spring @Async + ThreadPoolTaskExecutor

**Responsibilities**:

- User authentication & authorization
- Business logic implementation
- Data persistence
- SRS algorithm calculation
- File import/export processing
- Async operations (copy, import)
- API endpoints (RESTful)

**API Structure**:

```
/api
├── /auth          # Authentication (login, register, refresh, logout)
├── /folders       # Folder CRUD, move, copy, stats
├── /decks         # Deck CRUD, move, copy
├── /cards         # Card CRUD, import, export
├── /review        # Review session, submit rating, undo, skip
├── /srs           # SRS settings
└── /stats         # User statistics, box distribution
```

### PostgreSQL Database

**Role**: Persistent data storage

**Schema**:

- **Core Tables**: users, refresh_tokens, folders, decks, cards
- **SRS Tables**: srs_settings, card_box_position, review_logs
- **Statistics Tables**: user_stats, folder_stats
- **Indexes**: Composite indexes cho performance (review queries, folder tree)

**Key Features**:

- ACID transactions
- Foreign key constraints
- Check constraints (folder depth ≤ 10)
- Soft delete support (deleted_at timestamp)
- Materialized path cho folder tree

## Data Flow

### High-Level Data Flow

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
  │   {email, password}  │                     │
  │                      │─── Query user ─────>│
  │                      │<─── User data ──────│
  │                      │                     │
  │                      │─ Verify password    │
  │                      │─ Generate tokens    │
  │                      │─── Store refresh ──>│
  │                      │    token (hashed)   │
  │                      │                     │
  │<── 200 OK ───────────│                     │
  │  Body: {access_token}                      │
  │  Cookie: refresh_token                     │
  │                      │                     │
  │─── API with Bearer ─>│                     │
  │    Authorization     │                     │
  │                      │─ Verify JWT         │
  │<── Response ─────────│                     │
```

### Review Flow

```
Client                 Backend              Database
  │                      │                     │
  │─── GET /review/due ─>│                     │
  │  ?scope=folder&id=X  │                     │
  │                      │─── Query due ──────>│
  │                      │  SELECT cards       │
  │                      │  WHERE user_id=?    │
  │                      │  AND due_date<=NOW  │
  │                      │  ORDER BY due_date  │
  │                      │<─── Due cards ──────│
  │<── Due cards ────────│                     │
  │                      │                     │
  │─── POST /review/ ───>│                     │
  │    submit            │                     │
  │  {card_id, rating}   │                     │
  │                      │─ Calculate new box  │
  │                      │─ Calculate interval │
  │                      │─── Update box ─────>│
  │                      │    position         │
  │                      │─── Insert log ─────>│
  │                      │    review_logs      │
  │<── Success ──────────│                     │
```

### Import Flow

```
Client                 Backend              Database
  │                      │                     │
  │─ POST /cards/import ─>│                    │
  │   (multipart/form)   │                     │
  │   file: cards.xlsx   │                     │
  │                      │─ Parse file         │
  │                      │─ Validate rows      │
  │                      │  (format, required) │
  │                      │                     │
  │                      │─ If > 1000 cards:   │
  │                      │   Async job         │
  │                      │─ Else: Sync import  │
  │                      │                     │
  │                      │─── Batch insert ───>│
  │                      │    (1000/batch)     │
  │                      │<─── Success ────────│
  │<── Import summary ───│                     │
  │  {success: N,        │                     │
  │   errors: [...]}     │                     │
```

## External Systems (Future Phases)

### 1. Email Service (Phase 2)

**Purpose**: Send notifications, password reset emails

**Provider Options**:

- SendGrid
- AWS SES
- Mailgun

**Integration**:

- SMTP protocol
- REST API
- Template management

**Use cases**:

- Welcome email
- Password reset
- Weekly summary
- Streak reminders

### 2. Cloud Storage (Phase 3)

**Purpose**: Store images, audio files cho flashcards

**Provider Options**:

- AWS S3
- Google Cloud Storage
- Azure Blob Storage

**Integration**:

- SDK (Java, JavaScript)
- Pre-signed URLs
- CDN integration

**Use cases**:

- Image flashcards
- Audio pronunciation
- User avatars

### 3. Push Notification Service (MVP - Mobile)

**Purpose**: Send study reminders

**Provider**: Firebase Cloud Messaging (FCM)

**Integration**:

- Admin SDK (backend)
- Client SDK (React Native)

**Use cases**:

- Daily study reminders
- Due cards available
- Streak at risk

### 4. Analytics Service (Phase 2)

**Purpose**: Track user behavior, app performance

**Provider Options**:

- Google Analytics
- Mixpanel
- Amplitude

**Integration**:

- JavaScript SDK (web)
- Native SDK (mobile)

**Metrics**:

- User engagement (DAU, MAU)
- Feature usage
- Retention rates
- Performance metrics

## System Boundaries

### In Scope (MVP)

**Core Features**:

- ✅ User authentication (JWT + Refresh Token)
- ✅ Folder/Deck/Card management
- ✅ SRS 7-box algorithm
- ✅ Import/Export CSV/Excel
- ✅ Multiple study modes (SRS, Cram, Random)
- ✅ Basic statistics (streak, box distribution)
- ✅ Async operations (copy, import large files)
- ✅ Notification settings
- ✅ Web + Mobile apps

**Non-Functional**:

- ✅ Performance: Average < 200ms API, P95 < 300ms; < 300ms folder load
- ✅ Security: bcrypt, HTTPS, JWT rotation
- ✅ Reliability: Soft delete, daily backups
- ✅ Usability: Dark/Light mode, i18n (VI/EN)

### Out of Scope (MVP)

**Future Features**:

- ❌ OAuth (Google/Facebook)
- ❌ Rich text editor, images/audio
- ❌ Drag & drop UI
- ❌ Advanced analytics (heatmap, charts)
- ❌ Social features (share, community)
- ❌ AI-generated cards
- ❌ Offline mode with sync
- ❌ Team collaboration
- ❌ Premium features
- ❌ Third-party integrations

## Technology Stack Summary

### Frontend

- **Web**: React + TypeScript + Tailwind CSS + Shadcn/ui
- **Mobile**: React Native + TypeScript + React Native Paper
- **State**: TanStack Query (server state) + Context API (auth) + Zustand (UI state)
- **HTTP Client**: Axios with interceptors

### Backend

- **Framework**: Spring Boot 3 (Java 17)
- **Database**: PostgreSQL 15+
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT
- **File Processing**: Apache POI + OpenCSV
- **Async**: Spring @Async + ThreadPoolTaskExecutor

### Infrastructure (MVP)

- **Hosting**: Local / Docker containers
- **Database**: PostgreSQL standalone
- **File Storage**: Local filesystem (temp uploads)
- **Reverse Proxy**: Nginx (optional)

### Infrastructure (Production - Future)

- **Cloud**: AWS / Google Cloud / Azure
- **Database**: PostgreSQL RDS / Cloud SQL (managed)
- **Load Balancer**: AWS ALB / Cloud Load Balancing
- **File Storage**: S3 / Cloud Storage
- **CDN**: CloudFront / Cloud CDN
- **Monitoring**: CloudWatch / Stackdriver
- **Logging**: ELK Stack / Cloud Logging

## Security Considerations

### MVP Security

**Authentication & Authorization**:

- JWT access token (15 min)
- Refresh token rotation (7 days)
- HTTP-only cookies cho refresh token
- bcrypt password hashing (cost 12)

**Network Security**:

- HTTPS only (production)
- CORS configuration (whitelist origins)
- Rate limiting (100 req/min/user)

**Data Protection**:

- SQL injection prevention (JPA parameterized queries)
- XSS prevention (input sanitization)
- CSRF protection (SameSite cookies)

**File Upload Security**:

- File size limits (50MB)
- Format validation (CSV, XLSX only)
- Virus scanning (future)

### Production Security (Future)

**Enhanced Authentication**:

- OAuth 2.0 (Google, Facebook)
- Multi-factor authentication (MFA)
- Biometric authentication (mobile)

**Advanced Protection**:

- Web Application Firewall (WAF)
- DDoS protection
- Intrusion detection
- Security headers (CSP, HSTS)

**Compliance**:

- GDPR compliance
- Data encryption at rest
- Audit logging
- Regular security audits
- Penetration testing

## Deployment Architecture

### Development Environment

```
Developer Laptop
├── Backend: localhost:8080
├── Frontend Web: localhost:3000
├── Frontend Mobile: Expo Dev Client
└── Database: localhost:5432
```

### Production Environment (Future)

```
Internet
    │
    ▼
┌─────────────┐
│ CloudFlare  │ (CDN, DDoS protection)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│     ALB     │ (Load Balancer)
└──────┬──────┘
       │
       ├──────────────────┬──────────────────┐
       ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Backend    │    │  Backend    │    │  Backend    │
│  Instance 1 │    │  Instance 2 │    │  Instance 3 │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │
       └──────────────────┴──────────────────┘
                          │
                          ▼
                   ┌─────────────┐
                   │ PostgreSQL  │
                   │  (Primary)  │
                   └──────┬──────┘
                          │
                          ▼
                   ┌─────────────┐
                   │ PostgreSQL  │
                   │  (Replica)  │
                   └─────────────┘
```

## Monitoring & Logging (Future)

### Application Monitoring

- **Metrics**: Response time, throughput, error rate
- **Alerts**: Threshold-based alerts (Slack, Email)
- **Dashboards**: Grafana, CloudWatch

### Logging Strategy

- **Levels**: ERROR, WARN, INFO, DEBUG
- **Format**: Structured JSON logs
- **Aggregation**: ELK Stack / CloudWatch Logs
- **Retention**: 30 days (production), 7 days (dev)

### Key Metrics

- API response time (p50, p95, p99)
- Database query time
- Error rates by endpoint
- Active users (DAU, MAU)
- Review session completion rate
- Import/Export success rate

## Kết luận

System Context Diagram mô tả high-level view của RepeatWise MVP:

- **Simple architecture**: Web/Mobile → Backend API → PostgreSQL
- **Clear boundaries**: Core features only, no external integrations (MVP)
- **Scalable design**: Ready để mở rộng với external services (future)
- **Security-first**: JWT + Refresh Token, HTTPS, validation
- **Performance-focused**: Async operations, indexes, caching (folder_stats)

Kiến trúc này đảm bảo MVP delivery nhanh (3-4 tháng) trong khi vẫn dễ dàng mở rộng cho future phases.
