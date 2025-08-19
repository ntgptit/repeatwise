import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/di/providers.dart';
import 'package:spaced_learning_app/core/theme/app_color_scheme.dart';

part 'app_theme_data.g.dart';

abstract final class AppTheme {
  static ThemeData light = FlexThemeData.light(
    colorScheme: lightColorScheme,
    subThemesData: const FlexSubThemesData(
      interactionEffects: true,
      tintedDisabledControls: true,
      useM2StyleDividerInM3: true,
      inputDecoratorIsFilled: true,
      inputDecoratorBorderType: FlexInputBorderType.outline,
      alignedDropdown: true,
      navigationRailUseIndicator: true,
    ),
    keyColors: const FlexKeyColors(
      useSecondary: true,
      useTertiary: true,
      useError: true,
    ),
    visualDensity: FlexColorScheme.comfortablePlatformDensity,
    cupertinoOverrideTheme: const CupertinoThemeData(applyThemeToAll: true),
  );

  static ThemeData dark = FlexThemeData.dark(
    colorScheme: darkColorScheme,
    subThemesData: const FlexSubThemesData(
      interactionEffects: true,
      tintedDisabledControls: true,
      blendOnColors: true,
      useM2StyleDividerInM3: true,
      inputDecoratorIsFilled: true,
      inputDecoratorBorderType: FlexInputBorderType.outline,
      alignedDropdown: true,
      navigationRailUseIndicator: true,
    ),
    keyColors: const FlexKeyColors(
      useSecondary: true,
      useTertiary: true,
      useError: true,
    ),
    visualDensity: FlexColorScheme.comfortablePlatformDensity,
    cupertinoOverrideTheme: const CupertinoThemeData(applyThemeToAll: true),
  );
}

@riverpod
ThemeData lightTheme(Ref ref) => AppTheme.light;

@riverpod
ThemeData darkTheme(Ref ref) => AppTheme.dark;

@riverpod
class ThemeModeState extends _$ThemeModeState {
  @override
  ThemeMode build() {
    final isDarkMode = ref.watch(isDarkModeProvider).valueOrNull ?? false;
    return isDarkMode ? ThemeMode.dark : ThemeMode.light;
  }

  Future<void> toggleTheme() async {
    final storageService = ref.read(storageServiceProvider);
    final current = state;
    if (current == ThemeMode.dark) {
      await storageService.saveDarkMode(false);
      state = ThemeMode.light;
      // Invalidate isDarkModeProvider để cập nhật giá trị mới
      ref.invalidate(isDarkModeProvider);
    } else {
      await storageService.saveDarkMode(true);
      state = ThemeMode.dark;
      // Invalidate isDarkModeProvider để cập nhật giá trị mới
      ref.invalidate(isDarkModeProvider);
    }
  }
}

@riverpod
class IsDarkMode extends _$IsDarkMode {
  @override
  Future<bool> build() {
    final storageService = ref.watch(storageServiceProvider);
    return storageService.isDarkMode();
  }
}
