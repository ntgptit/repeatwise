# Data Classification and Protection

## 1. Overview

Data Classification và Protection framework của RepeatWise được thiết kế để đảm bảo bảo mật dữ liệu theo mức độ nhạy cảm và tuân thủ các quy định về bảo vệ dữ liệu cá nhân. Framework này áp dụng các biện pháp bảo vệ phù hợp cho từng loại dữ liệu.

## 2. Data Classification Levels

### 2.1 Classification Matrix

| Classification Level | Description | Examples | Protection Level |
|---------------------|-------------|----------|------------------|
| **Public** | Dữ liệu có thể chia sẻ công khai | Marketing materials, Public sets, System status | Basic |
| **Internal** | Dữ liệu nội bộ, không nhạy cảm | System logs, Performance metrics, Configuration | Standard |
| **Confidential** | Dữ liệu nhạy cảm, cần bảo vệ | User preferences, Learning progress, Analytics | High |
| **Restricted** | Dữ liệu rất nhạy cảm, cần bảo vệ cao nhất | Personal information, Authentication data, Financial data | Maximum |

### 2.2 Classification Criteria

#### 2.2.1 Public Data
**Criteria**:
- Không chứa thông tin cá nhân
- Có thể chia sẻ công khai
- Không ảnh hưởng đến bảo mật hệ thống

**Examples**:
- Marketing materials
- Public learning sets
- System status information
- General statistics (anonymized)

#### 2.2.2 Internal Data
**Criteria**:
- Dữ liệu nội bộ của tổ chức
- Không chứa thông tin cá nhân
- Cần bảo vệ khỏi truy cập trái phép

**Examples**:
- System configuration
- Performance metrics
- Operational logs
- Internal documentation

#### 2.2.3 Confidential Data
**Criteria**:
- Chứa thông tin cá nhân
- Cần bảo vệ theo quy định pháp luật
- Có thể gây hại nếu bị lộ

**Examples**:
- User learning progress
- Personal preferences
- Analytics data
- Communication history

#### 2.2.4 Restricted Data
**Criteria**:
- Dữ liệu rất nhạy cảm
- Cần bảo vệ cao nhất
- Có thể gây hại nghiêm trọng nếu bị lộ

**Examples**:
- Authentication credentials
- Personal identification data
- Financial information
- Health-related data

## 3. Data Classification by Entity

### 3.1 User Data Classification

#### 3.1.1 User Profile Data
```java
public enum UserDataClassification {
    // Restricted Data
    AUTHENTICATION_CREDENTIALS("restricted", "password_hash", "mfa_secret"),
    PERSONAL_IDENTIFIERS("restricted", "email", "phone_number"),
    
    // Confidential Data
    PERSONAL_INFORMATION("confidential", "full_name", "preferred_language", "timezone"),
    LEARNING_PREFERENCES("confidential", "learning_goals", "study_schedule"),
    PROFILE_SETTINGS("confidential", "notification_preferences", "privacy_settings"),
    
    // Internal Data
    ACCOUNT_METADATA("internal", "created_at", "last_login", "account_status"),
    
    // Public Data
    PUBLIC_PROFILE("public", "username", "avatar_url", "bio");
}
```

#### 3.1.2 User Activity Data
```java
public enum UserActivityClassification {
    // Confidential Data
    LEARNING_PROGRESS("confidential", "review_scores", "learning_patterns"),
    STUDY_HISTORY("confidential", "study_sessions", "time_spent"),
    PERSONAL_ANALYTICS("confidential", "performance_metrics", "improvement_trends"),
    
    // Internal Data
    SYSTEM_INTERACTIONS("internal", "login_attempts", "feature_usage"),
    TECHNICAL_METRICS("internal", "app_version", "device_info");
}
```

### 3.2 Learning Data Classification

