# Design Documentation - RepeatWise MVP

## Overview

Tài liệu thiết kế cho RepeatWise MVP, tối ưu hóa cho **AI Vibe Coding** - chatbot AI có thể hiểu và implement toàn bộ hệ thống từ design docs.

**Phase**: 03 - Design
**Status**: Complete - Optimized for AI Coding
**Version**: 2.0 (Cleaned up for AI)
**Last Updated**: 2025-01-10

## Design Principles

### 1. SOLID Principles
- **Single Responsibility**: Mỗi class có một trách nhiệm duy nhất
- **Open/Closed**: Mở cho extension, đóng cho modification
- **Liskov Substitution**: Strategy pattern cho behaviors
- **Interface Segregation**: Service interfaces riêng biệt
- **Dependency Inversion**: Phụ thuộc vào abstractions

### 2. Domain-Driven Design (DDD)
- **Bounded Contexts**: User, Content Organization, SRS, Statistics
- **Aggregate Roots**: User, Folder, CardBoxPosition
- **Domain Events**: Async operations, statistics updates
- **Value Objects**: FolderPath, immutable data

### 3. Performance First
- **Indexed queries**: Critical paths optimized
- **Caching strategy**: Denormalized stats, React Query
- **Async operations**: Background jobs for heavy operations
- **N+1 prevention**: Proper JPA fetch strategies

### 4. Security by Design
- **JWT with Refresh Token**: Short-lived access tokens
- **Input validation**: Backend và frontend validation
- **Rate limiting**: Prevent abuse
- **Audit logging**: Track critical operations

## Documentation Structure (Optimized for AI Coding)

**✅ CORE FILES (Essential for AI to code)**:
```
docs/03-design/
├── README.md                          # ← You are here - Design overview
├── design-doc-plan.md                 # Implementation roadmap
├── TRACEABILITY-MATRIX.md ⭐           # Design ↔ Requirements mapping (NEW)
├── CLEANUP-SUMMARY.md                 # Cleanup report
│
├── api/                               # API Design
│   ├── api-endpoints-summary.md ⭐     # Complete API reference (NEW)
│   └── openapi.yaml                   # OpenAPI 3.0 spec (optional)
│
├── architecture/                      # System Architecture
│   ├── backend-detailed-design.md ⭐   # Backend layered architecture
│   ├── frontend-architecture.md ⭐     # Frontend architecture
│   ├── design-patterns.md ⭐           # All design patterns (NEW)
│   └── srs-algorithm-design.md ⭐      # SRS algorithm details (NEW)
│
├── database/                          # Database Design
│   ├── schema.md ⭐                     # PostgreSQL tables
│   ├── jpa-entity-design.md ⭐         # JPA entities & mappings
│   └── indexing-strategy.md ⭐         # Performance indexes
│
└── security/                          # Security Design
    └── authn-authz-model.md ⭐         # JWT + refresh token auth
```

**❌ DELETED FILES (Not needed for AI vibe coding)**:
- ~~api/error-catalog.md~~ - Too detailed
- ~~api/api-guidelines.md~~ - Too detailed
- ~~api/pagination-filtering-sorting.md~~ - Covered in api-endpoints-summary.md
- ~~api/versioning-strategy.md~~ - Not needed for MVP
- ~~architecture/c4-*.md~~ - C4 diagrams too verbose
- ~~architecture/sequence-diagrams/~~ - All sequence diagrams (too detailed)
- ~~database/erd.md~~ - ERD redundant (schema.md sufficient)
- ~~database/migration-plan.md~~ - Too detailed
- ~~security/audit-logging.md~~ - Future feature
- ~~security/data-classification.md~~ - Too detailed
- ~~security/threat-model.md~~ - Too detailed
- ~~security/secrets-key-rotation.md~~ - Future feature

## Quick Start for AI Coding

### 📋 Prerequisites: Traceability Verification

