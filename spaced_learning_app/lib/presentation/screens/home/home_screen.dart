// lib/presentation/screens/home/home_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/services/screen_refresh_manager.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/home_content.dart';
import 'package:spaced_learning_app/presentation/screens/home/widgets/home_error.dart';
import 'package:spaced_learning_app/presentation/viewmodels/home_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/home/home_skeleton_screen.dart';

import '../../../core/theme/app_theme_data.dart';
import '../../widgets/home/home_app_bar.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen>
    with SingleTickerProviderStateMixin {
  final ScreenRefreshManager _refreshManager = ScreenRefreshManager();

  // Animation controller for smooth transitions
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;

  bool _isInitialLoading = true;

  @override
  void initState() {
    super.initState();
    _refreshManager.registerRefreshCallback('/', _refreshData);

    // Initialize animation controller
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: AppDimens.durationM),
    );
    _fadeAnimation = CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeIn,
    );

    // Schedule data loading after the frame
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadInitialData();
    });
  }

  @override
  void dispose() {
    _refreshManager.unregisterRefreshCallback('/', _refreshData);
    _animationController.dispose();
    super.dispose();
  }

  Future<void> _loadInitialData() async {
    setState(() {
      _isInitialLoading = true;
    });

    await ref.read(homeViewModelProvider.notifier).loadInitialData();

    if (mounted) {
      setState(() {
        _isInitialLoading = false;
      });
      _animationController.forward();
    }
  }

  Future<void> _refreshData() async {
    if (!mounted) return;

    setState(() {
      _isInitialLoading = true;
    });

    _animationController.reset();

    // Đảm bảo tất cả dữ liệu được tải lại đồng thời
    await ref.read(homeViewModelProvider.notifier).refreshData();

    if (mounted) {
      setState(() {
        _isInitialLoading = false;
      });
      _animationController.forward();
    }
  }

  @override
  Widget build(BuildContext context) {
    // Thay đổi cách theo dõi isDarkMode, sử dụng themeModeStateProvider
    final themeMode = ref.watch(themeModeStateProvider);
    final isDarkMode = themeMode == ThemeMode.dark;

    final homeState = ref.watch(homeViewModelProvider);

    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      appBar: HomeAppBar(
        isDarkMode: isDarkMode,
        onThemeToggle: () =>
            ref.read(themeModeStateProvider.notifier).toggleTheme(),
        onMenuPressed: () => Scaffold.of(context).openDrawer(),
      ),
      body: Builder(
        builder: (context) {
          // Kiểm tra trạng thái tải dữ liệu
          if (_isInitialLoading ||
              ref.read(homeViewModelProvider.notifier).isFirstLoading) {
            return const HomeSkeletonScreen();
          }

          // Kiểm tra trạng thái lỗi
          if (ref.read(homeViewModelProvider.notifier).hasError) {
            return HomeError(
              errorMessage: homeState.errorMessage,
              onRetry: _refreshData,
            );
          }

          return HomeContent(
            onRefresh: _refreshData,
            animationController: _animationController,
            fadeAnimation: _fadeAnimation,
          );
        },
      ),
    );
  }
}
