import 'package:spaced_learning_app/core/constants/app_constants.dart';

/// API endpoint constants
class ApiEndpoints {
  static String basePath = AppConstants.baseUrl + AppConstants.apiPrefix;

  // Auth related endpoints
  static final String login = '$basePath/auth/login';
  static final String register = '$basePath/auth/register';
  static final String refreshToken = '$basePath/auth/refresh-token';
  static final String validateToken = '$basePath/auth/validate';

  // User related endpoints
  static final String currentUser = '$basePath/users/me';
  static final String users = '$basePath/users';
  static final String updateUser = '$basePath/users/me';

  // Learning Set related endpoints
  static final String learningSets = '$basePath/learning-sets';
  static String learningSetById(String id) => '$basePath/learning-sets/$id';
  static final String learningSetCategories =
      '$basePath/learning-sets/categories';

  // Remind Schedule related endpoints
  static final String remindSchedules = '$basePath/remind-schedules';
  static String remindScheduleById(String id) =>
      '$basePath/remind-schedules/$id';
  static String remindSchedulesByUser(String userId) =>
      '$basePath/remind-schedules/user/$userId';

  // Review History related endpoints
  static final String reviewHistories = '$basePath/review-histories';
  static String reviewHistoryById(String id) =>
      '$basePath/review-histories/$id';
  static String reviewHistoriesBySet(String setId) =>
      '$basePath/review-histories/set/$setId';
  static String reviewHistoriesByUser(String userId) =>
      '$basePath/review-histories/user/$userId';
}
