# C4 Model - Level 3: Component Diagram

## Tổng quan

Component Diagram mô tả các component chính trong Web API container của RepeatWise. Mỗi component đại diện cho một module hoặc service có trách nhiệm cụ thể trong hệ thống.

## Component Diagram - Web API

```mermaid
graph TB
    %% External Systems
    MobileApp[Mobile Application<br/>Flutter App]
    
    %% API Gateway Layer
    APIGateway[API Gateway<br/>Spring Security<br/>[Authentication, Rate Limiting]]
    
    %% Controller Layer
    AuthController[Auth Controller<br/>[Login, Register, Reset Password]]
    SetController[Set Controller<br/>[CRUD Operations]]
    CycleController[Cycle Controller<br/>[Learning Cycle Management]]
    ReminderController[Reminder Controller<br/>[Reminder Management]]
    StatisticsController[Statistics Controller<br/>[Analytics & Reports]]
    DataController[Data Controller<br/>[Export, Import, Backup]]
    PreferencesController[Preferences Controller<br/>[Learning Preferences]]
    SettingsController[Settings Controller<br/>[System Settings]]
    
    %% Service Layer
    AuthService[Auth Service<br/>[JWT, Password Management]]
    SetService[Set Service<br/>[Set Business Logic]]
    CycleService[Cycle Service<br/>[SRS Algorithm, Cycle Logic]]
    ReminderService[Reminder Service<br/>[Reminder Calculation]]
    StatisticsService[Statistics Service<br/>[Data Analysis]]
    NotificationService[Notification Service<br/>[Email, Push Notifications]]
    DataService[Data Service<br/>[Export, Import Logic]]
    ExportService[Export Service<br/>[File Generation]]
    ImportService[Import Service<br/>[Data Validation]]
    BackupService[Backup Service<br/>[Backup, Restore]]
    PreferencesService[Preferences Service<br/>[Learning Settings]]
    SettingsService[Settings Service<br/>[System Configuration]]
    FileService[File Service<br/>[File Storage]]
    BackgroundJobService[Background Job Service<br/>[Async Processing]]
    
    %% Repository Layer
    UserRepository[User Repository<br/>[User Data Access]]
    SetRepository[Set Repository<br/>[Set Data Access]]
    CycleRepository[Cycle Repository<br/>[Cycle Data Access]]
    ReviewRepository[Review Repository<br/>[Review History]]
    ReminderRepository[Reminder Repository<br/>[Reminder Data]]
    StatisticsRepository[Statistics Repository<br/>[Statistics Data]]
    BackupRepository[Backup Repository<br/>[Backup History]]
    JobRepository[Job Repository<br/>[Background Jobs]]
    
    %% External Services
    EmailService[Email Service<br/>SendGrid]
    PushNotification[Push Notification<br/>Firebase FCM]
    Analytics[Analytics Service<br/>Google Analytics]
    StorageService[Storage Service<br/>AWS S3/Google Cloud Storage]
    
    %% Data Stores
    Database[(PostgreSQL<br/>Database)]
    Cache[(Redis<br/>Cache)]
    
    %% Relationships
    MobileApp -->|HTTPS/JSON| APIGateway
    
    APIGateway --> AuthController
    APIGateway --> SetController
    APIGateway --> CycleController
    APIGateway --> ReminderController
    APIGateway --> StatisticsController
    APIGateway --> DataController
    APIGateway --> PreferencesController
    APIGateway --> SettingsController
    
    AuthController --> AuthService
    SetController --> SetService
    CycleController --> CycleService
    ReminderController --> ReminderService
    StatisticsController --> StatisticsService
    DataController --> DataService
    PreferencesController --> PreferencesService
    SettingsController --> SettingsService
    
    AuthService --> UserRepository
    SetService --> SetRepository
    CycleService --> CycleRepository
    CycleService --> ReviewRepository
    ReminderService --> ReminderRepository
    StatisticsService --> StatisticsRepository
    StatisticsService --> ReviewRepository
    DataService --> ExportService
    DataService --> ImportService
    DataService --> BackupService
    DataService --> BackgroundJobService
    BackupService --> BackupRepository
    BackgroundJobService --> JobRepository
    PreferencesService --> UserRepository
    SettingsService --> UserRepository
    
    AuthService --> NotificationService
    ReminderService --> NotificationService
    DataService --> NotificationService
    BackupService --> NotificationService
    
    NotificationService --> EmailService
    NotificationService --> PushNotification
    StatisticsService --> Analytics
    ExportService --> FileService
    FileService --> StorageService
    
    UserRepository --> Database
    SetRepository --> Database
    CycleRepository --> Database
    ReviewRepository --> Database
    ReminderRepository --> Database
    StatisticsRepository --> Database
    BackupRepository --> Database
    JobRepository --> Database
    
    AuthService --> Cache
    StatisticsService --> Cache
    PreferencesService --> Cache
    SettingsService --> Cache
    
    %% Styling
    classDef external fill:#08427B,stroke:#073B6F,stroke-width:2px,color:#fff
    classDef controller fill:#438DD5,stroke:#3C7FC7,stroke-width:2px,color:#fff
    classDef service fill:#85BBF0,stroke:#7AAFE6,stroke-width:2px,color:#fff
    classDef repository fill:#FF8C00,stroke:#E67E00,stroke-width:2px,color:#fff
    classDef database fill:#FFD700,stroke:#FFC800,stroke-width:2px,color:#000
    classDef gateway fill:#32CD32,stroke:#28A428,stroke-width:2px,color:#fff
    
    class MobileApp external
    class APIGateway gateway
    class AuthController,SetController,CycleController,ReminderController,StatisticsController,DataController,PreferencesController,SettingsController controller
    class AuthService,SetService,CycleService,ReminderService,StatisticsService,NotificationService,DataService,ExportService,ImportService,BackupService,PreferencesService,SettingsService,FileService,BackgroundJobService service
    class UserRepository,SetRepository,CycleRepository,ReviewRepository,ReminderRepository,StatisticsRepository,BackupRepository,JobRepository repository
    class Database,Cache database
    class EmailService,PushNotification,Analytics,StorageService external
```

