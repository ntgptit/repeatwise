# Import/Export Management Module - Detail Design

## 1. Module Overview

### 1.1 Objectives
Import/Export Management Module xử lý tất cả các hoạt động liên quan đến nhập và xuất dữ liệu học tập bao gồm:
- Xuất learning sets và dữ liệu học tập
- Nhập learning sets từ các định dạng khác nhau
- Backup và restore dữ liệu
- Quản lý lịch sử backup
- Xử lý file upload và download

### 1.2 Scope
- **In Scope**: Data import/export, backup/restore, file management, format conversion
- **Out of Scope**: Learning set management, User management (handled by other modules)

### 1.3 Dependencies
- **Database**: import_jobs table, export_jobs table, backup_history table
- **External Services**: File storage service, Email service
- **Security**: File validation, data sanitization

## 2. API Contracts

### 2.1 Export Operations

#### POST /api/v1/export/learning-data
**Request:**
```json
{
  "format": "json",
  "includeSets": true,
  "includeProgress": true,
  "includeStatistics": true,
  "dateRange": {
    "from": "2024-01-01T00:00:00Z",
    "to": "2024-12-31T23:59:59Z"
  },
  "setIds": ["uuid-1", "uuid-2"]
}
```

**Response (Success - 202):**
```json
{
  "success": true,
  "message": "Export job đã được tạo",
  "data": {
    "jobId": "uuid-here",
    "status": "processing",
    "format": "json",
    "estimatedCompletionTime": "2024-12-19T10:05:00Z",
    "downloadUrl": null,
    "createdAt": "2024-12-19T10:00:00Z"
  }
}
```

#### GET /api/v1/export/jobs/{jobId}
**Response:**
```json
{
  "success": true,
  "data": {
    "jobId": "uuid-here",
    "status": "completed",
    "format": "json",
    "progress": 100,
    "downloadUrl": "https://storage.repeatwise.com/exports/uuid-here.json",
    "expiresAt": "2024-12-26T10:00:00Z",
    "fileSize": 2048576,
    "createdAt": "2024-12-19T10:00:00Z",
    "completedAt": "2024-12-19T10:03:00Z"
  }
}
```

#### GET /api/v1/export/jobs
**Query Parameters:**
- `status`: Filter by status (processing, completed, failed)
- `format`: Filter by format
- `page`: Page number
- `size`: Page size

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "jobId": "uuid-here",
        "status": "completed",
        "format": "json",
        "fileSize": 2048576,
        "createdAt": "2024-12-19T10:00:00Z",
        "completedAt": "2024-12-19T10:03:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

### 2.2 Import Operations

#### POST /api/v1/import/learning-data
**Request (multipart/form-data):**
```
file: [binary file data]
format: "json"
options: {
  "mergeStrategy": "skip_existing",
  "validateData": true,
  "createBackup": true
}
```

**Response (Success - 202):**
```json
{
  "success": true,
  "message": "Import job đã được tạo",
  "data": {
    "jobId": "uuid-here",
    "status": "processing",
    "format": "json",
    "fileName": "learning-data.json",
    "fileSize": 1024000,
    "estimatedCompletionTime": "2024-12-19T10:05:00Z",
    "createdAt": "2024-12-19T10:00:00Z"
  }
}
```

#### GET /api/v1/import/jobs/{jobId}
**Response:**
```json
{
  "success": true,
  "data": {
    "jobId": "uuid-here",
    "status": "completed",
    "format": "json",
    "fileName": "learning-data.json",
    "progress": 100,
    "results": {
      "totalItems": 100,
      "successfulImports": 95,
      "failedImports": 5,
      "skippedItems": 0,
      "errors": [
        {
          "itemId": "item-1",
          "error": "Invalid format",
          "line": 15
        }
      ]
    },
    "createdAt": "2024-12-19T10:00:00Z",
    "completedAt": "2024-12-19T10:03:00Z"
  }
}
```

### 2.3 Backup Operations

#### POST /api/v1/backup/create
**Request:**
```json
{
  "backupType": "full",
  "includeSettings": true,
  "includeStatistics": true,
  "description": "Monthly backup"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Backup đã được tạo",
  "data": {
    "backupId": "uuid-here",
    "backupType": "full",
    "status": "processing",
    "estimatedSize": 52428800,
    "createdAt": "2024-12-19T10:00:00Z"
  }
}
```

#### GET /api/v1/backup/history
**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "backupId": "uuid-here",
        "backupType": "full",
        "status": "completed",
        "fileSize": 52428800,
        "description": "Monthly backup",
        "createdAt": "2024-12-19T10:00:00Z",
        "completedAt": "2024-12-19T10:05:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

#### POST /api/v1/backup/{backupId}/restore
**Request:**
```json
{
  "restoreOptions": {
    "includeSettings": true,
    "includeStatistics": true,
    "mergeStrategy": "replace_all"
  }
}
```

## 3. Data Models

