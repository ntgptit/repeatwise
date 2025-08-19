import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'route_constants.dart';
import 'route_parameters.dart';

/// Lớp xử lý lỗi navigation tập trung
class NavigationErrorHandler {
  const NavigationErrorHandler._();

  /// Tạo màn hình lỗi chung
  static Widget buildErrorScreen({
    required String message,
    required VoidCallback onBack,
    String? title,
    IconData? icon,
  }) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title ?? 'Error'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: onBack,
        ),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (icon != null) ...[
                Icon(
                  icon,
                  size: 64,
                  color: Colors.grey[400],
                ),
                const SizedBox(height: 16),
              ],
              Text(
                message,
                style: const TextStyle(fontSize: 16),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              ElevatedButton.icon(
                onPressed: onBack,
                icon: const Icon(Icons.arrow_back),
                label: const Text('Go Back'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// Xử lý lỗi parameter không hợp lệ
  static Widget handleInvalidParameter({
    required BuildContext context,
    required String parameterName,
    required String errorMessage,
    String? fallbackRoute,
  }) {
    return buildErrorScreen(
      message: errorMessage,
      icon: Icons.error_outline,
      onBack: () {
        if (fallbackRoute != null) {
          GoRouter.of(context).go(fallbackRoute);
        } else {
          GoRouter.of(context).pop();
        }
      },
    );
  }

  /// Xử lý lỗi book ID không hợp lệ
  static Widget handleInvalidBookId(BuildContext context) {
    return handleInvalidParameter(
      context: context,
      parameterName: RouteParameters.bookId,
      errorMessage: RouteParameters.invalidBookIdMessage,
      fallbackRoute: RouteConstants.books,
    );
  }

  /// Xử lý lỗi module ID không hợp lệ
  static Widget handleInvalidModuleId(BuildContext context, {String? bookId}) {
    return handleInvalidParameter(
      context: context,
      parameterName: RouteParameters.moduleId,
      errorMessage: RouteParameters.invalidModuleIdMessage,
      fallbackRoute: bookId != null 
          ? RouteConstants.bookDetailRoute(bookId)
          : RouteConstants.books,
    );
  }

  /// Xử lý lỗi grammar ID không hợp lệ
  static Widget handleInvalidGrammarId(
    BuildContext context, {
    String? bookId,
    String? moduleId,
  }) {
    return handleInvalidParameter(
      context: context,
      parameterName: RouteParameters.grammarId,
      errorMessage: RouteParameters.invalidGrammarIdMessage,
      fallbackRoute: bookId != null && moduleId != null
          ? RouteConstants.moduleDetailRoute(bookId, moduleId)
          : RouteConstants.books,
    );
  }

  /// Xử lý lỗi progress ID không hợp lệ
  static Widget handleInvalidProgressId(BuildContext context) {
    return handleInvalidParameter(
      context: context,
      parameterName: RouteParameters.progressId,
      errorMessage: RouteParameters.invalidProgressIdMessage,
      fallbackRoute: RouteConstants.learning,
    );
  }

  /// Xử lý lỗi route không tồn tại
  static Widget handleRouteNotFound(BuildContext context) {
    return buildErrorScreen(
      message: 'Page not found',
      icon: Icons.search_off,
      onBack: () => GoRouter.of(context).go(RouteConstants.home),
    );
  }

  /// Xử lý lỗi network/API
  static Widget handleNetworkError(BuildContext context) {
    return buildErrorScreen(
      message: 'Network error. Please check your connection and try again.',
      icon: Icons.wifi_off,
      onBack: () => GoRouter.of(context).pop(),
    );
  }

  /// Xử lý lỗi permission/access
  static Widget handleAccessDenied(BuildContext context) {
    return buildErrorScreen(
      message: 'Access denied. You don\'t have permission to view this page.',
      icon: Icons.lock,
      onBack: () => GoRouter.of(context).pop(),
    );
  }
}
