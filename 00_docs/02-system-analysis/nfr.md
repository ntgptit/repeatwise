# Non-Functional Requirements (NFR) - RepeatWise MVP

## 1. Overview

Document này định nghĩa các yêu cầu phi chức năng cho RepeatWise MVP, bao gồm performance, scalability, security, reliability, usability, và maintainability requirements.

**Priority Levels**:
- **P0** (Must-have): Bắt buộc cho MVP launch
- **P1** (Should-have): Quan trọng nhưng có thể defer
- **P2** (Nice-to-have): Future enhancements

---

## 2. Performance Requirements

### 2.1 Response Time (P0)

| Operation | Target (p95) | Max Acceptable | Priority |
|-----------|--------------|----------------|----------|
| API CRUD operations | < 200ms | 500ms | P0 |
| Folder tree load | < 300ms | 500ms | P0 |
| Review session start | < 500ms | 1000ms | P0 |
| Import 1000 cards | < 10s | 30s | P0 |
| Export deck (1000 cards) | < 5s | 15s | P0 |
| Folder statistics (cached) | < 100ms | 300ms | P0 |
| Folder statistics (uncached) | < 500ms | 2000ms | P1 |
| Login/Register | < 500ms | 1000ms | P0 |

**Measurement Method**:
- Use Spring Boot Actuator metrics
- Monitor with Prometheus (Future) or application logs (MVP)
- p95 = 95th percentile response time

### 2.2 Throughput (P1)

| Metric | Target | Priority |
|--------|--------|----------|
| Concurrent users | 50 | P0 |
| Requests per second (total) | 100 RPS | P0 |
| Concurrent review sessions | 20 | P0 |
| Database connections | 20 connections | P0 |

**Note**: MVP targets < 100 users. Production targets (Future) would be 1000+ concurrent users.

### 2.3 Database Performance (P0)

**Query Performance Targets**:

```sql
-- Get due cards (CRITICAL)
-- Target: < 50ms
SELECT c.*, cbp.* FROM card_box_position cbp
JOIN cards c ON c.id = cbp.card_id
WHERE cbp.user_id = ? AND cbp.due_date <= CURRENT_DATE
ORDER BY cbp.due_date ASC, cbp.current_box ASC
LIMIT 200;
-- Expected: Index scan on idx_card_box_user_due

-- Get folder descendants
-- Target: < 100ms
SELECT * FROM folders
WHERE user_id = ? AND path LIKE '/parent_id/%'
  AND deleted_at IS NULL;
-- Expected: Index scan on idx_folders_path

-- Calculate folder stats (uncached)
-- Target: < 500ms
-- Recursive CTE with JOINs (see data-dictionary.md)
-- Expected: Multiple index scans
```

**Database Connection Pool**:
- Initial size: 10 connections
- Max size: 20 connections
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes

### 2.4 File Processing Performance (P0)

**Import Performance**:
- 1,000 rows: < 10 seconds
- 5,000 rows: < 30 seconds
- 10,000 rows: < 60 seconds (max limit)

**Export Performance**:
- 1,000 cards: < 5 seconds
- 5,000 cards: < 15 seconds
- 10,000 cards: < 30 seconds

**Implementation**:
- Batch insert: 1000 records per transaction
- Stream processing for large files
- Progress tracking every 500 rows
- Timeout: 2 minutes max

### 2.5 Async Operations Performance (P0)

**Folder Copy**:
- Sync (≤ 50 items): < 3 seconds
- Async (51-500 items): < 5 minutes total
- Progress updates: Every 10 items

**Deck Copy**:
- Sync (≤ 1000 cards): < 5 seconds
- Async (1001-10,000 cards): < 10 minutes total
- Progress updates: Every 100 cards

**Background Jobs**:
- ThreadPool core size: 5 threads
- ThreadPool max size: 10 threads
- Queue capacity: 100 tasks
- Job timeout: 10 minutes
- Retry policy: 3 attempts with exponential backoff

---

## 3. Scalability Requirements

### 3.1 Data Volume (P0 - MVP Scope)