**IMPORTANT**: Before coding, verify design mapping:
- **[TRACEABILITY-MATRIX.md](TRACEABILITY-MATRIX.md)** ⭐ **VERIFY FIRST**
  - Design ↔ Requirements mapping (99% coverage)
  - API endpoints → Use cases
  - Design patterns → Domain model
  - Database schema → Data dictionary
  - **Approval Status**: ✅ APPROVED for implementation

### 🚀 Start Here (Recommended Reading Order)

1. **[API Endpoints Summary](api/api-endpoints-summary.md)** ⭐ **START HERE**
   - Complete API reference với examples
   - Authentication flow (JWT + refresh token)
   - All endpoints: Auth, Folders, Decks, Cards, Review, Stats
   - Error responses và pagination

2. **[Design Patterns](architecture/design-patterns.md)** ⭐ **MUST READ**
   - Composite Pattern (Folder tree)
   - Strategy Pattern (SRS behaviors)
   - Visitor Pattern (Folder statistics)
   - Repository, DTO, Domain Events
   - Complete code examples

3. **[SRS Algorithm Design](architecture/srs-algorithm-design.md)** ⭐ **CORE LOGIC**
   - 7-box system với intervals
   - Rating system (Again, Hard, Good, Easy)
   - Forgotten card strategies
   - Study modes (SRS, Cram, Random)
   - Performance optimizations

4. **[Backend Detailed Design](architecture/backend-detailed-design.md)** ⭐
   - Layered architecture (Controller → Service → Repository)
   - Package structure
   - Exception handling, validation
   - Transaction management

5. **[JPA Entity Design](database/jpa-entity-design.md)** ⭐
   - All JPA entities với code examples
   - Relationships & mappings
   - Cascade strategies
   - Fetch strategies (LAZY loading)
   - N+1 prevention

6. **[Database Schema](database/schema.md)** ⭐
   - PostgreSQL tables
   - Relationships
   - Constraints
   - See also: [Indexing Strategy](database/indexing-strategy.md)

7. **[Frontend Architecture](architecture/frontend-architecture.md)** ⭐
   - React + React Native design
   - State management (React Query, Context, Zustand)
   - Component structure
   - Token refresh interceptor

8. **[Authentication Model](security/authn-authz-model.md)** ⭐
   - JWT with Refresh Token (MVP)
   - Token lifecycle (15min access, 7 days refresh)
   - Security best practices

---

## Core Design Documents

### 🔌 API Design
- **[API Endpoints Summary](api/api-endpoints-summary.md)** - Complete API reference ⭐
- [OpenAPI Spec](api/openapi.yaml) - OpenAPI 3.0 specification (optional)

### 🏗️ Architecture
- **[Backend Detailed Design](architecture/backend-detailed-design.md)** - Layered architecture ⭐
- **[Frontend Architecture](architecture/frontend-architecture.md)** - React + React Native ⭐
- **[Design Patterns](architecture/design-patterns.md)** - All patterns với code ⭐
- **[SRS Algorithm Design](architecture/srs-algorithm-design.md)** - SRS core logic ⭐

### 🗄️ Database
- **[Database Schema](database/schema.md)** - PostgreSQL tables ⭐
- **[JPA Entity Design](database/jpa-entity-design.md)** - JPA entities ⭐
- **[Indexing Strategy](database/indexing-strategy.md)** - Performance indexes ⭐

### 🔒 Security
- **[Authentication Model](security/authn-authz-model.md)** - JWT auth ⭐

## Key Design Decisions

### 1. Technology Stack

#### Backend
- **Java 17 + Spring Boot 3**: Modern, production-ready
- **Spring Data JPA**: ORM with repository pattern
- **PostgreSQL**: Reliable, powerful RDBMS
- **Spring @Async**: Background jobs (MVP), migrate to RabbitMQ later

**Why?**
- Strong typing, good IDE support
- Large ecosystem, mature libraries
- Easy to maintain and extend

