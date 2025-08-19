import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';

/// Observer cho navigation routes với các tính năng nâng cao
class AppRouteObserver extends NavigatorObserver {
  static final AppRouteObserver _instance = AppRouteObserver._internal();

  factory AppRouteObserver() => _instance;

  AppRouteObserver._internal();

  final List<RouteAware> _routeAwareWidgets = [];
  final Map<String, List<Function>> _routeHandlers = {};
  final List<Route<dynamic>> _routeHistory = [];

  /// Lấy route history
  List<Route<dynamic>> get routeHistory => List.unmodifiable(_routeHistory);

  /// Lấy route hiện tại
  Route<dynamic>? get currentRoute => _routeHistory.isNotEmpty ? _routeHistory.last : null;

  /// Lấy route trước đó
  Route<dynamic>? get previousRoute => _routeHistory.length > 1 ? _routeHistory[_routeHistory.length - 2] : null;

  /// Subscribe một widget để nhận thông báo route changes
  void subscribe(RouteAware routeAware, Route route) {
    if (!_routeAwareWidgets.contains(routeAware)) {
      _routeAwareWidgets.add(routeAware);
    }

    // Thông báo ngay lập tức cho widget mới
    routeAware.didPush();
  }

  /// Unsubscribe một widget khỏi route observer
  void unsubscribe(RouteAware routeAware) {
    _routeAwareWidgets.remove(routeAware);
  }

  /// Thêm handler cho một route cụ thể
  void addRouteHandler(String routePath, Function handler) {
    if (!_routeHandlers.containsKey(routePath)) {
      _routeHandlers[routePath] = [];
    }

    _routeHandlers[routePath]!.add(handler);
  }

  /// Xóa handler cho một route cụ thể
  void removeRouteHandler(String routePath, Function handler) {
    if (_routeHandlers.containsKey(routePath)) {
      _routeHandlers[routePath]!.remove(handler);

      if (_routeHandlers[routePath]!.isEmpty) {
        _routeHandlers.remove(routePath);
      }
    }
  }

  /// Xóa tất cả handlers
  void clearRouteHandlers() {
    _routeHandlers.clear();
  }

  /// Xóa tất cả route aware widgets
  void clearRouteAwareWidgets() {
    _routeAwareWidgets.clear();
  }

  /// Xóa route history
  void clearRouteHistory() {
    _routeHistory.clear();
  }

  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPush(route, previousRoute);
    
    // Thêm route vào history
    _routeHistory.add(route);
    
    // Log route push nếu đang debug
    if (kDebugMode) {
      print('Route pushed: ${route.settings.name}');
    }

    _notifyRouteHandlers(route);

    // Thông báo cho tất cả route aware widgets
    for (final widget in _routeAwareWidgets) {
      widget.didPushNext();
    }
  }

  @override
  void didReplace({Route<dynamic>? newRoute, Route<dynamic>? oldRoute}) {
    super.didReplace(newRoute: newRoute, oldRoute: oldRoute);

    if (newRoute == null) {
      return;
    }

    // Thay thế route cuối cùng trong history
    if (_routeHistory.isNotEmpty) {
      _routeHistory[_routeHistory.length - 1] = newRoute;
    } else {
      _routeHistory.add(newRoute);
    }

    // Log route replace nếu đang debug
    if (kDebugMode) {
      print('Route replaced: ${newRoute.settings.name}');
    }

    _notifyRouteHandlers(newRoute);
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPop(route, previousRoute);

    // Xóa route khỏi history
    if (_routeHistory.isNotEmpty) {
      _routeHistory.removeLast();
    }

    // Log route pop nếu đang debug
    if (kDebugMode) {
      print('Route popped: ${route.settings.name}');
    }

    // Thông báo cho tất cả route aware widgets
    for (final widget in _routeAwareWidgets) {
      widget.didPop();
    }

    if (previousRoute == null) {
      return;
    }

    _notifyRouteHandlers(previousRoute);

    for (final widget in _routeAwareWidgets) {
      widget.didPopNext();
    }
  }

  @override
  void didRemove(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didRemove(route, previousRoute);

    // Xóa route khỏi history
    _routeHistory.remove(route);

    // Log route remove nếu đang debug
    if (kDebugMode) {
      print('Route removed: ${route.settings.name}');
    }
  }

  /// Thông báo cho các route handlers
  void _notifyRouteHandlers(Route route) {
    final String routeName = route.settings.name ?? '';

    _routeHandlers.forEach((path, handlers) {
      if (!routeName.startsWith(path)) {
        return;
      }

      for (var handler in handlers) {
        try {
          handler();
        } catch (e) {
          if (kDebugMode) {
            print('Error in route handler: $e');
          }
        }
      }
    });
  }

  /// Kiểm tra xem có đang ở route cụ thể không
  bool isAtRoute(String routeName) {
    return currentRoute?.settings.name == routeName;
  }

  /// Lấy số lượng routes trong stack
  int get routeCount => _routeHistory.length;

  /// Kiểm tra xem có thể pop không
  bool get canPop => _routeHistory.length > 1;

  /// Lấy tất cả route names trong history
  List<String> get routeNames {
    return _routeHistory
        .map((route) => route.settings.name ?? '')
        .where((name) => name.isNotEmpty)
        .toList();
  }
}
