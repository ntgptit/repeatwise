# Flutter API Mapping - Kết nối với Spaced Learning API

## Tổng quan
Mapping các chức năng Flutter app với API endpoints từ `spaced-learning-api` để đảm bảo tương thích và hoàn thiện hệ thống.

## 1. Authentication Services

### Flutter Service: `AuthService`
```dart
class AuthService {
  // POST /api/v1/auth/login
  Future<AuthResponse> login(String usernameOrEmail, String password);
  
  // POST /api/v1/auth/register
  Future<UserResponse> register(RegisterRequest request);
  
  // POST /api/v1/auth/refresh-token
  Future<AuthResponse> refreshToken(String refreshToken);
  
  // GET /api/v1/auth/validate
  Future<bool> validateToken(String token);
}
```

### Flutter Models cần tạo:
```dart
// lib/domain/models/auth/
class AuthRequest {
  final String usernameOrEmail;
  final String password;
}

class RegisterRequest {
  final String username;
  final String email;
  final String password;
}

class AuthResponse {
  final String accessToken;
  final String refreshToken;
  final UserResponse user;
}
```

## 2. Learning Set Services

### Flutter Service: `LearningSetService`
```dart
class LearningSetService {
  // POST /api/v1/learning-sets
  Future<LearningSetResponse> createSet(LearningSetCreateRequest request);
  
  // GET /api/v1/learning-sets/{setId}
  Future<LearningSetResponse> getSet(String setId);
  
  // PUT /api/v1/learning-sets/{setId}
  Future<LearningSetResponse> updateSet(String setId, LearningSetUpdateRequest request);
  
  // DELETE /api/v1/learning-sets/{setId}
  Future<void> deleteSet(String setId);
  
  // GET /api/v1/learning-sets
  Future<Page<LearningSetResponse>> getUserSets({int page = 0, int size = 20});
  
  // GET /api/v1/learning-sets/category/{category}
  Future<Page<LearningSetResponse>> getSetsByCategory(SetCategory category, {int page = 0, int size = 20});
  
  // GET /api/v1/learning-sets/search
  Future<Page<LearningSetResponse>> searchSets(String query, {int page = 0, int size = 20});
  
  // POST /api/v1/learning-sets/{setId}/start-learning
  Future<void> startLearning(String setId);
  
  // POST /api/v1/learning-sets/{setId}/start-reviewing
  Future<void> startReviewing(String setId);
  
  // POST /api/v1/learning-sets/{setId}/mark-mastered
  Future<void> markAsMastered(String setId);
  
  // GET /api/v1/learning-sets/due-for-review
  Future<List<LearningSetResponse>> getSetsDueForReview(DateTime date);
  
  // GET /api/v1/learning-sets/overdue
  Future<List<LearningSetResponse>> getOverdueSets(DateTime date);
  
  // POST /api/v1/learning-sets/{setId}/schedule-next-cycle
  Future<void> scheduleNextCycle(String setId);
  
  // POST /api/v1/learning-sets/handle-overload
  Future<void> handleOverload(DateTime date);
  
  // GET /api/v1/learning-sets/stats/count-by-status
  Future<int> getSetCountByStatus(SetStatus status);
}
```

### Flutter Models cần tạo:
```dart
// lib/domain/models/learning_set/
class LearningSetCreateRequest {
  final String name;
  final String? description;
  final SetCategory category;
  final int wordCount;
}

class LearningSetUpdateRequest {
  final String? name;
  final String? description;
  final SetCategory? category;
  final int? wordCount;
}

class LearningSetResponse {
  final String id;
  final String name;
  final String? description;
  final SetCategory category;
  final int wordCount;
  final SetStatus status;
  final int currentCycle;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? deletedAt;
}

class Page<T> {
  final List<T> content;
  final int totalElements;
  final int totalPages;
  final int currentPage;
  final int size;
}
```

## 3. Review History Services

