# Stakeholders - RepeatWise

Tài liệu này mô tả các bên liên quan (stakeholders) trong dự án RepeatWise MVP, bao gồm vai trò, trách nhiệm, nhu cầu và mối quan tâm của từng bên.

## Tổng quan

Stakeholders của RepeatWise được chia thành 3 nhóm chính:

1. **Primary Stakeholders**: End users - người sử dụng trực tiếp ứng dụng
2. **Secondary Stakeholders**: Development team - đội ngũ phát triển
3. **Supporting Stakeholders**: Business & Operations - hỗ trợ kinh doanh và vận hành (future)

## 1. Primary Stakeholders - End Users

### 1.1. Học sinh, sinh viên

**Đặc điểm**:

- **Độ tuổi**: 15-30
- **Technical level**: Basic to Intermediate
- **Devices**: Mobile (primary - 70%), Web (secondary - 30%)
- **Usage pattern**: Daily, 15-30 minutes per session

**Nhu cầu chính**:

- Học từ vựng tiếng Anh (IELTS, TOEIC)
- Ghi nhớ công thức toán, hóa, lý
- Ôn tập kiến thức trước kỳ thi
- Giao diện đơn giản, dễ sử dụng
- Import nhanh từ Excel/CSV (word lists từ giáo viên)
- Ôn tập mọi lúc mọi nơi (mobile app)
- Theo dõi progress rõ ràng (streak, stats)

**Pain points hiện tại**:

- Anki quá phức tạp, learning curve cao
- Quizlet có quảng cáo nhiều, thiếu SRS tốt
- Khó tạo hàng nghìn thẻ thủ công
- Không biết khi nào cần ôn lại

**Use cases**:

- Import 500-1000 từ vựng từ file Excel của giáo viên
- Review 20-50 cards mỗi sáng trước khi đến trường
- Chuẩn bị kỳ thi: cram mode cho chapters cụ thể
- Track streak để duy trì động lực

**Success metrics**:

- Daily review completion rate > 80%
- 7-day retention > 70%
- Average session time: 15+ minutes
- Import success rate > 95%

### 1.2. Lập trình viên (Developers)

**Đặc điểm**:

- **Độ tuổi**: 20-40
- **Technical level**: Advanced
- **Devices**: Web (primary - 80%), Mobile (secondary - 20%)
- **Usage pattern**: Daily/Weekly, longer sessions (30-60 min)

**Nhu cầu chính**:

- Học algorithms, data structures
- Ghi nhớ design patterns, system design concepts
- Review coding syntax (khi học ngôn ngữ mới)
- Chuẩn bị phỏng vấn (LeetCode patterns)
- Performance tốt với large datasets (1000+ cards)
- Keyboard shortcuts (future)
- Code snippets với syntax highlighting (future)

**Pain points hiện tại**:

- Cần tool nhanh, không rườm rà
- Muốn self-host hoặc export data dễ dàng
- Thiếu support cho code blocks
- Không có API để tự động hóa (future)

**Use cases**:

- Create deck "System Design Concepts" với 200+ cards
- Daily review algorithms trước khi coding
- Prepare for interviews: review 50 LeetCode patterns
- Export data để backup hoặc share với team

**Success metrics**:

- Performance: < 200ms API response
- Large deck support: 10,000+ cards/deck
- Export success: 100% data integrity
- Feature adoption: 60% use import/export

### 1.3. Người học ngoại ngữ (Language Learners)

**Đặc điểm**:

- **Độ tuổi**: 18-45
- **Technical level**: Basic
- **Devices**: Mobile (primary - 90%), Web (secondary - 10%)
- **Usage pattern**: Multiple times per day, short sessions (5-10 min)

**Nhu cầu chính**:

- Học từ vựng hàng ngày (20-30 words/day)
- Ôn tập ngữ pháp, collocations
- Audio support cho pronunciation (future)
- Image flashcards (future)
- Spaced repetition hiệu quả
- Offline mode (future)

**Pain points hiện tại**:

- Quên từ nhanh nếu không ôn đúng lúc
- App hiện tại không có SRS tốt
- Thiếu notifications nhắc nhở
- Không track được tiến độ rõ ràng

**Use cases**:

- Daily routine: review due cards mỗi sáng (10 min)
- Create deck "JLPT N3 Vocabulary" với 1000+ words
- Track learning progress: streak, box distribution
- Receive daily notifications khi có due cards

**Success metrics**:

- Daily active users (DAU): > 70%
- Streak maintenance: > 30% users have 7+ day streak
- Notification open rate: > 40%
- Review completion: > 85%

## 2. Secondary Stakeholders - Development Team

### 2.1. Project Owner / Product Manager

**Vai trò**: Quyết định product direction, prioritize features

**Trách nhiệm**:

- Define product vision và roadmap
- Prioritize features cho MVP và future phases
- Approve releases và major changes
- Stakeholder communication
- Budget management
- Risk management

**Nhu cầu**:

- MVP delivery trong 3-4 tháng
- High code quality, dễ maintain
- Scalable architecture cho future growth
- Good documentation (technical & user)
- Clear success metrics
- Regular progress updates

**Mối quan tâm**:

- Timeline: delivery đúng hạn
- Budget: stay within cost estimate
- Quality: zero critical bugs at launch
- Scalability: support 10,000+ cards/user
- User adoption: validate product-market fit
- Technical debt: không tích lũy quá nhiều

**Success criteria**:

- MVP launch: Q1 2025
- Test coverage: ≥ 70%
- User satisfaction: > 4.5/5 rating
- Week 1 retention: > 80%

### 2.2. Backend Developers

**Vai trò**: Xây dựng API, business logic, database

**Trách nhiệm**:

- Implement REST APIs (auth, folders, decks, cards, review, stats)
- Database design và migrations (Flyway)
- SRS algorithm implementation (Box System)
- Performance optimization (queries, indexes)
- Security implementation (JWT, bcrypt, validation)
- File processing (CSV/Excel import/export)
- Async operations (copy, import large files)
- Unit & integration testing

**Nhu cầu**:

- Clear requirements và acceptance criteria
- Architecture guidelines (Layered, Design Patterns)
- API documentation standards
- Code review process
- Testing framework setup
- Development environment setup (Docker, PostgreSQL)

**Mối quan tâm**:

- Database schema design: normalized vs denormalized
- Performance với large datasets (10,000+ cards, 1,000+ folders)
- Index strategy: composite indexes cho review queries
- Async operations: ThreadPool config, timeout handling
- Folder tree operations: materialized path, circular reference prevention
- Import/Export validation: row-level error reporting
- SRS algorithm correctness: box transitions, interval calculations

**Technical challenges**:

- Folder copy với deep nested structure (depth validation)
- Due cards query optimization (composite index critical!)
- Folder statistics calculation (recursive, cached in folder_stats)
- Import large files: batch processing, memory management
- Async job tracking: in-memory store với TTL

**Success criteria**:

- API response time: < 200ms (95th percentile)
- Test coverage: ≥ 70% (core logic)
- Zero SQL N+1 queries
- All critical paths tested (folder ops, SRS, import)

### 2.3. Frontend Developers (Web & Mobile)

**Vai trò**: Xây dựng user interfaces (React + React Native)

**Trách nhiệm**:

**Web (React + TypeScript)**:

- Implement UI components (Shadcn/ui + Tailwind CSS)
- State management (TanStack Query + Context API + Zustand)
- API integration (Axios với interceptors)
- Responsive design (desktop, tablet)
- Dark/Light mode implementation
- i18n setup (react-i18next)
- Form validation
- Error handling & user feedback
- Performance optimization (lazy loading, pagination, virtual scrolling)

**Mobile (React Native + TypeScript)**:

- Implement mobile UI (React Native Paper)
- Navigation (React Navigation)
- Push notifications (Firebase Cloud Messaging)
- State management (same as Web)
- Platform-specific features (iOS & Android)
- Performance optimization (FlatList, memo)

**Nhu cầu**:

- API documentation rõ ràng (request/response schemas)
- Design mockups / wireframes
- Component library setup (Shadcn/ui, React Native Paper)
- Style guide (colors, typography, spacing)
- Error message standards
- Loading states guidelines
- Authentication flow documentation

**Mối quan tâm**:

- API stability: schema changes cần versioning
- Error handling: consistent error format từ backend
- Loading states: async operations (copy, import) cần progress tracking
- Offline support (future): data caching strategy
- Performance: large lists (1000+ folders) cần virtual scrolling
- Token refresh: automatic với Axios interceptors
- Form validation: client-side + server-side consistency

**Technical challenges**:

- Folder tree UI: expand/collapse, lazy loading
- Review session: smooth card transitions, undo/skip
- Import flow: file upload, preview, progress, error display
- Statistics visualization: charts với box distribution
- Dark mode: theme switching với smooth transition
- Notification handling: in-app + push (mobile)

**Success criteria**:

- UI/UX score: > 4.5/5
- Page load time: < 2s
- Smooth 60fps animations
- Zero accessibility violations (critical)

### 2.4. QA Engineers / Testers

**Vai trò**: Quality assurance, testing

**Trách nhiệm**:

- Test planning (test cases, scenarios)
- Manual testing (exploratory, regression)
- Automated testing (E2E với Playwright/Cypress - future)
- Bug reporting (detailed reproduction steps)
- Performance testing (load testing - future)
- Security testing (basic: SQL injection, XSS)
- User acceptance testing (UAT)

**Nhu cầu**:

- Clear acceptance criteria cho mỗi feature
- Test requirements document
- Testing environments (dev, staging)
- Bug tracking system (GitHub Issues)
- Access to test data (sample users, decks, cards)
- Test user accounts
- Release schedule

**Mối quan tâm**:

**Functional Testing**:

- Folder operations: create, rename, move, copy, delete
- Folder copy validation: depth limit, circular reference
- Deck operations: CRUD, move, copy
- Card operations: CRUD, import, export
- SRS algorithm: box transitions, interval calculations, rating effects
- Review session: due cards accuracy, undo/skip, edit card
- Authentication: login, logout, token refresh
- Statistics: streak calculation, box distribution accuracy

**Non-Functional Testing**:

- Performance: API response time, folder tree load, review session load
- Scalability: large datasets (10,000+ cards, 1,000+ folders)
- Security: password hashing, JWT validation, input sanitization
- Usability: UI/UX, error messages, loading states

**Edge Cases**:

- Async operations timeout (copy, import)
- Import validation: empty rows, missing fields, duplicates
- Folder depth limit (max 10 levels)
- Daily limits (new cards, reviews)
- Network errors: retry logic, error display
- Concurrent operations: race conditions

**Success criteria**:

- Bug detection rate: 80% of bugs found before release
- Test coverage: 100% critical paths tested
- Zero critical bugs at launch
- Regression: < 5% of fixed bugs resurface

### 2.5. DevOps Engineers (Future - Production)

**Vai trò**: Infrastructure, deployment, monitoring

**Trách nhiệm** (MVP - simplified):

- Docker setup cho development
- Database setup (PostgreSQL)
- Environment configuration
- Basic logging

**Trách nhiệm** (Production - future):

- CI/CD pipeline (GitHub Actions)
- Infrastructure as Code (Terraform)
- Container orchestration (Kubernetes)
- Database management (backups, migrations)
- Monitoring & alerting (Prometheus, Grafana)
- Logging aggregation (ELK Stack)
- Security hardening
- Performance tuning
- Incident response

**Nhu cầu** (Production):

- Infrastructure documentation
- Runbooks cho common issues
- Alerting thresholds configuration
- Backup & restore procedures
- Disaster recovery plan
- Scaling strategy

**Mối quan tâm** (Production):

- Resource management: ThreadPool cho async operations
- Database performance: query optimization, connection pooling
- Async job cleanup: expired job data removal
- File upload limits: prevent abuse (50MB max, format validation)
- Rate limiting: prevent API abuse (100 req/min/user)
- Monitoring: API latency, error rates, database query time
- Logs: structured logging, retention policy (30 days)

**Success criteria**:

- Uptime: 99.9% (production)
- Deployment time: < 10 minutes
- Incident response: < 1 hour
- Backup completion: 100% success rate

### 2.6. Security Engineer (Future - Production)

**Vai trò**: Security assessment, vulnerability management

**Mối quan tâm** (MVP - basic security):

- Authentication: email + password với bcrypt (cost 12)
- JWT with Refresh Token: rotation, revocation
- Input validation: import file validation (CSV/XLSX)
- SQL injection prevention: JPA parameterized queries
- XSS prevention: input sanitization
- HTTPS: required cho production
- CORS: whitelist allowed origins
- Rate limiting: basic in-memory (100 req/min/user)

**Mối quan tâm** (Production - enhanced):

- OAuth 2.0 integration (Google, Facebook)
- Multi-factor authentication (MFA)
- Security headers (CSP, HSTS, X-Frame-Options)
- Web Application Firewall (WAF)
- DDoS protection
- Penetration testing
- Security audits
- Compliance (GDPR)
- Data encryption at rest
- Audit logging

