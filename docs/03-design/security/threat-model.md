# Threat Model

## 1. Overview

Threat Model của RepeatWise được thiết kế để xác định, phân tích và giảm thiểu các mối đe dọa bảo mật trong hệ thống Spaced Repetition System. Model này tuân theo phương pháp STRIDE và tập trung vào việc bảo vệ dữ liệu người dùng và tính toàn vẹn của hệ thống.

## 2. System Architecture Overview

### 2.1 System Components
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Mobile App    │    │   Web App       │    │   API Gateway   │
│   (Flutter)     │    │   (React)       │    │   (Kong)        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Load Balancer │
                    │   (Nginx)       │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   API Server    │
                    │   (Spring Boot) │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Database      │
                    │   (PostgreSQL)  │
                    └─────────────────┘
```

### 2.2 Trust Boundaries
- **External Network**: Internet, Mobile Networks
- **DMZ**: Load Balancer, API Gateway
- **Application Layer**: API Server, Web App
- **Data Layer**: Database, File Storage
- **Internal Network**: Admin Tools, Monitoring

## 3. STRIDE Threat Analysis

### 3.1 Spoofing Threats

#### 3.1.1 User Identity Spoofing
**Threat**: Attacker giả mạo user identity để access dữ liệu
**Attack Vector**: 
- Credential theft
- Session hijacking
- Token manipulation
**Impact**: High - Unauthorized access to user data
**Mitigation**:
- Multi-factor authentication (MFA)
- JWT token với short expiry
- Secure session management
- Rate limiting cho login attempts

#### 3.1.2 API Endpoint Spoofing
**Threat**: Attacker giả mạo API endpoints
**Attack Vector**:
- DNS spoofing
- Man-in-the-middle attacks
**Impact**: Medium - Data interception
**Mitigation**:
- HTTPS/TLS encryption
- Certificate pinning
- API versioning
- Request signing

### 3.2 Tampering Threats

#### 3.2.1 Data Tampering
**Threat**: Attacker thay đổi dữ liệu trong transit hoặc storage
**Attack Vector**:
- SQL injection
- XSS attacks
- Man-in-the-middle
**Impact**: High - Data integrity compromise
**Mitigation**:
- Input validation và sanitization
- Prepared statements
- Data encryption at rest
- Checksums cho critical data

#### 3.2.2 Configuration Tampering
**Threat**: Attacker thay đổi system configuration
**Attack Vector**:
- Unauthorized admin access
- Configuration file manipulation
**Impact**: High - System behavior alteration
**Mitigation**:
- Configuration management
- Immutable infrastructure
- Access control cho config files
- Configuration validation

### 3.3 Repudiation Threats

#### 3.3.1 User Action Repudiation
**Threat**: User phủ nhận thực hiện actions
**Attack Vector**:
- Weak audit trails
- Insufficient logging
**Impact**: Medium - Dispute resolution
**Mitigation**:
- Comprehensive audit logging
- User action tracking
- Digital signatures
- Immutable logs

#### 3.3.2 System Action Repudiation
**Threat**: System actions không thể trace
**Attack Vector**:
- Inadequate system logging
- Log tampering
**Impact**: Medium - Operational transparency
**Mitigation**:
- System event logging
- Log integrity protection
- Centralized logging
- Log retention policies

### 3.4 Information Disclosure Threats

#### 3.4.1 Sensitive Data Exposure
**Threat**: Exposure of user personal data
**Attack Vector**:
- Insecure APIs
- Weak encryption
- Log exposure
**Impact**: High - Privacy violation
**Mitigation**:
- Data classification
- Encryption at rest và in transit
- API security headers
- Data masking

#### 3.4.2 System Information Disclosure
**Threat**: Exposure of system architecture
**Attack Vector**:
- Error messages
- Version disclosure
- Directory listing
**Impact**: Medium - Reconnaissance aid
**Mitigation**:
- Generic error messages
- Security headers
- Information hiding
- Regular security scans

### 3.5 Denial of Service Threats

#### 3.5.1 Application DoS
**Threat**: Service unavailability
**Attack Vector**:
- Resource exhaustion
- API abuse
- Database overload
**Impact**: High - Service disruption
**Mitigation**:
- Rate limiting
- Resource monitoring
- Auto-scaling
- DDoS protection

#### 3.5.2 Database DoS
**Threat**: Database performance degradation
**Attack Vector**:
- Slow queries
- Connection exhaustion
- Storage exhaustion
**Impact**: High - Data access disruption
**Mitigation**:
- Query optimization
- Connection pooling
- Storage monitoring
- Database indexing

### 3.6 Elevation of Privilege Threats

#### 3.6.1 User Privilege Escalation
**Threat**: User gain unauthorized privileges
**Attack Vector**:
- Weak authorization
- Role manipulation
- Session elevation
**Impact**: High - Unauthorized access
**Mitigation**:
- Role-based access control (RBAC)
- Principle of least privilege
- Session validation
- Privilege separation

#### 3.6.2 System Privilege Escalation
**Threat**: Process gain system privileges
**Attack Vector**:
- Vulnerable dependencies
- Configuration flaws
- Exploit chains
**Impact**: Critical - System compromise
**Mitigation**:
- Regular security updates
- Vulnerability scanning
- Container security
- Process isolation

## 4. Attack Trees

### 4.1 User Data Compromise Attack Tree
```
User Data Compromise
├── Authentication Bypass
│   ├── Weak Password
│   ├── Brute Force Attack
│   ├── Session Hijacking
│   └── Token Theft
├── Authorization Bypass
│   ├── Role Manipulation
│   ├── API Abuse
│   └── Direct Object Reference
├── Data Interception
│   ├── Man-in-the-Middle
│   ├── Network Sniffing
│   └── API Response Exposure
└── Data Extraction
    ├── SQL Injection
    ├── XSS Data Theft
    └── Log Information Leakage
