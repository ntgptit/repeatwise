# Domain Models Documentation

## Overview

This directory contains all domain models for the Spaced Learning App, organized into logical groups with proper separation of concerns and type safety.

## Architecture

### Core Models

- **User**: User profile and authentication data
- **AuthResponse**: Authentication response with tokens and user data
- **LearningSet**: Learning collections (vocabulary, grammar, etc.)
- **LearningInsight**: Insights and analytics data
- **DueStats**: Statistics about due items
- **ReviewHistory**: Review session tracking
- **RemindSchedule**: Reminder scheduling
- **NotificationLog**: Notification delivery tracking
- **ActivityLog**: User activity tracking

### Request Models

#### Authentication
- **AuthRequest**: Login credentials
- **RegisterRequest**: User registration data
- **RefreshTokenRequest**: Token renewal

#### User Management
- **UserUpdateRequest**: Profile update data

### Enums

All enums are organized in the `enums/` directory for better maintainability:

- **InsightType**: Types of learning insights
- **SetCategory**: Categories of learning sets
- **SetStatus**: Status of learning sets
- **NotificationType**: Types of notifications
- **NotificationStatus**: Status of notifications
- **ActivityType**: Types of user activities
- **ReviewStatus**: Status of review sessions
- **SkipReason**: Reasons for skipping reviews
- **RemindStatus**: Status of reminder schedules

## File Structure

```
lib/domain/models/
├── enums/                    # Enum definitions
│   ├── activity_type.dart
│   ├── insight_type.dart
│   ├── notification_status.dart
│   ├── notification_type.dart
│   ├── remind_status.dart
│   ├── review_status.dart
│   ├── set_category.dart
│   ├── set_status.dart
│   ├── skip_reason.dart
│   └── index.dart
├── auth/                     # Authentication models
│   ├── auth_request.dart
│   ├── register_request.dart
│   └── refresh_token_request.dart
├── user/                     # User management models
│   └── user_update_request.dart
├── activity_log.dart         # Activity tracking
├── auth_response.dart        # Authentication response
├── due_stats.dart           # Due statistics
├── learning_insight.dart    # Learning insights
├── learning_set.dart        # Learning sets
├── notification_log.dart    # Notification tracking
├── remind_schedule.dart     # Reminder scheduling
├── review_history.dart      # Review history
├── user.dart               # User model
├── index.dart              # Export file
└── README.md               # This documentation
```

## Usage

### Importing Models

```dart
// Import all models
import 'package:spaced_learning_app/domain/models/index.dart';

// Import specific models
import 'package:spaced_learning_app/domain/models/user.dart';
import 'package:spaced_learning_app/domain/models/auth/auth_request.dart';

// Import enums
import 'package:spaced_learning_app/domain/models/enums/index.dart';
```

### Using Models

```dart
// Create a user
final user = User(
  id: '1',
  username: 'john_doe',
  email: 'john@example.com',
  firstName: 'John',
  lastName: 'Doe',
);

// Create an auth request
final authRequest = AuthRequest(
  usernameOrEmail: 'john@example.com',
  password: 'password123',
);

// Use enums
final setStatus = SetStatus.learning;
final insightType = InsightType.vocabularyRate;
```

### Freezed Models

Some models use Freezed for immutable data classes with automatic generation of:
- `copyWith` methods
- `==` operator and `hashCode`
- `toString` method
- JSON serialization

```dart
// Freezed model usage
final insight = LearningInsightResponse(
  type: InsightType.vocabularyRate,
  message: 'Great progress!',
  icon: 'star',
  color: 'green',
  dataPoint: 85.5,
  priority: 1,
);

// Copy with modifications
final updatedInsight = insight.copyWith(
  dataPoint: 90.0,
  priority: 2,
);
```

## Best Practices

### 1. Use Enums for Type Safety

```dart
// ✅ Good
final status = SetStatus.learning;

// ❌ Bad
final status = 'learning';
```

### 2. Use Proper Documentation

```dart
/// User model representing a user in the application
class User {
  final String id;
  final String username;
  // ...
}
```

### 3. Use Immutable Models

```dart
// ✅ Good - Immutable
class User {
  final String id;
  final String username;
  
  const User({
    required this.id,
    required this.username,
  });
}

// ❌ Bad - Mutable
class User {
  String id;
  String username;
}
```

### 4. Use Copy Methods for Updates

```dart
// ✅ Good
final updatedUser = user.copyWith(
  displayName: 'New Name',
);

// ❌ Bad
user.displayName = 'New Name';
```

### 5. Use Proper JSON Serialization

```dart
// ✅ Good
factory User.fromJson(Map<String, dynamic> json) {
  return User(
    id: json['id'] as String,
    username: json['username'] as String,
  );
}

Map<String, dynamic> toJson() {
  return {
    'id': id,
    'username': username,
  };
}
```

## Validation

### Required Fields

All required fields should be marked with `required` keyword:

```dart
class User {
  final String id;        // Required
  final String username;  // Required
  final String? email;    // Optional
}
```

### Type Safety

Use proper types and avoid dynamic:

```dart
// ✅ Good
final List<String> roles;

// ❌ Bad
final dynamic roles;
```

## Testing

### Model Testing

```dart
test('User model should serialize correctly', () {
  final user = User(
    id: '1',
    username: 'test',
    email: 'test@example.com',
  );
  
  final json = user.toJson();
  final fromJson = User.fromJson(json);
  
  expect(fromJson, equals(user));
});
```

### Enum Testing

```dart
test('SetStatus enum should have correct values', () {
  expect(SetStatus.values, hasLength(4));
  expect(SetStatus.notStarted, isA<SetStatus>());
});
```

## Migration Guide

### From Old Models

1. **Update Imports**
   ```dart
   // Old
   import 'package:spaced_learning_app/domain/models/user.dart';
   
   // New
   import 'package:spaced_learning_app/domain/models/index.dart';
   ```

2. **Use Enums**
   ```dart
   // Old
   final status = 'learning';
   
   // New
   final status = SetStatus.learning;
   ```

3. **Use Proper Types**
   ```dart
   // Old
   final data = json['data'];
   
   // New
   final data = json['data'] as String;
   ```

## Contributing

When adding new models:

1. **Follow Naming Conventions**
   - Use PascalCase for class names
   - Use camelCase for field names
   - Use SCREAMING_SNAKE_CASE for enum values

2. **Add Documentation**
   - Document all public classes and methods
   - Include usage examples
   - Explain complex logic

3. **Use Proper Types**
   - Avoid `dynamic` types
   - Use nullable types (`String?`) for optional fields
   - Use proper collection types (`List<String>`)

4. **Add Tests**
   - Test serialization/deserialization
   - Test copy methods
   - Test enum values

5. **Update Exports**
   - Add new models to `index.dart`
   - Add new enums to `enums/index.dart`

## Resources

- [Freezed Documentation](https://pub.dev/packages/freezed)
- [JSON Serialization](https://dart.dev/guides/libraries/library-tour#json)
- [Dart Language Tour](https://dart.dev/guides/language/language-tour)
