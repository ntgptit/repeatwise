# Stakeholder Map

## 1. Primary Stakeholders

### 1.1 End Users (Students)
**Role**: Người dùng cuối, học viên sử dụng ứng dụng
**Decision Rights**: 
- Quyết định tạo, chỉnh sửa, xóa set học tập
- Quyết định nhập điểm số và skip lần ôn
- Quyết định reschedule reminder
**Responsibilities**:
- Sử dụng ứng dụng theo đúng quy trình SRS
- Nhập điểm số chính xác sau mỗi lần ôn
- Tuân thủ lịch học được đề xuất
- Báo cáo vấn đề và feedback

### 1.2 Product Owner
**Role**: Chủ sở hữu sản phẩm, quyết định roadmap
**Decision Rights**:
- Quyết định tính năng ưu tiên phát triển
- Phê duyệt thay đổi yêu cầu nghiệp vụ
- Quyết định timeline và milestone
**Responsibilities**:
- Định nghĩa vision và strategy
- Quản lý product backlog
- Đảm bảo ROI và business value
- Giao tiếp với stakeholders

## 2. Secondary Stakeholders

### 2.1 Development Team
**Role**: Đội phát triển kỹ thuật
**Decision Rights**:
- Quyết định technical implementation
- Quyết định architecture và design patterns
- Quyết định coding standards
**Responsibilities**:
- Phát triển tính năng theo specification
- Đảm bảo code quality và performance
- Viết unit tests và integration tests
- Code review và knowledge sharing

### 2.2 QA Team
**Role**: Đội đảm bảo chất lượng
**Decision Rights**:
- Quyết định test strategy và approach
- Quyết định test data và test cases
- Quyết định release criteria
**Responsibilities**:
- Thiết kế và thực hiện test cases
- Báo cáo bugs và issues
- Đảm bảo test coverage
- Validation user acceptance criteria

### 2.3 DevOps Team
**Role**: Đội vận hành và triển khai
**Decision Rights**:
- Quyết định deployment strategy
- Quyết định infrastructure setup
- Quyết định monitoring và alerting
**Responsibilities**:
- Setup CI/CD pipeline
- Quản lý production environment
- Monitoring system performance
- Backup và disaster recovery

## 3. External Stakeholders

### 3.1 Database Administrator
**Role**: Quản trị viên cơ sở dữ liệu
**Decision Rights**:
- Quyết định database schema changes
- Quyết định backup và recovery strategy
- Quyết định performance optimization
**Responsibilities**:
- Quản lý PostgreSQL database
- Đảm bảo data integrity và security
- Performance tuning và monitoring
- Backup và restore procedures

### 3.2 Security Team
**Role**: Đội bảo mật
**Decision Rights**:
- Quyết định security policies
- Quyết định authentication/authorization
- Quyết định data protection measures
**Responsibilities**:
- Security audit và penetration testing
- Đảm bảo compliance với security standards
- Monitoring security threats
- Incident response và recovery

## 4. Business Stakeholders

### 4.1 Business Analyst
**Role**: Phân tích nghiệp vụ
**Decision Rights**:
- Quyết định business requirements
- Quyết định process improvements
- Quyết định data analysis approach
**Responsibilities**:
- Phân tích yêu cầu nghiệp vụ
- Tạo business specifications
- Validate solution với stakeholders
- Đo lường business metrics

### 4.2 UX/UI Designer
**Role**: Thiết kế trải nghiệm người dùng
**Decision Rights**:
- Quyết định user interface design
- Quyết định user experience flow
- Quyết định design system
**Responsibilities**:
- Thiết kế wireframes và mockups
- Conduct user research và testing
- Đảm bảo accessibility standards
- Maintain design consistency

## 5. Stakeholder Communication Matrix

| Stakeholder | Communication Frequency | Communication Channel | Information Type |
|-------------|-------------------------|----------------------|------------------|
| End Users | Weekly | In-app notifications, Email | Feature updates, Tips |
| Product Owner | Daily | Slack, Email, Meetings | Progress updates, Decisions |
| Development Team | Daily | Slack, Jira, Standups | Technical updates, Blockers |
| QA Team | Daily | Slack, Test reports | Bug reports, Test results |
| DevOps Team | Weekly | Slack, Monitoring alerts | System status, Deployments |
| Business Analyst | Weekly | Email, Meetings | Requirements, Analysis |
| UX/UI Designer | As needed | Slack, Design tools | Design reviews, Feedback |

## 6. Decision Making Authority

### 6.1 Strategic Decisions
- **Product Owner**: Roadmap, feature priorities, business strategy
- **Business Analyst**: Business requirements, process changes
- **Security Team**: Security policies, compliance requirements

### 6.2 Technical Decisions
- **Development Team**: Architecture, technology stack, implementation
- **DevOps Team**: Infrastructure, deployment, monitoring
- **Database Administrator**: Database design, performance optimization

### 6.3 User Experience Decisions
- **UX/UI Designer**: Interface design, user flow
- **Product Owner**: Feature scope, user value
- **End Users**: Feedback, usability testing

## 7. Escalation Path

### 7.1 Technical Issues
1. Development Team → DevOps Team → CTO
2. Performance Issues → DevOps Team → Database Administrator
3. Security Issues → Security Team → CISO

### 7.2 Business Issues
1. Product Owner → Business Analyst → CEO
2. User Complaints → Product Owner → Customer Success
3. Compliance Issues → Legal Team → CEO

### 7.3 Resource Conflicts
1. Team Lead → Product Owner → Project Manager
2. Priority Conflicts → Product Owner → CEO
3. Budget Issues → Product Owner → CFO 
