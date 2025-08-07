# Freezed Usage Guidelines - RepeatWise UI

## 🎯 Migration Completed ✅

**All models have been migrated to Freezed for better maintainability and consistency.**

## 📊 Current Project Status

### ✅ **Migrated Models:**

| Model | Status | Fields | Benefits |
|-------|--------|--------|----------|
| User | ✅ Freezed | 6 | Immutable, copyWith, toString |
| RemindSchedule | ✅ Freezed | 8 | Immutable, copyWith, toString |
| Set | ✅ Freezed | 10 | Immutable, copyWith, toString |
| SetCycle | ✅ Freezed | 10 | Immutable, copyWith, toString |
| SetCreateRequest | ✅ Freezed | 2 | Immutable, copyWith, toString |

## 🎯 When to Use Freezed

### ✅ **Use Freezed for:**

1. **Union Types / Sealed Classes**
```dart
@freezed
class ApiResult<T> with _$ApiResult<T> {
  const factory ApiResult.success(T data) = Success<T>;
  const factory ApiResult.error(String message) = Error<T>;
  const factory ApiResult.loading() = Loading<T>;
}
```

2. **Complex Immutable Data**
```dart
@freezed
class UserProfile with _$UserProfile {
  const factory UserProfile({
    required User user,
    required List<Set> sets,
    required UserStats stats,
    required UserPreferences preferences,
  }) = _UserProfile;
}
```

3. **Large Models (10+ fields)**
```dart
@freezed
class ComplexSet with _$ComplexSet {
  const factory ComplexSet({
    required String id,
    required String name,
    required String description,
    required SetStatus status,
    required String userId,
    required DateTime createdAt,
    required DateTime updatedAt,
    required DateTime? lastReviewedAt,
    required int totalItems,
    required int completedItems,
    required List<SetCycle> cycles,
    required SetMetadata metadata,
    required SetSettings settings,
  }) = _ComplexSet;
}
```

4. **Frequent Updates with copyWith**
```dart
@freezed
class FormState with _$FormState {
  const factory FormState({
    required String email,
    required String password,
    required bool isLoading,
    required String? error,
  }) = _FormState;
}
```

### ❌ **Don't Use Freezed for:**

1. **Simple Enums**
```dart
// ✅ Keep as is - enums are already immutable
enum SetStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
}
```

2. **Very Simple DTOs (2-3 fields)**
```dart
// Consider manual implementation for very simple cases
@JsonSerializable()
class SimpleRequest {
  final String name;
  const SimpleRequest({required this.name});
  factory SimpleRequest.fromJson(Map<String, dynamic> json) => 
      _$SimpleRequestFromJson(json);
}
```

## 🛠️ Migration Benefits

### ✅ **What We Gained:**

1. **Consistency**: All models follow the same pattern
2. **Immutability**: All models are immutable by default
3. **copyWith**: Easy to create modified copies
4. **toString**: Automatic meaningful string representation
5. **equals/hashCode**: Automatic equality comparison
6. **JSON Serialization**: Works seamlessly with json_serializable
7. **Type Safety**: Better compile-time safety
8. **Maintainability**: Less boilerplate code

### 📝 **Example Usage:**

```dart
// Creating instances
final user = User(
  id: '1',
  name: 'John Doe',
  username: 'johndoe',
  email: 'john@example.com',
  createdAt: DateTime.now(),
  updatedAt: DateTime.now(),
);

// Using copyWith
final updatedUser = user.copyWith(
  name: 'Jane Doe',
  updatedAt: DateTime.now(),
);

// Using extensions for computed properties
print(user.displayName); // "John Doe"
print(user.initials); // "JD"
print(user.isRecentlyCreated); // true/false

// JSON serialization
final json = user.toJson();
final fromJson = User.fromJson(json);
```

## 📝 Best Practices

### 1. **Use Extensions for Computed Properties**
```dart
@freezed
class User with _$User {
  const factory User({
    required String id,
    required String name,
    required String email,
  }) = _User;
}

extension UserExtension on User {
  String get displayName => name.isNotEmpty ? name : email.split('@').first;
  String get initials => name.split(' ').map((e) => e[0]).join('').toUpperCase();
}
```

### 2. **Use @Default for Optional Fields**
```dart
@freezed
class Set with _$Set {
  const factory Set({
    required String id,
    required String name,
    @Default(0) int totalItems,
    @Default(0) int completedItems,
  }) = _Set;
}
```

### 3. **Keep Enums Simple**
```dart
enum SetStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
  @JsonValue('ARCHIVED')
  archived,
}
```

## 🚀 Future Development

### ✅ **For New Models:**

1. **Always use Freezed** for data models
2. **Use extensions** for computed properties
3. **Use @Default** for optional fields with default values
4. **Keep enums simple** and use @JsonValue for API compatibility

### 📋 **Code Generation Commands:**

```bash
# Generate all code
flutter pub run build_runner build --delete-conflicting-outputs

# Watch for changes
flutter pub run build_runner watch

# Clean and rebuild
flutter clean && flutter pub get && flutter pub run build_runner build
```

## 🎯 Conclusion

**RepeatWise UI project is now fully standardized with:**
- ✅ **All models use Freezed**
- ✅ **Consistent code generation**
- ✅ **Better maintainability**
- ✅ **Type safety and immutability**
- ✅ **Easy copyWith and JSON serialization**

This provides a solid foundation for future development and ensures code consistency across the project.