## Mô tả chi tiết các Component

### 1. API Gateway Layer

#### Spring Security (API Gateway)
- **Trách nhiệm**:
  - Xác thực và phân quyền người dùng
  - Rate limiting và throttling
  - Request/Response logging
  - CORS configuration
  - Security headers

- **Chức năng chính**:
  - JWT token validation
  - Role-based access control
  - Request filtering
  - Error handling

### 2. Controller Layer

#### Auth Controller
- **Trách nhiệm**: Xử lý các request liên quan đến authentication
- **Endpoints**:
  - `POST /api/auth/login` - Đăng nhập
  - `POST /api/auth/register` - Đăng ký
  - `POST /api/auth/refresh` - Refresh token
  - `POST /api/auth/reset-password` - Reset password
  - `GET /api/auth/profile` - Lấy thông tin profile

#### Set Controller
- **Trách nhiệm**: Quản lý set học tập
- **Endpoints**:
  - `GET /api/sets` - Lấy danh sách set
  - `POST /api/sets` - Tạo set mới
  - `GET /api/sets/{id}` - Lấy chi tiết set
  - `PUT /api/sets/{id}` - Cập nhật set
  - `DELETE /api/sets/{id}` - Xóa set

#### Cycle Controller
- **Trách nhiệm**: Quản lý chu kỳ học tập
- **Endpoints**:
  - `GET /api/cycles` - Lấy danh sách cycle
  - `POST /api/cycles/{id}/start` - Bắt đầu cycle
  - `POST /api/cycles/{id}/review` - Thực hiện review
  - `POST /api/cycles/{id}/complete` - Hoàn thành cycle
  - `POST /api/cycles/{id}/skip` - Bỏ qua cycle

#### Reminder Controller
- **Trách nhiệm**: Quản lý reminder
- **Endpoints**:
  - `GET /api/reminders` - Lấy danh sách reminder
  - `POST /api/reminders/{id}/reschedule` - Reschedule reminder
  - `POST /api/reminders/{id}/mark-done` - Đánh dấu hoàn thành
  - `DELETE /api/reminders/{id}` - Xóa reminder

#### Statistics Controller
- **Trách nhiệm**: Cung cấp thống kê và báo cáo
- **Endpoints**:
  - `GET /api/statistics/overview` - Thống kê tổng quan
  - `GET /api/statistics/sets/{id}` - Thống kê theo set
  - `GET /api/statistics/cycles` - Thống kê chu kỳ
  - `GET /api/statistics/performance` - Thống kê hiệu suất

