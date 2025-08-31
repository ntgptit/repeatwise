# Import/Export/Backup/Restore Sequence Diagrams - Chi tiết

## Tổng quan

Tài liệu này bổ sung cho `data-management-sequences.md` với các sequence diagrams chi tiết hơn cho các chức năng Import/Export/Backup/Restore, bao gồm các alternative flows và error handling phức tạp như được mô tả trong Use Cases.

## 1. Import với Conflict Resolution Chi tiết

### 1.1 Import với Conflicts và Resolution

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant ImportService as Import Service
    participant ConflictResolver as Conflict Resolver
    participant ValidationService as Validation Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL
    participant FileService as File Service

    MobileApp->>APIGateway: POST /api/data/import
    Note over MobileApp,APIGateway: {file, importOptions, conflictResolution}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>DataController: Forward request
    
    DataController->>DataService: importData(userId, file, options)
    
    DataService->>ImportService: validateAndParseFile(file)
    ImportService->>ImportService: detectFileFormat(file)
    ImportService->>ImportService: parseFile(file, format)
    ImportService-->>DataService: Parsed data
    
    DataService->>ValidationService: validateImportData(parsedData, userId)
    ValidationService->>ValidationService: checkDataIntegrity(parsedData)
    ValidationService->>ValidationService: validateRequiredFields(parsedData)
    ValidationService->>ValidationService: checkDataRelationships(parsedData)
    ValidationService-->>DataService: Validation result
    
    alt Validation Failed
        DataService->>DataService: generateValidationReport(validationErrors)
        DataService-->>DataController: ImportResult(failed, validationErrors)
        DataController-->>APIGateway: 400 Bad Request + Validation errors
        APIGateway-->>MobileApp: 400 Bad Request + Validation errors
    else Validation Success
        DataService->>ConflictResolver: analyzeConflicts(parsedData, userId)
        ConflictResolver->>SetRepository: findExistingSets(userId)
        SetRepository->>Database: SELECT * FROM sets WHERE user_id = ?
        Database-->>SetRepository: Existing sets
        SetRepository-->>ConflictResolver: List<Set> existingSets
        
        ConflictResolver->>CycleRepository: findExistingCycles(userId)
        CycleRepository->>Database: SELECT * FROM cycles WHERE user_id = ?
        Database-->>CycleRepository: Existing cycles
        CycleRepository-->>ConflictResolver: List<Cycle> existingCycles
        
        ConflictResolver->>ConflictResolver: compareData(parsedData, existingData)
        ConflictResolver->>ConflictResolver: generateConflictReport(conflicts)
        ConflictResolver-->>DataService: Conflict report
        
        alt Conflicts Found
            DataService->>DataService: generateImportId()
            DataService->>FileService: storeImportData(importId, parsedData)
            FileService-->>DataService: Import data stored
            
            DataService->>DataService: returnConflicts(importId, conflictReport)
            DataService-->>DataController: ImportResult(conflicts, importId, conflictReport)
            DataController-->>APIGateway: 409 Conflict + Conflict details
            APIGateway-->>MobileApp: 409 Conflict + Conflict details
            
            Note over MobileApp: User reviews conflicts and selects resolution
            
            MobileApp->>APIGateway: POST /api/data/import/{importId}/resolve
            Note over MobileApp,APIGateway: {globalStrategy, specificResolutions}
            
            APIGateway->>DataController: resolveConflicts(importId, resolutions)
            DataController->>DataService: applyConflictResolutions(importId, resolutions)
            
            DataService->>FileService: retrieveImportData(importId)
            FileService-->>DataService: Parsed data
            
            DataService->>ConflictResolver: applyResolutions(parsedData, conflicts, resolutions)
            ConflictResolver->>ConflictResolver: resolveUUIDConflicts(parsedData)
            ConflictResolver->>ConflictResolver: applyGlobalStrategy(parsedData, globalStrategy)
            ConflictResolver->>ConflictResolver: applySpecificResolutions(parsedData, specificResolutions)
            ConflictResolver-->>DataService: Resolved data
            
            DataService->>DataService: backupExistingData(userId)
            DataService->>Database: CREATE TABLE backup_import_xxx AS SELECT * FROM sets WHERE user_id = ?
            Database-->>DataService: Backup created
            
            DataService->>SetRepository: importSets(resolvedData.sets, userId)
            SetRepository->>Database: INSERT INTO sets (...) ON CONFLICT DO UPDATE
            Database-->>SetRepository: Sets imported
            
            DataService->>CycleRepository: importCycles(resolvedData.cycles, userId)
            CycleRepository->>Database: INSERT INTO cycles (...) ON CONFLICT DO UPDATE
            Database-->>CycleRepository: Cycles imported
            
            DataService->>DataService: logImportAction(userId, importStats)
            DataService->>FileService: cleanupImportData(importId)
            FileService-->>DataService: Cleanup completed
            
            DataService-->>DataController: ImportResult(success, importStats)
            DataController-->>APIGateway: 200 OK + Import summary
            APIGateway-->>MobileApp: 200 OK + Import summary
        else No Conflicts
            DataService->>DataService: backupExistingData(userId)
            DataService->>Database: CREATE TABLE backup_import_xxx AS SELECT * FROM sets WHERE user_id = ?
            Database-->>DataService: Backup created
            
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
    end
