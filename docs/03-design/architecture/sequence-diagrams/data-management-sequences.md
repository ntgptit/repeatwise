# Data Management Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình quản lý dữ liệu trong hệ thống RepeatWise, bao gồm export/import dữ liệu, backup/restore, quản lý preferences và system settings.

## 1. Export Learning Data Sequence

### 1.1 Successful Data Export

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant ExportService as Export Service
    participant FileService as File Service
    participant EmailService as Email Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant ReviewRepository as Review Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/data/export
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {exportType, format, dateRange, options}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>DataController: Forward request
    
    DataController->>DataService: exportData(userId, exportConfig)
    
    DataService->>DataService: validateExportConfig(exportConfig)
    DataService->>DataService: generateExportId()
    
    alt Export Type: All Data
        DataService->>SetRepository: findAllByUserId(userId)
        SetRepository->>Database: SELECT * FROM sets WHERE user_id = ?
        Database-->>SetRepository: Sets data
        SetRepository-->>DataService: List<Set> entities
        
        DataService->>CycleRepository: findAllByUserId(userId)
        CycleRepository->>Database: SELECT * FROM cycles WHERE user_id = ?
        Database-->>CycleRepository: Cycles data
        CycleRepository-->>DataService: List<Cycle> entities
        
        DataService->>ReviewRepository: findAllByUserId(userId)
        ReviewRepository->>Database: SELECT * FROM reviews r JOIN cycles c ON r.cycle_id = c.id WHERE c.user_id = ?
        Database-->>ReviewRepository: Reviews data
        ReviewRepository-->>DataService: List<Review> entities
    else Export Type: Sets Only
        DataService->>SetRepository: findAllByUserId(userId)
        SetRepository->>Database: SELECT * FROM sets WHERE user_id = ?
        Database-->>SetRepository: Sets data
        SetRepository-->>DataService: List<Set> entities
    end
    
    DataService->>ExportService: generateExportFile(data, format, options)
    ExportService->>ExportService: formatData(data, format)
    ExportService->>ExportService: applyOptions(data, options)
    ExportService->>ExportService: createExportFile(formattedData, format)
    
    ExportService->>FileService: uploadFile(exportFile, userId)
    FileService->>FileService: generateDownloadUrl(fileId)
    FileService-->>ExportService: Download URL
    
    DataService->>EmailService: sendExportEmail(user.email, downloadUrl, exportConfig)
    EmailService-->>DataService: Email sent successfully
    
    DataService->>DataService: logExportAction(userId, exportConfig, fileId)
    DataService-->>DataController: ExportResult(success, downloadUrl, expiryDate)
    DataController-->>APIGateway: 200 OK + Export confirmation
    APIGateway-->>MobileApp: 200 OK + Export confirmation
```

### 1.2 Export with Large Dataset

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant ExportService as Export Service
    participant BackgroundJobService as Background Job Service
    participant EmailService as Email Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/data/export
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {exportType: "all", format: "json", dateRange: "all"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>DataController: Forward request
    
    DataController->>DataService: exportData(userId, exportConfig)
    
    DataService->>DataService: validateExportConfig(exportConfig)
    DataService->>DataService: estimateDataSize(userId, exportConfig)
    Note over DataService: Large dataset detected (>10MB)
    
    DataService->>BackgroundJobService: scheduleExportJob(userId, exportConfig)
    BackgroundJobService->>BackgroundJobService: createBackgroundJob(jobConfig)
    BackgroundJobService-->>DataService: Job ID
    
    DataService->>EmailService: sendExportScheduledEmail(user.email, jobId)
    EmailService-->>DataService: Email sent successfully
    
    DataService-->>DataController: ExportResult(scheduled, jobId, estimatedTime)
    DataController-->>APIGateway: 202 Accepted + Job information
    APIGateway-->>MobileApp: 202 Accepted + Job information
```

## 2. Import Learning Data Sequence

### 2.1 Successful Data Import

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant ImportService as Import Service
    participant ValidationService as Validation Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/data/import
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: FormData with file and options
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>DataController: Forward request
    
    DataController->>DataService: importData(userId, importFile, options)
    
    DataService->>ImportService: validateImportFile(importFile)
    ImportService->>ImportService: detectFileFormat(importFile)
    ImportService->>ImportService: parseFile(importFile, format)
    ImportService-->>DataService: Parsed data
    
    DataService->>ValidationService: validateImportData(parsedData, userId)
    ValidationService->>ValidationService: checkDataIntegrity(parsedData)
    ValidationService->>ValidationService: checkConflicts(parsedData, userId)
    ValidationService-->>DataService: Validation result
    
    alt Validation: Conflicts Found
        DataService->>DataService: generateConflictReport(conflicts)
        DataService-->>DataController: ImportResult(conflicts, conflictReport)
        DataController-->>APIGateway: 409 Conflict + Conflict details
        APIGateway-->>MobileApp: 409 Conflict + Conflict details
    else Validation: Success
        DataService->>SetRepository: importSets(parsedData.sets, userId)
        SetRepository->>Database: INSERT INTO sets (...) ON CONFLICT DO UPDATE
        Database-->>SetRepository: Sets imported
        
        DataService->>CycleRepository: importCycles(parsedData.cycles, userId)
        CycleRepository->>Database: INSERT INTO cycles (...) ON CONFLICT DO UPDATE
        Database-->>CycleRepository: Cycles imported
        
        DataService->>DataService: logImportAction(userId, importStats)
        DataService-->>DataController: ImportResult(success, importStats)
        DataController-->>APIGateway: 200 OK + Import summary
        APIGateway-->>MobileApp: 200 OK + Import summary
    end