#### Data Controller
- **Trách nhiệm**: Quản lý export/import và backup dữ liệu
- **Endpoints**:
  - `POST /api/data/export` - Xuất dữ liệu
  - `POST /api/data/import` - Nhập dữ liệu
  - `POST /api/backup/create` - Tạo backup
  - `POST /api/backup/{id}/restore` - Khôi phục từ backup
  - `GET /api/backup/history` - Lịch sử backup

#### Preferences Controller
- **Trách nhiệm**: Quản lý cài đặt học tập
- **Endpoints**:
  - `GET /api/preferences/learning` - Lấy cài đặt học tập
  - `PUT /api/preferences/learning` - Cập nhật cài đặt học tập
  - `GET /api/preferences/notification` - Lấy cài đặt thông báo
  - `PUT /api/preferences/notification` - Cập nhật cài đặt thông báo

#### Settings Controller
- **Trách nhiệm**: Quản lý cài đặt hệ thống
- **Endpoints**:
  - `GET /api/settings/system` - Lấy cài đặt hệ thống
  - `PUT /api/settings/system` - Cập nhật cài đặt hệ thống
  - `GET /api/settings/privacy` - Lấy cài đặt bảo mật
  - `PUT /api/settings/privacy` - Cập nhật cài đặt bảo mật

### 3. Service Layer

#### Auth Service
- **Trách nhiệm**:
  - Xử lý logic authentication
  - JWT token management
  - Password hashing và validation
  - User session management

- **Chức năng chính**:
  - User registration và validation
  - Login authentication
  - Password reset process
  - Token refresh logic

#### Set Service
- **Trách nhiệm**:
  - Quản lý business logic cho set
  - Validation rules
  - Set lifecycle management

- **Chức năng chính**:
  - Set creation và validation
  - Set update logic
  - Set deletion với cascade
  - Set status management

#### Cycle Service
- **Trách nhiệm**:
  - Quản lý chu kỳ học tập
  - SRS algorithm implementation
  - Cycle state management

- **Chức năng chính**:
  - SRS algorithm calculation
  - Next review date computation
  - Cycle progression logic
  - Score processing và analysis

#### Reminder Service
- **Trách nhiệm**:
  - Quản lý reminder logic
  - Reminder calculation
  - Overload prevention

- **Chức năng chính**:
  - Reminder generation
  - Reschedule logic
  - Overload detection
  - Reminder optimization

#### Statistics Service
- **Trách nhiệm**:
  - Tính toán thống kê
  - Data analysis
  - Performance metrics

- **Chức năng chính**:
  - Learning progress calculation
  - Performance trend analysis
  - Set effectiveness metrics
  - User engagement statistics

#### Notification Service
- **Trách nhiệm**:
  - Gửi notifications
  - Email và push notification management
  - Notification templates

- **Chức năng chính**:
  - Email sending
  - Push notification delivery
  - Template management
  - Delivery tracking

#### Data Service
- **Trách nhiệm**:
  - Quản lý export/import dữ liệu
  - Backup và restore operations
  - Data validation và processing

- **Chức năng chính**:
  - Export data generation
  - Import data validation
  - Backup creation và management
  - Data integrity checks

#### Export Service
- **Trách nhiệm**:
  - Tạo file export
  - Format conversion
  - File compression và encryption

- **Chức năng chính**:
  - Multiple format support (JSON, CSV, Excel, PDF)
  - Data formatting
  - File compression
  - Encryption handling

#### Import Service
- **Trách nhiệm**:
  - Parse import files
  - Data validation
  - Conflict resolution

- **Chức năng chính**:
  - File format detection
  - Data parsing
  - Validation rules
  - Conflict detection và resolution

#### Backup Service
- **Trách nhiệm**:
  - Tạo và quản lý backup
  - Restore operations
  - Backup retention

- **Chức năng chính**:
  - Backup creation
  - Data restoration
  - Retention policy management
  - Backup verification

#### Preferences Service
- **Trách nhiệm**:
  - Quản lý learning preferences
  - SRS algorithm configuration
  - User settings management

- **Chức năng chính**:
  - Learning preferences storage
  - SRS algorithm settings
  - Schedule configuration
  - Difficulty settings

#### Settings Service
- **Trách nhiệm**:
  - Quản lý system settings
  - Privacy configuration
  - Display preferences

- **Chức năng chính**:
  - System configuration
  - Privacy settings
  - Display preferences
  - Language settings

