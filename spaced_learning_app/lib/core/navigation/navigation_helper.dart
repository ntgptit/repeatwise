// Không cần nhiều thay đổi
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class NavigationHelper {
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
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Invalid ID for navigation')),
      );
      return;
    }

    final formattedRoute = route.endsWith('/') ? '$route$id' : '$route/$id';

    GoRouter.of(context).go(formattedRoute, extra: extra);
  }

  /// Điều hướng và lấy kết quả khi quay lại
  static Future<T?> pushWithResult<T>(BuildContext context, String route) {
    return GoRouter.of(context).push<T>(route);
  }

  /// Thoát ứng dụng và quay về màn hình đăng nhập
  static void logout(BuildContext context) {
    clearStackAndGo(context, '/login');
  }

  /// Đặt lại history navigation khi đăng nhập thành công
  static void loginSuccess(BuildContext context) {
    clearStackAndGo(context, '/');
  }
}