### 3.1 Export Job Entity
```java
@Entity
@Table(name = "export_jobs")
public class ExportJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jobId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private ExportFormat format;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    
    @Column(nullable = false)
    private Integer progress;
    
    private String downloadUrl;
    
    private LocalDateTime expiresAt;
    
    private Long fileSize;
    
    private String errorMessage;
    
    // Export options
    @Column(columnDefinition = "TEXT")
    private String exportOptions; // JSON
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.2 Import Job Entity
```java
@Entity
@Table(name = "import_jobs")
public class ImportJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jobId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private ImportFormat format;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private Integer progress;
    
    // Import results
    @Column(columnDefinition = "TEXT")
    private String importResults; // JSON
    
    private String errorMessage;
    
    // Import options
    @Column(columnDefinition = "TEXT")
    private String importOptions; // JSON
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.3 Backup History Entity
```java
@Entity
@Table(name = "backup_history")
public class BackupHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID backupId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private BackupType backupType;
    
    @Enumerated(EnumType.STRING)
    private BackupStatus status;
    
    @Column(nullable = false)
    private String fileName;
    
    private Long fileSize;
    
    @Column(length = 500)
    private String description;
    
    private String downloadUrl;
    
    private LocalDateTime expiresAt;
    
    private String errorMessage;
    
    // Backup options
    @Column(columnDefinition = "TEXT")
    private String backupOptions; // JSON
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

## 4. Business Logic

### 4.1 Export Learning Data Logic
```pseudocode
FUNCTION exportLearningData(userId, exportRequest):
    // Validate input
    IF NOT validateExportRequest(exportRequest):
        RETURN validationError
    
    // Create export job
    exportJob = new ExportJob()
    exportJob.user = userRepository.findById(userId)
    exportJob.format = exportRequest.format
    exportJob.status = PROCESSING
    exportJob.progress = 0
    exportJob.exportOptions = toJson(exportRequest)
    exportJob.createdAt = now()
    exportJob.updatedAt = now()
    
    savedJob = exportJobRepository.save(exportJob)
    
    // Start async export process
    exportService.processExportAsync(savedJob.getJobId())
    
    RETURN successResponse(mapToExportJobDto(savedJob))
```

### 4.2 Process Export Async Logic
```pseudocode
FUNCTION processExportAsync(jobId):
    exportJob = exportJobRepository.findById(jobId)
    IF exportJob IS NULL:
        RETURN
    
    TRY:
        // Update progress
        exportJob.progress = 10
        exportJobRepository.save(exportJob)
        
        // Collect data based on options
        exportData = collectExportData(exportJob.user, exportJob.exportOptions)
        
        // Update progress
        exportJob.progress = 50
        exportJobRepository.save(exportJob)
        
        // Generate file based on format
        fileData = generateExportFile(exportData, exportJob.format)
        
        // Update progress
        exportJob.progress = 80
        exportJobRepository.save(exportJob)
        
        // Upload to storage
        downloadUrl = fileStorageService.upload(fileData, "exports/" + jobId + "." + exportJob.format)
        
        // Update job status
        exportJob.status = COMPLETED
        exportJob.progress = 100
        exportJob.downloadUrl = downloadUrl
        exportJob.fileSize = fileData.size()
        exportJob.expiresAt = now() + 7 days
        exportJob.completedAt = now()
        exportJob.updatedAt = now()
        
        exportJobRepository.save(exportJob)
        
        // Send completion notification
        notificationService.sendExportCompletionNotification(exportJob.user.getUserId(), exportJob)
        
    CATCH Exception e:
        exportJob.status = FAILED
        exportJob.errorMessage = e.getMessage()
        exportJob.updatedAt = now()
        exportJobRepository.save(exportJob)
        
        // Send failure notification
        notificationService.sendExportFailureNotification(exportJob.user.getUserId(), exportJob)
```

### 4.3 Import Learning Data Logic
```pseudocode
FUNCTION importLearningData(userId, file, importRequest):
    // Validate file
    IF NOT validateImportFile(file, importRequest.format):
        RETURN validationError
    
    // Create import job
    importJob = new ImportJob()
    importJob.user = userRepository.findById(userId)
    importJob.format = importRequest.format
    importJob.status = PROCESSING
    importJob.fileName = file.getOriginalFilename()
    importJob.fileSize = file.getSize()
    importJob.progress = 0
    importJob.importOptions = toJson(importRequest)
    importJob.createdAt = now()
    importJob.updatedAt = now()
    
    savedJob = importJobRepository.save(importJob)
    
    // Start async import process
    importService.processImportAsync(savedJob.getJobId(), file)
    
    RETURN successResponse(mapToImportJobDto(savedJob))
