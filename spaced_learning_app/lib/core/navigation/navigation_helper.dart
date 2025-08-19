import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'route_constants.dart';
import 'route_parameters.dart';

/// Lớp helper cho navigation với các method tiện ích
class NavigationHelper {
  const NavigationHelper._();

  /// Pop về màn hình gốc (root) của ứng dụng
  static void popToRoot(BuildContext context) {
    final router = GoRouter.of(context);
    while (router.canPop()) {
      router.pop();
    }
  }

  /// Xóa toàn bộ stack và điều hướng đến route mới
  static void clearStackAndGo(BuildContext context, String route) {
    popToRoot(context);
    GoRouter.of(context).go(route);
  }

  /// Điều hướng an toàn với kiểm tra ID hợp lệ
  static void safeNavigate(
    BuildContext context,
    String route, {
    String? id,
    Object? extra,
  }) {
    if (id == null || id.isEmpty) {
      _showErrorSnackBar(context, 'Invalid ID for navigation');
      return;
    }

    final formattedRoute = route.endsWith('/') ? '$route$id' : '$route/$id';
    GoRouter.of(context).go(formattedRoute, extra: extra);
  }

  /// Điều hướng đến book detail với validation
  static void navigateToBookDetail(BuildContext context, String bookId) {
    if (!RouteParameters.isValidBookId(bookId)) {
      _showErrorSnackBar(context, RouteParameters.invalidBookIdMessage);
      return;
    }
    GoRouter.of(context).go(RouteConstants.bookDetailRoute(bookId));
  }

  /// Điều hướng đến module detail với validation
  static void navigateToModuleDetail(
    BuildContext context,
    String bookId,
    String moduleId,
  ) {
    if (!RouteParameters.isValidBookId(bookId)) {
      _showErrorSnackBar(context, RouteParameters.invalidBookIdMessage);
      return;
    }
    if (!RouteParameters.isValidModuleId(moduleId)) {
      _showErrorSnackBar(context, RouteParameters.invalidModuleIdMessage);
      return;
    }
    GoRouter.of(context).go(RouteConstants.moduleDetailRoute(bookId, moduleId));
  }

  /// Điều hướng đến grammar detail với validation
  static void navigateToGrammarDetail(
    BuildContext context,
    String bookId,
    String moduleId,
    String grammarId,
  ) {
    if (!RouteParameters.isValidBookId(bookId)) {
      _showErrorSnackBar(context, RouteParameters.invalidBookIdMessage);
      return;
    }
    if (!RouteParameters.isValidModuleId(moduleId)) {
      _showErrorSnackBar(context, RouteParameters.invalidModuleIdMessage);
      return;
    }
    if (!RouteParameters.isValidGrammarId(grammarId)) {
      _showErrorSnackBar(context, RouteParameters.invalidGrammarIdMessage);
      return;
    }
    GoRouter.of(
      context,
    ).go(RouteConstants.grammarDetailRoute(bookId, moduleId, grammarId));
  }

  /// Điều hướng đến progress detail với validation
  static void navigateToProgressDetail(
    BuildContext context,
    String progressId,
  ) {
    if (!RouteParameters.isValidProgressId(progressId)) {
      _showErrorSnackBar(context, RouteParameters.invalidProgressIdMessage);
      return;
    }
    GoRouter.of(context).go(RouteConstants.progressDetailRoute(progressId));
  }

  /// Điều hướng và lấy kết quả khi quay lại
  static Future<T?> pushWithResult<T>(BuildContext context, String route) {
    return GoRouter.of(context).push<T>(route);
  }

  /// Thoát ứng dụng và quay về màn hình đăng nhập
  static void logout(BuildContext context) {
    clearStackAndGo(context, RouteConstants.login);
  }

  /// Đặt lại history navigation khi đăng nhập thành công
  static void loginSuccess(BuildContext context) {
    clearStackAndGo(context, RouteConstants.home);
  }

  /// Điều hướng đến home
  static void navigateToHome(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.home);
  }

  /// Điều hướng đến profile
  static void navigateToProfile(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.profile);
  }

  /// Điều hướng đến books
  static void navigateToBooks(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.books);
  }

  /// Điều hướng đến learning
  static void navigateToLearning(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.learning);
  }

  /// Điều hướng đến due progress
  static void navigateToDueProgress(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.dueProgress);
  }

  /// Điều hướng đến settings
  static void navigateToSettings(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.reminderSettings);
  }

  /// Điều hướng đến help
  static void navigateToHelp(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.help);
  }

  /// Điều hướng đến spaced repetition info
  static void navigateToSpacedRepetitionInfo(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.spacedRepetition);
  }

  /// Điều hướng đến task report
  static void navigateToTaskReport(BuildContext context) {
    GoRouter.of(context).go(RouteConstants.taskReport);
  }

  /// Hiển thị snackbar lỗi
  static void _showErrorSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red[600],
        behavior: SnackBarBehavior.floating,
      ),
    );
  }

  /// Kiểm tra xem có thể pop không
  static bool canPop(BuildContext context) {
    return GoRouter.of(context).canPop();
  }

  /// Pop với kết quả
  static void popWithResult<T>(BuildContext context, T result) {
    Navigator.of(context).pop(result);
  }

  /// Pop mà không có kết quả
  static void pop(BuildContext context) {
    Navigator.of(context).pop();
  }
}
