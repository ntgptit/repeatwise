import 'package:flutter/material.dart';
import 'app_colors.dart';
import 'app_dimens.dart';

class AppTheme {
  // Legacy color constants for backward compatibility
  static const Color primaryColor = AppColors.primary;
  static const Color secondaryColor = AppColors.secondary;
  static const Color accentColor = AppColors.accent;
  static const Color errorColor = AppColors.error;
  static const Color successColor = AppColors.success;
  static const Color warningColor = AppColors.warning;

  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: AppColors.primary,
        brightness: Brightness.light,
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.primary,
        foregroundColor: AppColors.onPrimary,
        elevation: AppDimens.appBarElevation,
        centerTitle: true,
        titleTextStyle: TextStyle(
          fontSize: AppDimens.textSize20,
          fontWeight: FontWeight.w600,
          color: AppColors.onPrimary,
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: AppColors.onPrimary,
          padding: AppDimens.paddingHorizontal24 + AppDimens.paddingVertical12,
          shape: const RoundedRectangleBorder(
            borderRadius: AppDimens.borderRadius8,
          ),
          minimumSize: const Size(0, AppDimens.buttonHeight48),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: const BorderSide(color: AppColors.primary),
          padding: AppDimens.paddingHorizontal24 + AppDimens.paddingVertical12,
          shape: const RoundedRectangleBorder(
            borderRadius: AppDimens.borderRadius8,
          ),
          minimumSize: const Size(0, AppDimens.buttonHeight48),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          padding: AppDimens.paddingHorizontal16 + AppDimens.paddingVertical8,
          shape: const RoundedRectangleBorder(
            borderRadius: AppDimens.borderRadius8,
          ),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        border: const OutlineInputBorder(
          borderRadius: AppDimens.borderRadius8,
        ),
        focusedBorder: const OutlineInputBorder(
          borderRadius: AppDimens.borderRadius8,
          borderSide: BorderSide(color: AppColors.primary, width: AppDimens.borderWidth2),
        ),
        contentPadding: AppDimens.paddingHorizontal16 + AppDimens.paddingVertical12,
      ),
      cardTheme: const CardThemeData(
        elevation: AppDimens.cardElevation2,
        shape: RoundedRectangleBorder(
          borderRadius: AppDimens.borderRadius12,
        ),
        margin: AppDimens.marginAll8,
      ),
      dividerTheme: const DividerThemeData(
        thickness: AppDimens.dividerThickness,
        color: AppColors.gray300,
      ),
      floatingActionButtonTheme: const FloatingActionButtonThemeData(
        backgroundColor: AppColors.primary,
        foregroundColor: AppColors.onPrimary,
        elevation: AppDimens.cardElevation8,
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.surface,
        selectedItemColor: AppColors.primary,
        unselectedItemColor: AppColors.gray600,
        type: BottomNavigationBarType.fixed,
        elevation: AppDimens.bottomNavElevation,
      ),
    );
  }

  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: AppColors.primary,
        brightness: Brightness.dark,
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.surfaceDark,
        foregroundColor: AppColors.onSurfaceDark,
        elevation: AppDimens.appBarElevation,
        centerTitle: true,
        titleTextStyle: TextStyle(
          fontSize: AppDimens.textSize20,
          fontWeight: FontWeight.w600,
          color: AppColors.onSurfaceDark,
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: AppColors.onPrimary,
          padding: AppDimens.paddingHorizontal24 + AppDimens.paddingVertical12,
          shape: const RoundedRectangleBorder(
            borderRadius: AppDimens.borderRadius8,
          ),
          minimumSize: const Size(0, AppDimens.buttonHeight48),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: AppColors.primary,
          side: const BorderSide(color: AppColors.primary),
          padding: AppDimens.paddingHorizontal24 + AppDimens.paddingVertical12,
          shape: const RoundedRectangleBorder(
            borderRadius: AppDimens.borderRadius8,
          ),
          minimumSize: const Size(0, AppDimens.buttonHeight48),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.primary,
          padding: AppDimens.paddingHorizontal16 + AppDimens.paddingVertical8,
          shape: const RoundedRectangleBorder(
            borderRadius: AppDimens.borderRadius8,
          ),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        border: const OutlineInputBorder(
          borderRadius: AppDimens.borderRadius8,
        ),
        focusedBorder: const OutlineInputBorder(
          borderRadius: AppDimens.borderRadius8,
          borderSide: BorderSide(color: AppColors.primary, width: AppDimens.borderWidth2),
        ),
        contentPadding: AppDimens.paddingHorizontal16 + AppDimens.paddingVertical12,
      ),
      cardTheme: const CardThemeData(
        elevation: AppDimens.cardElevation2,
        shape: RoundedRectangleBorder(
          borderRadius: AppDimens.borderRadius12,
        ),
        margin: AppDimens.marginAll8,
        color: AppColors.surfaceVariantDark,
      ),
      dividerTheme: const DividerThemeData(
        thickness: AppDimens.dividerThickness,
        color: AppColors.gray700,
      ),
      floatingActionButtonTheme: const FloatingActionButtonThemeData(
        backgroundColor: AppColors.primary,
        foregroundColor: AppColors.onPrimary,
        elevation: AppDimens.cardElevation8,
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.surfaceDark,
        selectedItemColor: AppColors.primary,
        unselectedItemColor: AppColors.gray600,
        type: BottomNavigationBarType.fixed,
        elevation: AppDimens.bottomNavElevation,
      ),
    );
  }
}
