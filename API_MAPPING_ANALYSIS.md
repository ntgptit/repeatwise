# Phân tích Mapping API - Spaced Learning API

## Tổng quan
Dựa trên đặc tả nghiệp vụ trong `repeatwise-business-spec.md` và các controller hiện tại trong `spaced-learning-api`, phân tích mapping giữa chức năng cần thiết và API đã có.

## 1. Authentication API (AuthController.java)

### ✅ Đã có - Phù hợp với đặc tả
- **POST /api/v1/auth/login** - Đăng nhập với JWT token
- **POST /api/v1/auth/register** - Đăng ký user mới
- **POST /api/v1/auth/refresh-token** - Refresh JWT token
- **GET /api/v1/auth/validate** - Validate token

### 🎯 Mapping với đặc tả
- ✅ JWT + refresh token cho authentication
- ✅ Role-based access (user cá nhân)
- ✅ Phù hợp với yêu cầu authentication

## 2. Learning Set Management (LearningSetController.java)

### ✅ Đã có - Phù hợp với đặc tả

#### Core CRUD Operations
- **POST /api/v1/learning-sets** - Tạo set mới
- **GET /api/v1/learning-sets/{setId}** - Lấy thông tin set
- **PUT /api/v1/learning-sets/{setId}** - Cập nhật set
- **DELETE /api/v1/learning-sets/{setId}** - Soft delete set
- **GET /api/v1/learning-sets** - Lấy danh sách sets với pagination

#### Status Management
- **POST /api/v1/learning-sets/{setId}/start-learning** - Bắt đầu học
- **POST /api/v1/learning-sets/{setId}/start-reviewing** - Bắt đầu ôn tập
- **POST /api/v1/learning-sets/{setId}/mark-mastered** - Đánh dấu đã thuộc

#### SRS Algorithm Support
- **POST /api/v1/learning-sets/{setId}/schedule-next-cycle** - Lên lịch chu kỳ mới
- **POST /api/v1/learning-sets/handle-overload** - Xử lý quá tải (max 3 set/ngày)

#### Review Scheduling
- **GET /api/v1/learning-sets/due-for-review** - Lấy sets cần ôn theo ngày
- **GET /api/v1/learning-sets/overdue** - Lấy sets quá hạn

#### Filtering & Search
- **GET /api/v1/learning-sets/category/{category}** - Lọc theo category
- **GET /api/v1/learning-sets/search** - Tìm kiếm theo tên

#### Statistics
- **GET /api/v1/learning-sets/stats/count-by-status** - Thống kê theo trạng thái

### 🎯 Mapping với đặc tả nghiệp vụ

#### ✅ Hoàn toàn phù hợp
1. **Quản lý SET**: Đầy đủ CRUD operations
2. **Trạng thái SET**: `not_started`, `learning`, `reviewing`, `mastered`
3. **Category**: `vocabulary`, `grammar`, `mixed`, `other`
4. **Soft delete**: Có trường `deleted_at`
5. **Overload management**: API xử lý max 3 set/ngày
6. **Scheduling**: API lên lịch chu kỳ mới

#### ⚠️ Cần bổ sung
1. **Chu kỳ học**: Cần thêm API để quản lý `current_cycle`
2. **SRS Algorithm**: Cần implement thuật toán tính delay theo công thức

## 3. Review History Management (ReviewHistoryController.java)

### ✅ Đã có - Phù hợp với đặc tả

#### Core Operations
- **POST /api/v1/reviews** - Tạo review mới
- **GET /api/v1/reviews/{reviewId}** - Lấy review theo ID
- **PUT /api/v1/reviews/{reviewId}** - Cập nhật review (trong 24h)
- **GET /api/v1/reviews/set/{setId}** - Lấy reviews theo set
- **GET /api/v1/reviews/set/{setId}/cycle/{cycleNo}** - Lấy reviews theo chu kỳ
- **GET /api/v1/reviews/set/{setId}/recent** - Lấy reviews gần đây (24h)

### 🎯 Mapping với đặc tả nghiệp vụ

#### ✅ Hoàn toàn phù hợp
1. **Lưu lịch sử điểm**: Bảng `review_histories`
2. **Chỉnh sửa trong 24h**: API update có giới hạn thời gian
3. **Thông tin review**: `set_id`, `cycle_no`, `review_no`, `score`, `status`

