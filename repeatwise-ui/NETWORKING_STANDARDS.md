# Networking Standards - Dio Implementation

## Overview

This document outlines the standardized networking layer using Dio for the RepeatWise Flutter application. The implementation provides a robust, maintainable, and scalable approach to API communication.

## Architecture

### Core Components

1. **DioService** - Core HTTP client with interceptors and error handling
2. **ApiRepository** - Business logic layer for API operations
3. **NetworkConfig** - Centralized configuration
4. **NetworkInterceptor** - Retry logic and request tracking
5. **NetworkUtils** - Utility functions for network operations

### Directory Structure

```
lib/core/
├── config/
│   └── network_config.dart          # Network configuration
├── services/
│   ├── dio_service.dart             # Core Dio implementation
│   ├── api_repository.dart          # API business logic
│   └── network_interceptor.dart     # Retry and tracking logic
├── utils/
│   └── network_utils.dart           # Network utilities
└── models/
    └── api_exception.dart           # Custom exception handling
```

## Features

### ✅ Implemented Features

- **Dio HTTP Client** - Modern HTTP client with interceptors
- **Automatic Token Management** - JWT token handling with interceptors
- **Retry Logic** - Automatic retry for network failures
- **Error Handling** - Comprehensive error categorization and user-friendly messages
- **Request Logging** - Pretty logging for debugging
- **Progress Tracking** - Upload/download progress support
- **Network Utilities** - Helper functions for common operations
- **Configuration Management** - Centralized settings for different environments

### 🔄 Standardized Error Handling

```dart
// Error types are categorized:
- Network errors (timeout, connection issues)
- Server errors (5xx status codes)
- Client errors (4xx status codes)
- Authentication errors (401, 403)
- Validation errors (422)
```

### 🔐 Authentication Flow

```dart
// Automatic token injection
- Tokens are automatically added to requests
- 401 responses trigger token cleanup
- Support for token refresh (configurable)
```

## Usage Examples

### Basic API Call

```dart
// Using ApiRepository
final apiRepo = ref.read(apiRepositoryProvider);
final result = await apiRepo.getSets();

if (result.isSuccess) {
  final sets = result.data!;
  // Handle success
} else {
  final error = result.error!;
  // Handle error
}
```

### Direct Dio Usage

```dart
// Using DioService directly
final dioService = ref.read(dioServiceProvider);

// GET request
final data = await dioService.get<Map<String, dynamic>>(
  '/sets',
  fromJson: (json) => Set.fromJson(json),
);

// POST request
final response = await dioService.post<Map<String, dynamic>>(
  '/sets',
  data: {'name': 'New Set'},
  fromJson: (json) => Set.fromJson(json),
);
```

### Error Handling

```dart
try {
  final result = await apiRepo.getSets();
  // Handle success
} on ApiException catch (e) {
  // Handle specific API errors
  print('Error: ${e.message}, Status: ${e.statusCode}');
} catch (e) {
  // Handle unexpected errors
  print('Unexpected error: $e');
}
```

## Configuration

### Environment Configuration

```dart
// network_config.dart
class NetworkConfig {
  static const String devBaseUrl = 'http://localhost:8080/api/v1';
  static const String stagingBaseUrl = 'https://staging-api.repeatwise.com/api/v1';
  static const String productionBaseUrl = 'https://api.repeatwise.com/api/v1';
  
  static String getBaseUrl() {
    // Implement environment detection
    return devBaseUrl;
  }
}
```

### Timeout Configuration

```dart
static const Duration connectTimeout = Duration(seconds: 30);
static const Duration receiveTimeout = Duration(seconds: 30);
static const Duration sendTimeout = Duration(seconds: 30);
```

### Retry Configuration

```dart
static const int maxRetries = 3;
static const Duration retryDelay = Duration(seconds: 1);
```

## Best Practices

### 1. Use ApiRepository for Business Logic

```dart
// ✅ Good - Use repository pattern
final apiRepo = ref.read(apiRepositoryProvider);
final result = await apiRepo.getSets();

// ❌ Avoid - Direct Dio usage in UI
final dioService = ref.read(dioServiceProvider);
final response = await dioService.get('/sets');
```

### 2. Proper Error Handling

```dart
// ✅ Good - Comprehensive error handling
try {
  final result = await apiRepo.login(email, password);
  if (result.isSuccess) {
    // Handle success
  } else {
    // Handle API error
    showError(result.error!);
  }
} on ApiException catch (e) {
  // Handle specific exceptions
} catch (e) {
  // Handle unexpected errors
}

// ❌ Avoid - Ignoring errors
final result = await apiRepo.login(email, password);
// Missing error handling
```

### 3. Use NetworkUtils for Common Operations

```dart
// ✅ Good - Use utility functions
if (await NetworkUtils.hasInternetConnection()) {
  // Make API call
}

// ✅ Good - User-friendly error messages
final message = NetworkUtils.getUserFriendlyErrorMessage(dioException);
```