#### 3.2.1 Set Data
```java
public enum SetDataClassification {
    // Public Data
    PUBLIC_SETS("public", "name", "description", "category", "word_count"),
    
    // Confidential Data
    PRIVATE_SETS("confidential", "personal_notes", "custom_content"),
    LEARNING_PROGRESS("confidential", "completion_status", "mastery_level"),
    
    // Internal Data
    SET_METADATA("internal", "created_at", "last_modified", "access_count");
}
```

#### 3.2.2 Review Data
```java
public enum ReviewDataClassification {
    // Confidential Data
    REVIEW_SCORES("confidential", "individual_scores", "performance_trends"),
    LEARNING_PATTERNS("confidential", "study_schedule", "preferred_times"),
    PERSONAL_NOTES("confidential", "review_notes", "learning_insights"),
    
    // Internal Data
    REVIEW_METADATA("internal", "review_duration", "completion_time");
}
```

### 3.3 System Data Classification

#### 3.3.1 Operational Data
```java
public enum SystemDataClassification {
    // Internal Data
    SYSTEM_LOGS("internal", "application_logs", "error_logs", "performance_logs"),
    INFRASTRUCTURE_DATA("internal", "server_metrics", "database_performance"),
    CONFIGURATION_DATA("internal", "system_config", "feature_flags"),
    
    // Public Data
    SYSTEM_STATUS("public", "uptime", "service_availability");
}
```

## 4. Data Protection Measures

### 4.1 Encryption Standards

#### 4.1.1 Data at Rest Encryption
```java
public class DataEncryptionService {
    
    // AES-256 for confidential and restricted data
    public String encryptSensitiveData(String data, String keyId) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = getKeyFromKMS(keyId);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            byte[] iv = cipher.getIV();
            
            return Base64.getEncoder().encodeToString(iv) + ":" + 
                   Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }
    
    // AES-128 for internal data
    public String encryptInternalData(String data) {
        // Similar implementation with AES-128
    }
}
```

#### 4.1.2 Data in Transit Encryption
```java
@Configuration
public class TransportSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.requiresChannel()
            .anyRequest().requiresSecure(); // Force HTTPS
        
        return http.build();
    }
    
    // TLS 1.3 configuration
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty("sslProtocol", "TLSv1.3");
            connector.setProperty("sslEnabledProtocols", "TLSv1.3");
        });
        return factory;
    }
}
```

### 4.2 Access Control by Classification

#### 4.2.1 Access Control Matrix
```java
public class DataAccessControlService {
    
    public boolean canAccessData(String userId, String dataId, DataClassification classification) {
        switch (classification) {
            case PUBLIC:
                return true; // Anyone can access
                
            case INTERNAL:
                return isInternalUser(userId) || isAdmin(userId);
                
            case CONFIDENTIAL:
                return isDataOwner(userId, dataId) || 
                       hasConfidentialAccess(userId) || 
                       isAdmin(userId);
                
            case RESTRICTED:
                return isDataOwner(userId, dataId) && 
                       hasRestrictedAccess(userId) && 
                       isMFAVerified(userId);
                
            default:
                return false;
        }
    }
}
```

#### 4.2.2 Data Masking
```java
public class DataMaskingService {
    
    public String maskPersonalData(String data, DataClassification classification) {
        switch (classification) {
            case CONFIDENTIAL:
                return maskEmail(data);
            case RESTRICTED:
                return maskAllPersonalData(data);
            default:
                return data;
        }
    }
    
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return username + "***@" + domain;
        }
        
        return username.substring(0, 2) + "***@" + domain;
    }
}
```

### 4.3 Data Retention Policies

#### 4.3.1 Retention Schedule
```java
public enum DataRetentionPolicy {
    // Public Data - No specific retention
    PUBLIC_DATA(null, "Keep until no longer needed"),
    
    // Internal Data - 2 years
    INTERNAL_DATA(Duration.ofDays(730), "Archive after 2 years"),
    
    // Confidential Data - 5 years
    CONFIDENTIAL_DATA(Duration.ofDays(1825), "Archive after 5 years"),
    
    // Restricted Data - 10 years
    RESTRICTED_DATA(Duration.ofDays(3650), "Archive after 10 years");
    
    private final Duration retentionPeriod;
    private final String description;
}
```

