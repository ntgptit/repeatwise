import 'package:flutter/material.dart';

/// Centralized colors for the RepeatWise app
class AppColors {
  // Private constructor to prevent instantiation
  AppColors._();

  // Primary colors
  static const Color primary = Color(0xFF2196F3);
  static const Color primaryLight = Color(0xFF64B5F6);
  static const Color primaryDark = Color(0xFF1976D2);
  static const Color primaryContainer = Color(0xFFE3F2FD);
  static const Color onPrimary = Color(0xFFFFFFFF);
  static const Color onPrimaryContainer = Color(0xFF0D47A1);

  // Secondary colors
  static const Color secondary = Color(0xFF03A9F4);
  static const Color secondaryLight = Color(0xFF81D4FA);
  static const Color secondaryDark = Color(0xFF0288D1);
  static const Color secondaryContainer = Color(0xFFE1F5FE);
  static const Color onSecondary = Color(0xFFFFFFFF);
  static const Color onSecondaryContainer = Color(0xFF01579B);

  // Tertiary colors
  static const Color tertiary = Color(0xFF00BCD4);
  static const Color tertiaryLight = Color(0xFF80DEEA);
  static const Color tertiaryDark = Color(0xFF0097A7);
  static const Color tertiaryContainer = Color(0xFFE0F7FA);
  static const Color onTertiary = Color(0xFFFFFFFF);
  static const Color onTertiaryContainer = Color(0xFF006064);

  // Accent colors
  static const Color accent = Color(0xFF00BCD4);
  static const Color accentLight = Color(0xFF80DEEA);
  static const Color accentDark = Color(0xFF0097A7);

  // Status colors
  static const Color success = Color(0xFF4CAF50);
  static const Color successLight = Color(0xFF81C784);
  static const Color successDark = Color(0xFF388E3C);
  static const Color successContainer = Color(0xFFE8F5E8);
  static const Color onSuccess = Color(0xFFFFFFFF);
  static const Color onSuccessContainer = Color(0xFF1B5E20);

  static const Color warning = Color(0xFFFF9800);
  static const Color warningLight = Color(0xFFFFB74D);
  static const Color warningDark = Color(0xFFF57C00);
  static const Color warningContainer = Color(0xFFFFF3E0);
  static const Color onWarning = Color(0xFFFFFFFF);
  static const Color onWarningContainer = Color(0xFFE65100);

  static const Color error = Color(0xFFF44336);
  static const Color errorLight = Color(0xFFE57373);
  static const Color errorDark = Color(0xFFD32F2F);
  static const Color errorContainer = Color(0xFFFFEBEE);
  static const Color onError = Color(0xFFFFFFFF);
  static const Color onErrorContainer = Color(0xFFB71C1C);

  static const Color info = Color(0xFF2196F3);
  static const Color infoLight = Color(0xFF64B5F6);
  static const Color infoDark = Color(0xFF1976D2);
  static const Color infoContainer = Color(0xFFE3F2FD);
  static const Color onInfo = Color(0xFFFFFFFF);
  static const Color onInfoContainer = Color(0xFF0D47A1);

  // Neutral colors
  static const Color surface = Color(0xFFFFFFFF);
  static const Color surfaceVariant = Color(0xFFF5F5F5);
  static const Color surfaceDark = Color(0xFF121212);
  static const Color surfaceVariantDark = Color(0xFF1E1E1E);

  static const Color background = Color(0xFFFFFFFF);
  static const Color backgroundDark = Color(0xFF000000);

  static const Color onSurface = Color(0xFF000000);
  static const Color onSurfaceVariant = Color(0xFF666666);
  static const Color onSurfaceDark = Color(0xFFFFFFFF);
  static const Color onSurfaceVariantDark = Color(0xFFB3B3B3);

  static const Color onBackground = Color(0xFF000000);
  static const Color onBackgroundDark = Color(0xFFFFFFFF);