```

## 3. Learning Preferences Management Sequence

### 3.1 Update Learning Preferences

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant PreferencesController as Preferences Controller
    participant PreferencesService as Preferences Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL
    participant Cache as Redis Cache

    MobileApp->>APIGateway: PUT /api/preferences/learning
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {srsSettings, scheduleSettings, difficultySettings, reviewSettings}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>PreferencesController: Forward request
    
    PreferencesController->>PreferencesService: updateLearningPreferences(userId, preferences)
    
    PreferencesService->>PreferencesService: validatePreferences(preferences)
    PreferencesService->>PreferencesService: validateSRSAlgorithm(srsSettings)
    PreferencesService->>PreferencesService: validateScheduleSettings(scheduleSettings)
    
    PreferencesService->>UserRepository: findById(userId)
    UserRepository->>Database: SELECT * FROM users WHERE id = ?
    Database-->>UserRepository: User data
    UserRepository-->>PreferencesService: User entity
    
    PreferencesService->>PreferencesService: updateUserPreferences(user, preferences)
    PreferencesService->>UserRepository: updateUser(user)
    UserRepository->>Database: UPDATE users SET learning_preferences = ? WHERE id = ?
    Database-->>UserRepository: User updated
    
    PreferencesService->>Cache: invalidateUserCache(userId)
    Cache-->>PreferencesService: Cache invalidated
    
    PreferencesService->>PreferencesService: applyPreferencesToActiveCycles(userId, preferences)
    PreferencesService-->>PreferencesController: PreferencesResult(success)
    PreferencesController-->>APIGateway: 200 OK + Success message
    APIGateway-->>MobileApp: 200 OK + Success message
```

### 3.2 Get Learning Preferences

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant PreferencesController as Preferences Controller
    participant PreferencesService as Preferences Service
    participant UserRepository as User Repository
    participant Cache as Redis Cache
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/preferences/learning
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>PreferencesController: Forward request
    
    PreferencesController->>PreferencesService: getLearningPreferences(userId)
    
    PreferencesService->>Cache: getCachedPreferences(userId)
    Cache-->>PreferencesService: Cached preferences found
    
    PreferencesService->>PreferencesService: validateCacheData(cachedData)
    PreferencesService-->>PreferencesController: LearningPreferences object
    PreferencesController-->>APIGateway: 200 OK + Preferences data
    APIGateway-->>MobileApp: 200 OK + Preferences data
```

## 4. System Settings Management Sequence

### 4.1 Update System Settings

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant SettingsController as Settings Controller
    participant SettingsService as Settings Service
    participant UserRepository as User Repository
    participant NotificationService as Notification Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: PUT /api/settings/system
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {notificationSettings, privacySettings, displaySettings, languageSettings}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>SettingsController: Forward request
    
    SettingsController->>SettingsService: updateSystemSettings(userId, settings)
    
    SettingsService->>SettingsService: validateSystemSettings(settings)
    SettingsService->>SettingsService: validateNotificationSettings(notificationSettings)
    SettingsService->>SettingsService: validatePrivacySettings(privacySettings)
    
    SettingsService->>UserRepository: findById(userId)
    UserRepository->>Database: SELECT * FROM users WHERE id = ?
    Database-->>UserRepository: User data
    UserRepository-->>SettingsService: User entity
    
    SettingsService->>SettingsService: updateUserSettings(user, settings)
    SettingsService->>UserRepository: updateUser(user)
    UserRepository->>Database: UPDATE users SET system_settings = ? WHERE id = ?
    Database-->>UserRepository: User updated
    
    alt Notification Settings Changed
        SettingsService->>NotificationService: updateNotificationPreferences(userId, notificationSettings)
        NotificationService->>NotificationService: updateUserNotificationSettings(userId, settings)
        NotificationService-->>SettingsService: Notification settings updated
    end
    
    SettingsService->>SettingsService: logSettingsChange(userId, settings)
    SettingsService-->>SettingsController: SettingsResult(success)
    SettingsController-->>APIGateway: 200 OK + Success message
    APIGateway-->>MobileApp: 200 OK + Success message
```

## 5. Backup and Restore Sequence