#### 4.3.2 Data Lifecycle Management
```java
@Component
public class DataLifecycleService {
    
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void manageDataLifecycle() {
        // Archive expired data
        archiveExpiredData();
        
        // Delete data past retention period
        deleteExpiredData();
        
        // Update data classification
        updateDataClassification();
    }
    
    private void archiveExpiredData() {
        List<DataRecord> expiredRecords = dataRepository.findExpiredRecords();
        for (DataRecord record : expiredRecords) {
            archiveService.archive(record);
            dataRepository.markAsArchived(record.getId());
        }
    }
}
```

## 5. Data Handling Procedures

### 5.1 Data Collection

#### 5.1.1 Consent Management
```java
public class ConsentManagementService {
    
    public boolean hasValidConsent(String userId, ConsentType consentType) {
        UserConsent consent = consentRepository.findByUserIdAndType(userId, consentType);
        return consent != null && 
               consent.isActive() && 
               !consent.isExpired();
    }
    
    public void recordConsent(String userId, ConsentType consentType, 
                            String purpose, String legalBasis) {
        UserConsent consent = UserConsent.builder()
            .userId(userId)
            .consentType(consentType)
            .purpose(purpose)
            .legalBasis(legalBasis)
            .grantedAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofDays(365)))
            .active(true)
            .build();
        
        consentRepository.save(consent);
    }
}
```

#### 5.1.2 Data Minimization
```java
public class DataMinimizationService {
    
    public boolean isDataMinimized(String dataType, String purpose) {
        // Check if collected data is minimal for the purpose
        Set<String> requiredFields = getRequiredFieldsForPurpose(purpose);
        Set<String> collectedFields = getCollectedFields(dataType);
        
        return collectedFields.containsAll(requiredFields) && 
               collectedFields.size() == requiredFields.size();
    }
    
    public void validateDataCollection(DataCollectionRequest request) {
        if (!isDataMinimized(request.getDataType(), request.getPurpose())) {
            throw new DataMinimizationException("Data collection exceeds minimum requirements");
        }
    }
}
```

### 5.2 Data Processing

#### 5.2.1 Purpose Limitation
```java
public class PurposeLimitationService {
    
    public boolean isProcessingAllowed(String userId, String dataType, String purpose) {
        // Check if processing is allowed for the stated purpose
        UserConsent consent = consentRepository.findByUserIdAndDataType(userId, dataType);
        
        if (consent == null || !consent.isActive()) {
            return false;
        }
        
        return consent.getPurposes().contains(purpose);
    }
    
    public void validateProcessingPurpose(String userId, String dataType, String purpose) {
        if (!isProcessingAllowed(userId, dataType, purpose)) {
            throw new PurposeLimitationException("Processing not allowed for this purpose");
        }
    }
}
```

#### 5.2.2 Data Accuracy
```java
public class DataAccuracyService {
    
    public void validateDataAccuracy(String dataType, Object data) {
        switch (dataType) {
            case "email":
                validateEmailFormat((String) data);
                break;
            case "phone":
                validatePhoneFormat((String) data);
                break;
            case "score":
                validateScoreRange((Integer) data);
                break;
            default:
                // Generic validation
                validateGenericData(data);
        }
    }
    
    private void validateEmailFormat(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new DataAccuracyException("Invalid email format");
        }
    }
}
```

### 5.3 Data Sharing

