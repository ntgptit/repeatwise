import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/navigation/route_observer.dart';

mixin ViewModelRefresher<T extends StatefulWidget> on State<T>
    implements RouteAware {
  final AppRouteObserver _routeObserver = AppRouteObserver();
  bool _isInitialized = false;

  void refreshData() {
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    if (!_isInitialized) {
      _isInitialized = true;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        final route = ModalRoute.of(context);
        if (route != null) {
          _routeObserver.subscribe(this, route);
        }
      });
    }
  }

  @override
  void didPopNext() {
    refreshData();
  }

  @override
  void didPush() {
  }

  @override
  void didPop() {
  }

  @override
  void didPushNext() {
  }

  @override
  void dispose() {
    _routeObserver.unsubscribe(this);
    super.dispose();
  }
}