### 5.1 Create Backup

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant BackupController as Backup Controller
    participant BackupService as Backup Service
    participant Database as PostgreSQL
    participant FileService as File Service
    participant EmailService as Email Service

    MobileApp->>APIGateway: POST /api/backup/create
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {backupType, encryption, retention}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>BackupController: Forward request
    
    BackupController->>BackupService: createBackup(userId, backupConfig)
    
    BackupService->>BackupService: validateBackupConfig(backupConfig)
    BackupService->>BackupService: generateBackupId()
    
    BackupService->>Database: createBackupSnapshot(userId)
    Database->>Database: CREATE TABLE backup_xxx AS SELECT * FROM users WHERE id = ?
    Database->>Database: CREATE TABLE backup_xxx_sets AS SELECT * FROM sets WHERE user_id = ?
    Database->>Database: CREATE TABLE backup_xxx_cycles AS SELECT * FROM cycles WHERE user_id = ?
    Database-->>BackupService: Backup snapshot created
    
    BackupService->>FileService: compressAndEncryptBackup(backupData, encryption)
    FileService->>FileService: compressBackup(backupData)
    FileService->>FileService: encryptBackup(compressedData, encryption)
    FileService->>FileService: uploadBackupFile(encryptedBackup, userId)
    FileService-->>BackupService: Backup file URL
    
    BackupService->>BackupService: createBackupRecord(userId, backupConfig, fileUrl)
    BackupService->>Database: INSERT INTO backups (user_id, backup_type, file_url, created_at, expires_at)
    Database-->>BackupService: Backup record created
    
    BackupService->>EmailService: sendBackupConfirmation(user.email, backupId, fileUrl)
    EmailService-->>BackupService: Email sent successfully
    
    BackupService-->>BackupController: BackupResult(success, backupId, fileUrl)
    BackupController-->>APIGateway: 201 Created + Backup information
    APIGateway-->>MobileApp: 201 Created + Backup information
```

### 5.2 Restore from Backup

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant BackupController as Backup Controller
    participant BackupService as Backup Service
    participant RestoreService as Restore Service
    participant ValidationService as Validation Service
    participant Database as PostgreSQL
    participant FileService as File Service

    MobileApp->>APIGateway: POST /api/backup/{backupId}/restore
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {restoreOptions, conflictResolution}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>BackupController: Forward request
    
    BackupController->>BackupService: restoreFromBackup(userId, backupId, restoreConfig)
    
    BackupService->>BackupService: validateBackupAccess(userId, backupId)
    BackupService->>Database: findBackupById(backupId)
    Database-->>BackupService: Backup record
    
    BackupService->>FileService: downloadBackupFile(backup.fileUrl)
    FileService->>FileService: decryptBackupFile(encryptedFile)
    FileService->>FileService: decompressBackup(compressedFile)
    FileService-->>BackupService: Backup data
    
    BackupService->>RestoreService: validateBackupData(backupData)
    RestoreService->>ValidationService: validateDataIntegrity(backupData)
    ValidationService-->>RestoreService: Validation result
    
    alt Validation: Success
        BackupService->>RestoreService: restoreData(backupData, userId, restoreConfig)
        RestoreService->>Database: BEGIN TRANSACTION
        RestoreService->>Database: DELETE FROM sets WHERE user_id = ?
        RestoreService->>Database: DELETE FROM cycles WHERE user_id = ?
        RestoreService->>Database: INSERT INTO sets SELECT * FROM backup_xxx_sets
        RestoreService->>Database: INSERT INTO cycles SELECT * FROM backup_xxx_cycles
        RestoreService->>Database: COMMIT TRANSACTION
        Database-->>RestoreService: Data restored successfully
        
        BackupService->>BackupService: logRestoreAction(userId, backupId, restoreStats)
        BackupService-->>BackupController: RestoreResult(success, restoreStats)
        BackupController-->>APIGateway: 200 OK + Restore summary
        APIGateway-->>MobileApp: 200 OK + Restore summary
    else Validation: Failed
        BackupService-->>BackupController: RestoreResult(failed, validationErrors)
        BackupController-->>APIGateway: 400 Bad Request + Error details
        APIGateway-->>MobileApp: 400 Bad Request + Error details
    end
```

## Ghi chú kỹ thuật

### 1. Data Export/Import
- Export hỗ trợ nhiều format: JSON, CSV, Excel, PDF
- Import có validation và conflict resolution
- Large datasets được xử lý async với background jobs
- File encryption cho bảo mật dữ liệu

### 2. Preferences Management
- Learning preferences ảnh hưởng đến SRS algorithm
- System settings bao gồm notification và privacy
- Cache được invalidate khi preferences thay đổi
- Settings được apply cho active cycles

### 3. Backup/Restore
- Backup được encrypt và compress
- Retention policy tự động xóa backup cũ
- Restore có validation và rollback capability
- Backup history được track và log

### 4. Security Considerations
- Tất cả data operations yêu cầu authentication
- File upload/download có virus scanning
- Sensitive data được encrypt
- Access logs được maintain

### 5. Performance Optimization
- Large exports được chunked và streamed
- Background jobs cho heavy operations
- Database transactions cho data consistency
- Caching cho frequently accessed data
