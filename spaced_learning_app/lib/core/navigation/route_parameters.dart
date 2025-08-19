/// Lớp chứa các parameter cho routes với type safety
class RouteParameters {
  const RouteParameters._();

  /// Book detail parameters
  static const String bookId = 'id';
  static const String moduleId = 'moduleId';
  static const String grammarId = 'grammarId';
  static const String progressId = 'id';

  /// Validation methods
  static bool isValidBookId(String? id) => id != null && id.isNotEmpty;
  static bool isValidModuleId(String? id) => id != null && id.isNotEmpty;
  static bool isValidGrammarId(String? id) => id != null && id.isNotEmpty;
  static bool isValidProgressId(String? id) => id != null && id.isNotEmpty;

  /// Error messages
  static const String invalidBookIdMessage = 'Invalid book ID';
  static const String invalidModuleIdMessage = 'Invalid module ID';
  static const String invalidGrammarIdMessage = 'Invalid grammar ID';
  static const String invalidProgressIdMessage = 'Invalid progress ID';
}

/// Extension để validate route parameters
extension RouteParameterValidation on Map<String, String> {
  String? getBookId() => this[RouteParameters.bookId];
  String? getModuleId() => this[RouteParameters.moduleId];
  String? getGrammarId() => this[RouteParameters.grammarId];
  String? getProgressId() => this[RouteParameters.progressId];

  bool hasValidBookId() => RouteParameters.isValidBookId(getBookId());
  bool hasValidModuleId() => RouteParameters.isValidModuleId(getModuleId());
  bool hasValidGrammarId() => RouteParameters.isValidGrammarId(getGrammarId());
  bool hasValidProgressId() => RouteParameters.isValidProgressId(getProgressId());
}
