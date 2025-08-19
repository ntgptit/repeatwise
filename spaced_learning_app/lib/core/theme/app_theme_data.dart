import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/di/providers.dart';
import 'package:spaced_learning_app/core/theme/app_color_scheme.dart';
import 'package:spaced_learning_app/core/theme/app_typography.dart';

part 'app_theme_data.g.dart';

/// Main theme configuration class for the application
/// Provides light and dark theme configurations with Material 3 support
abstract final class AppTheme {
  const AppTheme._();

  /// Light theme configuration with Material 3 design system
  static ThemeData get light => FlexThemeData.light(
    colorScheme: lightColorScheme,
    subThemesData: const FlexSubThemesData(
      interactionEffects: true,
      tintedDisabledControls: true,
      useM2StyleDividerInM3: true,
      inputDecoratorIsFilled: true,
      inputDecoratorBorderType: FlexInputBorderType.outline,
      alignedDropdown: true,
      navigationRailUseIndicator: true,
      elevatedButtonSchemeColor: SchemeColor.primary,
      outlinedButtonOutlineSchemeColor: SchemeColor.outline,
      toggleButtonsSchemeColor: SchemeColor.primary,
      segmentedButtonSchemeColor: SchemeColor.primary,
      bottomSheetRadius: 28.0,
      bottomSheetElevation: 8.0,
      navigationBarSelectedLabelSchemeColor: SchemeColor.primary,
      navigationBarUnselectedLabelSchemeColor: SchemeColor.onSurface,
      navigationBarSelectedIconSchemeColor: SchemeColor.primary,
      navigationBarUnselectedIconSchemeColor: SchemeColor.onSurface,
      navigationBarIndicatorSchemeColor: SchemeColor.primaryContainer,
      navigationBarIndicatorOpacity: 1.0,
      navigationRailSelectedLabelSchemeColor: SchemeColor.primary,
      navigationRailUnselectedLabelSchemeColor: SchemeColor.onSurface,
      navigationRailSelectedIconSchemeColor: SchemeColor.primary,
      navigationRailUnselectedIconSchemeColor: SchemeColor.onSurface,
      navigationRailIndicatorSchemeColor: SchemeColor.primaryContainer,
      navigationRailIndicatorOpacity: 1.0,
    ),
    keyColors: const FlexKeyColors(
      useSecondary: true,
      useTertiary: true,
      useError: true,
    ),
    visualDensity: FlexColorScheme.comfortablePlatformDensity,
    cupertinoOverrideTheme: const CupertinoThemeData(applyThemeToAll: true),
    useMaterial3: true,
    textTheme: AppTypography.getTextTheme(Brightness.light),
    primaryTextTheme: AppTypography.getTextTheme(Brightness.light),
  );

  /// Dark theme configuration with Material 3 design system
  static ThemeData get dark => FlexThemeData.dark(
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
      elevatedButtonSchemeColor: SchemeColor.primary,
      outlinedButtonOutlineSchemeColor: SchemeColor.outline,
      toggleButtonsSchemeColor: SchemeColor.primary,
      segmentedButtonSchemeColor: SchemeColor.primary,
      bottomSheetRadius: 28.0,
      bottomSheetElevation: 8.0,
      navigationBarSelectedLabelSchemeColor: SchemeColor.primary,
      navigationBarUnselectedLabelSchemeColor: SchemeColor.onSurface,
      navigationBarSelectedIconSchemeColor: SchemeColor.primary,
      navigationBarUnselectedIconSchemeColor: SchemeColor.onSurface,
      navigationBarIndicatorSchemeColor: SchemeColor.primaryContainer,
      navigationBarIndicatorOpacity: 1.0,
      navigationRailSelectedLabelSchemeColor: SchemeColor.primary,
      navigationRailUnselectedLabelSchemeColor: SchemeColor.onSurface,
      navigationRailSelectedIconSchemeColor: SchemeColor.primary,
      navigationRailUnselectedIconSchemeColor: SchemeColor.onSurface,
      navigationRailIndicatorSchemeColor: SchemeColor.primaryContainer,
      navigationRailIndicatorOpacity: 1.0,
    ),
    keyColors: const FlexKeyColors(
      useSecondary: true,
      useTertiary: true,
      useError: true,
    ),
    visualDensity: FlexColorScheme.comfortablePlatformDensity,
    cupertinoOverrideTheme: const CupertinoThemeData(applyThemeToAll: true),
    useMaterial3: true,
    textTheme: AppTypography.getTextTheme(Brightness.dark),
    primaryTextTheme: AppTypography.getTextTheme(Brightness.dark),
  );

  /// Get theme data based on brightness
  static ThemeData getTheme(Brightness brightness) {
    return brightness == Brightness.dark ? dark : light;
  }

  /// Check if current theme is dark mode
  static bool isDarkMode(ThemeData theme) {
    return theme.brightness == Brightness.dark;
  }

  /// Get color scheme from theme
  static ColorScheme getColorScheme(ThemeData theme) {
    return theme.colorScheme;
  }
}

/// Provider for light theme
@riverpod
ThemeData lightTheme(Ref ref) => AppTheme.light;

/// Provider for dark theme
@riverpod
ThemeData darkTheme(Ref ref) => AppTheme.dark;

/// Provider for current theme mode state
@riverpod
class ThemeModeState extends _$ThemeModeState {
  @override
  ThemeMode build() {
    final isDarkMode = ref.watch(isDarkModeProvider).valueOrNull ?? false;
    return isDarkMode ? ThemeMode.dark : ThemeMode.light;
  }

  /// Toggle between light and dark theme
  Future<void> toggleTheme() async {
    try {
      final storageService = ref.read(storageServiceProvider);
      final current = state;

      if (current == ThemeMode.dark) {
        await storageService.saveDarkMode(false);
        state = ThemeMode.light;
      } else {
        await storageService.saveDarkMode(true);
        state = ThemeMode.dark;
      }

      // Invalidate isDarkModeProvider để cập nhật giá trị mới
      ref.invalidate(isDarkModeProvider);
    } catch (e) {
      // Log error but don't throw to prevent app crash
      debugPrint('Error toggling theme: $e');
    }
  }

  /// Set theme mode explicitly
  Future<void> setThemeMode(ThemeMode mode) async {
    try {
      final storageService = ref.read(storageServiceProvider);
      final isDark = mode == ThemeMode.dark;

      await storageService.saveDarkMode(isDark);
      state = mode;

      // Invalidate isDarkModeProvider để cập nhật giá trị mới
      ref.invalidate(isDarkModeProvider);
    } catch (e) {
      debugPrint('Error setting theme mode: $e');
    }
  }

  /// Get current theme data
  ThemeData get currentTheme {
    return state == ThemeMode.dark ? AppTheme.dark : AppTheme.light;
  }

  /// Check if current theme is dark
  bool get isDark => state == ThemeMode.dark;

  /// Check if current theme is light
  bool get isLight => state == ThemeMode.light;
}

/// Provider for dark mode state
@riverpod
class IsDarkMode extends _$IsDarkMode {
  @override
  Future<bool> build() async {
    try {
      final storageService = ref.watch(storageServiceProvider);
      return await storageService.isDarkMode();
    } catch (e) {
      debugPrint('Error getting dark mode state: $e');
      // Return false as fallback (light mode)
      return false;
    }
  }

  /// Refresh dark mode state
  Future<void> refresh() async {
    ref.invalidateSelf();
  }
}