```

### 4.2 System Compromise Attack Tree
```
System Compromise
├── Infrastructure Attack
│   ├── Server Exploitation
│   ├── Database Compromise
│   └── Network Penetration
├── Application Attack
│   ├── Code Injection
│   ├── Configuration Manipulation
│   └── Dependency Exploitation
├── Supply Chain Attack
│   ├── Malicious Dependencies
│   ├── Compromised Build Process
│   └── Third-party Service Breach
└── Social Engineering
    ├── Phishing
    ├── Credential Harvesting
    └── Insider Threat
```

## 5. Risk Assessment Matrix

### 5.1 Risk Calculation
**Risk = Probability × Impact**

| Probability | Description | Value |
|-------------|-------------|-------|
| Very Low | < 1% chance per year | 1 |
| Low | 1-10% chance per year | 2 |
| Medium | 10-30% chance per year | 3 |
| High | 30-70% chance per year | 4 |
| Very High | > 70% chance per year | 5 |

| Impact | Description | Value |
|--------|-------------|-------|
| Very Low | Minimal business impact | 1 |
| Low | Minor business impact | 2 |
| Medium | Moderate business impact | 3 |
| High | Significant business impact | 4 |
| Critical | Severe business impact | 5 |

### 5.2 Risk Levels
- **Low Risk (1-4)**: Acceptable, monitor
- **Medium Risk (5-9)**: Mitigate, reduce probability/impact
- **High Risk (10-15)**: High priority mitigation
- **Critical Risk (16-25)**: Immediate action required

### 5.3 Risk Assessment Results

| Threat | Probability | Impact | Risk Score | Priority |
|--------|-------------|--------|------------|----------|
| User Data Breach | Medium (3) | High (4) | 12 | High |
| SQL Injection | Low (2) | High (4) | 8 | Medium |
| XSS Attack | Medium (3) | Medium (3) | 9 | Medium |
| DDoS Attack | High (4) | Medium (3) | 12 | High |
| Authentication Bypass | Low (2) | High (4) | 8 | Medium |
| Privilege Escalation | Low (2) | Critical (5) | 10 | High |
| Data Tampering | Low (2) | High (4) | 8 | Medium |
| Log Information Leakage | Medium (3) | Medium (3) | 9 | Medium |

## 6. Security Controls

### 6.1 Preventive Controls

#### 6.1.1 Authentication Controls
- **Multi-Factor Authentication (MFA)**
  - SMS/Email verification
  - TOTP (Time-based One-Time Password)
  - Biometric authentication (mobile)
- **Password Policy**
  - Minimum 8 characters
  - Complexity requirements
  - Regular password rotation
  - Password history enforcement
- **Session Management**
  - Secure session tokens
  - Session timeout
  - Concurrent session limits
  - Session invalidation

#### 6.1.2 Authorization Controls
- **Role-Based Access Control (RBAC)**
  - User roles: Student, Teacher, Admin
  - Resource-based permissions
  - Dynamic permission checking
- **API Security**
  - Rate limiting
  - Request validation
  - Input sanitization
  - Output encoding

#### 6.1.3 Data Protection Controls
- **Encryption**
  - AES-256 for data at rest
  - TLS 1.3 for data in transit
  - Key management system
- **Data Classification**
  - Public data
  - Internal data
  - Confidential data
  - Restricted data

### 6.2 Detective Controls

#### 6.2.1 Monitoring and Logging
- **Security Event Logging**
  - Authentication events
  - Authorization failures
  - Data access patterns
  - System changes
- **Real-time Monitoring**
  - Anomaly detection
  - Threat intelligence
  - Behavioral analysis
  - Performance monitoring

#### 6.2.2 Vulnerability Management
- **Regular Security Scans**
  - Dependency scanning
  - Container scanning
  - Infrastructure scanning
  - Application scanning
- **Penetration Testing**
  - Annual external testing
  - Quarterly internal testing
  - Bug bounty program
  - Security code review

### 6.3 Responsive Controls

#### 6.3.1 Incident Response
- **Incident Detection**
  - Automated alerts
  - Manual reporting
  - Threat intelligence feeds
  - User reports
- **Response Procedures**
  - Incident classification
  - Escalation matrix
  - Communication plan
  - Recovery procedures

#### 6.3.2 Business Continuity
- **Disaster Recovery**
  - Data backup strategies
  - System recovery procedures
  - Alternative infrastructure
  - Communication channels
- **Data Recovery**
  - Point-in-time recovery
  - Data integrity verification
  - Service restoration
  - Post-incident analysis

## 7. Security Testing Strategy

### 7.1 Automated Testing
- **Static Application Security Testing (SAST)**
  - Code analysis
  - Dependency scanning
  - Configuration validation
- **Dynamic Application Security Testing (DAST)**
  - Vulnerability scanning
  - API security testing
  - Web application testing

### 7.2 Manual Testing
- **Penetration Testing**
  - External network testing
  - Web application testing
  - Mobile application testing
  - Social engineering testing
- **Security Code Review**
  - Manual code analysis
  - Architecture review
  - Configuration review

### 7.3 Continuous Security
- **DevSecOps Integration**
  - Security in CI/CD pipeline
  - Automated security gates
  - Security metrics tracking
  - Compliance monitoring

## 8. Compliance Requirements

### 8.1 GDPR Compliance
- **Data Protection Principles**
  - Lawful processing
  - Purpose limitation
  - Data minimization
  - Accuracy
  - Storage limitation
  - Integrity and confidentiality
- **User Rights**
  - Right to access
  - Right to rectification
  - Right to erasure
  - Right to portability
  - Right to object

### 8.2 Industry Standards
- **OWASP Top 10**
  - Injection prevention
  - Authentication security
  - Sensitive data protection
  - XML external entity prevention
  - Access control
  - Security misconfiguration
  - XSS prevention
  - Insecure deserialization
  - Vulnerable components
  - Insufficient logging

### 8.3 Security Frameworks
- **ISO 27001 Alignment**
  - Information security management
  - Risk assessment
  - Security controls
  - Continuous improvement
- **NIST Cybersecurity Framework**
  - Identify
  - Protect
  - Detect
  - Respond
  - Recover

## 9. Security Metrics and KPIs

### 9.1 Security Performance Metrics
- **Vulnerability Management**
  - Time to patch critical vulnerabilities
  - Number of open vulnerabilities
  - Vulnerability scan coverage
- **Incident Response**
  - Mean time to detect (MTTD)
  - Mean time to respond (MTTR)
  - Incident resolution rate
- **Access Control**
  - Failed authentication attempts
  - Privilege escalation attempts
  - Unauthorized access attempts

### 9.2 Compliance Metrics
- **GDPR Compliance**
  - Data subject requests processed
  - Data breach notifications
  - Privacy impact assessments
- **Security Standards**
  - OWASP Top 10 compliance
  - Security control effectiveness
  - Audit findings resolution

## 10. Threat Model Maintenance

### 10.1 Regular Updates
- **Quarterly Review**
  - Threat landscape analysis
  - New attack vectors
  - Control effectiveness
  - Risk reassessment
- **Annual Refresh**
  - Complete threat model review
  - Architecture changes
  - New compliance requirements
  - Technology updates

### 10.2 Continuous Improvement
- **Lessons Learned**
  - Incident analysis
  - Security testing results
  - User feedback
  - Industry trends
- **Process Improvement**
  - Security procedures
  - Response capabilities
  - Monitoring effectiveness
  - Training programs 