```

### 1.2 Import với Large Dataset và Background Processing

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant BackgroundJobService as Background Job Service
    participant ImportService as Import Service
    participant EmailService as Email Service
    participant NotificationService as Notification Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/data/import
    Note over MobileApp,APIGateway: Large file (>50MB)
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>DataController: Forward request
    
    DataController->>DataService: importData(userId, file, options)
    
    DataService->>ImportService: validateAndParseFile(file)
    ImportService->>ImportService: estimateDataSize(file)
    Note over ImportService: Large dataset detected (>50MB)
    
    DataService->>BackgroundJobService: scheduleImportJob(userId, file, options)
    BackgroundJobService->>BackgroundJobService: createBackgroundJob(jobConfig)
    BackgroundJobService->>BackgroundJobService: storeJobMetadata(jobId, jobConfig)
    BackgroundJobService-->>DataService: Job ID
    
    DataService->>EmailService: sendImportScheduledEmail(user.email, jobId)
    EmailService-->>DataService: Email sent successfully
    
    DataService->>NotificationService: sendImportScheduledNotification(userId, jobId)
    NotificationService-->>DataService: Notification sent
    
    DataService-->>DataController: ImportResult(scheduled, jobId, estimatedTime)
    DataController-->>APIGateway: 202 Accepted + Job information
    APIGateway-->>MobileApp: 202 Accepted + Job information
    
    Note over BackgroundJobService: Background job execution
    
    BackgroundJobService->>ImportService: executeImportJob(jobId)
    ImportService->>ImportService: processLargeImport(jobId)
    ImportService->>ImportService: updateProgress(jobId, progress)
    
    ImportService->>EmailService: sendImportProgressEmail(user.email, jobId, progress)
    EmailService-->>ImportService: Email sent
    
    ImportService->>NotificationService: sendImportProgressNotification(userId, jobId, progress)
    NotificationService-->>ImportService: Notification sent
    
    Note over ImportService: Import completed
    
    ImportService->>EmailService: sendImportCompletedEmail(user.email, jobId, result)
    EmailService-->>ImportService: Email sent
    
    ImportService->>NotificationService: sendImportCompletedNotification(userId, jobId, result)
    NotificationService-->>ImportService: Notification sent
```

## 2. Export với Progress Tracking

