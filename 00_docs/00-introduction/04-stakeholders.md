# Stakeholders - RepeatWise

## Tổng quan

Tài liệu này mô tả các bên liên quan (stakeholders) trong dự án RepeatWise, vai trò, lợi ích và kỳ vọng của họ.

## Primary Stakeholders

### 1. End Users

#### 1.1. Students & Learners

**Đặc điểm**:
- Độ tuổi: 15-35
- Mục đích: Học từ vựng, công thức, khái niệm
- Technical level: Basic to Intermediate

**Nhu cầu**:
- Giao diện đơn giản, dễ sử dụng
- Import nhanh từ file Excel/CSV
- Ôn tập mọi lúc mọi nơi (mobile)
- Theo dõi progress rõ ràng

**Lợi ích**:
- Ghi nhớ hiệu quả hơn 300%
- Tiết kiệm thời gian học
- Tổ chức kiến thức có hệ thống

#### 1.2. Programmers & Tech Professionals

**Đặc điểm**:
- Độ tuổi: 20-40
- Mục đích: Học algorithms, design patterns, syntax
- Technical level: Advanced

**Nhu cầu**:
- Performance tốt với large datasets
- Keyboard shortcuts
- API integration (future)
- Markdown/Code snippets support (future)

**Lợi ích**:
- Ghi nhớ technical concepts
- Chuẩn bị phỏng vấn
- Continuous learning

#### 1.3. Language Learners

**Đặc điểm**:
- Độ tuổi: 18-45
- Mục đích: Học từ vựng, ngữ pháp ngoại ngữ
- Technical level: Basic

**Nhu cầu**:
- Audio support (future)
- Image flashcards (future)
- Pronunciation tracking (future)
- Spaced repetition effective

**Lợi ích**:
- Nhớ từ vựng lâu dài
- Luyện tập hàng ngày
- Track vocabulary progress

### 2. Content Creators (Future)

#### 2.1. Teachers & Educators

**Vai trò**: Tạo và chia sẻ flashcard sets

**Nhu cầu**:
- Bulk creation tools
- Template library
- Share với students
- Track student progress

**Lợi ích**:
- Tạo tài liệu học tập nhanh
- Monitor student engagement
- Reuse content

#### 2.2. Subject Matter Experts

**Vai trò**: Tạo nội dung chuyên môn

**Nhu cầu**:
- Advanced formatting
- Rich media support
- Monetization (future)

**Lợi ích**:
- Share expertise
- Build reputation
- Earn revenue

## Secondary Stakeholders

### 3. Development Team

#### 3.1. Project Owner

**Vai trò**: Quyết định product direction

**Trách nhiệm**:
- Define product vision
- Prioritize features
- Approve releases
- Stakeholder communication

**Kỳ vọng**:
- MVP trong 3-4 tháng
- High code quality
- Scalable architecture
- Good documentation

#### 3.2. Backend Developers

**Vai trò**: Xây dựng API và business logic

**Trách nhiệm**:
- Implement REST APIs
- Database design
- SRS algorithm
- Performance optimization

**Kỳ vọng**:
- Clear requirements
- Architecture guidelines
- Code review process
- Testing standards

#### 3.3. Frontend Developers

**Vai trò**: Xây dựng Web & Mobile UI

**Trách nhiệm**:
- React/React Native implementation
- UI/UX implementation
- API integration
- Responsive design

**Kỳ vọng**:
- Design mockups
- API documentation
- Component library
- Style guide

#### 3.4. QA Engineers

**Vai trò**: Quality assurance

**Trách nhiệm**:
- Test planning
- Manual testing
- Automated testing
- Bug reporting

**Kỳ vọng**:
- Test requirements
- Testing environments
- Bug tracking system
- Release schedule

### 4. Business Stakeholders (Future)

#### 4.1. Investors

**Quan tâm**:
- ROI (Return on Investment)
- Market size
- Growth metrics
- Monetization strategy

**Metrics**:
- User acquisition cost
- Lifetime value (LTV)
- Monthly recurring revenue (MRR)
- Churn rate

#### 4.2. Marketing Team

**Vai trò**: User acquisition và retention

**Trách nhiệm**:
- Marketing campaigns
- User onboarding
- Content marketing
- Community management