#### Frontend
- **React + TypeScript** (Web): Industry standard
- **React Native** (Mobile): Code sharing with web
- **TanStack Query**: Server state management
- **Tailwind + Shadcn/ui** (Web): Fast UI development
- **React Native Paper** (Mobile): Material Design

**Why?**
- Single language (TypeScript) for web + mobile
- Great developer experience
- Large community, many resources

### 2. Architecture Patterns

#### Composite Pattern (Folder Tree)
**Problem**: Hierarchical folder structure với unlimited nesting
**Solution**: Folder contains Folders + Decks
**Benefit**: Uniform treatment, easy traversal

#### Strategy Pattern (SRS Behaviors)
**Problem**: Multiple review order options, forgotten card actions
**Solution**: Strategy interfaces với multiple implementations
**Benefit**: Easy to add new strategies, testable

#### Visitor Pattern (Folder Statistics)
**Problem**: Calculate recursive statistics efficiently
**Solution**: Traverse tree với visitor pattern
**Benefit**: Separation of concerns, reusable

#### Repository Pattern (Data Access)
**Problem**: Decouple business logic from data access
**Solution**: Spring Data JPA repositories
**Benefit**: Testable, swappable implementation

### 3. State Management (Frontend)

#### Why NOT Redux?
- App complexity chưa cao (6-7 screens)
- Overhead lớn (boilerplate ~300 lines)
- React Query tốt hơn cho server state

#### Chosen Architecture
- **TanStack Query**: Server state (folders, decks, cards)
- **Context API**: Auth state
- **Zustand**: UI state (sidebar, modals)

**Benefits**: Less code, better DX, auto caching

### 4. Authentication

#### JWT with Refresh Token (MVP)
- **Access token**: 15 minutes (short-lived)
- **Refresh token**: 7 days (HTTP-only cookie)
- **Token rotation**: New refresh token on each refresh

**Why in MVP?**
- Better security from day 1
- Industry best practice
- Not much more complex than simple JWT

### 5. Async Operations

#### Spring @Async (MVP)
- ThreadPoolTaskExecutor (core: 5, max: 10)
- In-memory job tracking (ConcurrentHashMap)
- Simple, no external dependencies

**Migration Path**: RabbitMQ for production (distributed systems)

### 6. Caching Strategy

#### MVP (No Redis)
- FolderStats table (denormalized, TTL: 5 min)
- React Query client cache (staleTime: 5 min)
- In-memory job status (TTL: 1 hour)

**Future**: Add Redis for session cache, distributed rate limiting

## Design Constraints

### Technical Constraints
- **Max folder depth**: 10 levels
- **Max deck size**: 10,000 cards
- **Max import file**: 10,000 rows, 50MB
- **Async thresholds**:
  - Folder copy: > 50 items
  - Deck copy: > 1000 cards

### Non-Functional Requirements
- **Response time**: < 500ms (p95)
- **Concurrent users**: 50 (MVP), 100+ (future)
- **Availability**: 99% uptime (MVP)
- **Data retention**: 30 days soft delete

See [../02-system-analysis/nfr.md](../02-system-analysis/nfr.md) for details.

## Design Review Process

### Review Checklist
- [ ] Design aligns with requirements
- [ ] Performance considerations addressed
- [ ] Security best practices followed
- [ ] Scalability path defined
- [ ] Error handling covered
- [ ] Testing strategy included
- [ ] Documentation complete

### Review Stages
1. **Self-review**: Author reviews own design
2. **Peer review**: Team members review
3. **Tech lead approval**: Final sign-off
4. **Update docs**: Incorporate feedback

## Design to Implementation

### Implementation Order
1. **Database schema**: Create tables, indexes
2. **Backend entities**: JPA entities & repositories
3. **Backend services**: Business logic
4. **REST APIs**: Controllers & DTOs
5. **Frontend state**: React Query setup
6. **Frontend components**: UI implementation
7. **Integration**: Wire everything together
8. **Testing**: Unit, integration, E2E tests

