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

  // Book related endpoints
  static final String books = '$basePath/books';
  static final String bookCategories = '$basePath/books/categories';
  static final String bookSearch = '$basePath/books/search';
  static final String bookFilter = '$basePath/books/filter';

  // Module related endpoints
  static final String modules = '$basePath/modules';

  static String modulesByBook(String bookId) =>
      '$basePath/modules/book/$bookId';

  // static String allModulesByBook(String bookId) =>       // <-- Không sử dụng
  //     '$basePath/modules/book/$bookId/all';
  // static String nextModuleNumber(String bookId) =>        // <-- Không sử dụng
  //     '$basePath/modules/book/$bookId/next-number';

  // Progress related endpoints
  static final String progress = '$basePath/progress';

  // static String progressByUser(String userId) =>            // <-- Không sử dụng
  //     '$basePath/progress/user/$userId';
  // static String progressByUserAndBook(String userId, String bookId) => // <-- Không sử dụng
  //     '$basePath/progress/user/$userId/book/$bookId';
  // static String progressByUserAndModule(String userId, String moduleId) => // <-- Không sử dụng
  //     '$basePath/progress/user/$userId/module/$moduleId';
  // static String moduleProgress(String moduleId) =>           // <-- Không sử dụng (Function definition)
  //     '$basePath/progress/module/$moduleId';
  static String dueProgress(String userId) => '$basePath/progress/due';

  // Repetition related endpoints
  static final String repetitions = '$basePath/repetitions';

  static String repetitionsByProgress(String progressId) =>
      '$basePath/repetitions/progress/$progressId';

  static String repetitionSchedule(String progressId) =>
      '$basePath/repetitions/progress/$progressId/schedule';

  // static String dueRepetitions(String userId) =>          // <-- Không sử dụng
  //     '$basePath/repetitions/user/$userId/due';
  // static String repetitionByOrder(String progressId, String order) => // <-- Không sử dụng
  //     '$basePath/repetitions/progress/$progressId/order/$order';

  // Learning Statistics endpoints
  static final String dashboardStats = '$basePath/stats/dashboard';

  // static String userDashboardStats(String userId) =>      // <-- Không sử dụng
  //     '$basePath/stats/users/$userId/dashboard';
  static final String learningInsights = '$basePath/stats/insights';

  // static String userLearningInsights(String userId) =>    // <-- Không sử dụng
  //     '$basePath/stats/users/$userId/insights';

  // Learning Progress endpoints
  static final String learningModules = '$basePath/learning/modules';
  static final String dueModules = '$basePath/learning/modules/due';

  // static final String completedModules = '$basePath/learning/modules/completed'; // <-- Không sử dụng
  static final String uniqueBooks = '$basePath/learning/books';

  // static final String bookStats = '$basePath/learning/books/{book}/stats'; // <-- Không sử dụng
  static final String exportData = '$basePath/learning/export';

  // Grammar related endpoints
  static final String grammars = '$basePath/grammars';

  static String grammarsByModule(String moduleId) =>
      '$basePath/grammars/module/$moduleId/all';
  static final String grammarSearch = '$basePath/grammars/search';
}