| Entity | MVP Limit | Production Target (Future) | Notes |
|--------|-----------|----------------------------|-------|
| Users | < 100 | 10,000+ | MVP focus: personal use |
| Folders per user | 1,000 | 10,000 | Practical limit |
| Decks per user | 1,000 | 10,000 | |
| Cards per deck | 10,000 | 50,000 | Hard limit in import |
| Total cards per user | 100,000 | 1,000,000 | |
| Review logs per user | Unlimited | Unlimited | Archive old logs |
| Folder depth | 10 levels | 10 levels | Hard constraint |

### 3.2 Concurrent Operations (P0)

| Operation | MVP Limit | Notes |
|-----------|-----------|-------|
| Concurrent logins | 50 | |
| Concurrent review sessions | 20 | Per server |
| Concurrent imports | 5 | Rate limited |
| Concurrent folder copies | 3 | Resource intensive |
| Concurrent deck copies | 5 | |

### 3.3 Horizontal Scaling (P2 - Future)

**MVP**: Single server deployment, vertical scaling only

**Future Production**:
- Load balancer (Nginx)
- Multiple app server instances
- Database read replicas
- Redis cache cluster
- CDN for static assets

---

## 4. Security Requirements

### 4.1 Authentication & Authorization (P0)

**Authentication**:
- ✅ Email + password (MVP)
- ✅ Password hashed with bcrypt (cost factor 12)
- ✅ JWT with Refresh Token mechanism (MVP) ⭐
  - **Access token**: 15 minutes expiry (short-lived, secure)
  - **Refresh token**: 7 days expiry (HTTP-only cookie)
  - **Token rotation**: New refresh token on each refresh
  - **Revocation**: Stored in database, can be invalidated
  - **Security**: Reduced attack window (15m vs 24h)
  - See section 4.6 below for complete implementation
- ❌ OAuth (Google/Facebook) → Future
- ❌ Multi-factor authentication → Future

**Access Token Structure** (JWT):
```json
{
  "sub": "user_id",
  "email": "user@example.com",
  "iat": 1640000000,
  "exp": 1640000900
}
```

**Refresh Token**:
- Random secure token (UUID or crypto.randomBytes)
- Stored in database as bcrypt hash
- Transmitted only in HTTP-only cookie
- Never exposed to JavaScript (XSS protection)

**Authorization**:
- Row-level security: User can only access their own data
- Role-based access control (RBAC): Not in MVP, single role "USER"
- API endpoints protected with @PreAuthorize("hasRole('USER')")

### 4.2 Password Policy (P0)

**Requirements**:
- Minimum length: 8 characters
- Maximum length: 128 characters
- Must contain: No special requirements for MVP (Future: require mix of chars)
- Password strength indicator: Client-side only (MVP)

**Password Reset** (P1 - Future):
- Email-based reset flow
- Reset token valid for 1 hour
- Single-use tokens

### 4.3 Data Encryption (P0)

**In Transit**:
- ✅ HTTPS only in production (TLS 1.2+)
- ✅ HTTP Strict Transport Security (HSTS) header
- ❌ Certificate pinning → Future

**At Rest**:
- ❌ Database encryption → Future (rely on cloud provider)
- ✅ Passwords hashed (never stored plain text)
- ❌ Sensitive fields encryption → Future

### 4.4 Input Validation & Sanitization (P0)

**Server-Side Validation**:
- All inputs validated with Spring Validation (@Valid, @NotNull, @Size, etc.)
- SQL injection prevention: Use parameterized queries (JPA)
- XSS prevention: Escape HTML in text fields
- CSRF protection: Spring Security CSRF tokens

**Rate Limiting** (P0 - MVP):
- Simple in-memory rate limiting
- Limit: 100 requests/minute/user
- Exceeded: HTTP 429 Too Many Requests

**Rate Limiting** (P1 - Future Production):
- Redis-based distributed rate limiting
- Different limits per endpoint type:
  - Auth endpoints: 5 req/min
  - Read endpoints: 100 req/min
  - Write endpoints: 50 req/min
  - Copy operations: 10 req/hour

### 4.5 Security Headers (P0)