### Flutter Service: `ReviewHistoryService`
```dart
class ReviewHistoryService {
  // POST /api/v1/reviews
  Future<ReviewHistoryResponse> createReview(ReviewHistoryCreateRequest request);
  
  // GET /api/v1/reviews/{reviewId}
  Future<ReviewHistoryResponse> getReview(String reviewId);
  
  // PUT /api/v1/reviews/{reviewId}
  Future<ReviewHistoryResponse> updateReview(String reviewId, ReviewHistoryUpdateRequest request);
  
  // GET /api/v1/reviews/set/{setId}
  Future<Page<ReviewHistoryResponse>> getReviewsBySet(String setId, {int page = 0, int size = 20});
  
  // GET /api/v1/reviews/set/{setId}/cycle/{cycleNo}
  Future<List<ReviewHistoryResponse>> getReviewsBySetAndCycle(String setId, int cycleNo);
  
  // GET /api/v1/reviews/set/{setId}/recent
  Future<List<ReviewHistoryResponse>> getRecentReviews(String setId);
}
```

### Flutter Models cần tạo:
```dart
// lib/domain/models/review/
class ReviewHistoryCreateRequest {
  final String setId;
  final int cycleNo;
  final int reviewNo;
  final int score;
  final String? note;
  final SkipReason? skipReason;
}

class ReviewHistoryUpdateRequest {
  final int? score;
  final String? note;
  final SkipReason? skipReason;
}

class ReviewHistoryResponse {
  final String id;
  final String setId;
  final String setName;
  final int cycleNo;
  final int reviewNo;
  final int score;
  final ReviewStatus status;
  final String? note;
  final SkipReason? skipReason;
  final DateTime createdAt;
  final DateTime updatedAt;
}
```

## 4. Reminder Schedule Services

### Flutter Service: `RemindScheduleService`
```dart
class RemindScheduleService {
  // POST /api/v1/reminders
  Future<RemindScheduleResponse> createReminder(RemindScheduleCreateRequest request);
  
  // GET /api/v1/reminders/{reminderId}
  Future<RemindScheduleResponse> getReminder(String reminderId);
  
  // PUT /api/v1/reminders/{reminderId}
  Future<RemindScheduleResponse> updateReminder(String reminderId, RemindScheduleUpdateRequest request);
  
  // DELETE /api/v1/reminders/{reminderId}
  Future<void> deleteReminder(String reminderId);
  
  // GET /api/v1/reminders/set/{setId}
  Future<List<RemindScheduleResponse>> getRemindersBySet(String setId);
  
  // GET /api/v1/reminders/date/{date}
  Future<List<RemindScheduleResponse>> getRemindersByDate(DateTime date);
}
```

### Flutter Models cần tạo:
```dart
// lib/domain/models/reminder/
class RemindScheduleCreateRequest {
  final String setId;
  final DateTime remindDate;
}

class RemindScheduleUpdateRequest {
  final DateTime? remindDate;
  final RemindStatus? status;
}

class RemindScheduleResponse {
  final String id;
  final String setId;
  final String userId;
  final DateTime remindDate;
  final RemindStatus status;
  final DateTime createdAt;
  final DateTime updatedAt;
}
```

## 5. User Services

### Flutter Service: `UserService`
```dart
class UserService {
  // GET /api/v1/users/profile
  Future<UserResponse> getProfile();
  
  // PUT /api/v1/users/profile
  Future<UserResponse> updateProfile(UserUpdateRequest request);
  
  // DELETE /api/v1/users/profile
  Future<void> deleteProfile();
}
```

### Flutter Models cần tạo:
```dart
// lib/domain/models/user/
class UserUpdateRequest {
  final String? username;
  final String? email;
  final String? displayName;
}

class UserResponse {
  final String id;
  final String username;
  final String email;
  final String? displayName;
  final DateTime createdAt;
  final DateTime updatedAt;
}
```

## 6. API Client Configuration