### 2.1 Export với Progress Updates

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant ExportService as Export Service
    participant ProgressTracker as Progress Tracker
    participant FileService as File Service
    participant EmailService as Email Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/data/export
    Note over MobileApp,APIGateway: {exportType: "all", format: "json"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>DataController: Forward request
    
    DataController->>DataService: exportData(userId, exportConfig)
    
    DataService->>ExportService: createExportJob(userId, exportConfig)
    ExportService->>ExportService: generateExportId()
    ExportService->>ProgressTracker: initializeProgress(exportId)
    ProgressTracker-->>ExportService: Progress initialized
    
    ExportService-->>DataService: Export ID
    DataService-->>DataController: ExportResult(scheduled, exportId)
    DataController-->>APIGateway: 202 Accepted + Export ID
    APIGateway-->>MobileApp: 202 Accepted + Export ID
    
    Note over ExportService: Background export execution
    
    ExportService->>ProgressTracker: updateProgress(exportId, 10, "Collecting data")
    ProgressTracker-->>ExportService: Progress updated
    
    ExportService->>SetRepository: findAllByUserId(userId)
    SetRepository->>Database: SELECT * FROM sets WHERE user_id = ?
    Database-->>SetRepository: Sets data
    SetRepository-->>ExportService: List<Set> sets
    
    ExportService->>ProgressTracker: updateProgress(exportId, 30, "Processing sets")
    ProgressTracker-->>ExportService: Progress updated
    
    ExportService->>CycleRepository: findAllByUserId(userId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE user_id = ?
    Database-->>CycleRepository: Cycles data
    CycleRepository-->>ExportService: List<Cycle> cycles
    
    ExportService->>ProgressTracker: updateProgress(exportId, 50, "Processing cycles")
    ProgressTracker-->>ExportService: Progress updated
    
    ExportService->>ExportService: formatData(sets, cycles, format)
    ExportService->>ProgressTracker: updateProgress(exportId, 70, "Formatting data")
    ProgressTracker-->>ExportService: Progress updated
    
    ExportService->>FileService: createExportFile(formattedData, format)
    FileService->>FileService: generateDownloadUrl(fileId)
    FileService-->>ExportService: Download URL
    
    ExportService->>ProgressTracker: updateProgress(exportId, 90, "Creating file")
    ProgressTracker-->>ExportService: Progress updated
    
    ExportService->>ProgressTracker: completeProgress(exportId, downloadUrl)
    ProgressTracker-->>ExportService: Progress completed
    
    ExportService->>EmailService: sendExportCompletedEmail(user.email, exportId, downloadUrl)
    EmailService-->>ExportService: Email sent
    
    Note over MobileApp: User checks export status
    
    MobileApp->>APIGateway: GET /api/data/export/{exportId}
    APIGateway->>DataController: getExportStatus(exportId)
    DataController->>ProgressTracker: getProgress(exportId)
    ProgressTracker-->>DataController: Progress status
    DataController-->>APIGateway: 200 OK + Export status
    APIGateway-->>MobileApp: 200 OK + Export status
```

## 3. Backup với Scheduling và Cloud Integration

### 3.1 Scheduled Backup với Cloud Storage

```mermaid
sequenceDiagram
    participant Scheduler as Backup Scheduler
    participant BackupService as Backup Service
    participant CloudStorageService as Cloud Storage Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL
    participant FileService as File Service
    participant EmailService as Email Service
    participant NotificationService as Notification Service
    participant EncryptionService as Encryption Service

    Note over Scheduler: Scheduled backup time reached
    
    Scheduler->>BackupService: executeScheduledBackups()
    BackupService->>UserRepository: findUsersWithScheduledBackups()
    UserRepository->>Database: SELECT users with backup_schedule
    Database-->>UserRepository: Users with scheduled backups
    UserRepository-->>BackupService: List of users
    
    loop For each user
        BackupService->>BackupService: createBackup(userId, scheduledConfig)
        
        BackupService->>Database: createBackupSnapshot(userId)
        Database->>Database: CREATE TABLE backup_xxx AS SELECT * FROM users WHERE id = ?
        Database->>Database: CREATE TABLE backup_xxx_sets AS SELECT * FROM sets WHERE user_id = ?
        Database->>Database: CREATE TABLE backup_xxx_cycles AS SELECT * FROM cycles WHERE user_id = ?
        Database-->>BackupService: Backup snapshot created
        
        BackupService->>FileService: compressBackupData(backupData)
        FileService->>FileService: compressData(backupData)
        FileService-->>BackupService: Compressed data
        
        BackupService->>EncryptionService: encryptBackup(compressedData, userKey)
        EncryptionService->>EncryptionService: encryptData(compressedData, key)
        EncryptionService-->>BackupService: Encrypted data
        
        BackupService->>CloudStorageService: uploadBackupFile(encryptedData, userId)
        CloudStorageService->>CloudStorageService: authenticateUser(userId)
        CloudStorageService->>CloudStorageService: uploadToCloud(encryptedData, path)
        CloudStorageService-->>BackupService: Cloud file URL
        
        BackupService->>Database: INSERT INTO backups (user_id, backup_type, file_url, created_at, expires_at)
        Database-->>BackupService: Backup record created
        
        BackupService->>EmailService: sendBackupConfirmation(user.email, backupId, cloudUrl)
        EmailService-->>BackupService: Email sent successfully
        
        BackupService->>NotificationService: sendBackupNotification(userId, backupId)
        NotificationService-->>BackupService: Notification sent
        
        BackupService->>BackupService: cleanupOldBackups(userId, retentionPolicy)
        BackupService->>Database: SELECT old backups for user
        Database-->>BackupService: Old backup records
        
        loop For each old backup
            BackupService->>CloudStorageService: deleteBackupFile(oldBackup.fileUrl)
            CloudStorageService-->>BackupService: File deleted
            BackupService->>Database: DELETE FROM backups WHERE id = ?
            Database-->>BackupService: Backup record deleted
        end
    end
```

### 3.2 Incremental Backup

```mermaid
sequenceDiagram
    participant Scheduler as Backup Scheduler
    participant BackupService as Backup Service
    participant IncrementalService as Incremental Service
    participant Database as PostgreSQL
    participant FileService as File Service
    participant CloudStorageService as Cloud Storage Service

    Note over Scheduler: Incremental backup time reached
    
    Scheduler->>BackupService: executeIncrementalBackups()
    BackupService->>IncrementalService: findChangedData(userId, lastBackupTime)
    
    IncrementalService->>Database: SELECT changed sets since last backup
    Database->>Database: SELECT * FROM sets WHERE user_id = ? AND updated_at > ?
    Database-->>IncrementalService: Changed sets
    
    IncrementalService->>Database: SELECT changed cycles since last backup
    Database->>Database: SELECT * FROM cycles WHERE user_id = ? AND updated_at > ?
    Database-->>IncrementalService: Changed cycles
    
    IncrementalService->>IncrementalService: createIncrementalBackup(changedData)
    IncrementalService->>FileService: compressIncrementalData(changedData)
    FileService-->>IncrementalService: Compressed incremental data
    
    IncrementalService->>CloudStorageService: uploadIncrementalBackup(compressedData, userId)
    CloudStorageService-->>IncrementalService: Incremental backup URL
    
    IncrementalService->>Database: INSERT INTO incremental_backups (user_id, base_backup_id, file_url, changes)
    Database-->>IncrementalService: Incremental backup record created
    
    IncrementalService-->>BackupService: Incremental backup completed
    BackupService->>BackupService: updateLastBackupTime(userId)
```

## 4. Restore với Preview và Conflict Resolution

### 4.1 Restore Preview và Execution

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant BackupController as Backup Controller
    participant BackupService as Backup Service
    participant RestoreService as Restore Service
    participant ValidationService as Validation Service
    participant CloudStorageService as Cloud Storage Service
    participant Database as PostgreSQL
    participant FileService as File Service
    participant EncryptionService as Encryption Service

    MobileApp->>APIGateway: POST /api/backup/{backupId}/preview
    Note over MobileApp,APIGateway: {restoreOptions}
    
    APIGateway->>BackupController: previewRestore(backupId, options)
    BackupController->>BackupService: generateRestorePreview(userId, backupId, options)
    
    BackupService->>Database: findBackupById(backupId)
    Database-->>BackupService: Backup record
    
    BackupService->>CloudStorageService: downloadBackupFile(backup.fileUrl)
    CloudStorageService-->>BackupService: Encrypted backup file
    
    BackupService->>EncryptionService: decryptBackupFile(encryptedFile)
    EncryptionService-->>BackupService: Decrypted backup file
    
    BackupService->>FileService: decompressBackup(compressedFile)
    FileService-->>BackupService: Backup data
    
    BackupService->>RestoreService: analyzeRestoreImpact(backupData, currentData, options)
    RestoreService->>Database: getCurrentData(userId)
    Database-->>RestoreService: Current data
    
    RestoreService->>RestoreService: identifyConflicts(backupData, currentData)
    RestoreService->>RestoreService: calculateRestoreStats(backupData, options)
    RestoreService-->>BackupService: Restore preview with conflicts and stats
    
    BackupService->>ValidationService: validateRestoreData(backupData)
    ValidationService-->>BackupService: Validation result
    
    BackupService-->>BackupController: RestorePreview(preview, conflicts, stats, validation)
    BackupController-->>APIGateway: 200 OK + Restore preview
    APIGateway-->>MobileApp: 200 OK + Restore preview
    
    Note over MobileApp: User reviews preview and confirms
    
    MobileApp->>APIGateway: POST /api/backup/{backupId}/restore
    Note over MobileApp,APIGateway: {restoreOptions, conflictResolutions}
    
    APIGateway->>BackupController: executeRestore(backupId, options, resolutions)
    BackupController->>BackupService: restoreFromBackup(userId, backupId, options, resolutions)
    
    BackupService->>BackupService: backupCurrentData(userId)
    BackupService->>Database: CREATE TABLE current_backup_xxx AS SELECT * FROM users WHERE id = ?
    Database-->>BackupService: Current data backed up
    
    BackupService->>RestoreService: restoreData(backupData, userId, options, resolutions)
    RestoreService->>Database: BEGIN TRANSACTION
    
    alt Selective Restore
        RestoreService->>Database: DELETE FROM sets WHERE user_id = ? AND id IN (selectedSets)
        RestoreService->>Database: INSERT INTO sets SELECT * FROM backup_xxx_sets WHERE id IN (selectedSets)
    else Full Restore
        RestoreService->>Database: DELETE FROM sets WHERE user_id = ?
        RestoreService->>Database: INSERT INTO sets SELECT * FROM backup_xxx_sets
        RestoreService->>Database: DELETE FROM cycles WHERE user_id = ?
        RestoreService->>Database: INSERT INTO cycles SELECT * FROM backup_xxx_cycles
    end
    
    RestoreService->>Database: COMMIT TRANSACTION
    Database-->>RestoreService: Data restored successfully
    
    BackupService->>BackupService: logRestoreAction(userId, backupId, restoreStats)
    BackupService-->>BackupController: RestoreResult(success, restoreStats)
    BackupController-->>APIGateway: 200 OK + Restore summary
    APIGateway-->>MobileApp: 200 OK + Restore summary
```

### 4.2 Restore với Conflict Resolution

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant BackupController as Backup Controller
    participant BackupService as Backup Service
    participant RestoreService as Restore Service
    participant ConflictResolver as Conflict Resolver
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/backup/{backupId}/restore
    Note over MobileApp,APIGateway: {restoreOptions, conflictResolutions}
    
    APIGateway->>BackupController: executeRestore(backupId, options, resolutions)
    BackupController->>BackupService: restoreFromBackup(userId, backupId, options, resolutions)
    
    BackupService->>RestoreService: restoreData(backupData, userId, options, resolutions)
    RestoreService->>ConflictResolver: resolveConflicts(backupData, currentData, resolutions)
    
    ConflictResolver->>ConflictResolver: applyGlobalStrategy(backupData, globalStrategy)
    ConflictResolver->>ConflictResolver: applySpecificResolutions(backupData, specificResolutions)
    
    loop For each conflict
        ConflictResolver->>ConflictResolver: resolveConflict(conflict, resolution)
        alt Resolution: Overwrite
            ConflictResolver->>Database: UPDATE existing record with backup data
        else Resolution: Skip
            ConflictResolver->>ConflictResolver: Skip this conflict
        else Resolution: Merge
            ConflictResolver->>Database: MERGE backup data with existing data
        end
    end
    
    ConflictResolver-->>RestoreService: Resolved data
    RestoreService->>Database: Apply resolved data
    Database-->>RestoreService: Data applied successfully
    
    RestoreService-->>BackupService: Restore completed
    BackupService->>BackupService: logRestoreAction(userId, backupId, restoreStats)
    BackupService-->>BackupController: RestoreResult(success, restoreStats)
    BackupController-->>APIGateway: 200 OK + Restore summary
    APIGateway-->>MobileApp: 200 OK + Restore summary
```

## 5. Error Handling và Rollback

### 5.1 Import Error Handling với Rollback

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant DataController as Data Controller
    participant DataService as Data Service
    participant ImportService as Import Service
    participant RollbackService as Rollback Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/data/import
    Note over MobileApp,APIGateway: {file, importOptions}
    
    APIGateway->>DataController: Forward request
    DataController->>DataService: importData(userId, file, options)
    
    DataService->>ImportService: validateAndParseFile(file)
    ImportService-->>DataService: Parsed data
    
    DataService->>Database: BEGIN TRANSACTION
    DataService->>Database: CREATE TABLE import_backup_xxx AS SELECT * FROM sets WHERE user_id = ?
    Database-->>DataService: Backup created
    
    DataService->>ImportService: importSets(parsedData.sets, userId)
    ImportService->>Database: INSERT INTO sets (...) VALUES (...)
    
    alt Error occurs during import
        Database-->>ImportService: Error (e.g., constraint violation)
        ImportService->>RollbackService: rollbackImport(userId, importId)
        RollbackService->>Database: ROLLBACK TRANSACTION
        Database-->>RollbackService: Transaction rolled back
        
        RollbackService->>Database: RESTORE FROM import_backup_xxx
        Database-->>RollbackService: Data restored
        
        RollbackService->>Database: DROP TABLE import_backup_xxx
        Database-->>RollbackService: Backup table dropped
        
        RollbackService-->>ImportService: Rollback completed
        ImportService->>DataService: generateErrorReport(error)
        DataService-->>DataController: ImportResult(failed, errorReport)
        DataController-->>APIGateway: 500 Internal Server Error + Error details
        APIGateway-->>MobileApp: 500 Internal Server Error + Error details
    else Import successful
        DataService->>Database: COMMIT TRANSACTION
        Database-->>DataService: Transaction committed
        
        DataService->>Database: DROP TABLE import_backup_xxx
        Database-->>DataService: Backup table dropped
        
        DataService->>DataService: logImportAction(userId, importStats)
        DataService-->>DataController: ImportResult(success, importStats)
        DataController-->>APIGateway: 200 OK + Import summary
        APIGateway-->>MobileApp: 200 OK + Import summary
    end
```

## Ghi chú kỹ thuật

### 1. Performance Considerations
- **Large dataset processing**: Sử dụng background jobs và streaming
- **Progress tracking**: Real-time updates qua WebSocket hoặc polling
- **Memory management**: Chunked processing cho large files
- **Database optimization**: Batch operations và proper indexing

### 2. Security Considerations
- **File validation**: Virus scanning và format validation
- **Data encryption**: End-to-end encryption cho sensitive data
- **Access control**: User-specific data isolation
- **Audit logging**: Comprehensive logging cho all operations

### 3. Error Handling
- **Graceful degradation**: Partial success handling
- **Automatic retry**: Exponential backoff cho transient failures
- **Rollback mechanisms**: Automatic rollback on failure
- **User notification**: Clear error messages và recovery options

### 4. Scalability
- **Background processing**: Async operations cho heavy tasks
- **Resource management**: Proper cleanup và resource limits
- **Caching**: Cache frequently accessed data
- **Load balancing**: Distribute load across multiple instances
