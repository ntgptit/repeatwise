# RepeatWise - Flashcard + Spaced Repetition System

Ứng dụng học tập thông minh sử dụng thuật toán Spaced Repetition (SRS) với Box System để tối ưu hóa việc ghi nhớ.

## 🎯 Project Overview

RepeatWise là một hệ thống quản lý flashcard với 3 components chính:

1. **Backend API** - Java Spring Boot REST API
2. **Frontend Web** - React + TypeScript Web Application
3. **Frontend Mobile** - React Native iOS/Android App

## 📊 Project Status

| Component | Status | Technology | Details |
|-----------|--------|-----------|---------|
| 📚 **Documentation** | ✅ Complete | Markdown | [docs/](docs/) |
| 🖥️ **Backend API** | ⏳ Structure Ready | Java 17 + Spring Boot 3 | [backend-api/](backend-api/) |
| 🌐 **Frontend Web** | ✅ Initialized | React 19 + Vite + TypeScript | [frontend-web/](frontend-web/) |
| 📱 **Frontend Mobile** | ✅ Initialized | React Native 0.82 + TypeScript | [frontend-mobile/](frontend-mobile/) |

## 🚀 Quick Start

### Option 1: Docker (Recommended) 🐳

**One-command startup:**
```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh && ./start.sh
```

**Access URLs:**
- Web: http://localhost:3000
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

See [DOCKER-QUICKSTART.md](DOCKER-QUICKSTART.md) for details.

### Option 2: Manual Setup

#### Prerequisites
- **Docker Desktop** (for Option 1)
- **Node.js 20+** (for frontend)
- **Java 17+** (for backend)
- **PostgreSQL 15** (for database)
- **Maven 3.8+** (for backend)
- **Android Studio** (for mobile Android)
- **Xcode** (for mobile iOS, Mac only)

#### Frontend Web
```bash
cd frontend-web
npm run dev
# Open http://localhost:5173
```

#### Frontend Mobile
```bash
cd frontend-mobile
npm start              # Start Metro bundler
npm run android        # Run on Android
npm run ios            # Run on iOS (Mac only)
```

#### Backend API
```bash
cd backend-api
# Setup pom.xml first, then:
mvn clean install
mvn spring-boot:run
# API runs on http://localhost:8080
```

## 📁 Project Structure

```
repeatwise/
├── 00_docs/                      # 📚 Design Documentation
│   ├── 01-business/              # Business requirements
│   ├── 02-system-analysis/       # System analysis, use cases
│   ├── 03-design/                # System design
│   │   ├── api/                  # API specifications
│   │   ├── architecture/         # Architecture design
│   │   ├── database/             # Database schema
│   │   └── security/             # Security model
│   ├── 04-detail-design/         # Detailed specifications
│   └── 05-quality/               # Coding conventions
│
├── backend-api/                   # 🖥️ Backend API
│   ├── src/main/java/com/repeatwise/
│   │   ├── config/               # Spring configurations
│   │   ├── controller/           # REST controllers
│   │   ├── service/              # Business logic
│   │   ├── repository/           # Data access
│   │   ├── entity/               # JPA entities
│   │   ├── dto/                  # Data transfer objects
│   │   ├── mapper/               # MapStruct mappers
│   │   ├── strategy/             # Strategy patterns
│   │   ├── visitor/              # Visitor patterns
│   │   ├── event/                # Domain events
│   │   ├── exception/            # Exception handling
│   │   ├── security/             # JWT security
│   │   ├── util/                 # Utilities
│   │   └── job/                  # Background jobs
│   └── src/main/resources/
│       ├── application.yml       # Configuration
│       └── db/migration/         # Flyway migrations
│
├── frontend-web/                  # 🌐 Web Application
│   ├── src/
│   │   ├── components/           # React components
│   │   ├── pages/                # Page components
│   │   ├── services/             # API services
│   │   ├── hooks/                # Custom hooks
│   │   ├── contexts/             # React contexts
│   │   ├── store/                # Zustand store
│   │   ├── lib/                  # Utilities
│   │   ├── types/                # TypeScript types
│   │   └── constants/            # Constants
│   └── public/                   # Static assets
│
├── frontend-mobile/               # 📱 Mobile Application
│   ├── src/                      # Source code (to be created)
│   │   ├── components/           # React Native components
│   │   ├── screens/              # Screen components
│   │   ├── navigation/           # Navigation setup
│   │   ├── services/             # API services (shared with web)
│   │   ├── hooks/                # Custom hooks (shared with web)
│   │   └── types/                # TypeScript types (shared with web)
│   ├── android/                  # Android native code
│   └── ios/                      # iOS native code
│
├── docker-compose.yml            # 🐳 Docker orchestration
├── DOCKER-QUICKSTART.md          # 🚀 Docker quick start
├── DOCKER-SETUP.md               # 📖 Docker setup guide
├── TESTING-GUIDE.md              # 🧪 Testing guide
└── README.md                     # 📄 This file
```

