# Sets Feature - Clean Architecture Implementation

## Tổng quan

Feature Sets đã được nâng cấp để tuân thủ chặt chẽ Clean Architecture với việc tách biệt rõ ràng các tầng và nguyên tắc SOLID.

## Cấu trúc thư mục

```
lib/features/sets/
├── data/
│   └── repositories/
│       └── set_repository_impl.dart
├── domain/
│   ├── repositories/
│   │   └── set_repository.dart
│   └── usecases/
│       ├── usecases.dart
│       ├── get_sets_usecase.dart
│       ├── get_set_by_id_usecase.dart
│       ├── create_set_usecase.dart
│       ├── update_set_usecase.dart
│       ├── delete_set_usecase.dart
│       ├── start_learning_usecase.dart
│       ├── mark_as_mastered_usecase.dart
│       ├── get_daily_review_sets_usecase.dart
│       └── get_set_statistics_usecase.dart
├── di/
│   └── set_dependencies.dart
├── presentation/
│   ├── pages/
│   └── widgets/
└── providers/
    └── set_providers.dart
```

## Cải tiến chính

### 1. Tầng Domain với Use Cases

**Trước đây:**
```dart
// Notifier gọi trực tiếp Repository
final apiRepository = ref.read(apiRepositoryProvider);
final response = await apiRepository.createSet(userId, request);
```

**Bây giờ:**
```dart
// Notifier sử dụng Use Cases
final createSetUseCase = ref.read(createSetUseCaseProvider);
final response = await createSetUseCase.execute(userId, request);
```

### 2. Business Logic được đóng gói trong Use Cases

Mỗi Use Case chứa logic nghiệp vụ cụ thể:

- **CreateSetUseCase**: Validation cho tên set, mô tả
- **UpdateSetUseCase**: Kiểm tra quyền cập nhật
- **StartLearningUseCase**: Kiểm tra điều kiện học tập
- **MarkAsMasteredUseCase**: Kiểm tra tiêu chí thành thạo

### 3. Dependency Injection với Riverpod

```dart
@riverpod
CreateSetUseCase createSetUseCase(CreateSetUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return CreateSetUseCase(repository);
}
```

### 4. Tách biệt Repository Interface

```dart
abstract class SetRepository {
  Future<ApiResponse<List<Set>>> getSetsByUser(String userId);
  Future<ApiResponse<Set>> createSet(String userId, SetCreateRequest request);
  // ... other methods
}
```

## Lợi ích

### 1. Tuân thủ SOLID Principles

- **Single Responsibility**: Mỗi Use Case chỉ có một trách nhiệm
- **Open/Closed**: Dễ dàng mở rộng mà không sửa đổi code hiện tại
- **Dependency Inversion**: Phụ thuộc vào abstraction, không phải concrete implementation

### 2. Dễ Test

```dart
// Unit test cho Use Case
test('should return error when set name is empty', () async {
  final result = await useCase.execute(userId, invalidRequest);
  expect(result.isSuccess, false);
  expect(result.error, 'Set name cannot be empty');
});
```

### 3. Tái sử dụng

- Use Cases có thể được sử dụng bởi nhiều Notifier khác nhau
- Business logic được tập trung, không bị phân tán

### 4. Maintainability

- Code dễ đọc, dễ hiểu
- Thay đổi business logic chỉ cần sửa Use Case
- Không ảnh hưởng đến UI layer

## Hướng dẫn sử dụng

### Thêm Use Case mới

1. Tạo file trong `domain/usecases/`
2. Thêm vào `usecases.dart`
3. Thêm dependency injection trong `set_dependencies.dart`
4. Sử dụng trong Notifier

### Thêm Business Logic

```dart
class CustomUseCase {
  Future<ApiResponse<Result>> execute(Params params) {
    // Business logic validation
    if (!isValid(params)) {
      return Future.value(ApiResponse.error('Validation failed'));
    }
    
    // Business rules
    if (!meetsBusinessRules(params)) {
      return Future.value(ApiResponse.error('Business rule violation'));
    }
    
    return _repository.performAction(params);
  }
}
```

## Kết luận

Việc áp dụng Clean Architecture với Use Cases đã tạo ra một codebase:
- **Sạch**: Tách biệt rõ ràng các tầng
- **Bền vững**: Dễ dàng mở rộng và bảo trì
- **Testable**: Có thể unit test độc lập
- **Maintainable**: Code dễ đọc và hiểu

Đây là nền tảng vững chắc cho việc phát triển các feature khác trong tương lai.
