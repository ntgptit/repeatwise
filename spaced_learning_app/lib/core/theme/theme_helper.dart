import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'app_theme_data.dart';
import 'app_color_scheme.dart';
import 'app_typography.dart';
import 'app_dimens.dart';

/// Helper class for theme-related utilities and operations
class ThemeHelper {
  const ThemeHelper._();

  /// Get current theme data from context
  static ThemeData getTheme(BuildContext context) {
    return Theme.of(context);
  }

  /// Get current color scheme from context
  static ColorScheme getColorScheme(BuildContext context) {
    return Theme.of(context).colorScheme;
  }

  /// Get current text theme from context
  static TextTheme getTextTheme(BuildContext context) {
    return Theme.of(context).textTheme;
  }

  /// Check if current theme is dark mode
  static bool isDarkMode(BuildContext context) {
    return Theme.of(context).brightness == Brightness.dark;
  }

  /// Check if current theme is light mode
  static bool isLightMode(BuildContext context) {
    return Theme.of(context).brightness == Brightness.light;
  }

  /// Get responsive padding based on screen size
  static EdgeInsets getResponsivePadding(
    BuildContext context, {
    double horizontal = AppDimens.paddingL,
    double vertical = AppDimens.paddingM,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    final responsiveHorizontal = AppDimens.getResponsivePadding(
      horizontal,
      screenWidth,
    );
    final responsiveVertical = AppDimens.getResponsivePadding(
      vertical,
      screenWidth,
    );

    return EdgeInsets.symmetric(
      horizontal: responsiveHorizontal,
      vertical: responsiveVertical,
    );
  }

  /// Get responsive margin based on screen size
  static EdgeInsets getResponsiveMargin(
    BuildContext context, {
    double horizontal = AppDimens.paddingL,
    double vertical = AppDimens.paddingM,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    final responsiveHorizontal = AppDimens.getResponsivePadding(
      horizontal,
      screenWidth,
    );
    final responsiveVertical = AppDimens.getResponsivePadding(
      vertical,
      screenWidth,
    );

    return EdgeInsets.symmetric(
      horizontal: responsiveHorizontal,
      vertical: responsiveVertical,
    );
  }

  /// Get responsive border radius based on screen size
  static BorderRadius getResponsiveBorderRadius(
    BuildContext context, {
    double radius = AppDimens.radiusM,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    final responsiveRadius = AppDimens.getResponsiveRadius(radius, screenWidth);

    return BorderRadius.circular(responsiveRadius);
  }

  /// Get responsive font size based on screen size
  static double getResponsiveFontSize(
    BuildContext context, {
    double fontSize = AppDimens.fontL,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    return AppDimens.getResponsiveFontSize(fontSize, screenWidth);
  }

  /// Get theme-aware color based on brightness
  static Color getThemeAwareColor(
    BuildContext context, {
    required Color lightColor,
    required Color darkColor,
  }) {
    return isDarkMode(context) ? darkColor : lightColor;
  }

  /// Get theme-aware text color
  static Color getTextColor(BuildContext context) {
    return getColorScheme(context).onSurface;
  }

  /// Get theme-aware background color
  static Color getBackgroundColor(BuildContext context) {
    return getColorScheme(context).surface;
  }

  /// Get theme-aware primary color
  static Color getPrimaryColor(BuildContext context) {
    return getColorScheme(context).primary;
  }

  /// Get theme-aware secondary color
  static Color getSecondaryColor(BuildContext context) {
    return getColorScheme(context).secondary;
  }

  /// Get theme-aware error color
  static Color getErrorColor(BuildContext context) {
    return getColorScheme(context).error;
  }

  /// Get theme-aware surface color
  static Color getSurfaceColor(BuildContext context) {
    return getColorScheme(context).surface;
  }

  /// Get theme-aware outline color
  static Color getOutlineColor(BuildContext context) {
    return getColorScheme(context).outline;
  }

  /// Get theme-aware shadow color
  static Color getShadowColor(BuildContext context) {
    return getColorScheme(context).shadow;
  }

  /// Get theme-aware scrim color
  static Color getScrimColor(BuildContext context) {
    return getColorScheme(context).scrim;
  }

  /// Get theme-aware inverse surface color
  static Color getInverseSurfaceColor(BuildContext context) {
    return getColorScheme(context).inverseSurface;
  }

  /// Get theme-aware inverse primary color
  static Color getInversePrimaryColor(BuildContext context) {
    return getColorScheme(context).inversePrimary;
  }

  /// Get theme-aware surface tint color
  static Color getSurfaceTintColor(BuildContext context) {
    return getColorScheme(context).surfaceTint;
  }

  /// Get theme-aware surface container lowest color
  static Color getSurfaceContainerLowestColor(BuildContext context) {
    return getColorScheme(context).surfaceContainerLowest;
  }

  /// Get theme-aware surface container low color
  static Color getSurfaceContainerLowColor(BuildContext context) {
    return getColorScheme(context).surfaceContainerLow;
  }

  /// Get theme-aware surface container color
  static Color getSurfaceContainerColor(BuildContext context) {
    return getColorScheme(context).surfaceContainer;
  }

  /// Get theme-aware surface container high color
  static Color getSurfaceContainerHighColor(BuildContext context) {
    return getColorScheme(context).surfaceContainerHigh;
  }

  /// Get theme-aware surface container highest color
  static Color getSurfaceContainerHighestColor(BuildContext context) {
    return getColorScheme(context).surfaceContainerHighest;
  }

  /// Get theme-aware surface dim color
  static Color getSurfaceDimColor(BuildContext context) {
    return getColorScheme(context).surfaceDim;
  }

  /// Get theme-aware surface bright color
  static Color getSurfaceBrightColor(BuildContext context) {
    return getColorScheme(context).surfaceBright;
  }

  /// Get theme-aware on surface variant color
  static Color getOnSurfaceVariantColor(BuildContext context) {
    return getColorScheme(context).onSurfaceVariant;
  }

  /// Get theme-aware outline variant color
  static Color getOutlineVariantColor(BuildContext context) {
    return getColorScheme(context).outlineVariant;
  }

  /// Get theme-aware primary container color
  static Color getPrimaryContainerColor(BuildContext context) {
    return getColorScheme(context).primaryContainer;
  }

  /// Get theme-aware on primary container color
  static Color getOnPrimaryContainerColor(BuildContext context) {
    return getColorScheme(context).onPrimaryContainer;
  }

  /// Get theme-aware secondary container color
  static Color getSecondaryContainerColor(BuildContext context) {
    return getColorScheme(context).secondaryContainer;
  }

  /// Get theme-aware on secondary container color
  static Color getOnSecondaryContainerColor(BuildContext context) {
    return getColorScheme(context).onSecondaryContainer;
  }

  /// Get theme-aware tertiary container color
  static Color getTertiaryContainerColor(BuildContext context) {
    return getColorScheme(context).tertiaryContainer;
  }

  /// Get theme-aware on tertiary container color
  static Color getOnTertiaryContainerColor(BuildContext context) {
    return getColorScheme(context).onTertiaryContainer;
  }

  /// Get theme-aware error container color
  static Color getErrorContainerColor(BuildContext context) {
    return getColorScheme(context).errorContainer;
  }

  /// Get theme-aware on error container color
  static Color getOnErrorContainerColor(BuildContext context) {
    return getColorScheme(context).onErrorContainer;
  }

  /// Get theme-aware on primary color
  static Color getOnPrimaryColor(BuildContext context) {
    return getColorScheme(context).onPrimary;
  }

  /// Get theme-aware on secondary color
  static Color getOnSecondaryColor(BuildContext context) {
    return getColorScheme(context).onSecondary;
  }

  /// Get theme-aware on tertiary color
  static Color getOnTertiaryColor(BuildContext context) {
    return getColorScheme(context).onTertiary;
  }

  /// Get theme-aware on error color
  static Color getOnErrorColor(BuildContext context) {
    return getColorScheme(context).onError;
  }

  /// Get theme-aware on surface color
  static Color getOnSurfaceColor(BuildContext context) {
    return getColorScheme(context).onSurface;
  }

  /// Get theme-aware on inverse surface color
  static Color getOnInverseSurfaceColor(BuildContext context) {
    return getColorScheme(context).onInverseSurface;
  }
}