#### 5.3.1 Third-Party Sharing
```java
public class DataSharingService {
    
    public boolean canShareWithThirdParty(String userId, String thirdPartyId, 
                                        String dataType, String purpose) {
        // Check user consent for third-party sharing
        if (!hasThirdPartySharingConsent(userId, thirdPartyId)) {
            return false;
        }
        
        // Check third-party data protection standards
        if (!thirdPartyService.meetsDataProtectionStandards(thirdPartyId)) {
            return false;
        }
        
        // Check data classification allows sharing
        DataClassification classification = getDataClassification(dataType);
        return classification != DataClassification.RESTRICTED;
    }
    
    public void shareData(String userId, String thirdPartyId, String dataType, 
                         String purpose, Object data) {
        if (!canShareWithThirdParty(userId, thirdPartyId, dataType, purpose)) {
            throw new DataSharingException("Data sharing not allowed");
        }
        
        // Log data sharing
        logDataSharing(userId, thirdPartyId, dataType, purpose);
        
        // Share data with appropriate protection
        thirdPartyService.receiveData(thirdPartyId, data, getProtectionLevel(dataType));
    }
}
```

## 6. Data Breach Response

### 6.1 Breach Detection

#### 6.1.1 Anomaly Detection
```java
@Component
public class DataBreachDetectionService {
    
    @EventListener
    public void handleDataAccessEvent(DataAccessEvent event) {
        // Check for suspicious access patterns
        if (isSuspiciousAccess(event)) {
            triggerBreachInvestigation(event);
        }
        
        // Check for data exfiltration patterns
        if (isDataExfiltration(event)) {
            triggerDataBreachResponse(event);
        }
    }
    
    private boolean isSuspiciousAccess(DataAccessEvent event) {
        // Check for unusual access patterns
        return event.getAccessCount() > getThreshold(event.getUserId()) ||
               event.getAccessTime().isAfter(LocalTime.of(23, 0)) ||
               event.getIpAddress().isInBlacklist();
    }
}
```

#### 6.1.2 Data Loss Prevention
```java
@Component
public class DataLossPreventionService {
    
    public void monitorDataTransfer(DataTransferEvent event) {
        // Check for unauthorized data transfer
        if (isUnauthorizedTransfer(event)) {
            blockTransfer(event);
            notifySecurityTeam(event);
        }
        
        // Check for sensitive data in non-secure channels
        if (containsSensitiveData(event.getData()) && !isSecureChannel(event.getChannel())) {
            blockTransfer(event);
            logSecurityViolation(event);
        }
    }
    
    private boolean containsSensitiveData(String data) {
        // Check for patterns indicating sensitive data
        return data.matches(".*\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b.*") ||
               data.matches(".*\\b\\d{3}-\\d{2}-\\d{4}\\b.*") ||
               data.matches(".*\\b\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}\\b.*");
    }
}
```

### 6.2 Breach Response

#### 6.2.1 Incident Response Plan
```java
public class DataBreachResponseService {
    
    public void handleDataBreach(DataBreachEvent event) {
        // Step 1: Contain the breach
        containBreach(event);
        
        // Step 2: Assess the impact
        BreachImpact impact = assessImpact(event);
        
        // Step 3: Notify stakeholders
        notifyStakeholders(event, impact);
        
        // Step 4: Implement remediation
        implementRemediation(event, impact);
        
        // Step 5: Document and learn
        documentBreach(event, impact);
    }
    
    private void containBreach(DataBreachEvent event) {
        // Isolate affected systems
        systemIsolationService.isolate(event.getAffectedSystems());
        
        // Revoke compromised credentials
        credentialService.revokeCompromisedCredentials(event.getAffectedUsers());
        
        // Block suspicious IP addresses
        networkSecurityService.blockIPs(event.getSuspiciousIPs());
    }
    
    private BreachImpact assessImpact(DataBreachEvent event) {
        return BreachImpact.builder()
            .affectedUsers(event.getAffectedUsers().size())
            .dataTypes(event.getAffectedDataTypes())
            .severity(calculateSeverity(event))
            .complianceImpact(assessComplianceImpact(event))
            .build();
    }
}
```