**Success criteria**:

- Zero critical vulnerabilities at launch
- Regular security audits (quarterly)
- Incident response time: < 1 hour

## 3. Supporting Stakeholders (Future)

### 3.1. Business Stakeholders

**Investors** (Future - if seeking funding):

- ROI expectations
- Growth metrics (DAU, MAU, retention)
- Monetization strategy (freemium, premium)
- Market size và competition analysis
- User acquisition cost (CAC) vs Lifetime value (LTV)

**Marketing Team** (Future - growth phase):

- User acquisition campaigns
- Onboarding optimization
- Content marketing (blog, tutorials)
- Community management
- Social media integration
- Landing pages
- Email campaigns
- Analytics integration (Google Analytics, Mixpanel)

### 3.2. Content Creators (Future - Phase 3)

**Teachers & Educators**:

- Create và share flashcard sets
- Template library
- Track student progress
- Bulk creation tools

**Subject Matter Experts**:

- Create premium content
- Rich media support (images, audio)
- Monetization (sell decks)

## Stakeholder Matrix

### Power/Interest Grid

```
High Power │
           │  [Project Owner]        [End Users]
           │   (High Interest)      (High Interest)
           │
    Power  │
           │  [Dev Team]            [Content Creators]
           │  (High Interest)        (Medium Interest)
           │
Low Power  │  [Investors]           [Marketing]
           │  (Medium Interest)      (Low Interest)
           │
           └────────────────────────────────────────
              Low Interest          High Interest
```

### Influence vs. Impact

| Stakeholder | Influence | Impact | Priority | Engagement Strategy |
|------------|-----------|---------|----------|---------------------|
| End Users | High | High | **Critical** | Daily feedback, surveys, usage analytics |
| Project Owner | High | High | **Critical** | Weekly updates, demos, roadmap reviews |
| Backend Devs | High | Medium | **High** | Daily standups, code reviews, pair programming |
| Frontend Devs | High | Medium | **High** | Daily standups, design reviews, API sync |
| QA Engineers | Medium | High | **High** | Test planning, bug triage, UAT |
| DevOps | Medium | Medium | **Medium** | Infrastructure reviews, incident response |
| Security | Medium | High | **High** | Security reviews, pen testing, audits |
| Content Creators | Medium | Medium | **Medium** | Feature requests, beta testing (future) |
| Investors | Low | Medium | **Medium** | Quarterly reports (future) |
| Marketing | Low | Low | **Low** | Launch coordination (future) |

## Communication Plan

### End Users

**Channels**:

- In-app announcements (new features, maintenance)
- Email newsletters (optional opt-in)
- GitHub Discussions (Q&A, feature requests)
- Discord/Slack community (future)

**Frequency**:

- Feature updates: Monthly
- Bug fixes: As needed
- Tips & tricks: Weekly (blog/email)
- Surveys: Quarterly (satisfaction, feature requests)

**Feedback mechanisms**:

- In-app feedback button (trong settings)
- GitHub Issues (bug reports, feature requests)
- Support email: <support@repeatwise.com>
- App store reviews (monitor & respond)

### Development Team

**Channels**:

- Daily standups (15 min, video call)
- Sprint planning (bi-weekly, 2 hours)
- Sprint retrospectives (bi-weekly, 1 hour)
- Slack/Discord (async communication)
- GitHub (code reviews, PRs, issues)
- Documentation wiki (Notion, Confluence)

**Frequency**:

- Standups: Daily (9:00 AM)
- Sprint planning: Every 2 weeks (Monday)
- Sprint reviews: Every 2 weeks (Friday)
- Retrospectives: Every 2 weeks (Friday)
- Code reviews: Continuous (within 24h)
- Architecture discussions: Ad-hoc (as needed)

**Artifacts**:

- Sprint backlog (Jira, GitHub Projects)
- Burndown chart (sprint progress)
- Technical documentation (architecture, API docs)
- Meeting notes (decisions, action items)

### Project Owner

**Channels**:

- Weekly progress reports (email + dashboard)
- Sprint demos (bi-weekly, video call)
- One-on-one meetings (bi-weekly, 30 min)
- Roadmap reviews (monthly, 1 hour)

**Metrics dashboard**:

- Sprint velocity (story points completed)
- Test coverage (% coverage trend)
- Bug count (open/closed trend)
- Feature completion (% MVP done)
- Timeline (on track / at risk / delayed)

