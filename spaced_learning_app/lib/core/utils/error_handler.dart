import 'package:flutter/foundation.dart';

class ErrorHandler {
  static void logError(String source, dynamic error, {String? prefix}) {
    final message = prefix != null ? '$prefix: $error' : '$error';
    debugPrint('[$source] $message');
  }

  static Future<T?> tryAsync<T>({
    required Future<T> Function() action,
    required String source,
    String? errorPrefix,
    T? defaultValue,
  }) async {
    try {
      return await action();
    } catch (e) {
      final prefix = errorPrefix ?? 'Error';
      logError(source, e, prefix: prefix);
      return defaultValue;
    }
  }
}
