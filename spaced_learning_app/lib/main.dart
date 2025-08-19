import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:spaced_learning_app/core/navigation/router.dart';
import 'package:spaced_learning_app/core/theme/app_theme_data.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';

import 'core/di/providers.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize daily task checker if needed
  final prefs = await SharedPreferences.getInstance();
  final isCheckerActive = prefs.getBool('daily_task_checker_active') ?? false;

  final container = ProviderContainer();

  // Khởi tạo trạng thái xác thực ngay khi ứng dụng khởi động
  await container.read(authStateProvider.future);

  if (isCheckerActive) {
    // Initialize the daily task checker
    await container.read(dailyTaskCheckerProvider.future);
  }

  runApp(UncontrolledProviderScope(container: container, child: const MyApp()));
}

class MyApp extends ConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeMode = ref.watch(themeModeStateProvider);
    final router = ref.watch(routerProvider);

    return MaterialApp.router(
      title: 'Spaced Learning App',
      theme: ref.watch(lightThemeProvider),
      darkTheme: ref.watch(darkThemeProvider),
      themeMode: themeMode,
      routerConfig: router,
    );
  }
}
