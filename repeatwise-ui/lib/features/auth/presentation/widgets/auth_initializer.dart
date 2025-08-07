import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/services/storage_service.dart';
import '../providers/auth_providers.dart';

class AuthInitializer extends ConsumerStatefulWidget {
  final Widget child;

  const AuthInitializer({
    super.key,
    required this.child,
  });

  @override
  ConsumerState<AuthInitializer> createState() => _AuthInitializerState();
}

class _AuthInitializerState extends ConsumerState<AuthInitializer> {
  bool _isInitialized = false;

  @override
  void initState() {
    super.initState();
    _initializeAuth();
  }

  Future<void> _initializeAuth() async {
    try {
      final storageService = ref.read(storageServiceProvider);
      final token = await storageService.getToken();

      if (token != null) {
        // Try to get current user from API to validate token
        await ref.read(authNotifierProvider.notifier).getCurrentUser();
      }
    } catch (e) {
      // Clear invalid data
      final storageService = ref.read(storageServiceProvider);
      await storageService.removeToken();
      await storageService.removeUser();
    } finally {
      setState(() {
        _isInitialized = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (!_isInitialized) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    return widget.child;
  }
}
