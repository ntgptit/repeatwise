// Không cần thay đổi, giữ nguyên
/// Lớp chứa các constant cho các route trong ứng dụng
/// Sử dụng thay vì hardcode string để tránh lỗi typo
class RouteConstants {
  // Root routes
  static const String home = '/';
  static const String login = '/login';
  static const String register = '/register';
  static const String forgotPassword = '/forgot-password';

  // Feature routes
  static const String books = '/books';
  static const String bookDetail = '/books/:id';
  static const String moduleDetail = '/books/:id/modules/:moduleId';

  static const String learning = '/learning';
  static const String learningProgress = '/learning/progress/:id';
  static const String learningModule = '/learning/modules/:id';

  static const String profile = '/profile';
  static const String dueProgress = '/due-progress';
  static const String reminderSettings = '/settings/reminders';
  static const String help = '/help';
  static const String spacedRepetition = '/help/spaced-repetition';
  static const String taskReport = '/task-report';
  static const String progressDetail = '/progress/:id';

  // Helper methods to format routes with parameters
  static String bookDetailRoute(String bookId) => '/books/$bookId';

  static String moduleDetailRoute(String bookId, String moduleId) =>
      '/books/$bookId/modules/$moduleId';

  static String progressDetailRoute(String progressId) =>
      '/progress/$progressId';

  static String learningProgressRoute(String progressId) =>
      '/learning/progress/$progressId';

  static String learningModuleRoute(String moduleId) =>
      '/learning/modules/$moduleId';
}