```yaml
Content-Security-Policy: "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'"
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: "1; mode=block"
Strict-Transport-Security: "max-age=31536000; includeSubDomains"
```

### 4.6 JWT Refresh Token Implementation (P0 - MVP) ⭐

**Status**: Included in MVP for security best practices

**Access Token**:
- Short-lived: 15 minutes expiry
- Stored in memory (not localStorage)
- Contains user claims: user_id, email, roles
- Transmitted in Authorization header: `Bearer <token>`

**Refresh Token**:
- Long-lived: 7 days expiry
- Stored in HTTP-only cookie (XSS protection)
- Cannot be accessed by JavaScript
- Used only to obtain new access token

**Token Rotation Strategy**:
```
1. User logs in → Receive access token (15m) + refresh token (7d)
2. Access token expires after 15m
3. Client detects 401 → Call /api/auth/refresh with refresh token (cookie)
4. Server validates refresh token → Issue new access + refresh tokens
5. Old refresh token invalidated (one-time use)
6. Repeat cycle
```

**Refresh Token Storage**:
```sql
CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  token_hash VARCHAR(255) NOT NULL, -- bcrypt hashed
  expires_at TIMESTAMP NOT NULL,
  revoked_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);
```

**Revocation**:
- Logout: Revoke refresh token immediately
- Logout all devices: Revoke all user's refresh tokens
- Password change: Revoke all refresh tokens
- Suspicious activity: Revoke tokens + require re-login

**Security Benefits**:
- Reduced attack window (15m vs 24h)
- Token rotation prevents replay attacks
- Refresh token in HTTP-only cookie prevents XSS theft
- Revocation capability for compromised tokens
- Per-device session management

**Implementation Libraries**:
- Backend: Spring Security + Custom refresh token service
- Frontend: Axios interceptor for auto-refresh

**Implementation Effort**:
- Backend: ~500 lines code (AuthService, RefreshTokenRepository, endpoints)
- Frontend: Axios interceptor for auto-refresh (~100 lines)
- Database: 1 table (refresh_tokens) + indexes
- Testing: Unit tests + integration tests for auth flows

**Why Include in MVP**:
- Security best practice from day 1
- Prevents long-lived token vulnerabilities
- Industry standard (OAuth 2.0 pattern)
- Easier to implement initially than to retrofit
- Better user experience (transparent token refresh)

### 4.7 Audit Logging (P1 - Future)

**Log Events**:
- Failed login attempts
- Password changes
- Folder/Deck copy operations
- Bulk delete operations
- Admin actions

**Log Retention**: 90 days

---

## 5. Reliability & Availability

### 5.1 Availability (P1)

**Target**:
- MVP: 95% uptime (允许 36 hours/month downtime)
- Production (Future): 99.5% uptime (< 3.6 hours/month downtime)

**Downtime Windows**:
- Planned maintenance: Saturdays 2-4 AM (low traffic)
- Emergency maintenance: As needed with notification

### 5.2 Error Handling (P0)

**Error Response Format**:
```json
{
  "timestamp": "2025-01-10T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Folder name must not be empty",
  "path": "/api/folders",
  "trace_id": "abc-123-def"
}
```

**Error Types**:
- 400 Bad Request: Validation errors
- 401 Unauthorized: Auth required
- 403 Forbidden: Insufficient permissions
- 404 Not Found: Resource not found
- 409 Conflict: Duplicate or constraint violation
- 429 Too Many Requests: Rate limit exceeded
- 500 Internal Server Error: Server errors

**Error Logging**:
- All errors logged with stack trace
- Log level: ERROR for 5xx, WARN for 4xx
- Include request ID for tracing

### 5.3 Data Backup (P0)

**Backup Strategy**:
- **Frequency**: Daily automated backups at 2 AM
- **Retention**: 7 daily, 4 weekly, 3 monthly backups
- **Storage**: Cloud storage (AWS S3 or equivalent)
- **Backup Method**: pg_dump with compression
- **Backup Size**: ~100MB for 100 users (estimate)

**Backup Verification**:
- Weekly restore test to verify backup integrity
- Automated backup health checks

