# Secrets Management and Key Rotation

## 1. Overview

Secrets Management và Key Rotation framework của RepeatWise được thiết kế để đảm bảo bảo mật các thông tin nhạy cảm như API keys, database credentials, encryption keys, và certificates. Framework này tuân theo các best practices về bảo mật và compliance requirements.

## 2. Secrets Classification

### 2.1 Secrets Categories

#### 2.1.1 Authentication Secrets
```java
public enum AuthenticationSecretType {
    // Database credentials
    DATABASE_PASSWORD("database", "db_password", RotationFrequency.MONTHLY),
    DATABASE_SSL_CERT("database", "db_ssl_cert", RotationFrequency.QUARTERLY),
    
    // API credentials
    JWT_SIGNING_KEY("api", "jwt_signing_key", RotationFrequency.MONTHLY),
    API_GATEWAY_KEY("api", "api_gateway_key", RotationFrequency.MONTHLY),
    
    // Third-party service credentials
    TWILIO_API_KEY("external", "twilio_api_key", RotationFrequency.QUARTERLY),
    SENDGRID_API_KEY("external", "sendgrid_api_key", RotationFrequency.QUARTERLY),
    AWS_ACCESS_KEY("external", "aws_access_key", RotationFrequency.MONTHLY);
}
```

#### 2.1.2 Encryption Secrets
```java
public enum EncryptionSecretType {
    // Data encryption keys
    DATA_ENCRYPTION_KEY("encryption", "data_encryption_key", RotationFrequency.MONTHLY),
    BACKUP_ENCRYPTION_KEY("encryption", "backup_encryption_key", RotationFrequency.QUARTERLY),
    
    // TLS certificates
    SSL_CERTIFICATE("tls", "ssl_certificate", RotationFrequency.QUARTERLY),
    SSL_PRIVATE_KEY("tls", "ssl_private_key", RotationFrequency.QUARTERLY),
    
    // Application secrets
    APP_SECRET("application", "app_secret", RotationFrequency.MONTHLY),
    SESSION_SECRET("application", "session_secret", RotationFrequency.MONTHLY);
}
```

#### 2.1.3 Configuration Secrets
```java
public enum ConfigurationSecretType {
    // Environment-specific secrets
    PROD_DATABASE_URL("environment", "prod_db_url", RotationFrequency.MONTHLY),
    STAGING_DATABASE_URL("environment", "staging_db_url", RotationFrequency.MONTHLY),
    
    // Feature flags
    FEATURE_FLAG_SECRET("feature", "feature_flag_secret", RotationFrequency.MONTHLY),
    
    // Monitoring secrets
    MONITORING_API_KEY("monitoring", "monitoring_api_key", RotationFrequency.QUARTERLY);
}
```

### 2.2 Secret Sensitivity Levels

```java
public enum SecretSensitivityLevel {
    CRITICAL("critical", "Immediate rotation required if compromised"),
    HIGH("high", "Rotation within 24 hours if compromised"),
    MEDIUM("medium", "Rotation within 7 days if compromised"),
    LOW("low", "Rotation within 30 days if compromised");
    
    private final String level;
    private final String description;
}
```

## 3. Secrets Storage Architecture

### 3.1 Centralized Secrets Management

#### 3.1.1 AWS Secrets Manager Integration
```java
@Component
public class AWSSecretsManagerService {
    
    private final AWSSecretsManager secretsManager;
    private final String region = "us-east-1";
    
    public String getSecret(String secretName) {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
            
            GetSecretValueResponse response = secretsManager.getSecretValue(request);
            return response.secretString();
        } catch (Exception e) {
            throw new SecretRetrievalException("Failed to retrieve secret: " + secretName, e);
        }
    }
    
    public void createSecret(String secretName, String secretValue, String description) {
        try {
            CreateSecretRequest request = CreateSecretRequest.builder()
                .name(secretName)
                .description(description)
                .secretString(secretValue)
                .build();
            
            secretsManager.createSecret(request);
        } catch (Exception e) {
            throw new SecretCreationException("Failed to create secret: " + secretName, e);
        }
    }
    
    public void updateSecret(String secretName, String newValue) {
        try {
            UpdateSecretRequest request = UpdateSecretRequest.builder()
                .secretId(secretName)
                .secretString(newValue)
                .build();
            
            secretsManager.updateSecret(request);
        } catch (Exception e) {
            throw new SecretUpdateException("Failed to update secret: " + secretName, e);
        }
    }
}
```

