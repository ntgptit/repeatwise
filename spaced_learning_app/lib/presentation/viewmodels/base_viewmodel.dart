import 'package:flutter/foundation.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

abstract class BaseAsyncNotifier<T> extends AsyncNotifier<T> {
  DateTime? lastUpdated;
  bool _isRefreshing = false;

  @override
  Future<T> build(); // Để lớp con bắt buộc override

  Future<void> refresh() async {
    if (_isRefreshing) return;

    _isRefreshing = true;
    state = const AsyncValue.loading();

    try {
      final result = await build();
      lastUpdated = DateTime.now();
      state = AsyncValue.data(result);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    } finally {
      _isRefreshing = false;
    }
  }

  Future<R> executeTask<R>(
    Future<R> Function() task, {
    bool updateOnSuccess = true,
    bool handleLoading = true,
  }) async {
    if (handleLoading) {
      state = const AsyncValue.loading();
    }

    try {
      final result = await task();
      if (updateOnSuccess) {
        lastUpdated = DateTime.now();
        await refresh();
      }
      return result;
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
      rethrow;
    }
  }

  bool shouldRefresh({Duration threshold = const Duration(minutes: 5)}) {
    if (lastUpdated == null) return true;
    final now = DateTime.now();
    return now.difference(lastUpdated!) > threshold;
  }
}

mixin ErrorHandling {
  String? errorMessage;

  void setError(String? message) {
    errorMessage = message;
    debugPrint('${runtimeType.toString()}: setError($message)');
  }

  void clearError() {
    errorMessage = null;
    debugPrint('${runtimeType.toString()}: clearError()');
  }

  void handleError(dynamic error, {String prefix = 'An error occurred'}) {
    final message = error is Exception
        ? '$prefix: ${error.toString()}'
        : '$prefix: ${error.toString()}';
    debugPrint('Error in ${runtimeType.toString()}: $message');
    setError(message);
  }
}
