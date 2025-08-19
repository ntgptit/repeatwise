// lib/core/navigation/router.dart
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/navigation/route_builders.dart';
import 'package:spaced_learning_app/core/navigation/route_constants.dart';
import 'package:spaced_learning_app/core/navigation/route_observer.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/scaffold_with_bottom_bar.dart';

part 'router.g.dart';

/// Provider cho GoRouter với cấu hình nâng cao
@riverpod
GoRouter router(Ref ref) {
  // Sử dụng trực tiếp AuthState provider từ auth_viewmodel
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: RouteConstants.home,
    debugLogDiagnostics: kDebugMode,
    redirect: (context, state) => _handleRedirect(authState, state),
    observers: [AppRouteObserver(), _buildGoRouterObserver()],
    routes: _buildAllRoutes(),
    errorBuilder: (context, state) => _buildErrorScreen(context, state),
  );
}

/// Xử lý redirect logic
String? _handleRedirect(AsyncValue<bool> authState, GoRouterState state) {
  // Đảm bảo rằng chúng ta chờ đợi trạng thái authState được tải hoàn tất
  if (authState.isLoading) {
    // Không chuyển hướng khi đang kiểm tra xác thực
    return null;
  }

  final isLoggedIn = authState.valueOrNull ?? false;
  final isGoingToLogin = state.matchedLocation == RouteConstants.login;

  // Kiểm tra xem có đang đi đến public route không
  final isGoingToPublicRoute = RouteConstants.isPublicRoute(
    state.matchedLocation,
  );

  // Nếu chưa đăng nhập và đang cố truy cập route cần đăng nhập
  if (!isLoggedIn && !isGoingToPublicRoute) {
    return RouteConstants.login;
  }

  // Nếu đã đăng nhập và cố truy cập route đăng nhập
  if (isLoggedIn && isGoingToLogin) {
    return RouteConstants.home;
  }

  // Không cần redirect
  return null;
}

/// Tạo tất cả routes
List<RouteBase> _buildAllRoutes() {
  return [
    // Auth routes (outside shell)
    ...RouteBuilders.authRoutes,

    // Modal routes (outside shell)
    ...RouteBuilders.modalRoutes,

    // Main app shell with bottom navigation
    ShellRoute(
      builder: (context, state, child) =>
          _buildShellBuilder(context, state, child),
      routes: [
        ...RouteBuilders.homeRoutes,
        ...RouteBuilders.bookRoutes,
        ...RouteBuilders.learningRoutes,
        ...RouteBuilders.profileRoutes,
        ...RouteBuilders.dueProgressRoutes,
        ...RouteBuilders.settingsRoutes,
        ...RouteBuilders.helpRoutes,
        ...RouteBuilders.reportRoutes,
      ],
    ),
  ];
}

/// Builder cho shell route với bottom navigation
Widget _buildShellBuilder(
  BuildContext context,
  GoRouterState state,
  Widget child,
) {
  int currentIndex = _getCurrentIndex(state.matchedLocation);

  return ScaffoldWithBottomBar(currentIndex: currentIndex, child: child);
}

/// Xác định current index cho bottom navigation
int _getCurrentIndex(String location) {
  if (location.startsWith(RouteConstants.books)) {
    return 1;
  } else if (location.startsWith(RouteConstants.dueProgress)) {
    return 2;
  } else if (location.startsWith(RouteConstants.learning)) {
    return 3;
  } else if (location.startsWith(RouteConstants.profile)) {
    return 4;
  }
  return 0; // Home
}

/// Tạo GoRouter observer
NavigatorObserver _buildGoRouterObserver() {
  return GoRouterObserver(
    onPop: (route, result) {
      if (kDebugMode) {
        print('Popped route: ${route.settings.name}');
      }
    },
  );
}

/// Xây dựng error screen
Widget _buildErrorScreen(BuildContext context, GoRouterState state) {
  return Scaffold(
    appBar: AppBar(
      title: const Text('Page Not Found'),
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () => GoRouter.of(context).go(RouteConstants.home),
      ),
    ),
    body: Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 64, color: Colors.red[400]),
            const SizedBox(height: 16),
            const Text(
              'Page Not Found',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              'The page you are looking for does not exist.',
              style: TextStyle(fontSize: 16, color: Colors.grey[600]),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),
            ElevatedButton.icon(
              onPressed: () => GoRouter.of(context).go(RouteConstants.home),
              icon: const Icon(Icons.home),
              label: const Text('Go Home'),
            ),
          ],
        ),
      ),
    ),
  );
}

/// Custom GoRouter observer
class GoRouterObserver extends NavigatorObserver {
  final Function(Route<dynamic> route, dynamic result)? onPop;

  GoRouterObserver({this.onPop});

  @override
  void didPop(Route route, Route? previousRoute) {
    super.didPop(route, previousRoute);
    onPop?.call(route, null);
  }
}