**Kỳ vọng**:
- Analytics integration
- Landing pages
- Social media integration
- Email templates

## Stakeholder Matrix

### Power/Interest Grid

```
High Power │
           │  [Project Owner]     [End Users]
           │
           │
    Power  │
           │  [Dev Team]         [Content Creators]
           │  [QA Team]
           │
Low Power  │  [Investors]        [Marketing]
           │
           └────────────────────────────────────
              Low Interest      High Interest
```

### Influence vs. Impact

| Stakeholder | Influence | Impact | Priority |
|------------|-----------|---------|----------|
| End Users | High | High | Critical |
| Project Owner | High | High | Critical |
| Backend Devs | High | Medium | High |
| Frontend Devs | High | Medium | High |
| QA Engineers | Medium | High | High |
| Content Creators | Medium | Medium | Medium |
| Investors | Low | Medium | Medium |
| Marketing | Low | Low | Low |

## Communication Plan

### End Users

**Channels**:
- In-app announcements
- Email newsletters
- Social media
- Blog posts

**Frequency**:
- Feature updates: Monthly
- Bug fixes: As needed
- Tips & tricks: Weekly

### Development Team

**Channels**:
- Daily standups
- Sprint planning
- Slack/Discord
- GitHub issues

**Frequency**:
- Standups: Daily
- Sprint planning: Bi-weekly
- Retrospectives: Bi-weekly
- Code reviews: Continuous

### Project Owner

**Channels**:
- Progress reports
- Demo sessions
- One-on-ones

**Frequency**:
- Progress reports: Weekly
- Demos: End of sprint
- One-on-ones: Bi-weekly

## Success Criteria by Stakeholder

### End Users

- **Satisfaction**: App rating > 4.5/5
- **Engagement**: Daily active users > 70%
- **Retention**: Month 1 retention > 50%
- **Performance**: App load time < 2s

### Development Team

- **Code Quality**: Test coverage > 70%
- **Maintainability**: Code review approval rate > 95%
- **Documentation**: All APIs documented
- **Performance**: API response time < 200ms

### Project Owner

- **Timeline**: MVP delivered in 3-4 months
- **Budget**: Stay within budget
- **Quality**: Zero critical bugs at launch
- **Scalability**: Support 10,000+ cards/user

## Risk Management by Stakeholder

### End Users

**Risks**:
- Poor user experience
- Data loss
- Slow performance
- Bugs and crashes

**Mitigation**:
- User testing
- Daily backups
- Performance monitoring
- Comprehensive testing

### Development Team

**Risks**:
- Unclear requirements
- Technical debt
- Resource constraints
- Scope creep

**Mitigation**:
- Detailed documentation
- Code reviews
- Agile methodology
- Change management process

### Project Owner

**Risks**:
- Delayed launch
- Over budget
- Low adoption
- Competition

**Mitigation**:
- Realistic planning
- Budget tracking
- MVP approach
- Market research

## Feedback Mechanisms

### End Users

- **In-app feedback**: Button trong settings
- **App store reviews**: Monitor và respond
- **Support email**: support@repeatwise.com
- **User surveys**: Quarterly

### Development Team

- **Retrospectives**: Bi-weekly
- **Code reviews**: Continuous
- **Standup meetings**: Daily
- **Anonymous feedback**: Monthly

### Project Owner

- **Sprint demos**: Bi-weekly
- **Progress reports**: Weekly
- **Metrics dashboard**: Real-time
- **Quarterly reviews**: Quarterly

## Stakeholder Engagement Strategy

### Phase 1 - MVP Development

**Focus**: Development team collaboration

**Activities**:
- Daily standups
- Sprint planning
- Code reviews
- Documentation

### Phase 2 - Beta Testing

**Focus**: Early user feedback

**Activities**:
- Beta program
- User interviews
- Bug reporting
- Feature voting

### Phase 3 - Public Launch

**Focus**: User acquisition

**Activities**:
- Marketing campaigns
- Onboarding tutorials
- Community building
- Support system

### Phase 4 - Growth

**Focus**: Scaling and expansion

**Activities**:
- Feature development
- Performance optimization
- Market expansion
- Partnership building
