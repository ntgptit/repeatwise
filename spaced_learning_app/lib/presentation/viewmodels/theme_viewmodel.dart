// lib/presentation/viewmodels/theme_viewmodel.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/di/providers.dart';

part 'theme_viewmodel.g.dart';

@riverpod
class ThemeState extends _$ThemeState {
  @override
  ThemeMode build() {
    _loadThemePreference();
    return ThemeMode.system;
  }

  Future<void> _loadThemePreference() async {
    try {
      final isDarkMode = await ref.read(storageServiceProvider).isDarkMode();
      state = isDarkMode ? ThemeMode.dark : ThemeMode.light;
    } catch (e) {
      state = ThemeMode.light;
    }
  }

  Future<void> toggleTheme() async {
    final newIsDarkMode = state != ThemeMode.dark;
    try {
      await ref.read(storageServiceProvider).saveDarkMode(newIsDarkMode);
      state = newIsDarkMode ? ThemeMode.dark : ThemeMode.light;
    } catch (e) {
      // Keep current state on error
    }
  }

  Future<void> setDarkMode(bool isDarkMode) async {
    if ((state == ThemeMode.dark) == isDarkMode) return;

    try {
      await ref.read(storageServiceProvider).saveDarkMode(isDarkMode);
      state = isDarkMode ? ThemeMode.dark : ThemeMode.light;
    } catch (e) {
      // Keep current state on error
    }
  }
}

@riverpod
bool isDarkMode(Ref ref) {
  final themeMode = ref.watch(themeStateProvider);
  return themeMode == ThemeMode.dark;
}
