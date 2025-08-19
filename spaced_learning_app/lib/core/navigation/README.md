# Navigation System

Hệ thống navigation được thiết kế theo các best practices với cấu trúc modular và type-safe.

## Cấu trúc thư mục

```
navigation/
├── index.dart                    # Export tất cả navigation files
├── router.dart                   # Main router configuration
├── route_constants.dart          # Route constants và helper methods
├── route_parameters.dart         # Route parameters với type safety
├── route_builders.dart           # Route builders cho từng feature
├── route_observer.dart           # Route observer với advanced features
├── navigation_helper.dart        # Helper methods cho navigation
├── navigation_error_handler.dart # Error handling cho navigation
├── go_router_extensions.dart     # Extensions cho GoRouter
└── README.md                     # Documentation này
```

## Các tính năng chính

### 1. Type Safety
- Route parameters được định nghĩa với type safety
- Validation cho tất cả route parameters
- Helper methods để kiểm tra tính hợp lệ

### 2. Modular Architecture
- Tách biệt route builders theo từng feature
- Dễ dàng maintain và mở rộng
- Clear separation of concerns

### 3. Error Handling
- Centralized error handling
- Custom error screens cho từng loại lỗi
- Graceful fallback mechanisms

### 4. Advanced Features
- Route history tracking
- Custom animations
- Route observers với advanced capabilities
- Debug logging

## Cách sử dụng

### Import navigation
```dart
import 'package:spaced_learning_app/core/navigation/index.dart';
```

### Navigation với validation
```dart
// Navigate to book detail with validation
NavigationHelper.navigateToBookDetail(context, bookId);

// Navigate to module detail with validation
NavigationHelper.navigateToModuleDetail(context, bookId, moduleId);
```

### Custom animations
```dart
// Navigate with fade animation
GoRouter.of(context).pushWithFadeAnimation(context, '/books/123');

// Navigate with slide animation
GoRouter.of(context).pushWithBottomSlideAnimation(context, '/profile');
```

### Route checking
```dart
// Check current route
if (GoRouter.of(context).isAtHome) {
  // Currently at home
}

// Get current route name
final routeName = GoRouter.of(context).currentRouteName;
```

### Error handling
```dart
// Handle invalid parameters
if (!RouteParameters.isValidBookId(bookId)) {
  return NavigationErrorHandler.handleInvalidBookId(context);
}
```

## Route Constants

Tất cả routes được định nghĩa trong `RouteConstants`:

```dart
// Basic routes
RouteConstants.home
RouteConstants.login
RouteConstants.profile

// Parameterized routes
RouteConstants.bookDetailRoute(bookId)
RouteConstants.moduleDetailRoute(bookId, moduleId)
```

## Route Parameters

Route parameters được validate tự động:

```dart
// Validation methods
RouteParameters.isValidBookId(bookId)
RouteParameters.isValidModuleId(moduleId)
RouteParameters.isValidGrammarId(grammarId)
RouteParameters.isValidProgressId(progressId)
```

## Best Practices

1. **Luôn sử dụng RouteConstants** thay vì hardcode strings
2. **Validate parameters** trước khi navigate
3. **Sử dụng NavigationHelper** cho các navigation operations phổ biến
4. **Handle errors gracefully** với NavigationErrorHandler
5. **Sử dụng type-safe methods** cho parameter validation

## Adding New Routes

1. Thêm route constant vào `RouteConstants`
2. Thêm route builder vào `RouteBuilders`
3. Thêm navigation helper method vào `NavigationHelper`
4. Update documentation

## Debugging

Navigation system có built-in debugging:

```dart
// Enable debug logging
debugLogDiagnostics: kDebugMode

// Route observer logging
if (kDebugMode) {
  print('Route pushed: ${route.settings.name}');
}
```

## Performance

- Lazy loading cho route builders
- Efficient route matching
- Minimal memory footprint
- Optimized navigation stack management