### 4. Proper Resource Management

```dart
// ✅ Good - Dispose resources
@riverpod
DioService dioService(Ref ref) {
  final service = DioService(storageService);
  ref.onDispose(() => service.dispose());
  return service;
}
```

## Migration from HTTP Package

### Before (HTTP Package)

```dart
// Old implementation
final response = await http.get(
  Uri.parse('$baseUrl/sets'),
  headers: await _getAuthHeaders(),
);

if (response.statusCode == 200) {
  final data = json.decode(response.body) as List;
  return ApiResponse.success(data);
} else {
  return ApiResponse.error('Failed to fetch sets');
}
```

### After (Dio Implementation)

```dart
// New implementation
final data = await _dioService.get<List<dynamic>>(
  '/sets',
  fromJson: (json) => (json as List)
      .map((item) => Set.fromJson(item as Map<String, dynamic>))
      .toList(),
);
return ApiResponse.success(data);
```

## Testing

### Unit Testing

```dart
// Test DioService
test('should handle network errors', () async {
  final dioService = DioService(mockStorageService);
  
  expect(
    () => dioService.get('/invalid-endpoint'),
    throwsA(isA<ApiException>()),
  );
});
```

### Integration Testing

```dart
// Test ApiRepository
test('should fetch sets successfully', () async {
  final apiRepo = ApiRepository(mockDioService);
  final result = await apiRepo.getSets();
  
  expect(result.isSuccess, true);
  expect(result.data, isA<List<Set>>());
});
```

## Performance Considerations

### 1. Connection Pooling

Dio automatically manages connection pooling for better performance.

### 2. Request Cancellation

```dart
// Cancel ongoing requests
final cancelToken = CancelToken();
dioService.get('/sets', cancelToken: cancelToken);

// Cancel when needed
cancelToken.cancel('User cancelled');
```

### 3. Caching

```dart
// Implement caching for frequently accessed data
class CachedApiRepository {
  final Map<String, dynamic> _cache = {};
  
  Future<ApiResponse<List<Set>>> getSets() async {
    if (_cache.containsKey('sets')) {
      return ApiResponse.success(_cache['sets']);
    }
    
    final result = await _apiRepo.getSets();
    if (result.isSuccess) {
      _cache['sets'] = result.data;
    }
    return result;
  }
}
```

## Security Considerations

### 1. Token Security

- Tokens are stored securely using StorageService
- Automatic token cleanup on 401 responses
- Support for token refresh mechanism

### 2. HTTPS Enforcement

```dart
// Production configuration
static const String productionBaseUrl = 'https://api.repeatwise.com/api/v1';
```

### 3. Certificate Pinning

```dart
// Add certificate pinning for production
_dio.httpClientAdapter = IOHttpClientAdapter(
  createHttpClient: () {
    final client = HttpClient();
    client.badCertificateCallback = (cert, host, port) {
      // Implement certificate validation
      return false;
    };
    return client;
  },
);
```

## Monitoring and Logging

### Request Logging

PrettyDioLogger provides detailed request/response logging:

```
DIO | POST | http://localhost:8080/api/v1/auth/login
DIO | REQUEST | {"email":"user@example.com","password":"password"}
DIO | RESPONSE | {"success":true,"user":{"id":1,"name":"User"}}
```

### Performance Monitoring

```dart
// Track request duration
final duration = response.extra['requestDuration'] as Duration?;
if (duration != null) {
  print('Request took: ${duration.inMilliseconds}ms');
}
```

## Troubleshooting

### Common Issues

1. **Connection Timeout**
   - Check network connectivity
   - Verify server is running
   - Adjust timeout settings

2. **401 Unauthorized**
   - Check token validity
   - Verify authentication flow
   - Clear stored tokens

3. **500 Server Error**
   - Check server logs
   - Verify API endpoints
   - Test with Postman/curl

### Debug Mode

Enable detailed logging in debug mode:

```dart
// PrettyDioLogger is automatically added in debug builds
// Disable in release builds
if (kDebugMode) {
  _dio.interceptors.add(PrettyDioLogger());
}
```

## Future Enhancements

### Planned Features

- [ ] GraphQL support
- [ ] WebSocket integration
- [ ] Offline-first architecture
- [ ] Request/response compression
- [ ] Advanced caching strategies
- [ ] API rate limiting
- [ ] Request queuing
- [ ] Background sync

### Performance Optimizations

- [ ] Request deduplication
- [ ] Response caching
- [ ] Lazy loading
- [ ] Image optimization
- [ ] Bundle size optimization

## Conclusion

This standardized networking layer provides a robust foundation for API communication in the RepeatWise application. The implementation follows Flutter best practices and provides excellent developer experience with comprehensive error handling, logging, and testing support.

For questions or contributions, please refer to the project documentation or contact the development team.