### Flutter API Client: `ApiClient`
```dart
class ApiClient {
  static const String baseUrl = 'http://localhost:8080/api/v1';
  
  // Headers
  static Map<String, String> getAuthHeaders(String token) {
    return {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };
  }
  
  // Interceptors
  static void setupInterceptors(Dio dio) {
    dio.interceptors.add(AuthInterceptor());
    dio.interceptors.add(LoggingInterceptor());
  }
}
```

## 7. ViewModels cần cập nhật

### LearningSetViewModel
```dart
class LearningSetViewModel extends ChangeNotifier {
  final LearningSetService _learningSetService;
  
  List<LearningSetResponse> _sets = [];
  bool _isLoading = false;
  String? _error;
  
  // Methods
  Future<void> loadUserSets();
  Future<void> createSet(LearningSetCreateRequest request);
  Future<void> updateSet(String setId, LearningSetUpdateRequest request);
  Future<void> deleteSet(String setId);
  Future<void> startLearning(String setId);
  Future<void> startReviewing(String setId);
  Future<void> markAsMastered(String setId);
  Future<List<LearningSetResponse>> getSetsDueForReview(DateTime date);
  Future<void> handleOverload(DateTime date);
}
```

### ReviewViewModel
```dart
class ReviewViewModel extends ChangeNotifier {
  final ReviewHistoryService _reviewHistoryService;
  
  // Methods
  Future<void> submitReview(ReviewHistoryCreateRequest request);
  Future<void> updateReview(String reviewId, ReviewHistoryUpdateRequest request);
  Future<List<ReviewHistoryResponse>> getReviewsBySet(String setId);
  Future<List<ReviewHistoryResponse>> getRecentReviews(String setId);
}
```

### ReminderViewModel
```dart
class ReminderViewModel extends ChangeNotifier {
  final RemindScheduleService _remindScheduleService;
  
  // Methods
  Future<void> createReminder(RemindScheduleCreateRequest request);
  Future<void> updateReminder(String reminderId, RemindScheduleUpdateRequest request);
  Future<void> deleteReminder(String reminderId);
  Future<List<RemindScheduleResponse>> getRemindersByDate(DateTime date);
}
```

## 8. Screens cần tạo mới

### Set Management Screens
- `SetListScreen` - Danh sách sets
- `SetCreateScreen` - Tạo set mới
- `SetDetailScreen` - Chi tiết set
- `SetEditScreen` - Chỉnh sửa set

### Review Screens
- `ReviewInputScreen` - Nhập điểm số
- `ReviewHistoryScreen` - Lịch sử ôn tập
- `ReviewStatsScreen` - Thống kê ôn tập

### Reminder Screens
- `ReminderListScreen` - Danh sách nhắc nhở
- `ReminderCreateScreen` - Tạo nhắc nhở
- `ReminderSettingsScreen` - Cài đặt nhắc nhở

### Dashboard Screens
- `HomeScreen` - Màn hình chính với sets cần ôn
- `ProgressScreen` - Tiến trình học tập
- `StatsScreen` - Thống kê tổng quan

## 9. Implementation Priority

### Phase 1: Core Authentication & Set Management
1. ✅ AuthService + AuthViewModel
2. ✅ LearningSetService + LearningSetViewModel
3. ✅ SetListScreen + SetDetailScreen

### Phase 2: Review System
1. ✅ ReviewHistoryService + ReviewViewModel
2. ✅ ReviewInputScreen + ReviewHistoryScreen

### Phase 3: Reminder System
1. ✅ RemindScheduleService + ReminderViewModel
2. ✅ ReminderListScreen + ReminderSettingsScreen

### Phase 4: Dashboard & Statistics
1. ✅ HomeScreen với sets cần ôn
2. ✅ ProgressScreen với thống kê
3. ✅ StatsScreen với insights

## 10. Testing Strategy

### Unit Tests
- Service layer tests
- ViewModel tests
- Model validation tests

### Integration Tests
- API integration tests
- End-to-end user flows

### Widget Tests
- Screen component tests
- Form validation tests
