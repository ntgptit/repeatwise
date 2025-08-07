class NetworkConfig {
  // Base URLs
  static const String devBaseUrl = 'http://localhost:8080/api/v1';
  static const String stagingBaseUrl = 'https://staging-api.repeatwise.com/api/v1';
  static const String productionBaseUrl = 'https://api.repeatwise.com/api/v1';

  // Timeouts
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  static const Duration sendTimeout = Duration(seconds: 30);

  // Retry configuration
  static const int maxRetries = 3;
  static const Duration retryDelay = Duration(seconds: 1);

  // API Endpoints
  static const String loginEndpoint = '/auth/login';
  static const String registerEndpoint = '/auth/register';
  static const String logoutEndpoint = '/auth/logout';
  static const String refreshTokenEndpoint = '/auth/refresh';

  // Sets endpoints
  static const String setsEndpoint = '/sets';
  static String setByIdEndpoint(String id) => '/sets/$id';
  static String setCyclesEndpoint(String setId) => '/sets/$setId/cycles';

  // Remind schedules endpoints
  static const String remindSchedulesEndpoint = '/remind-schedules';
  static String remindScheduleByIdEndpoint(String id) => '/remind-schedules/$id';

  // Headers
  static const Map<String, String> defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  // Get base URL based on environment
  static String getBaseUrl() {
    // You can implement environment detection here
    // For now, using development URL
    return devBaseUrl;
  }

  // API Response codes
  static const int successCode = 200;
  static const int createdCode = 201;
  static const int noContentCode = 204;
  static const int badRequestCode = 400;
  static const int unauthorizedCode = 401;
  static const int forbiddenCode = 403;
  static const int notFoundCode = 404;
  static const int unprocessableEntityCode = 422;
  static const int internalServerErrorCode = 500;
}
