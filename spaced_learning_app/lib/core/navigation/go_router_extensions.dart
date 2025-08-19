// Không cần thay đổi gì
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

extension GoRouterExtensions on GoRouter {
  /// Đi đến route và xóa tất cả các route khác khỏi stack
  void goAndRemoveUntil(
    String location,
    bool Function(Route<dynamic>) predicate,
  ) {
    while (canPop()) {
      pop();
    }
    go(location);
  }

  /// Push với animation tùy chỉnh
  Future<T?> pushWithAnimation<T>(
    BuildContext context,
    String location, {
    Object? extra,
    Duration duration = const Duration(milliseconds: 300),
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
    transition,
  }) {
    // Mặc định sử dụng slide transition nếu không có tùy chỉnh
    transition ??= (context, animation, secondaryAnimation, child) =>
        SlideTransition(
          position: Tween<Offset>(
            begin: const Offset(1.0, 0.0),
            end: Offset.zero,
          ).animate(animation),
          child: child,
        );

    return push<T>(location, extra: extra);
  }

  /// Pop toàn bộ route và thay thế bằng route mới
  void popAllAndPush(String location, {Object? extra}) {
    while (canPop()) {
      pop();
    }
    push(location, extra: extra);
  }

  /// Kiểm tra xem route hiện tại có phải là route cần kiểm tra không
  bool isCurrentRoute(String routeName) {
    // Cách hiện đại để lấy location hiện tại từ GoRouter
    final currentLocation = routeInformationProvider.value.uri.toString();
    return currentLocation == routeName ||
        currentLocation.startsWith('$routeName/');
  }
}
