# RepeatWise Flutter UI

A Flutter application for the RepeatWise learning platform, built with Riverpod for state management and following clean architecture principles.

## Project Structure

```
lib/
├── main.dart                          # Application entry point
├── core/                              # Core application layer
│   ├── app.dart                       # Main app widget
│   ├── providers/                     # Global app providers
│   │   └── app_providers.dart
│   ├── routing/                       # Navigation configuration
│   │   └── app_router.dart
│   ├── theme/                         # App theming
│   │   └── app_theme.dart
│   ├── services/                      # Core services
│   │   ├── api_service.dart
│   │   └── storage_service.dart
│   └── models/                        # Core data models
│       ├── api_response.dart
│       ├── user.dart
│       ├── set.dart
│       ├── set_cycle.dart
│       └── remind_schedule.dart
└── features/                          # Feature-based modules
    ├── auth/                          # Authentication feature
    │   ├── domain/
    │   │   ├── repositories/
    │   │   │   └── auth_repository.dart
    │   │   └── usecases/
    │   │       ├── login_usecase.dart
    │   │       └── register_usecase.dart
    │   └── presentation/
    │       ├── pages/
    │       │   ├── login_page.dart
    │       │   └── register_page.dart
    │       ├── providers/
    │       │   └── auth_providers.dart
    │       └── widgets/
    │           ├── login_form.dart
    │           └── register_form.dart
    ├── dashboard/                      # Dashboard feature
    │   └── presentation/
    │       ├── pages/
    │       │   └── dashboard_page.dart
    │       └── widgets/
    │           ├── dashboard_stats.dart
    │           ├── recent_sets.dart
    │           └── upcoming_reminders.dart
    ├── sets/                          # Sets management feature
    │   └── presentation/
    │       ├── pages/
    │       │   ├── sets_page.dart
    │       │   └── set_detail_page.dart
    │       └── widgets/
    └── profile/                       # Profile feature
        └── presentation/
            └── pages/
                └── profile_page.dart
```

## Architecture

This project follows **Clean Architecture** principles with **Feature-First** organization:

### Layers:
1. **Presentation Layer**: UI components, pages, and widgets
2. **Domain Layer**: Business logic, repositories, and use cases
3. **Data Layer**: Services, models, and external data sources

### State Management:
- **Riverpod**: Used for state management with annotations
- **Provider Pattern**: Dependency injection and state management
- **Code Generation**: Automatic provider generation with `build_runner`

## Key Features

### State Management with Riverpod
- Uses `@riverpod` annotations for automatic code generation
- Provider-based dependency injection
- Reactive state management with `ConsumerWidget`

### Navigation
- **GoRouter**: Declarative routing with type-safe navigation
- Nested routes and shell routes
- Deep linking support

### Theming
- Material 3 design system
- Light and dark theme support
- Custom color palette and typography

### Data Models
- JSON serialization with `json_annotation`
- Immutable data classes
- Type-safe API responses

## Getting Started

### Prerequisites
- Flutter SDK (>=3.0.0)
- Dart SDK (>=3.0.0)

### Installation

1. **Install dependencies**:
   ```bash
   flutter pub get
   ```

2. **Generate code**:
   ```bash
   flutter packages pub run build_runner build
   ```

3. **Run the app**:
   ```bash
   flutter run
   ```

### Development Commands

- **Generate code**: `flutter packages pub run build_runner build`
- **Watch for changes**: `flutter packages pub run build_runner watch`
- **Clean generated files**: `flutter packages pub run build_runner clean`

## Dependencies

### Core Dependencies
- `flutter_riverpod`: State management
- `riverpod_annotation`: Code generation for providers
- `go_router`: Navigation
- `http`: HTTP client for API calls
- `shared_preferences`: Local storage
- `json_annotation`: JSON serialization

### Development Dependencies
- `build_runner`: Code generation
- `riverpod_generator`: Riverpod code generation
- `json_serializable`: JSON serialization code generation
- `flutter_lints`: Code linting

## Project Structure Benefits

### 1. **Feature-First Organization**
- Each feature is self-contained with its own domain, data, and presentation layers
- Easy to locate and modify feature-specific code
- Clear separation of concerns

### 2. **Clean Architecture**
- **Domain Layer**: Contains business logic and entities
- **Data Layer**: Handles data operations and external dependencies
- **Presentation Layer**: Manages UI and user interactions

### 3. **Riverpod Integration**
- Type-safe dependency injection
- Automatic provider generation
- Reactive state management
- Easy testing and mocking

### 4. **Scalable Structure**
- Easy to add new features
- Clear dependency boundaries
- Maintainable codebase
- Team-friendly organization

## Code Generation

The project uses code generation for:
- **Riverpod Providers**: Automatic provider generation
- **JSON Serialization**: Model serialization/deserialization
- **Type Safety**: Compile-time type checking

Run code generation after any changes to annotated classes:
```bash
flutter packages pub run build_runner build
```

## Testing

The project structure supports easy testing:
- **Unit Tests**: Test business logic in domain layer
- **Widget Tests**: Test UI components
- **Integration Tests**: Test complete features

## Future Enhancements

1. **Additional Features**:
   - Statistics and analytics
   - Advanced reminder scheduling
   - Offline support
   - Push notifications

2. **Architecture Improvements**:
   - Repository pattern implementation
   - Error handling strategies
   - Caching mechanisms
   - Background processing

3. **UI/UX Enhancements**:
   - Custom animations
   - Accessibility improvements
   - Internationalization
   - Advanced theming

## Contributing

1. Follow the existing project structure
2. Use Riverpod annotations for state management
3. Implement proper error handling
4. Add tests for new features
5. Follow Flutter coding conventions

## License

This project is part of the RepeatWise learning platform.