**Restore SLA**:
- Recovery Time Objective (RTO): < 4 hours
- Recovery Point Objective (RPO): < 24 hours (daily backup)

### 5.4 Monitoring & Alerting (P1 - Future)

**Monitoring Metrics**:
- Server CPU, memory, disk usage
- API response times (p50, p95, p99)
- Error rates (4xx, 5xx)
- Database query times
- Active users, concurrent sessions
- Background job queue length

**Alerting Rules**:
- CPU > 80% for 5 minutes
- Memory > 90% for 5 minutes
- Error rate > 5% for 10 minutes
- API p95 > 1000ms for 15 minutes
- Database connection pool exhausted

**Notification Channels**:
- Email (MVP)
- Slack (Future)
- PagerDuty (Production)

### 5.5 Disaster Recovery (P1 - Future)

**Scenarios**:
1. Database corruption: Restore from backup (< 4 hours)
2. Server failure: Deploy to new server (< 2 hours)
3. Data center outage: Failover to backup region (< 8 hours)

**Business Continuity Plan**:
- Documented recovery procedures
- Contact list for emergency response
- Regular disaster recovery drills (quarterly)

---

## 6. Usability Requirements

### 6.1 User Interface (P0)

**Responsive Design**:
- ✅ Desktop: 1920x1080, 1366x768 (min)
- ✅ Tablet: 768x1024 (iPad)
- ✅ Mobile: 375x667 (iPhone SE), 360x640 (Android)

**Browser Support**:
- ✅ Chrome 90+ (primary)
- ✅ Safari 14+ (Mac/iOS)
- ✅ Edge 90+
- ❌ IE 11 (not supported)
- ❌ Firefox → Future (should work but not tested)

**Mobile App**:
- ✅ iOS 13+ (React Native)
- ✅ Android 8+ (API level 26+)

### 6.2 Theme Support (P0) ⭐

**Themes**:
- Light mode
- Dark mode
- System preference (auto-detect)

**Implementation**:
- Tailwind CSS dark mode classes
- Smooth transition animation (200ms)
- Persistent user preference in database
- Apply theme immediately on login

### 6.3 Internationalization (P0)

**Supported Languages**:
- Vietnamese (VI) - Primary
- English (EN) - Secondary

**i18n Implementation**:
- Web: react-i18next
- Mobile: i18n-js
- Language switcher in settings
- Persistent user preference

**Translation Coverage**:
- UI labels: 100%
- Error messages: 100%
- System notifications: 100%
- Help/docs: 80% (MVP), 100% (Future)

### 6.4 Accessibility (P1 - Future)

**WCAG 2.1 Level AA Compliance**:
- Keyboard navigation support
- Screen reader compatibility
- Color contrast ratio ≥ 4.5:1
- Focus indicators
- Alt text for images
- ARIA labels

**MVP**: Basic accessibility, full compliance in Future

### 6.5 User Experience (P0)

**Loading States**:
- Show spinner for operations > 500ms
- Progress bar for long operations (import, copy)
- Skeleton screens for data loading

**Empty States**:
- Clear message when no data
- CTA button to create first item
- Helpful illustrations (Future)

**Error States**:
- Clear error messages (user-friendly, not technical)
- Actionable suggestions (e.g., "Check your internet connection")
- Retry button for failed operations

**Success Feedback**:
- Toast notifications for successful actions
- Confirmation dialogs for destructive actions (delete)
- Undo option for accidental actions (P1)

---

## 7. Maintainability Requirements

### 7.1 Code Quality (P0)

**Code Standards**:
- Follow Java conventions (Google Java Style Guide)
- Follow TypeScript/React best practices (Airbnb style)
- Use ESLint/Prettier for formatting
- Use CheckStyle/SonarLint for Java

**Code Review**:
- All PRs require 1 reviewer approval
- Automated checks must pass (tests, linting)
- No merge without passing CI/CD

**Documentation**:
- Javadoc for public APIs
- JSDoc for complex functions
- README for each module
- Architecture Decision Records (ADRs) for major decisions

### 7.2 Testing Requirements (P0)