#### 3.1.2 HashiCorp Vault Integration
```java
@Component
public class VaultSecretsService {
    
    private final VaultTemplate vaultTemplate;
    
    public String getSecret(String path) {
        try {
            VaultResponseSupport<Map<String, Object>> response = 
                vaultTemplate.read("secret/data/" + path);
            
            if (response != null && response.getData() != null) {
                return (String) response.getData().get("value");
            }
            throw new SecretNotFoundException("Secret not found: " + path);
        } catch (Exception e) {
            throw new SecretRetrievalException("Failed to retrieve secret: " + path, e);
        }
    }
    
    public void storeSecret(String path, String value, Map<String, Object> metadata) {
        try {
            Map<String, Object> secretData = Map.of(
                "data", Map.of("value", value),
                "metadata", metadata
            );
            
            vaultTemplate.write("secret/data/" + path, secretData);
        } catch (Exception e) {
            throw new SecretStorageException("Failed to store secret: " + path, e);
        }
    }
}
```

### 3.2 Local Development Secrets

#### 3.2.1 Environment Variables
```yaml
# application-local.yml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/repeatwise_local}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:local_password}
  
  jpa:
    hibernate:
      ddl-auto: create-drop
  
jwt:
  secret: ${JWT_SECRET:local_jwt_secret_key_for_development_only}
  expiration: ${JWT_EXPIRATION:3600000}

# External services
twilio:
  account-sid: ${TWILIO_ACCOUNT_SID:local_account_sid}
  auth-token: ${TWILIO_AUTH_TOKEN:local_auth_token}

sendgrid:
  api-key: ${SENDGRID_API_KEY:local_api_key}
```

#### 3.2.2 Docker Secrets
```yaml
# docker-compose.yml
version: '3.8'
services:
  api:
    image: repeatwise-api:latest
    secrets:
      - db_password
      - jwt_secret
      - api_keys
    environment:
      - DB_PASSWORD_FILE=/run/secrets/db_password
      - JWT_SECRET_FILE=/run/secrets/jwt_secret

secrets:
  db_password:
    file: ./secrets/db_password.txt
  jwt_secret:
    file: ./secrets/jwt_secret.txt
  api_keys:
    file: ./secrets/api_keys.json
```

## 4. Key Rotation Strategy

### 4.1 Rotation Policies

#### 4.1.1 Rotation Frequency Matrix
```java
public enum RotationFrequency {
    DAILY("daily", Duration.ofDays(1), "Critical secrets"),
    WEEKLY("weekly", Duration.ofDays(7), "High-risk secrets"),
    MONTHLY("monthly", Duration.ofDays(30), "Standard secrets"),
    QUARTERLY("quarterly", Duration.ofDays(90), "Low-risk secrets"),
    ANNUALLY("annually", Duration.ofDays(365), "Certificates");
    
    private final String frequency;
    private final Duration duration;
    private final String description;
}
```

#### 4.1.2 Rotation Policy Configuration
```yaml
key_rotation:
  policies:
    authentication:
      jwt_signing_key:
        frequency: "monthly"
        grace_period_days: 7
        auto_rotation: true
        notification_days: [30, 7, 1]
      
      database_password:
        frequency: "monthly"
        grace_period_days: 3
        auto_rotation: true
        notification_days: [30, 7, 1]
    
    encryption:
      data_encryption_key:
        frequency: "monthly"
        grace_period_days: 7
        auto_rotation: true
        notification_days: [30, 7, 1]
      
      backup_encryption_key:
        frequency: "quarterly"
        grace_period_days: 14
        auto_rotation: true
        notification_days: [90, 30, 7]
    
    certificates:
      ssl_certificate:
        frequency: "quarterly"
        grace_period_days: 30
        auto_rotation: false
        notification_days: [90, 60, 30, 7]
```

### 4.2 Automated Key Rotation