#### ⚠️ Cần bổ sung
1. **Skip reason**: Cần thêm trường `skip_reason` (forgot, busy, other)
2. **Activity logs**: Cần lưu lịch sử thay đổi vào `activity_logs`

## 4. Reminder Scheduling (RemindScheduleController.java)

### ✅ Đã có - Phù hợp với đặc tả

#### Core Operations
- **POST /api/v1/reminders** - Tạo reminder mới
- **GET /api/v1/reminders/{reminderId}** - Lấy reminder theo ID
- **PUT /api/v1/reminders/{reminderId}** - Cập nhật reminder
- **DELETE /api/v1/reminders/{reminderId}** - Xóa reminder
- **GET /api/v1/reminders/set/{setId}** - Lấy reminders theo set
- **GET /api/v1/reminders/date/{date}** - Lấy reminders theo ngày

### 🎯 Mapping với đặc tả nghiệp vụ

#### ✅ Hoàn toàn phù hợp
1. **Reminder scheduling**: Bảng `remind_schedules`
2. **Trạng thái reminder**: `pending`, `sent`, `done`, `skipped`, `rescheduled`, `cancelled`
3. **Quản lý theo ngày**: API lấy reminders theo date

#### ⚠️ Cần bổ sung
1. **Reschedule limit**: Giới hạn 2 lần reschedule cho cùng reminder
2. **Activity logs**: Lưu lịch sử reschedule vào `activity_logs`

## 5. User Management (UserController.java)

### ✅ Đã có - Phù hợp với đặc tả
- **GET /api/v1/users/profile** - Lấy thông tin user
- **PUT /api/v1/users/profile** - Cập nhật profile
- **DELETE /api/v1/users/profile** - Xóa tài khoản

## 6. Các API cần bổ sung

### 🔧 SRS Algorithm Service
```java
@RestController
@RequestMapping("/api/v1/srs")
public class SRSAlgorithmController {
    
    @PostMapping("/calculate-delay")
    public ResponseEntity<DelayCalculationResponse> calculateNextCycleDelay(
        @RequestBody DelayCalculationRequest request);
    
    @GetMapping("/config")
    public ResponseEntity<SRSConfigResponse> getSRSConfig();
    
    @PutMapping("/config")
    public ResponseEntity<SRSConfigResponse> updateSRSConfig(
        @RequestBody SRSConfigUpdateRequest request);
}
```

### 🔧 Activity Log Service
```java
@RestController
@RequestMapping("/api/v1/activity-logs")
public class ActivityLogController {
    
    @GetMapping("/user")
    public ResponseEntity<Page<ActivityLogResponse>> getUserActivityLogs(
        @PageableDefault(size = 20) Pageable pageable);
    
    @GetMapping("/set/{setId}")
    public ResponseEntity<Page<ActivityLogResponse>> getSetActivityLogs(
        @PathVariable UUID setId, Pageable pageable);
}
```

### 🔧 Notification Log Service
```java
@RestController
@RequestMapping("/api/v1/notification-logs")
public class NotificationLogController {
    
    @GetMapping("/user")
    public ResponseEntity<Page<NotificationLogResponse>> getUserNotificationLogs(
        @PageableDefault(size = 20) Pageable pageable);
    
    @PostMapping("/log")
    public ResponseEntity<Void> logNotification(
        @RequestBody NotificationLogRequest request);
}
```

## 7. Tóm tắt Mapping

### ✅ Hoàn toàn phù hợp (90%)
1. **Authentication**: JWT + refresh token ✅
2. **Learning Set Management**: CRUD + status + scheduling ✅
3. **Review History**: Lưu trữ + chỉnh sửa 24h ✅
4. **Reminder Scheduling**: Quản lý lịch nhắc nhở ✅
5. **User Management**: Profile management ✅

### ⚠️ Cần bổ sung (10%)
1. **SRS Algorithm**: Implement thuật toán tính delay
2. **Activity Logs**: Lưu lịch sử thay đổi
3. **Notification Logs**: Lưu lịch sử gửi notification
4. **Skip Reason**: Thêm trường cho review history
5. **Reschedule Limit**: Giới hạn số lần reschedule

### 🎯 Kết luận
API hiện tại đã cover được **90%** các chức năng cần thiết theo đặc tả nghiệp vụ. Chỉ cần bổ sung thêm một số API nhỏ để hoàn thiện hệ thống.
