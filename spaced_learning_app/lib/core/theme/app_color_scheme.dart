import 'package:flutter/material.dart';

/// Light color scheme for the application
/// Follows Material 3 design system with custom brand colors
const ColorScheme lightColorScheme = ColorScheme(
  brightness: Brightness.light,
  primary: Color(0xff65558f),
  surfaceTint: Color(0xff65558f),
  onPrimary: Color(0xffffffff),
  primaryContainer: Color(0xffe9ddff),
  onPrimaryContainer: Color(0xff4d3d75),
  secondary: Color(0xff625b70),
  onSecondary: Color(0xffffffff),
  secondaryContainer: Color(0xffe8def8),
  onSecondaryContainer: Color(0xff4a4458),
  tertiary: Color(0xff7e5260),
  onTertiary: Color(0xffffffff),
  tertiaryContainer: Color(0xffffd9e3),
  onTertiaryContainer: Color(0xff633b48),
  error: Color(0xffba1a1a),
  onError: Color(0xffffffff),
  errorContainer: Color(0xffffdad6),
  onErrorContainer: Color(0xff93000a),
  surface: Color(0xfffdf7ff),
  onSurface: Color(0xff1d1b20),
  onSurfaceVariant: Color(0xff49454e),
  outline: Color(0xff7a757f),
  outlineVariant: Color(0xffcac4cf),
  shadow: Color(0xff000000),
  scrim: Color(0xff000000),
  inverseSurface: Color(0xff322f35),
  inversePrimary: Color(0xffcfbdfe),
  primaryFixed: Color(0xffe9ddff),
  onPrimaryFixed: Color(0xff211047),
  primaryFixedDim: Color(0xffcfbdfe),
  onPrimaryFixedVariant: Color(0xff4d3d75),
  secondaryFixed: Color(0xffe8def8),
  onSecondaryFixed: Color(0xff1e192b),
  secondaryFixedDim: Color(0xffccc2db),
  onSecondaryFixedVariant: Color(0xff4a4458),
  tertiaryFixed: Color(0xffffd9e3),
  onTertiaryFixed: Color(0xff31101d),
  tertiaryFixedDim: Color(0xffefb8c8),
  onTertiaryFixedVariant: Color(0xff633b48),
  surfaceDim: Color(0xffded8e0),
  surfaceBright: Color(0xfffdf7ff),
  surfaceContainerLowest: Color(0xffffffff),
  surfaceContainerLow: Color(0xfff8f2fa),
  surfaceContainer: Color(0xfff2ecf4),
  surfaceContainerHigh: Color(0xffece6ee),
  surfaceContainerHighest: Color(0xffe6e0e9),
);

/// Dark color scheme for the application
/// Follows Material 3 design system with custom brand colors
const ColorScheme darkColorScheme = ColorScheme(
  brightness: Brightness.dark,
  primary: Color(0xffcfbdfe),
  surfaceTint: Color(0xffcfbdfe),
  onPrimary: Color(0xff36265d),
  primaryContainer: Color(0xff4d3d75),
  onPrimaryContainer: Color(0xffe9ddff),
  secondary: Color(0xffccc2db),
  onSecondary: Color(0xff332d41),
  secondaryContainer: Color(0xff4a4458),
  onSecondaryContainer: Color(0xffe8def8),
  tertiary: Color(0xffefb8c8),
  onTertiary: Color(0xff4a2532),
  tertiaryContainer: Color(0xff633b48),
  onTertiaryContainer: Color(0xffffd9e3),
  error: Color(0xffffb4ab),
  onError: Color(0xff690005),
  errorContainer: Color(0xff93000a),
  onErrorContainer: Color(0xffffdad6),
  surface: Color(0xff141218),
  onSurface: Color(0xffe6e0e9),
  onSurfaceVariant: Color(0xffcac4cf),
  outline: Color(0xff948f99),
  outlineVariant: Color(0xff49454e),
  shadow: Color(0xff000000),
  scrim: Color(0xff000000),
  inverseSurface: Color(0xffe6e0e9),
  inversePrimary: Color(0xff65558f),
  primaryFixed: Color(0xffe9ddff),
  onPrimaryFixed: Color(0xff211047),
  primaryFixedDim: Color(0xffcfbdfe),
  onPrimaryFixedVariant: Color(0xff4d3d75),
  secondaryFixed: Color(0xffe8def8),
  onSecondaryFixed: Color(0xff1e192b),
  secondaryFixedDim: Color(0xffccc2db),
  onSecondaryFixedVariant: Color(0xff4a4458),
  tertiaryFixed: Color(0xffffd9e3),
  onTertiaryFixed: Color(0xff31101d),
  tertiaryFixedDim: Color(0xffefb8c8),
  onTertiaryFixedVariant: Color(0xff633b48),
  surfaceDim: Color(0xff141218),
  surfaceBright: Color(0xff3b383e),
  surfaceContainerLowest: Color(0xff0f0d13),
  surfaceContainerLow: Color(0xff1d1b20),
  surfaceContainer: Color(0xff211f24),
  surfaceContainerHigh: Color(0xff2b292f),
  surfaceContainerHighest: Color(0xff36343a),
);