#### 6.2.2 Notification Procedures
```java
public class BreachNotificationService {
    
    public void notifyAffectedUsers(DataBreachEvent event, BreachImpact impact) {
        for (String userId : event.getAffectedUsers()) {
            UserNotification notification = createBreachNotification(userId, impact);
            notificationService.send(notification);
        }
    }
    
    public void notifyRegulatoryAuthorities(DataBreachEvent event, BreachImpact impact) {
        if (impact.getSeverity() == BreachSeverity.HIGH || 
            impact.getSeverity() == BreachSeverity.CRITICAL) {
            
            RegulatoryNotification notification = createRegulatoryNotification(event, impact);
            regulatoryService.submit(notification);
        }
    }
    
    private UserNotification createBreachNotification(String userId, BreachImpact impact) {
        return UserNotification.builder()
            .userId(userId)
            .type(NotificationType.DATA_BREACH)
            .title("Security Notice: Data Breach")
            .message(createBreachMessage(impact))
            .actions(Arrays.asList("Change Password", "Enable MFA", "Review Activity"))
            .priority(NotificationPriority.HIGH)
            .build();
    }
}
```

## 7. Compliance Monitoring

### 7.1 GDPR Compliance

#### 7.1.1 Data Subject Rights
```java
public class DataSubjectRightsService {
    
    public void handleDataSubjectRequest(DataSubjectRequest request) {
        switch (request.getType()) {
            case ACCESS:
                handleAccessRequest(request);
                break;
            case RECTIFICATION:
                handleRectificationRequest(request);
                break;
            case ERASURE:
                handleErasureRequest(request);
                break;
            case PORTABILITY:
                handlePortabilityRequest(request);
                break;
            case RESTRICTION:
                handleRestrictionRequest(request);
                break;
            case OBJECTION:
                handleObjectionRequest(request);
                break;
        }
    }
    
    private void handleAccessRequest(DataSubjectRequest request) {
        // Collect all personal data
        List<PersonalData> personalData = collectPersonalData(request.getUserId());
        
        // Generate data export
        DataExport export = DataExport.builder()
            .userId(request.getUserId())
            .data(personalData)
            .format(ExportFormat.JSON)
            .generatedAt(Instant.now())
            .build();
        
        // Send to user
        dataExportService.sendToUser(export);
    }
    
    private void handleErasureRequest(DataSubjectRequest request) {
        // Anonymize personal data
        anonymizePersonalData(request.getUserId());
        
        // Keep learning progress for system functionality
        preserveLearningProgress(request.getUserId());
        
        // Confirm erasure
        notifyUserOfErasure(request.getUserId());
    }
}
```

#### 7.1.2 Privacy Impact Assessment
```java
public class PrivacyImpactAssessmentService {
    
    public PrivacyImpactAssessment conductPIA(String dataProcessingActivity) {
        return PrivacyImpactAssessment.builder()
            .activity(dataProcessingActivity)
            .dataTypes(identifyDataTypes(dataProcessingActivity))
            .purposes(identifyPurposes(dataProcessingActivity))
            .risks(assessPrivacyRisks(dataProcessingActivity))
            .mitigations(proposeMitigations(dataProcessingActivity))
            .complianceStatus(assessCompliance(dataProcessingActivity))
            .build();
    }
    
    private List<PrivacyRisk> assessPrivacyRisks(String activity) {
        List<PrivacyRisk> risks = new ArrayList<>();
        
        // Check for high-risk processing
        if (isHighRiskProcessing(activity)) {
            risks.add(PrivacyRisk.HIGH_RISK_PROCESSING);
        }
        
        // Check for large-scale processing
        if (isLargeScaleProcessing(activity)) {
            risks.add(PrivacyRisk.LARGE_SCALE_PROCESSING);
        }
        
        // Check for special categories of data
        if (processesSpecialCategories(activity)) {
            risks.add(PrivacyRisk.SPECIAL_CATEGORIES);
        }
        
        return risks;
    }
}
```

### 7.2 Compliance Reporting