  // Gray scale
  static const Color gray50 = Color(0xFFFAFAFA);
  static const Color gray100 = Color(0xFFF5F5F5);
  static const Color gray200 = Color(0xFFEEEEEE);
  static const Color gray300 = Color(0xFFE0E0E0);
  static const Color gray400 = Color(0xFFBDBDBD);
  static const Color gray500 = Color(0xFF9E9E9E);
  static const Color gray600 = Color(0xFF757575);
  static const Color gray700 = Color(0xFF616161);
  static const Color gray800 = Color(0xFF424242);
  static const Color gray900 = Color(0xFF212121);

  // Transparent colors
  static const Color transparent = Color(0x00000000);
  static const Color whiteTransparent10 = Color(0x1AFFFFFF);
  static const Color whiteTransparent20 = Color(0x33FFFFFF);
  static const Color whiteTransparent30 = Color(0x4DFFFFFF);
  static const Color whiteTransparent40 = Color(0x66FFFFFF);
  static const Color whiteTransparent50 = Color(0x80FFFFFF);
  static const Color whiteTransparent60 = Color(0x99FFFFFF);
  static const Color whiteTransparent70 = Color(0xB3FFFFFF);
  static const Color whiteTransparent80 = Color(0xCCFFFFFF);
  static const Color whiteTransparent90 = Color(0xE6FFFFFF);

  static const Color blackTransparent10 = Color(0x1A000000);
  static const Color blackTransparent20 = Color(0x33000000);
  static const Color blackTransparent30 = Color(0x4D000000);
  static const Color blackTransparent40 = Color(0x66000000);
  static const Color blackTransparent50 = Color(0x80000000);
  static const Color blackTransparent60 = Color(0x99000000);
  static const Color blackTransparent70 = Color(0xB3000000);
  static const Color blackTransparent80 = Color(0xCC000000);
  static const Color blackTransparent90 = Color(0xE6000000);

  // Gradient colors
  static const LinearGradient primaryGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [primary, secondary],
  );

  static const LinearGradient successGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [success, successLight],
  );

  static const LinearGradient warningGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [warning, warningLight],
  );

  static const LinearGradient errorGradient = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [error, errorLight],
  );

  // Status color helpers
  static Color getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'active':
      case 'completed':
      case 'success':
        return success;
      case 'inactive':
      case 'pending':
      case 'warning':
        return warning;
      case 'archived':
      case 'failed':
      case 'error':
        return error;
      default:
        return primary;
    }
  }

  static Color getStatusContainerColor(String status) {
    switch (status.toLowerCase()) {
      case 'active':
      case 'completed':
      case 'success':
        return successContainer;
      case 'inactive':
      case 'pending':
      case 'warning':
        return warningContainer;
      case 'archived':
      case 'failed':
      case 'error':
        return errorContainer;
      default:
        return primaryContainer;
    }
  }

  static Color getStatusOnColor(String status) {
    switch (status.toLowerCase()) {
      case 'active':
      case 'completed':
      case 'success':
        return onSuccess;
      case 'inactive':
      case 'pending':
      case 'warning':
        return onWarning;
      case 'archived':
      case 'failed':
      case 'error':
        return onError;
      default:
        return onPrimary;
    }
  }

  static Color getStatusOnContainerColor(String status) {
    switch (status.toLowerCase()) {
      case 'active':
      case 'completed':
      case 'success':
        return onSuccessContainer;
      case 'inactive':
      case 'pending':
      case 'warning':
        return onWarningContainer;
      case 'archived':
      case 'failed':
      case 'error':
        return onErrorContainer;
      default:
        return onPrimaryContainer;
    }
  }

  // Opacity helpers
  static Color withOpacity(Color color, double opacity) {
    return color.withOpacity(opacity);
  }

  static Color primaryWithOpacity(double opacity) => primary.withOpacity(opacity);
  static Color secondaryWithOpacity(double opacity) => secondary.withOpacity(opacity);
  static Color successWithOpacity(double opacity) => success.withOpacity(opacity);
  static Color warningWithOpacity(double opacity) => warning.withOpacity(opacity);
  static Color errorWithOpacity(double opacity) => error.withOpacity(opacity);
  static Color grayWithOpacity(double opacity) => gray500.withOpacity(opacity);
}
