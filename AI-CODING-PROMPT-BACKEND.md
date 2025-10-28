# AI Coding Prompt - Backend (Java Spring Boot)

## 🎯 Mục tiêu

Implement **RepeatWise Backend** - REST API cho ứng dụng flashcard learning với SRS (Spaced Repetition System).

**Tech Stack**: Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL 15, Maven

---

## 📚 Tài liệu bắt buộc đọc (Thứ tự ưu tiên)

### 1️⃣ Coding Convention ⭐ ĐỌC ĐẦU TIÊN
[Backend Coding Convention](../docs/05-quality/coding-convention-backend.md) - **BẮT BUỘC TUÂN THỦ 100%**

**Key Rules**: Method ≤ 30 lines, NO viết tắt, dùng Apache Commons, MessageSource i18n, final cho biến không đổi, Early Return

### 2️⃣ API Design ⭐ BẮT ĐẦU CODE
[API Endpoints Summary](../docs/03-design/api/api-endpoints-summary.md) - Tất cả endpoints, DTOs, pagination, error handling

### 3️⃣ Database Design ⭐ THIẾT KẾ DATABASE
- [Database Schema](../docs/03-design/database/schema.md)
- [JPA Entity Design](../docs/03-design/database/jpa-entity-design.md)
- [Indexing Strategy](../docs/03-design/database/indexing-strategy.md)

### 4️⃣ Architecture & Patterns ⭐ HIỂU KIẾN TRÚC
- [Backend Detailed Design](../docs/03-design/architecture/backend-detailed-design.md) - Layered architecture
- [Design Patterns](../docs/03-design/architecture/design-patterns.md) - Composite, Strategy, Repository patterns
- [SRS Algorithm Design](../docs/03-design/architecture/srs-algorithm-design.md) - 7-box system

### 5️⃣ Security
[Authentication Model](../docs/03-design/security/authn-authz-model.md) - JWT + Refresh Token

### 6️⃣ Detail Design
- [Entity Specifications](../docs/04-detail-design/01-entity-specifications.md)
- [API Request/Response Specs](../docs/04-detail-design/02-api-request-response-specs.md)
- [Business Logic Flows](../docs/04-detail-design/03-business-logic-flows.md)
- [SRS Algorithm Implementation](../docs/04-detail-design/04-srs-algorithm-implementation.md)
- [Validation Rules](../docs/04-detail-design/05-validation-rules.md)
- [Error Handling Specs](../docs/04-detail-design/06-error-handling-specs.md)

---

## ✅ Coding Checklist

### Trước khi code
- [ ] Đọc Backend Coding Convention
- [ ] Đọc use case và API spec liên quan
- [ ] Hiểu entity spec và business logic flow

### Khi viết code
- [ ] Package structure: `com.repeatwise.{module}`
- [ ] Method ≤ 30 lines, tách helpers
- [ ] Dùng Apache Commons (StringUtils, CollectionUtils)
- [ ] Dùng MessageSource cho error messages
- [ ] Transaction boundaries đúng (`@Transactional`)
- [ ] Exception handling đầy đủ
- [ ] N+1 prevention (`JOIN FETCH`/`@EntityGraph`)
- [ ] Early Return & Guard Clauses
- [ ] Logging với SLF4J (`@Slf4j`)

### Testing
- [ ] Unit tests cho service (coverage ≥ 70%)
- [ ] Repository tests với `@DataJpaTest`
- [ ] Controller tests với MockMvc

---

**Version**: 1.0 | **Last Updated**: 2025-01-10