```

### 4.4 Process Import Async Logic
```pseudocode
FUNCTION processImportAsync(jobId, file):
    importJob = importJobRepository.findById(jobId)
    IF importJob IS NULL:
        RETURN
    
    TRY:
        // Update progress
        importJob.progress = 10
        importJobRepository.save(importJob)
        
        // Parse file based on format
        parsedData = parseImportFile(file, importJob.format)
        
        // Update progress
        importJob.progress = 30
        importJobRepository.save(importJob)
        
        // Validate data
        validationResults = validateImportData(parsedData, importJob.user)
        
        // Update progress
        importJob.progress = 50
        importJobRepository.save(importJob)
        
        // Process import based on strategy
        importResults = processImportData(parsedData, importJob.user, importJob.importOptions)
        
        // Update progress
        importJob.progress = 90
        importJobRepository.save(importJob)
        
        // Update job status
        importJob.status = COMPLETED
        importJob.progress = 100
        importJob.importResults = toJson(importResults)
        importJob.completedAt = now()
        importJob.updatedAt = now()
        
        importJobRepository.save(importJob)
        
        // Send completion notification
        notificationService.sendImportCompletionNotification(importJob.user.getUserId(), importJob)
        
    CATCH Exception e:
        importJob.status = FAILED
        importJob.errorMessage = e.getMessage()
        importJob.updatedAt = now()
        importJobRepository.save(importJob)
        
        // Send failure notification
        notificationService.sendImportFailureNotification(importJob.user.getUserId(), importJob)
```

### 4.5 Create Backup Logic
```pseudocode
FUNCTION createBackup(userId, backupRequest):
    // Validate input
    IF NOT validateBackupRequest(backupRequest):
        RETURN validationError
    
    // Create backup job
    backup = new BackupHistory()
    backup.user = userRepository.findById(userId)
    backup.backupType = backupRequest.backupType
    backup.status = PROCESSING
    backup.description = backupRequest.description
    backup.backupOptions = toJson(backupRequest)
    backup.createdAt = now()
    backup.updatedAt = now()
    
    savedBackup = backupHistoryRepository.save(backup)
    
    // Start async backup process
    backupService.processBackupAsync(savedBackup.getBackupId())
    
    RETURN successResponse(mapToBackupDto(savedBackup))
```

## 5. Validation Rules

### 5.1 Export Validation
- **Format**: Must be valid enum value (json, csv, xlsx)
- **Date Range**: Start date must be before end date
- **SetIds**: Must exist and belong to user (if provided)
- **File Size Limit**: Maximum 100MB for export

### 5.2 Import Validation
- **Format**: Must match file extension
- **File Size**: Maximum 50MB for import
- **File Type**: Must be valid import format
- **Data Structure**: Must match expected schema

### 5.3 Backup Validation
- **Backup Type**: Must be valid enum value
- **Description**: Optional, max 500 characters
- **Frequency**: Maximum 1 backup per day per user

## 6. Error Handling

### 6.1 Error Codes
- `IMPORT_001`: Import job not found
- `IMPORT_002`: Invalid file format
- `IMPORT_003`: File too large
- `IMPORT_004`: Invalid data structure
- `IMPORT_005`: Import processing failed
- `EXPORT_001`: Export job not found
- `EXPORT_002`: Export processing failed
- `EXPORT_003`: Download link expired
- `BACKUP_001`: Backup not found
- `BACKUP_002`: Backup processing failed

## 7. Security Considerations

### 7.1 File Security
- Validate file types and sizes
- Scan files for malware
- Sanitize imported data
- Implement rate limiting

### 7.2 Data Privacy
- Encrypt sensitive data in exports
- Implement data retention policies
- Secure file storage
- Audit file access

## 8. Observability

### 8.1 Logging
```java
// Log export job creation
log.info("Export job created", 
    "jobId", exportJob.getJobId(), 
    "userId", exportJob.getUser().getUserId(),
    "format", exportJob.getFormat());

// Log import completion
log.info("Import job completed", 
    "jobId", importJob.getJobId(), 
    "successfulImports", importResults.getSuccessfulImports(),
    "failedImports", importResults.getFailedImports());
```

### 8.2 Metrics
- Export/import job success rate
- Average processing time
- File size distribution
- User activity patterns

### 8.3 Alerts
- High failure rate (>10%)
- Large file processing
- Storage quota exceeded
- Security violations

## 9. Testing Strategy

### 9.1 Unit Tests
- File parsing logic
- Data validation
- Format conversion
- Error handling

### 9.2 Integration Tests
- End-to-end import/export flow
- File storage integration
- Notification system
- Database operations

### 9.3 Performance Tests
- Large file processing
- Concurrent operations
- Memory usage
- Storage performance

## 10. Dependencies

### 10.1 Internal Dependencies
- `ExportJobRepository`: Export job management
- `ImportJobRepository`: Import job management
- `BackupHistoryRepository`: Backup management
- `FileStorageService`: File operations
- `NotificationService`: User notifications

### 10.2 External Dependencies
- File Storage Service (AWS S3/Google Cloud Storage)
- Email Service (for notifications)
- Database (PostgreSQL)

### 10.3 Configuration
```yaml
import-export:
  limits:
    max-export-size: 104857600  # 100MB
    max-import-size: 52428800   # 50MB
    max-concurrent-jobs: 5
  formats:
    supported-export: [json, csv, xlsx]
    supported-import: [json, csv, xlsx]
  storage:
    retention-days: 7
    cleanup-interval: 3600
  processing:
    timeout-seconds: 1800
    retry-attempts: 3
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Backend Team  
**Stakeholders**: Development Team, QA Team, DevOps Team