**Test Coverage**:
- Unit tests: ≥ 70% coverage for core logic
- Integration tests: Cover critical flows
- E2E tests: ❌ MVP, ✅ Future

**Critical Test Cases**:
- ✅ SRS algorithm correctness (box transitions)
- ✅ Folder operations (copy, move, delete cascade)
- ✅ Folder move validation (circular reference prevention)
- ✅ Import/Export (CSV/Excel parsing, validation)
- ✅ Review session (due cards query, rating updates)
- ✅ Authentication & authorization

**Test Automation**:
- CI/CD pipeline runs all tests on every commit
- Fail build if tests fail or coverage drops

### 7.3 Logging (P0)

**Log Levels**:
- ERROR: Unexpected errors, exceptions
- WARN: Validation failures, business rule violations
- INFO: Important business events (login, review submit)
- DEBUG: Detailed execution flow (disabled in production)

**Log Format**:
```
[2025-01-10 10:30:00.123] [INFO] [RequestID: abc-123] [UserID: user-456] [FolderService] Created folder: "English Learning"
```

**Log Rotation**:
- Daily rotation
- Keep 30 days of logs
- Compress old logs

### 7.4 Deployment (P0)

**Deployment Strategy**:
- MVP: Manual deployment with documented process
- Future: Automated CI/CD pipeline

**Deployment Process**:
1. Run all tests locally
2. Create release branch
3. Deploy to staging environment
4. Run smoke tests
5. Deploy to production
6. Monitor for errors (15 minutes)
7. Rollback if critical issues

**Rollback Plan**:
- Keep previous version deployable
- Database migrations must be backward compatible
- Rollback SOP documented

### 7.5 Configuration Management (P0)

**Configuration Profiles**:
- `application.yml` (common)
- `application-dev.yml` (development)
- `application-prod.yml` (production)

**Environment Variables**:
- Database credentials
- JWT secret key
- Email SMTP settings
- Cloud storage credentials

**Secrets Management**:
- ❌ MVP: Environment variables
- ✅ Future: Vault or AWS Secrets Manager

---

## 8. Compatibility Requirements

### 8.1 Browser Compatibility (P0)

| Browser | Minimum Version | Support Level |
|---------|----------------|---------------|
| Chrome | 90+ | Full support (primary) |
| Safari | 14+ | Full support |
| Edge | 90+ | Full support |
| Firefox | 88+ | Should work (not tested) |
| IE 11 | - | Not supported |

### 8.2 Mobile OS Compatibility (P0)

| OS | Minimum Version | Support Level |
|---------|----------------|---------------|
| iOS | 13.0+ | Full support |
| Android | 8.0+ (API 26) | Full support |

### 8.3 Database Compatibility (P0)

| Database | Version | Support Level |
|---------|---------|---------------|
| PostgreSQL | 15+ | Recommended |
| PostgreSQL | 13-14 | Compatible |
| PostgreSQL | < 13 | Not supported |

---

## 9. Legal & Compliance

### 9.1 Data Privacy (P0)

**GDPR Compliance** (if applicable):
- Right to access: User can export all their data
- Right to erasure: User can delete account and all data
- Right to portability: Export in CSV/Excel format
- Consent: Terms of Service acceptance

**Data Minimization**:
- Only collect necessary data
- No tracking cookies (MVP)
- No third-party analytics (MVP)

**Data Retention**:
- User data: Retained while account active
- Deleted accounts: Soft delete for 30 days, then permanent delete
- Review logs: Retained for 2 years
- Backups: 3 months retention

### 9.2 Terms of Service & Privacy Policy (P1)

**Required Documents**:
- Terms of Service
- Privacy Policy
- Cookie Policy (Future)

**User Consent**:
- Accept ToS during registration
- Privacy policy link in footer

---

## 10. Performance Benchmarks

### 10.1 Load Testing Scenarios (P1 - Future)

**Scenario 1: Normal Load**
- 50 concurrent users
- 20% creating/editing content
- 60% reviewing cards
- 20% browsing folders
- Duration: 30 minutes
- Expected: All requests < 500ms (p95)