**Escalation**:

- Blockers: immediate notification (Slack)
- Timeline risks: weekly (progress report)
- Budget overruns: immediate (email + meeting)

## Success Criteria by Stakeholder

### End Users

**Satisfaction**:

- App rating: > 4.5/5 (iOS & Android app stores)
- Net Promoter Score (NPS): > 50 (Excellent)
- Feature completion rate: > 90% (users complete onboarding)

**Engagement**:

- Daily active users (DAU): > 70% of total users
- Average session time: > 15 minutes/day
- Review completion rate: > 80% (finish review sessions)

**Retention**:

- Week 1 retention: > 80%
- Month 1 retention: > 50%
- Streak maintenance: > 30% users maintain 7+ day streak

**Performance**:

- App load time: < 2s
- Page transitions: < 500ms
- API response: < 200ms (perceived as instant)

### Development Team

**Code Quality**:

- Test coverage: ≥ 70% (core logic)
- Code review approval rate: > 95%
- Code duplication: < 5%
- Critical issues (SonarQube): < 10

**Performance**:

- API response time: < 200ms (95th percentile)
- Load folder tree: < 300ms (even with 1000+ folders)
- Review session load: < 500ms (with 100+ due cards)
- Database query time: < 100ms (critical queries)

**Documentation**:

- API endpoints: 100% documented (OpenAPI/Swagger)
- Public methods: 100% JavaDoc
- Architecture docs: Complete (C4 diagrams, design patterns)
- User guides: Complete (onboarding, features)

**Maintainability**:

- Technical debt ratio: < 5% (SonarQube)
- Cyclomatic complexity: < 15 per method
- Method length: < 50 lines (average)
- Clear separation of concerns (Layered Architecture)

### Project Owner

**Timeline**:

- MVP delivered: Q1 2025 (on schedule)
- Sprint velocity: Consistent (variance < 20%)
- Sprint commitment: > 90% completed

**Budget**:

- Stay within budget (cost estimate)
- No unexpected expenses (> 10% budget)

**Quality**:

- Zero critical bugs at launch
- < 5 high-priority bugs at launch
- < 20 medium-priority bugs at launch

**Scalability**:

- Support 10,000+ cards/user
- Support 1,000+ folders/user
- Support 1,000+ concurrent users (future)

## Risk Management by Stakeholder

### End Users

**Risks**:

- Poor user experience → low adoption
- Data loss → user churn
- Slow performance → frustration
- Bugs and crashes → negative reviews

**Mitigation**:

- User testing (beta program)
- Daily backups (automated)
- Performance monitoring (real-time)
- Comprehensive testing (QA process)
- Soft delete (30 days restore window)
- Confirm dialogs (delete operations)

### Development Team

**Risks**:

- Unclear requirements → rework
- Technical debt → maintainability issues
- Resource constraints → delays
- Scope creep → missed deadlines

**Mitigation**:

- Detailed documentation (requirements, architecture)
- Code reviews (enforce quality standards)
- Agile methodology (iterative, adaptive)
- Change management process (prioritize, evaluate impact)
- Regular retrospectives (identify issues early)
- Pair programming (knowledge sharing)

### Project Owner

**Risks**:

- Delayed launch → market opportunity loss
- Over budget → financial strain
- Low adoption → product-market fit issues
- Competition → user acquisition challenges

**Mitigation**:

- Realistic planning (buffer time, contingency)
- Budget tracking (weekly reviews)
- MVP approach (validate early, iterate)
- Market research (understand competitors)
- Beta testing (validate assumptions)
- User feedback loops (continuous improvement)

## Kết luận

Stakeholder management trong RepeatWise MVP tập trung vào:

**Primary Focus** (MVP):

- **End Users**: Validate product-market fit, high satisfaction
- **Development Team**: Deliver quality product on time
- **Project Owner**: Ensure successful MVP launch

**Key Success Factors**:

- Clear communication (daily standups, weekly updates)
- User-centric approach (feedback loops, testing)
- Quality focus (test coverage, code reviews)
- Performance metrics (API latency, user engagement)
- Risk management (technical, business, user)

**Future Expansion**:

- Content creators (Phase 3)
- Business stakeholders (monetization phase)
- Marketing team (growth phase)
- Enterprise customers (Phase 4)

Chiến lược stakeholder engagement đảm bảo tất cả các bên liên quan được inform, engaged và satisfied throughout dự án lifecycle.