#### 4.2.1 Rotation Service
```java
@Component
public class KeyRotationService {
    
    private final SecretsManagerService secretsManager;
    private final NotificationService notificationService;
    private final RotationPolicyService policyService;
    
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void checkAndRotateKeys() {
        List<SecretMetadata> secretsDueForRotation = getSecretsDueForRotation();
        
        for (SecretMetadata secret : secretsDueForRotation) {
            try {
                rotateSecret(secret);
                logRotationSuccess(secret);
            } catch (Exception e) {
                logRotationFailure(secret, e);
                notifyRotationFailure(secret, e);
            }
        }
    }
    
    private void rotateSecret(SecretMetadata secret) {
        // Step 1: Generate new secret
        String newSecret = generateNewSecret(secret.getType());
        
        // Step 2: Update in secrets manager
        secretsManager.updateSecret(secret.getName(), newSecret);
        
        // Step 3: Update application configuration
        updateApplicationConfig(secret.getName(), newSecret);
        
        // Step 4: Validate rotation
        validateRotation(secret.getName(), newSecret);
        
        // Step 5: Clean up old secret
        cleanupOldSecret(secret.getName());
    }
    
    private String generateNewSecret(SecretType type) {
        switch (type) {
            case JWT_SIGNING_KEY:
                return generateJWTKey();
            case DATABASE_PASSWORD:
                return generateDatabasePassword();
            case DATA_ENCRYPTION_KEY:
                return generateEncryptionKey();
            default:
                return generateRandomSecret();
        }
    }
    
    private String generateJWTKey() {
        // Generate RSA key pair for JWT signing
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            
            return Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        } catch (Exception e) {
            throw new KeyGenerationException("Failed to generate JWT key", e);
        }
    }
}
```

#### 4.2.2 Rotation Validation
```java
@Component
public class RotationValidationService {
    
    public void validateRotation(String secretName, String newSecret) {
        // Test the new secret
        if (!testSecret(secretName, newSecret)) {
            throw new RotationValidationException("New secret validation failed: " + secretName);
        }
        
        // Verify application functionality
        if (!verifyApplicationFunctionality(secretName)) {
            throw new RotationValidationException("Application functionality test failed: " + secretName);
        }
        
        // Check for any errors in logs
        if (hasErrorsInLogs(secretName)) {
            throw new RotationValidationException("Errors detected after rotation: " + secretName);
        }
    }
    
    private boolean testSecret(String secretName, String secret) {
        switch (secretName) {
            case "jwt_signing_key":
                return testJWTSigning(secret);
            case "database_password":
                return testDatabaseConnection(secret);
            case "data_encryption_key":
                return testDataEncryption(secret);
            default:
                return true;
        }
    }
    
    private boolean testJWTSigning(String key) {
        try {
            // Test JWT signing and verification
            String testToken = jwtService.generateToken("test_user", key);
            return jwtService.validateToken(testToken, key);
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 4.3 Manual Key Rotation

#### 4.3.1 Emergency Rotation
```java
@Component
public class EmergencyRotationService {
    
    public void performEmergencyRotation(String secretName, String reason) {
        // Log emergency rotation
        logEmergencyRotation(secretName, reason);
        
        // Notify security team
        notifySecurityTeam(secretName, reason);
        
        // Perform immediate rotation
        rotateSecretImmediately(secretName);
        
        // Validate rotation
        validateEmergencyRotation(secretName);
        
        // Update rotation schedule
        updateRotationSchedule(secretName);
    }
    
    private void rotateSecretImmediately(String secretName) {
        // Generate new secret
        String newSecret = generateNewSecret(secretName);
        
        // Update immediately without grace period
        secretsManager.updateSecret(secretName, newSecret);
        
        // Restart affected services
        restartAffectedServices(secretName);
    }
}
```

#### 4.3.2 Manual Rotation Workflow
```java
@RestController
@RequestMapping("/api/admin/secrets")
public class SecretsManagementController {
    
