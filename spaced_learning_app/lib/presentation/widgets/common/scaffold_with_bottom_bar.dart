import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/services/screen_refresh_manager.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_drawer.dart';

import '../../../core/navigation/navigation_helper.dart';

class ScaffoldWithBottomBar extends ConsumerStatefulWidget {
  final Widget child;
  final int currentIndex;

  const ScaffoldWithBottomBar({
    super.key,
    required this.child,
    required this.currentIndex,
  });

  @override
  ConsumerState<ScaffoldWithBottomBar> createState() =>
      _ScaffoldWithBottomBarState();
}

class _ScaffoldWithBottomBarState extends ConsumerState<ScaffoldWithBottomBar> {
  DateTime? _lastTabChangeTime;

  @override
  void didUpdateWidget(ScaffoldWithBottomBar oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.currentIndex != oldWidget.currentIndex) {
      final now = DateTime.now();
      if (_lastTabChangeTime == null ||
          now.difference(_lastTabChangeTime!).inSeconds > 2) {
        _lastTabChangeTime = now;

        WidgetsBinding.instance.addPostFrameCallback((_) {
          if (!mounted) return;

          switch (widget.currentIndex) {
            case 0:
              ref.read(screenRefreshProvider.notifier).refreshScreen('/');
              break;
            case 1:
              ref.read(screenRefreshProvider.notifier).refreshScreen('/books');
              break;
            case 2:
              ref
                  .read(screenRefreshProvider.notifier)
                  .refreshScreen('/due-progress');
              break;
            case 3:
              ref
                  .read(screenRefreshProvider.notifier)
                  .refreshScreen('/learning');
              break;
            case 4:
              ref
                  .read(screenRefreshProvider.notifier)
                  .refreshScreen('/profile');
              break;
          }
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: const SLDrawer(),
      body: widget.child,
      bottomNavigationBar: _buildBottomNavigationBar(context),
    );
  }

  Widget _buildBottomNavigationBar(BuildContext context) {
    final theme = Theme.of(context);

    return BottomNavigationBar(
      currentIndex: widget.currentIndex,
      onTap: (index) => _onTabTapped(context, index),
      type: BottomNavigationBarType.fixed,
      selectedItemColor: theme.colorScheme.primary,
      unselectedItemColor: theme.colorScheme.onSurface.withValues(alpha: 0.6),
      items: const [
        BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
        BottomNavigationBarItem(icon: Icon(Icons.book), label: 'Books'),
        BottomNavigationBarItem(icon: Icon(Icons.today), label: 'Due'),
        BottomNavigationBarItem(
          icon: Icon(Icons.analytics_outlined),
          label: 'Stats',
        ),
        BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),
      ],
    );
  }

  void _onTabTapped(BuildContext context, int index) {
    if (index == widget.currentIndex) {
      switch (index) {
        case 0:
          ref.read(screenRefreshProvider.notifier).refreshScreen('/');
          break;
        case 1:
          ref.read(screenRefreshProvider.notifier).refreshScreen('/books');
          break;
        case 2:
          ref
              .read(screenRefreshProvider.notifier)
              .refreshScreen('/due-progress');
          break;
        case 3:
          ref.read(screenRefreshProvider.notifier).refreshScreen('/learning');
          break;
        case 4:
          ref.read(screenRefreshProvider.notifier).refreshScreen('/profile');
          break;
      }
    }

    String route = '/';
    switch (index) {
      case 0:
        route = '/';
        break;
      case 1:
        route = '/books';
        break;
      case 2:
        route = '/due-progress';
        break;
      case 3:
        route = '/learning';
        break;
      case 4:
        route = '/profile';
        break;
    }

    // Clear stack và đi đến route mới (pop to root within tab)
    NavigationHelper.clearStackAndGo(context, route);
  }
}