## 🏗️ Architecture

### Backend (Layered Architecture)
```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (PostgreSQL)
```

### Frontend (Component-Based)
```
Pages (Routes)
    ↓
Components (UI)
    ↓
Hooks (React Query)
    ↓
Services (Axios)
    ↓
Backend API
```

## 🎨 Design Patterns

### Backend
- **Composite Pattern**: Folder tree structure
- **Strategy Pattern**: Review order, Forgotten card actions
- **Visitor Pattern**: Folder statistics calculation
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: MapStruct mapping
- **Domain Events**: Async operations

### Frontend
- **Smart vs Dumb Components**: Container vs Presentational
- **Custom Hooks**: Reusable logic
- **Context Pattern**: Global state (Auth)
- **Store Pattern**: UI state (Zustand)
- **Service Pattern**: API layer abstraction

## 🔑 Key Features

### Core Features
- ✅ **User Management**: Registration, login, profile management
- ✅ **Tree-based Folders**: Unlimited depth hierarchy organization
- ✅ **Deck Management**: Create, organize, import/export decks
- ✅ **Flashcard Management**: Rich text, images, audio support
- ✅ **SRS Algorithm**: 7-box system with configurable intervals
- ✅ **Multiple Study Modes**: SRS, Cram, Test, Random
- ✅ **Statistics & Analytics**: User, folder, deck level insights
- ✅ **Notifications**: Daily reminders, due card alerts

### Technical Features
- ✅ **JWT Authentication**: Access + refresh token strategy
- ✅ **RESTful API**: Clean API design with OpenAPI spec
- ✅ **Type Safety**: TypeScript across all frontends
- ✅ **Responsive Design**: Mobile-first approach
- ✅ **Dark Mode**: Theme toggle support
- ✅ **i18n Support**: English, Vietnamese

## 📚 Documentation

### Getting Started
- [Docker Quick Start](DOCKER-QUICKSTART.md) - Start with Docker (5 minutes)
- [Docker Setup Guide](DOCKER-SETUP.md) - Complete Docker guide
- [Testing Guide](TESTING-GUIDE.md) - Testing procedures
- [Test Results](DOCKER-TEST-RESULTS.md) - Latest test results

### Business Documents
- [Business Specification](repeatwise-business-spec.md) - Business requirements
- [MVP Specification](repeatwise-mvp-spec.md) - MVP scope & features

### Design Documents
- [API Endpoints](docs/03-design/api/api-endpoints-summary.md) - Complete API reference
- [Backend Design](docs/03-design/architecture/backend-detailed-design.md) - Backend architecture
- [Frontend Architecture](docs/03-design/architecture/frontend-architecture.md) - Frontend architecture
- [Database Schema](docs/03-design/database/schema.md) - Database design
- [JPA Entities](docs/03-design/database/jpa-entity-design.md) - Entity design
- [Design Patterns](docs/03-design/architecture/design-patterns.md) - Pattern implementations
- [SRS Algorithm](docs/03-design/architecture/srs-algorithm-design.md) - SRS algorithm details

### System Analysis
- [System Specification](docs/02-system-analysis/system-spec.md)
- [Use Cases](docs/02-system-analysis/use-cases/) - All use cases (UC-001 to UC-024)
- [Domain Model](docs/02-system-analysis/domain-model.md)
- [Non-Functional Requirements](docs/02-system-analysis/nfr.md)

