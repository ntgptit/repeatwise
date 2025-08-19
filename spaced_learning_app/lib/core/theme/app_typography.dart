import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

/// Typography configuration for the application
/// Provides consistent text styles following Material 3 design system
class AppTypography {
  const AppTypography._();

  /// Get text theme based on brightness
  static TextTheme getTextTheme(Brightness brightness) {
    final baseTextTheme = GoogleFonts.interTextTheme();

    final isLight = brightness == Brightness.light;
    final textColor = isLight ? Colors.black87 : Colors.white;
    final subtleTextColor = isLight
        ? Colors.black.withValues(alpha: AppDimens.opacityTextSubtle)
        : Colors.white.withValues(alpha: AppDimens.opacityTextSubtle);

    return baseTextTheme.copyWith(
      displayLarge: baseTextTheme.displayLarge?.copyWith(
        fontSize: AppDimens.fontDisplayL, // 57.0
        fontWeight: FontWeight.w400,
        letterSpacing: -0.25,
        color: textColor,
        height: 1.12, // ~64/57
      ),
      displayMedium: baseTextTheme.displayMedium?.copyWith(
        fontSize: AppDimens.fontDisplayM, // 45.0
        fontWeight: FontWeight.w400,
        color: textColor,
        height: 1.15, // ~52/45
      ),
      displaySmall: baseTextTheme.displaySmall?.copyWith(
        fontSize: AppDimens.fontDisplayS, // 36.0
        fontWeight: FontWeight.w400,
        color: textColor,
        height: 1.22, // ~44/36
      ),

      headlineLarge: baseTextTheme.headlineLarge?.copyWith(
        fontSize: AppDimens.fontHeadlineL, // 32.0
        fontWeight: FontWeight.w600, // Make headlines bolder
        color: textColor,
        height: 1.25, // ~40/32
      ),
      headlineMedium: baseTextTheme.headlineMedium?.copyWith(
        fontSize: AppDimens.fontHeadlineM, // 28.0
        fontWeight: FontWeight.w600,
        color: textColor,
        height: 1.28, // ~36/28
      ),
      headlineSmall: baseTextTheme.headlineSmall?.copyWith(
        fontSize: AppDimens.fontHeadlineS, // 24.0
        fontWeight: FontWeight.w600,
        color: textColor,
        height: 1.33, // ~32/24
      ),

      titleLarge: baseTextTheme.titleLarge?.copyWith(
        fontSize: AppDimens.fontTitle, // 22.0
        fontWeight: FontWeight.w600, // Keep titles bold
        color: textColor,
        letterSpacing: 0.0,
        height: 1.27, // ~28/22
      ),
      titleMedium: baseTextTheme.titleMedium?.copyWith(
        fontSize: AppDimens.fontXL, // 16.0
        fontWeight: FontWeight.w600,
        letterSpacing: 0.15,
        color: textColor,
        height: 1.5, // ~24/16
      ),
      titleSmall: baseTextTheme.titleSmall?.copyWith(
        fontSize: AppDimens.fontL, // 14.0
        fontWeight: FontWeight.w600,
        letterSpacing: 0.1,
        color: textColor,
        height: 1.43, // ~20/14
      ),

      bodyLarge: baseTextTheme.bodyLarge?.copyWith(
        fontSize: AppDimens.fontXL, // 16.0
        fontWeight: FontWeight.w400,
        letterSpacing: 0.15, // M3 uses 0.15 for bodyLarge
        color: textColor,
        height: 1.5, // ~24/16
      ),
      bodyMedium: baseTextTheme.bodyMedium?.copyWith(
        fontSize: AppDimens.fontL, // 14.0
        fontWeight: FontWeight.w400,
        letterSpacing: 0.25,
        color: textColor,
        height: 1.43, // ~20/14
      ),
      bodySmall: baseTextTheme.bodySmall?.copyWith(
        fontSize: AppDimens.fontM, // 12.0
        fontWeight: FontWeight.w400,
        letterSpacing: 0.4,
        color: subtleTextColor,
        height: 1.33, // ~16/12
      ),

      labelLarge: baseTextTheme.labelLarge?.copyWith(
        fontSize: AppDimens.fontL, // 14.0
        fontWeight: FontWeight.w500, // Medium weight for labels
        letterSpacing: 0.1,
        color: textColor,
        height: 1.43, // ~20/14
      ),
      labelMedium: baseTextTheme.labelMedium?.copyWith(
        fontSize: AppDimens.fontM, // 12.0
        fontWeight: FontWeight.w500,
        letterSpacing: 0.5,
        color: textColor,
        height: 1.33, // ~16/12
      ),
      labelSmall: baseTextTheme.labelSmall?.copyWith(
        fontSize: AppDimens.fontS, // 11.0
        fontWeight: FontWeight.w500,
        letterSpacing: 0.5,
        color: textColor,
        height: 1.45, // ~16/11
      ),
    );
  }

  /// Get display text style
  static TextStyle? getDisplayStyle(
    TextTheme textTheme, {
    bool isLarge = true,
  }) {
    return isLarge ? textTheme.displayLarge : textTheme.displayMedium;
  }

  /// Get headline text style
  static TextStyle? getHeadlineStyle(
    TextTheme textTheme, {
    bool isLarge = true,
  }) {
    return isLarge ? textTheme.headlineLarge : textTheme.headlineMedium;
  }

  /// Get title text style
  static TextStyle? getTitleStyle(TextTheme textTheme, {bool isLarge = true}) {
    return isLarge ? textTheme.titleLarge : textTheme.titleMedium;
  }

  /// Get body text style
  static TextStyle? getBodyStyle(TextTheme textTheme, {bool isLarge = true}) {
    return isLarge ? textTheme.bodyLarge : textTheme.bodyMedium;
  }

  /// Get label text style
  static TextStyle? getLabelStyle(TextTheme textTheme, {bool isLarge = true}) {
    return isLarge ? textTheme.labelLarge : textTheme.labelMedium;
  }
}
