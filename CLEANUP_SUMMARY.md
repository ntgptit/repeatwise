# Tóm tắt Cleanup - Loại bỏ chức năng không cần thiết

## Mục tiêu
Dựa trên đặc tả nghiệp vụ trong `repeatwise-business-spec.md`, loại bỏ các chức năng không cần thiết khỏi ứng dụng Flutter để tập trung vào core functionality.

## Các chức năng cần thiết theo đặc tả
1. **Quản lý SET** (không phải book, module, grammar riêng lẻ)
2. **Chu kỳ học SRS** với 5 lần ôn tập
3. **Nhập điểm số** và quản lý lịch sử
4. **Reminder và scheduling**
5. **Thống kê tiến trình**

## Các thay đổi đã thực hiện

### 1. Loại bỏ Models không cần thiết
- ✅ Xóa `learning_module.dart`
- ✅ Xóa `vocabulary_stats.dart`
- ✅ Xóa `streak_stats.dart`
- ✅ Xóa `module_stats.dart`

### 2. Loại bỏ ViewModels không cần thiết
- ✅ Xóa `book_viewmodel.dart`
- ✅ Xóa `grammar_viewmodel.dart`
- ✅ Xóa `module_viewmodel.dart`
- ✅ Xóa `learning_stats_viewmodel.dart`
- ✅ Xóa `learning_progress_viewmodel.dart`

### 3. Loại bỏ Widgets không cần thiết
- ✅ Xóa toàn bộ thư mục `widgets/books/`
- ✅ Xóa toàn bộ thư mục `widgets/grammars/`
- ✅ Xóa toàn bộ thư mục `widgets/modules/`
- ✅ Xóa toàn bộ thư mục `widgets/learning/`

### 4. Cập nhật Models còn lại
- ✅ Cập nhật `learning_set.dart` - loại bỏ Freezed, sử dụng class thông thường
- ✅ Cập nhật `review_history.dart` - thêm SkipReason enum
- ✅ Cập nhật `remind_schedule.dart` - cập nhật RemindStatus enum
- ✅ Tạo mới `activity_log.dart` - để lưu lịch sử thay đổi
- ✅ Tạo mới `notification_log.dart` - để lưu lịch sử notification

### 5. Loại bỏ Dependencies không cần thiết
- ✅ Xóa `freezed_annotation` khỏi pubspec.yaml
- ✅ Xóa `freezed` khỏi dev_dependencies
- ✅ Xóa tất cả file `.freezed.dart` và `.g.dart`

### 6. Cấu trúc Models mới

#### LearningSet
```dart
class LearningSet {
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
```

#### ReviewHistory
```dart
class ReviewHistory {
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

#### RemindSchedule
```dart
class RemindSchedule {
  final String id;
  final String setId;
  final String userId;
  final DateTime remindDate;
  final RemindStatus status;
  final DateTime createdAt;
  final DateTime updatedAt;
}
```

## Các chức năng cần phát triển tiếp theo

### 1. Core Business Logic
- [ ] Implement SRS algorithm (tính delay chu kỳ mới)
- [ ] Implement overload management (max 3 set/ngày)
- [ ] Implement reminder scheduling

### 2. UI Components cần tạo mới
- [ ] Set management screens
- [ ] Review input screens
- [ ] Progress tracking screens
- [ ] Reminder management screens

### 3. Services cần cập nhật
- [ ] LearningSetService - quản lý CRUD cho sets
- [ ] ReviewService - quản lý điểm số và lịch sử
- [ ] ReminderService - quản lý lịch nhắc nhở
- [ ] SRSAlgorithmService - thuật toán tính delay

## Lưu ý
- Tất cả models đã được chuyển từ Freezed sang class thông thường
- Cần chạy `flutter pub get` để cập nhật dependencies
- Cần chạy `flutter pub run build_runner build` nếu muốn sử dụng JSON serialization
- Các file còn lại cần được cập nhật để tương thích với cấu trúc mới
