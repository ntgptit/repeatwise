import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:repeatwise_ui/core/providers/app_providers.dart';

import 'theme/app_theme.dart';
import '../features/auth/presentation/widgets/auth_initializer.dart';

class RepeatWiseApp extends ConsumerWidget {
  const RepeatWiseApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final router = ref.watch(appRouterProvider);
    
    return MaterialApp.router(
      title: 'RepeatWise',
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      routerConfig: router,
      debugShowCheckedModeBanner: false,
      builder: (context, child) {
        return AuthInitializer(
          child: child!,
        );
      },
    );
  }
}
