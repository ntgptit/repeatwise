import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../features/auth/presentation/pages/login_page.dart';
import '../../features/auth/presentation/pages/register_page.dart';
import '../../features/auth/presentation/widgets/auth_guard.dart';
import '../../features/dashboard/presentation/pages/dashboard_page.dart';
import '../../features/sets/presentation/pages/sets_page.dart';
import '../../features/sets/presentation/pages/set_detail_page.dart';
import '../../features/profile/presentation/pages/profile_page.dart';

class AppRouter {
  static GoRouter createRouter() {
    return GoRouter(
      initialLocation: '/login',
      routes: [
        // Auth routes
        GoRoute(
          path: '/login',
          name: 'login',
          builder: (context, state) => const AuthGuard(
            requireAuth: false,
            child: LoginPage(),
          ),
        ),
        GoRoute(
          path: '/register',
          name: 'register',
          builder: (context, state) => const AuthGuard(
            requireAuth: false,
            child: RegisterPage(),
          ),
        ),
        
        // Main app routes
        ShellRoute(
          builder: (context, state, child) => AuthGuard(
            child: MainLayout(child: child),
          ),
          routes: [
            GoRoute(
              path: '/dashboard',
              name: 'dashboard',
              builder: (context, state) => const DashboardPage(),
            ),
            GoRoute(
              path: '/sets',
              name: 'sets',
              builder: (context, state) => const SetsPage(),
            ),
            GoRoute(
              path: '/sets/:id',
              name: 'set-detail',
              builder: (context, state) {
                final setId = state.pathParameters['id']!;
                return SetDetailPage(setId: setId);
              },
            ),
            GoRoute(
              path: '/profile',
              name: 'profile',
              builder: (context, state) => const ProfilePage(),
            ),
          ],
        ),
      ],
    );
  }
}

class MainLayout extends StatelessWidget {
  final Widget child;

  const MainLayout({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: child,
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.dashboard),
            label: 'Dashboard',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.list),
            label: 'Sets',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
        onTap: (index) {
          switch (index) {
            case 0:
              context.go('/dashboard');
              break;
            case 1:
              context.go('/sets');
              break;
            case 2:
              context.go('/profile');
              break;
          }
        },
      ),
    );
  }
}