/// Additional colors for compatibility with original code
/// These colors provide semantic meaning and are used throughout the app
class AppColors {
  const AppColors._();

  /// Primary gradient colors
  static const List<Color> primaryGradient = [
    Color(0xff65558f), // Light primary
    Color(0xffcfbdfe), // Dark primary
  ];

  /// Grey gradient colors
  static const List<Color> greyGradient = [
    Color(0xfff8f2fa), // Light surfaceContainerLow
    Color(0xffe6e0e9), // Light surfaceContainerHighest
  ];

  /// Primary colors with alpha transparency
  static const Color primaryAlpha10 = Color(
    0x1A65558f,
  ); // 10% alpha of light primary
  static const Color primaryAlpha20 = Color(
    0x3365558f,
  ); // 20% alpha of light primary
  static const Color greyAlpha10 = Color(
    0x1A49454e,
  ); // 10% alpha of light onSurfaceVariant

  /// Semantic colors for different states
  static const Color successGreen = Color(0xff2e7d32); // Material Design green
  static const Color warningOrange = Color(
    0xfff57c00,
  ); // Material Design orange
  static const Color infoBlue = Color(0xff1976d2); // Material Design blue

  /// Text colors
  static const Color textPlaceholder = Color(
    0xff7a757f,
  ); // Light outline as placeholder
  static const Color darkTextSecondary = Color(
    0xffcac4cf,
  ); // Dark onSurfaceVariant

  /// Border colors
  static const Color borderLight = Color(0xffcac4cf); // Light outlineVariant
  static const Color borderMedium = Color(0xff7a757f); // Light outline
  static const Color darkBorder = Color(0xff948f99); // Dark outline

  /// Divider and separator colors
  static const Color divider = Color(0xffcac4cf); // Light outlineVariant

  /// Interactive state colors
  static const Color hoverGrey = Color(0xfff2ecf4); // Light surfaceContainer
  static const Color activeGrey = Color(
    0xffece6ee,
  ); // Light surfaceContainerHigh

  /// Disabled state colors
  static const Color disabledBackground = Color(
    0xffe6e0e9,
  ); // Light surfaceContainerHighest
  static const Color disabledContent = Color(0xff7a757f); // Light outline

  /// Get color scheme based on brightness
  static ColorScheme getColorScheme(Brightness brightness) {
    return brightness == Brightness.dark ? darkColorScheme : lightColorScheme;
  }

  /// Get primary color based on brightness
  static Color getPrimaryColor(Brightness brightness) {
    return brightness == Brightness.dark
        ? darkColorScheme.primary
        : lightColorScheme.primary;
  }

  /// Get surface color based on brightness
  static Color getSurfaceColor(Brightness brightness) {
    return brightness == Brightness.dark
        ? darkColorScheme.surface
        : lightColorScheme.surface;
  }

  /// Get text color based on brightness
  static Color getTextColor(Brightness brightness) {
    return brightness == Brightness.dark
        ? darkColorScheme.onSurface
        : lightColorScheme.onSurface;
  }
}