#### File Service
- **Trách nhiệm**:
  - File storage management
  - File upload/download
  - Storage optimization

- **Chức năng chính**:
  - File upload/download
  - Storage management
  - File compression
  - CDN integration

#### Background Job Service
- **Trách nhiệm**:
  - Xử lý async operations
  - Job scheduling
  - Task management

- **Chức năng chính**:
  - Async job processing
  - Job scheduling
  - Task monitoring
  - Error handling

### 4. Repository Layer

#### User Repository
- **Trách nhiệm**: Data access cho user entities
- **Operations**:
  - CRUD operations cho users
  - User search và filtering
  - Profile management

#### Set Repository
- **Trách nhiệm**: Data access cho set entities
- **Operations**:
  - CRUD operations cho sets
  - Set search và filtering
  - Set statistics queries

#### Cycle Repository
- **Trách nhiệm**: Data access cho cycle entities
- **Operations**:
  - CRUD operations cho cycles
  - Cycle state queries
  - Cycle history tracking

#### Review Repository
- **Trách nhiệm**: Data access cho review entities
- **Operations**:
  - Review history management
  - Score tracking
  - Performance analysis queries

#### Reminder Repository
- **Trách nhiệm**: Data access cho reminder entities
- **Operations**:
  - Reminder CRUD operations
  - Reminder scheduling queries
  - Overload detection queries

#### Statistics Repository
- **Trách nhiệm**: Data access cho statistics
- **Operations**:
  - Statistics calculation queries
  - Performance metrics queries
  - Historical data analysis

#### Backup Repository
- **Trách nhiệm**: Data access cho backup records
- **Operations**:
  - Backup history management
  - Backup metadata storage
  - Retention policy queries

#### Job Repository
- **Trách nhiệm**: Data access cho background jobs
- **Operations**:
  - Job status tracking
  - Job history management
  - Job scheduling queries

## Luồng xử lý chính

### 1. User Authentication Flow
```
MobileApp → APIGateway → AuthController → AuthService → UserRepository → Database
MobileApp ← APIGateway ← AuthController ← AuthService ← Cache (JWT)
```

### 2. Set Management Flow
```
MobileApp → APIGateway → SetController → SetService → SetRepository → Database
MobileApp ← APIGateway ← SetController ← SetService ← SetRepository ← Database
```

### 3. Learning Cycle Flow
```
MobileApp → APIGateway → CycleController → CycleService → CycleRepository → Database
CycleService → ReviewRepository → Database (Save review)
CycleService → ReminderService → ReminderRepository → Database (Calculate next)
```

### 4. Statistics Flow
```
MobileApp → APIGateway → StatisticsController → StatisticsService → Cache (Check cached)
StatisticsService → StatisticsRepository → Database (If not cached)
StatisticsService → Analytics (Send usage data)
```

### 5. Data Export Flow
```
MobileApp → APIGateway → DataController → DataService → ExportService → FileService → StorageService
DataService → NotificationService → EmailService (Send download link)
```

### 6. Data Import Flow
```
MobileApp → APIGateway → DataController → DataService → ImportService → Validation
DataService → SetRepository/CycleRepository → Database (Import data)
```

### 7. Backup/Restore Flow
```
MobileApp → APIGateway → DataController → BackupService → Database (Create snapshot)
BackupService → FileService → StorageService (Store backup file)
BackupService → NotificationService → EmailService (Send confirmation)
```

### 8. Preferences Management Flow
```
MobileApp → APIGateway → PreferencesController → PreferencesService → UserRepository → Database
PreferencesService → Cache (Invalidate cached preferences)
```

## Design Patterns

### 1. Layered Architecture
- **Controller Layer**: Request handling
- **Service Layer**: Business logic
- **Repository Layer**: Data access

### 2. Dependency Injection
- Constructor injection cho tất cả dependencies
- Spring IoC container management

### 3. Repository Pattern
- Abstraction cho data access
- Consistent interface cho database operations

### 4. Service Pattern
- Business logic encapsulation
- Transaction management
- Cross-cutting concerns

## Error Handling

### 1. Global Exception Handler
- Centralized error handling
- Consistent error responses
- Logging và monitoring

### 2. Validation
- Input validation
- Business rule validation
- Data integrity checks

### 3. Error Codes
- Standardized error codes
- User-friendly error messages
- Developer debugging information 
