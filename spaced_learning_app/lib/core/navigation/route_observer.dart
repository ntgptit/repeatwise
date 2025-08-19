// Không cần thay đổi gì nhiều
import 'package:flutter/material.dart';

class AppRouteObserver extends NavigatorObserver {
  static final AppRouteObserver _instance = AppRouteObserver._internal();

  factory AppRouteObserver() => _instance;

  AppRouteObserver._internal();

  final List<RouteAware> _routeAwareWidgets = [];
  final Map<String, List<Function>> _routeHandlers = {};

  void subscribe(RouteAware routeAware, Route route) {
    _routeAwareWidgets.add(routeAware);

    for (final widget in _routeAwareWidgets) {
      if (widget == routeAware) {
        widget.didPush();
        break;
      }
    }
  }

  void unsubscribe(RouteAware routeAware) {
    _routeAwareWidgets.remove(routeAware);
  }

  void addRouteHandler(String routePath, Function handler) {
    if (!_routeHandlers.containsKey(routePath)) {
      _routeHandlers[routePath] = [];
    }

    _routeHandlers[routePath]!.add(handler);
  }

  void removeRouteHandler(String routePath, Function handler) {
    if (_routeHandlers.containsKey(routePath)) {
      _routeHandlers[routePath]!.remove(handler);

      if (_routeHandlers[routePath]!.isEmpty) {
        _routeHandlers.remove(routePath);
      }
    }
  }

  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPush(route, previousRoute);
    _notifyRouteHandlers(route);

    if (previousRoute == null) {
      return;
    }

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

    _notifyRouteHandlers(newRoute);
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    super.didPop(route, previousRoute);

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

  void _notifyRouteHandlers(Route route) {
    final String routeName = route.settings.name ?? '';

    _routeHandlers.forEach((path, handlers) {
      if (!routeName.startsWith(path)) {
        return;
      }

      for (var handler in handlers) {
        handler();
      }
    });
  }
}