## 🛠️ Technology Stack

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA (Hibernate)
- **Mapping**: MapStruct
- **Security**: Spring Security + JWT
- **Build**: Maven
- **Migration**: Flyway

### Frontend Web
- **Framework**: React 19
- **Language**: TypeScript 5.9
- **Build Tool**: Vite 7
- **State Management**: TanStack Query v5, Context API, Zustand
- **Routing**: React Router v6
- **HTTP**: Axios
- **Styling**: Tailwind CSS + Shadcn/ui
- **Forms**: React Hook Form + Zod
- **i18n**: react-i18next

### Frontend Mobile
- **Framework**: React Native 0.82
- **Language**: TypeScript 5.8
- **Navigation**: React Navigation v6
- **State Management**: TanStack Query v5, Context API
- **HTTP**: Axios (shared with web)
- **UI**: React Native Paper
- **i18n**: i18n-js
- **Notifications**: React Native Firebase

## 📊 Project Metrics

| Metric | Count |
|--------|-------|
| Total Files | 1000+ |
| Backend Files | 141 |
| Frontend Web Files | 15+ (base) |
| Frontend Mobile Files | 20+ (base) |
| Documentation Files | 50+ |
| Use Cases | 24 |
| API Endpoints | 40+ |
| Database Tables | 12 |

## 🔄 Development Workflow

### 1. Start Backend (After Setup)
```bash
cd backend-api
mvn spring-boot:run
# API: http://localhost:8080
```

### 2. Start Frontend Web
```bash
cd frontend-web
npm run dev
# Web: http://localhost:5173
```

### 3. Start Frontend Mobile
```bash
cd frontend-mobile
npm start               # Terminal 1: Metro
npm run android         # Terminal 2: Android
# or
npm run ios            # Terminal 2: iOS
```

## 🧪 Testing

### Backend
```bash
cd backend-api
mvn test                # Unit tests
mvn verify              # Integration tests
```

### Frontend Web
```bash
cd frontend-web
npm test                # Unit tests
npm run test:e2e        # E2E tests
```

### Frontend Mobile
```bash
cd frontend-mobile
npm test                # Unit tests
npm run test:e2e:android  # E2E Android
npm run test:e2e:ios      # E2E iOS
```

## 🐳 Docker Deployment

### Quick Deploy (All Services)
```bash
# Start all services (DB + Backend + Web)
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs -f

# Stop all
docker compose down
```

**Services**:
- PostgreSQL 16: `localhost:5432`
- Backend API: `localhost:8080`
- Frontend Web: `localhost:3000`

See [DOCKER-SETUP.md](DOCKER-SETUP.md) for complete guide.

### Production Deployment

#### Backend
- Docker container on AWS ECS/EKS
- PostgreSQL RDS
- Load balancer + Auto-scaling

#### Frontend Web
- Static build: `npm run build`
- Deploy to Vercel/Netlify/CloudFront

#### Frontend Mobile
- Android: APK/AAB via Play Store
- iOS: IPA via App Store

## 👥 Team

- **Project Lead**: [Your Name]
- **Backend Developer**: [Name]
- **Frontend Developer**: [Name]
- **Mobile Developer**: [Name]
- **UI/UX Designer**: [Name]

## 📝 License

Proprietary - All rights reserved

## 🤝 Contributing

This is a private project. For internal team members:

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Submit a pull request
5. Get code review approval
6. Merge to main

## 📞 Support

- **Documentation**: See [docs/](docs/)
- **Issues**: Create GitHub issue
- **Questions**: Contact team lead

---

**Status**: ✅ Docker Configuration Complete - Ready for Development
**Last Updated**: October 14, 2025
**Version**: 0.1.0

**Docker Status**: ✅ All services tested and working
- Backend: ✅ Built (564MB)
- Frontend: ✅ Built (80MB)
- Stack: ✅ All services healthy

**Next Steps**:
1. ✅ Docker setup complete
2. ⏳ Implement backend entities and services
3. ⏳ Implement frontend features
4. ⏳ Integration testing
5. ⏳ Deploy to production
