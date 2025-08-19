/// Lớp chứa các constant cho các route trong ứng dụng
/// Sử dụng thay vì hardcode string để tránh lỗi typo và đảm bảo type safety
class RouteConstants {
  // Private constructor để ngăn instantiation
  RouteConstants._();

  // Root routes
  static const String home = '/';
  static const String login = '/login';
  static const String register = '/register';
  static const String forgotPassword = '/forgot-password';

  // Feature routes
  static const String books = '/books';
  static const String bookDetail = '/books/:id';
  static const String moduleDetail = '/books/:id/modules/:moduleId';
  static const String moduleGrammar = '/books/:id/modules/:moduleId/grammar';
  static const String grammarDetail =
      '/books/:id/modules/:moduleId/grammars/:grammarId';

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

  // Route names for navigation
  static const String homeName = 'home';
  static const String loginName = 'login';
  static const String registerName = 'register';
  static const String booksName = 'books';
  static const String bookDetailName = 'bookDetail';
  static const String moduleDetailName = 'moduleDetail';
  static const String moduleGrammarName = 'moduleGrammar';
  static const String grammarDetailName = 'grammarDetail';
  static const String learningName = 'learning';
  static const String learningProgressName = 'learningProgress';
  static const String learningModuleName = 'learningModule';
  static const String profileName = 'profile';
  static const String dueProgressName = 'dueProgress';
  static const String reminderSettingsName = 'reminderSettings';
  static const String helpName = 'help';
  static const String spacedRepetitionName = 'spacedRepetition';
  static const String taskReportName = 'taskReport';
  static const String progressDetailName = 'progressDetail';

  // Public routes that don't require authentication
  static const List<String> publicRoutes = [login, register, forgotPassword];

  // Helper methods to format routes with parameters
  static String bookDetailRoute(String bookId) => '/books/$bookId';

  static String moduleDetailRoute(String bookId, String moduleId) =>
      '/books/$bookId/modules/$moduleId';

  static String moduleGrammarRoute(String bookId, String moduleId) =>
      '/books/$bookId/modules/$moduleId/grammar';

  static String grammarDetailRoute(
    String bookId,
    String moduleId,
    String grammarId,
  ) => '/books/$bookId/modules/$moduleId/grammars/$grammarId';

  static String progressDetailRoute(String progressId) =>
      '/progress/$progressId';

  static String learningProgressRoute(String progressId) =>
      '/learning/progress/$progressId';

  static String learningModuleRoute(String moduleId) =>
      '/learning/modules/$moduleId';

  /// Kiểm tra xem route có phải là public route không
  static bool isPublicRoute(String route) {
    return publicRoutes.contains(route);
  }

  /// Lấy route name từ path
  static String? getRouteName(String path) {
    switch (path) {
      case home:
        return homeName;
      case login:
        return loginName;
      case register:
        return registerName;
      case books:
        return booksName;
      case learning:
        return learningName;
      case profile:
        return profileName;
      case dueProgress:
        return dueProgressName;
      case reminderSettings:
        return reminderSettingsName;
      case help:
        return helpName;
      case spacedRepetition:
        return spacedRepetitionName;
      case taskReport:
        return taskReportName;
      default:
        return null;
    }
  }
}