    @PostMapping("/{secretName}/rotate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RotationResponse> rotateSecret(
            @PathVariable String secretName,
            @RequestBody RotationRequest request) {
        
        try {
            // Validate request
            validateRotationRequest(secretName, request);
            
            // Perform rotation
            RotationResult result = keyRotationService.rotateSecret(secretName);
            
            return ResponseEntity.ok(RotationResponse.builder()
                .secretName(secretName)
                .status("SUCCESS")
                .rotatedAt(Instant.now())
                .nextRotationDate(result.getNextRotationDate())
                .build());
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RotationResponse.builder()
                    .secretName(secretName)
                    .status("FAILED")
                    .error(e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/{secretName}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecretStatus> getSecretStatus(@PathVariable String secretName) {
        SecretStatus status = secretsService.getSecretStatus(secretName);
        return ResponseEntity.ok(status);
    }
}
```

## 5. Secrets Monitoring and Alerting

### 5.1 Secrets Health Monitoring

#### 5.1.1 Health Check Service
```java
@Component
public class SecretsHealthService {
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkSecretsHealth() {
        List<SecretMetadata> secrets = secretsService.getAllSecrets();
        
        for (SecretMetadata secret : secrets) {
            SecretHealth health = checkSecretHealth(secret);
            
            if (health.getStatus() == HealthStatus.UNHEALTHY) {
                alertUnhealthySecret(secret, health);
            }
            
            // Store health metrics
            healthMetricsService.recordHealth(secret.getName(), health);
        }
    }
    
    private SecretHealth checkSecretHealth(SecretMetadata secret) {
        try {
            // Check if secret exists
            if (!secretsManager.secretExists(secret.getName())) {
                return SecretHealth.unhealthy("Secret not found");
            }
            
            // Check if secret is accessible
            String secretValue = secretsManager.getSecret(secret.getName());
            if (secretValue == null || secretValue.isEmpty()) {
                return SecretHealth.unhealthy("Secret is empty");
            }
            
            // Check if secret is expired
            if (secret.isExpired()) {
                return SecretHealth.unhealthy("Secret is expired");
            }
            
            // Check if secret is due for rotation
            if (secret.isDueForRotation()) {
                return SecretHealth.warning("Secret due for rotation");
            }
            
            return SecretHealth.healthy();
            
        } catch (Exception e) {
            return SecretHealth.unhealthy("Error checking secret: " + e.getMessage());
        }
    }
}
```

#### 5.1.2 Secrets Metrics
```java
@Component
public class SecretsMetricsService {
    
    private final MeterRegistry meterRegistry;
    
    public void recordSecretAccess(String secretName, boolean success, Duration duration) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        if (success) {
            meterRegistry.counter("secrets.access.success", "secret", secretName).increment();
        } else {
            meterRegistry.counter("secrets.access.failure", "secret", secretName).increment();
        }
        
        sample.stop(Timer.builder("secrets.access.duration")
            .tag("secret", secretName)
            .register(meterRegistry));
    }
    
    public void recordRotationEvent(String secretName, boolean success) {
        if (success) {
            meterRegistry.counter("secrets.rotation.success", "secret", secretName).increment();
        } else {
            meterRegistry.counter("secrets.rotation.failure", "secret", secretName).increment();
        }
    }
    
    public void recordSecretAge(String secretName, Duration age) {
        meterRegistry.gauge("secrets.age.days", 
            Tags.of("secret", secretName), 
            age.toDays());
    }
}
```

### 5.2 Alerting System

#### 5.2.1 Alert Rules
```yaml
alerts:
  secrets:
    - name: "SecretRotationDue"
      condition: "secrets_rotation_due > 0"
      severity: "warning"
      message: "{{ $value }} secrets are due for rotation"
      
    - name: "SecretRotationFailed"
      condition: "secrets_rotation_failures > 0"
      severity: "critical"
      message: "{{ $value }} secret rotations failed"
      
    - name: "SecretAccessFailure"
      condition: "secrets_access_failures > 5"
      severity: "critical"
      message: "{{ $value }} secret access failures detected"
      
    - name: "SecretExpired"
      condition: "secrets_expired > 0"
      severity: "critical"
      message: "{{ $value }} secrets have expired"
```

#### 5.2.2 Alert Notification
```java
@Component
public class SecretsAlertService {
    
    public void sendAlert(SecretAlert alert) {
        // Send to different channels based on severity
        switch (alert.getSeverity()) {
            case CRITICAL:
                sendCriticalAlert(alert);
                break;
            case WARNING:
                sendWarningAlert(alert);
                break;
            case INFO:
                sendInfoAlert(alert);
                break;
        }
    }
    
    private void sendCriticalAlert(SecretAlert alert) {
        // Send to multiple channels for critical alerts
        notificationService.sendSlackAlert(alert);
        notificationService.sendEmailAlert(alert);
        notificationService.sendPagerDutyAlert(alert);
        
        // Create incident ticket
        incidentService.createIncident(alert);
    }
    
    private void sendWarningAlert(SecretAlert alert) {
        // Send to Slack and email for warnings
        notificationService.sendSlackAlert(alert);
        notificationService.sendEmailAlert(alert);
    }
}
```

## 6. Secrets Audit and Compliance

### 6.1 Audit Logging

#### 6.1.1 Secrets Audit Events
```java
public enum SecretsAuditEventType {
    SECRET_CREATED,
    SECRET_ACCESSED,
    SECRET_UPDATED,
    SECRET_DELETED,
    SECRET_ROTATED,
    SECRET_EXPORTED,
    SECRET_IMPORTED,
    ACCESS_DENIED,
    ROTATION_FAILED
}
```

#### 6.1.2 Audit Service
```java
@Component
public class SecretsAuditService {
    
    public void logSecretEvent(SecretsAuditEvent event) {
        SecretsAuditLog log = SecretsAuditLog.builder()
            .eventType(event.getType())
            .secretName(event.getSecretName())
            .userId(event.getUserId())
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .timestamp(Instant.now())
            .details(event.getDetails())
            .build();
        
        auditLogRepository.save(log);
    }
    
    public List<SecretsAuditLog> getAuditTrail(String secretName, Duration period) {
        Instant startTime = Instant.now().minus(period);
        return auditLogRepository.findBySecretNameAndTimestampAfter(secretName, startTime);
    }
    
    public List<SecretsAuditLog> getUserAccessHistory(String userId, Duration period) {
        Instant startTime = Instant.now().minus(period);
        return auditLogRepository.findByUserIdAndTimestampAfter(userId, startTime);
    }
}
```

### 6.2 Compliance Reporting

#### 6.2.1 Compliance Metrics
```java
@Component
public class SecretsComplianceService {
    
    @Scheduled(cron = "0 0 1 * * *") // Monthly at 1 AM
    public void generateComplianceReport() {
        SecretsComplianceReport report = SecretsComplianceReport.builder()
            .period(getCurrentPeriod())
            .totalSecrets(secretsService.getTotalSecrets())
            .rotatedSecrets(secretsService.getRotatedSecrets())
            .expiredSecrets(secretsService.getExpiredSecrets())
            .failedRotations(secretsService.getFailedRotations())
            .accessViolations(secretsService.getAccessViolations())
            .complianceScore(calculateComplianceScore())
            .build();
        
        complianceRepository.save(report);
        notifyComplianceTeam(report);
    }
    
    private double calculateComplianceScore() {
        int totalSecrets = secretsService.getTotalSecrets();
        int compliantSecrets = secretsService.getCompliantSecrets();
        
        return totalSecrets > 0 ? (double) compliantSecrets / totalSecrets * 100 : 100.0;
    }
}
```

#### 6.2.2 Compliance Checks
```java
@Component
public class ComplianceCheckService {
    
    public List<ComplianceViolation> checkCompliance() {
        List<ComplianceViolation> violations = new ArrayList<>();
        
        // Check rotation compliance
        violations.addAll(checkRotationCompliance());
        
        // Check access compliance
        violations.addAll(checkAccessCompliance());
        
        // Check storage compliance
        violations.addAll(checkStorageCompliance());
        
        return violations;
    }
    
    private List<ComplianceViolation> checkRotationCompliance() {
        List<ComplianceViolation> violations = new ArrayList<>();
        
        List<SecretMetadata> overdueSecrets = secretsService.getOverdueSecrets();
        for (SecretMetadata secret : overdueSecrets) {
            violations.add(ComplianceViolation.builder()
                .type(ComplianceViolationType.ROTATION_OVERDUE)
                .secretName(secret.getName())
                .severity(ComplianceViolationSeverity.HIGH)
                .description("Secret overdue for rotation")
                .build());
        }
        
        return violations;
    }
}
```

## 7. Disaster Recovery

### 7.1 Secrets Backup

#### 7.1.1 Backup Strategy
```java
@Component
public class SecretsBackupService {
    
    @Scheduled(cron = "0 0 3 * * *") // Daily at 3 AM
    public void backupSecrets() {
        List<SecretMetadata> secrets = secretsService.getAllSecrets();
        
        for (SecretMetadata secret : secrets) {
            try {
                // Create encrypted backup
                String secretValue = secretsManager.getSecret(secret.getName());
                String encryptedBackup = encryptBackup(secretValue);
                
                // Store backup
                backupStorage.store(secret.getName(), encryptedBackup);
                
                // Log backup
                logBackupSuccess(secret.getName());
                
            } catch (Exception e) {
                logBackupFailure(secret.getName(), e);
                notifyBackupFailure(secret.getName(), e);
            }
        }
    }
    
    private String encryptBackup(String secretValue) {
        // Encrypt backup with master key
        String masterKey = getMasterBackupKey();
        return encryptionService.encrypt(secretValue, masterKey);
    }
}
```

#### 7.1.2 Recovery Procedures
```java
@Component
public class SecretsRecoveryService {
    
    public void recoverSecret(String secretName) {
        try {
            // Retrieve encrypted backup
            String encryptedBackup = backupStorage.retrieve(secretName);
            
            // Decrypt backup
            String secretValue = decryptBackup(encryptedBackup);
            
            // Restore secret
            secretsManager.updateSecret(secretName, secretValue);
            
            // Validate restoration
            validateSecretRestoration(secretName);
            
            logRecoverySuccess(secretName);
            
        } catch (Exception e) {
            logRecoveryFailure(secretName, e);
            throw new RecoveryException("Failed to recover secret: " + secretName, e);
        }
    }
    
    public void performFullRecovery() {
        List<String> secretNames = backupStorage.listBackups();
        
        for (String secretName : secretNames) {
            try {
                recoverSecret(secretName);
            } catch (Exception e) {
                logRecoveryFailure(secretName, e);
                // Continue with other secrets
            }
        }
    }
}
```

### 7.2 Business Continuity

#### 7.2.1 Failover Procedures
```java
@Component
public class SecretsFailoverService {
    
    public void initiateFailover() {
        // Switch to backup secrets manager
        secretsManagerService.switchToBackup();
        
        // Validate failover
        validateFailover();
        
        // Notify stakeholders
        notifyFailover();
        
        // Monitor failover status
        monitorFailoverStatus();
    }
    
    private void validateFailover() {
        // Test critical secrets
        List<String> criticalSecrets = getCriticalSecrets();
        
        for (String secretName : criticalSecrets) {
            if (!testSecretAccess(secretName)) {
                throw new FailoverValidationException("Critical secret access failed: " + secretName);
            }
        }
    }
}
```

## 8. Security Best Practices

### 8.1 Secrets Security Guidelines

#### 8.1.1 Development Guidelines
```java
public class SecretsSecurityGuidelines {
    
    // Never hardcode secrets
    public static final String BAD_PRACTICE = "password123"; // ❌
    
    // Use environment variables or secrets manager
    public static final String GOOD_PRACTICE = System.getenv("DB_PASSWORD"); // ✅
    
    // Use secure random generation
    public static String generateSecureSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    // Validate secret strength
    public static boolean isStrongSecret(String secret) {
        return secret != null && 
               secret.length() >= 16 && 
               secret.matches(".*[A-Z].*") && 
               secret.matches(".*[a-z].*") && 
               secret.matches(".*[0-9].*") && 
               secret.matches(".*[!@#$%^&*].*");
    }
}
```

#### 8.1.2 Operational Guidelines
```yaml
operational_guidelines:
  access_control:
    - "Use least privilege principle"
    - "Implement role-based access control"
    - "Regular access reviews"
    - "Immediate revocation on role changes"
  
  monitoring:
    - "Monitor all secret access"
    - "Alert on unusual access patterns"
    - "Regular security audits"
    - "Compliance reporting"
  
  rotation:
    - "Automated rotation where possible"
    - "Manual rotation for critical secrets"
    - "Grace period for application updates"
    - "Validation after rotation"
  
  backup:
    - "Encrypted backups only"
    - "Off-site backup storage"
    - "Regular backup testing"
    - "Recovery procedure documentation"
``` 