**Scenario 2: Peak Load**
- 100 concurrent users
- Same distribution
- Duration: 10 minutes
- Expected: Some degradation acceptable (< 1000ms p95)

**Scenario 3: Stress Test**
- 200 concurrent users
- Find breaking point
- Identify bottlenecks

**Scenario 4: Import Spike**
- 20 concurrent imports (1000 cards each)
- Expected: No timeouts, queue managed properly

### 10.2 Performance Optimization Techniques

**Database**:
- ✅ Indexes on critical columns (user_id, due_date, path)
- ✅ Denormalized folder_stats table
- ✅ Query result pagination
- ❌ Database query cache → Future
- ❌ Read replicas → Future

**Application**:
- ✅ Async operations for long-running tasks
- ✅ Batch operations for bulk inserts
- ❌ Application-level caching (Redis) → Future
- ❌ CDN for static assets → Future

**Frontend**:
- ✅ React Query for caching API responses
- ✅ Lazy loading for folder tree
- ✅ Virtual scrolling for large lists
- ✅ Code splitting for faster initial load
- ❌ Service Worker for offline support → Future

---

## 11. NFR Validation Matrix

| NFR Category | Requirement | Validation Method | Target | Status |
|--------------|-------------|-------------------|--------|--------|
| Performance | API response time | Load testing | < 500ms (p95) | ✅ P0 |
| Performance | Database query time | EXPLAIN ANALYZE | < 100ms | ✅ P0 |
| Performance | Import 1000 cards | Integration test | < 10s | ✅ P0 |
| Scalability | Support 100 users | Load testing | 100 concurrent | ✅ P0 |
| Scalability | 10,000 cards/deck | Integration test | No errors | ✅ P0 |
| Security | Password hashing | Unit test | bcrypt cost 12 | ✅ P0 |
| Security | JWT expiry | Integration test | 24h expiry | ✅ P0 |
| Security | Rate limiting | Integration test | 100 req/min | ✅ P0 |
| Reliability | Backup daily | Automated job | Success rate 100% | ✅ P0 |
| Reliability | Restore test | Manual process | Weekly | ✅ P0 |
| Usability | Responsive design | Manual testing | 3 breakpoints | ✅ P0 |
| Usability | Dark mode | Manual testing | Smooth transition | ✅ P0 |
| Usability | Multi-language | Manual testing | VI + EN | ✅ P0 |
| Maintainability | Unit test coverage | SonarQube | ≥ 70% | ✅ P0 |
| Maintainability | Code review | Process | 100% PRs | ✅ P0 |

---

## 12. NFR Roadmap

### Phase 1 (MVP - Current)
- ✅ Basic performance targets (< 500ms API)
- ✅ Basic security (bcrypt, JWT, input validation)
- ✅ Daily backups
- ✅ Responsive design + dark mode
- ✅ Multi-language (VI/EN)
- ✅ 70% test coverage

### Phase 2 (Post-MVP, 3-6 months)
- JWT refresh token mechanism
- Redis caching for folder_stats
- Enhanced rate limiting (per-endpoint)
- Monitoring & alerting (Prometheus + Grafana)
- Accessibility improvements (WCAG 2.1 AA)

### Phase 3 (Production, 6-12 months)
- Horizontal scaling (load balancer, multiple instances)
- Database read replicas
- CDN for static assets
- Advanced security (audit logging, intrusion detection)
- 99.5% uptime SLA
- Load testing & performance tuning

---

## 13. Conclusion

NFRs cho RepeatWise MVP focus vào:
1. **Performance**: Fast response times (< 500ms), optimized queries
2. **Security**: Secure authentication, input validation, HTTPS
3. **Reliability**: Daily backups, error handling
4. **Usability**: Responsive, dark mode, multi-language
5. **Maintainability**: 70% test coverage, code quality standards

**MVP Priorities**: P0 requirements must be met before launch. P1 and P2 can be deferred to future releases.

**Next Steps**:
1. Implement P0 NFRs during development
2. Validate NFRs with testing (unit, integration, load)
3. Monitor production metrics
4. Iterate on performance and security improvements