#### 7.2.1 Compliance Metrics
```java
@Component
public class ComplianceReportingService {
    
    @Scheduled(cron = "0 0 1 * * *") // Monthly at 1 AM
    public void generateComplianceReport() {
        ComplianceReport report = ComplianceReport.builder()
            .period(getCurrentPeriod())
            .dataSubjectRequests(getDataSubjectRequests())
            .dataBreaches(getDataBreaches())
            .consentCompliance(getConsentCompliance())
            .dataRetentionCompliance(getRetentionCompliance())
            .securityIncidents(getSecurityIncidents())
            .build();
        
        complianceRepository.save(report);
        notifyComplianceTeam(report);
    }
    
    private ConsentCompliance getConsentCompliance() {
        return ConsentCompliance.builder()
            .totalUsers(userRepository.count())
            .usersWithValidConsent(consentRepository.countValidConsents())
            .consentRate(calculateConsentRate())
            .expiredConsents(consentRepository.countExpiredConsents())
            .build();
    }
}
```

#### 7.2.2 Audit Trail
```java
@Component
public class ComplianceAuditService {
    
    public void logComplianceEvent(ComplianceEvent event) {
        ComplianceAuditLog log = ComplianceAuditLog.builder()
            .eventType(event.getType())
            .userId(event.getUserId())
            .dataType(event.getDataType())
            .action(event.getAction())
            .timestamp(Instant.now())
            .details(event.getDetails())
            .complianceStatus(event.getComplianceStatus())
            .build();
        
        complianceAuditRepository.save(log);
    }
    
    public List<ComplianceAuditLog> getAuditTrail(String userId, Duration period) {
        Instant startTime = Instant.now().minus(period);
        return complianceAuditRepository.findByUserIdAndTimestampAfter(userId, startTime);
    }
}
```

## 8. Data Protection Training

### 8.1 Training Requirements
```java
public class DataProtectionTrainingService {
    
    public TrainingRequirement getTrainingRequirement(UserRole role) {
        switch (role) {
            case ADMIN:
                return TrainingRequirement.builder()
                    .frequency(TrainingFrequency.QUARTERLY)
                    .modules(Arrays.asList("GDPR", "Data Classification", "Incident Response"))
                    .certificationRequired(true)
                    .build();
            case TEACHER:
                return TrainingRequirement.builder()
                    .frequency(TrainingFrequency.SEMI_ANNUALLY)
                    .modules(Arrays.asList("Data Classification", "User Privacy"))
                    .certificationRequired(false)
                    .build();
            default:
                return TrainingRequirement.builder()
                    .frequency(TrainingFrequency.ANNUALLY)
                    .modules(Arrays.asList("Basic Privacy"))
                    .certificationRequired(false)
                    .build();
        }
    }
    
    public void assignTraining(String userId, TrainingRequirement requirement) {
        TrainingAssignment assignment = TrainingAssignment.builder()
            .userId(userId)
            .requirement(requirement)
            .assignedAt(Instant.now())
            .dueDate(Instant.now().plus(Duration.ofDays(30)))
            .status(TrainingStatus.PENDING)
            .build();
        
        trainingRepository.save(assignment);
        notificationService.sendTrainingNotification(assignment);
    }
}
```

### 8.2 Training Compliance
```java
@Component
public class TrainingComplianceService {
    
    @Scheduled(cron = "0 0 9 * * 1") // Weekly on Monday at 9 AM
    public void checkTrainingCompliance() {
        List<TrainingAssignment> overdueAssignments = 
            trainingRepository.findOverdueAssignments();
        
        for (TrainingAssignment assignment : overdueAssignments) {
            sendReminder(assignment);
            
            if (assignment.isCritical()) {
                escalateToManager(assignment);
            }
        }
    }
    
    public TrainingComplianceReport generateComplianceReport() {
        return TrainingComplianceReport.builder()
            .totalUsers(userRepository.count())
            .usersWithValidTraining(trainingRepository.countValidTraining())
            .overdueTraining(trainingRepository.countOverdueTraining())
            .complianceRate(calculateComplianceRate())
            .build();
    }
}
``` 
