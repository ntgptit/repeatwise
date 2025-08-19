import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'route_constants.dart';

/// Extension cho GoRouter với các method tiện ích
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

  /// Kiểm tra xem có đang ở home route không
  bool get isAtHome => isCurrentRoute(RouteConstants.home);

  /// Kiểm tra xem có đang ở login route không
  bool get isAtLogin => isCurrentRoute(RouteConstants.login);

  /// Kiểm tra xem có đang ở profile route không
  bool get isAtProfile => isCurrentRoute(RouteConstants.profile);

  /// Kiểm tra xem có đang ở books route không
  bool get isAtBooks => isCurrentRoute(RouteConstants.books);

  /// Kiểm tra xem có đang ở learning route không
  bool get isAtLearning => isCurrentRoute(RouteConstants.learning);

  /// Kiểm tra xem có đang ở due progress route không
  bool get isAtDueProgress => isCurrentRoute(RouteConstants.dueProgress);

  /// Lấy current location
  String get currentLocation => routeInformationProvider.value.uri.toString();

  /// Lấy current route name
  String? get currentRouteName => RouteConstants.getRouteName(currentLocation);

  /// Điều hướng với animation fade
  Future<T?> pushWithFadeAnimation<T>(
    BuildContext context,
    String location, {
    Object? extra,
    Duration duration = const Duration(milliseconds: 300),
  }) {
    return pushWithAnimation<T>(
      context,
      location,
      extra: extra,
      duration: duration,
      transition: (context, animation, secondaryAnimation, child) =>
          FadeTransition(
            opacity: animation,
            child: child,
          ),
    );
  }

  /// Điều hướng với animation scale
  Future<T?> pushWithScaleAnimation<T>(
    BuildContext context,
    String location, {
    Object? extra,
    Duration duration = const Duration(milliseconds: 300),
  }) {
    return pushWithAnimation<T>(
      context,
      location,
      extra: extra,
      duration: duration,
      transition: (context, animation, secondaryAnimation, child) =>
          ScaleTransition(
            scale: animation,
            child: child,
          ),
    );
  }

  /// Điều hướng với animation slide từ bottom
  Future<T?> pushWithBottomSlideAnimation<T>(
    BuildContext context,
    String location, {
    Object? extra,
    Duration duration = const Duration(milliseconds: 300),
  }) {
    return pushWithAnimation<T>(
      context,
      location,
      extra: extra,
      duration: duration,
      transition: (context, animation, secondaryAnimation, child) =>
          SlideTransition(
            position: Tween<Offset>(
              begin: const Offset(0.0, 1.0),
              end: Offset.zero,
            ).animate(animation),
            child: child,
          ),
    );
  }

  /// Điều hướng với animation slide từ top
  Future<T?> pushWithTopSlideAnimation<T>(
    BuildContext context,
    String location, {
    Object? extra,
    Duration duration = const Duration(milliseconds: 300),
  }) {
    return pushWithAnimation<T>(
      context,
      location,
      extra: extra,
      duration: duration,
      transition: (context, animation, secondaryAnimation, child) =>
          SlideTransition(
            position: Tween<Offset>(
              begin: const Offset(0.0, -1.0),
              end: Offset.zero,
            ).animate(animation),
            child: child,
          ),
    );
  }
}