### Traceability Matrix

| Design Document | Implementation | Tests |
|----------------|----------------|-------|
| Database Schema | Flyway migrations | @DataJpaTest |
| JPA Entities | Entity classes | Repository tests |
| Backend Services | Service classes | Service tests |
| REST APIs | Controllers | MockMvc tests |
| Frontend State | React Query hooks | Hook tests |
| Frontend Components | React components | Component tests |

## Change Management

### Design Changes
- **Minor changes**: Update doc, notify team
- **Major changes**: Review process, update specs
- **Breaking changes**: Approval required, migration plan

### Version Control
- All design docs in Git
- Review via Pull Requests
- Changelog in each document

## References

### Related Documentation
- [MVP Specification](../../repeatwise-mvp-spec.md)
- [Business Documents](../01-business/)
- [System Analysis](../02-system-analysis/)
- [Use Cases](../02-system-analysis/use-cases/)

### External Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [C4 Model](https://c4model.com/)

## Glossary

| Term | Definition |
|------|------------|
| **Aggregate Root** | DDD pattern, entity that controls access to other entities |
| **DTO** | Data Transfer Object, carries data between layers |
| **JPA** | Java Persistence API, ORM specification |
| **SRS** | Spaced Repetition System, learning algorithm |
| **TTL** | Time To Live, cache expiration time |
| **p95** | 95th percentile, performance metric |

## Contact & Support

### Design Review Requests
- Create issue in GitHub: `design-review` label
- Tag: @tech-lead for review

### Questions
- Design questions: Design channel in Slack
- Implementation questions: Dev channel

---

---

## Summary for AI Coding

### ✅ What AI Needs to Know

**Core Files** (8 essential documents):
1. [api/api-endpoints-summary.md](api/api-endpoints-summary.md) - API spec
2. [architecture/design-patterns.md](architecture/design-patterns.md) - Patterns
3. [architecture/srs-algorithm-design.md](architecture/srs-algorithm-design.md) - SRS logic
4. [architecture/backend-detailed-design.md](architecture/backend-detailed-design.md) - Backend
5. [architecture/frontend-architecture.md](architecture/frontend-architecture.md) - Frontend
6. [database/schema.md](database/schema.md) - Database
7. [database/jpa-entity-design.md](database/jpa-entity-design.md) - JPA
8. [security/authn-authz-model.md](security/authn-authz-model.md) - Auth

**Key Patterns**:
- **Composite**: Folder tree (parent-child relationships)
- **Strategy**: SRS behaviors (review order, forgotten card actions)
- **Visitor**: Folder statistics (recursive traversal)
- **Repository**: Data access (Spring Data JPA)
- **DTO**: API layer (MapStruct mapping)
- **Domain Events**: Async updates (stats, notifications)

**Tech Stack**:
- Backend: Java 17 + Spring Boot 3 + Spring Data JPA + PostgreSQL
- Frontend: React + TypeScript + TanStack Query + Tailwind + Shadcn
- Mobile: React Native + React Native Paper
- Auth: JWT with Refresh Token (15min/7 days)
- State: React Query (server) + Context (auth) + Zustand (UI)

**Performance**:
- Critical index: `idx_card_box_user_due` (user_id, due_date, current_box)
- JOIN FETCH để tránh N+1 queries
- Batch operations: 1000 items/transaction
- Async operations: Folder copy (>50 items), Deck copy (>1000 cards)

**Links to Source Specs**:
- [MVP Specification](../../repeatwise-mvp-spec.md)
- [System Analysis](../02-system-analysis/)
- [Business Requirements](../01-business/)
- [Use Cases](../02-system-analysis/use-cases/)

---

**Document Status**: Design Complete - Optimized for AI Coding
**Version**: 2.0 (Cleaned up, removed 15 unnecessary files)
**Last Updated**: 2025-01-10
**Ready for**: AI Vibe Coding Implementation
