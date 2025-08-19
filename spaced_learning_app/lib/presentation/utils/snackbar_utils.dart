// lib/presentation/utils/snackbar_utils.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

enum SnackBarType { success, error, warning, info, standard }

class SnackBarUtils {
  static void show(
    BuildContext context,
    String message, {
    SnackBarType type = SnackBarType.standard,
    Color? backgroundColor,
    Color? textColor,
    Duration duration = const Duration(seconds: 4),
    SnackBarAction? action,
    SnackBarBehavior behavior = SnackBarBehavior.floating,
    ShapeBorder? shape,
    EdgeInsetsGeometry? margin,
    EdgeInsetsGeometry? padding,
    double? elevation,
    IconData? icon,
  }) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;

    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final snackBarTheme = theme.snackBarTheme;

    Color effectiveBgColor;
    Color effectiveTextColor;
    IconData effectiveIcon;

    switch (type) {
      case SnackBarType.success:
        effectiveBgColor = backgroundColor ?? colorScheme.primaryContainer;
        effectiveTextColor = textColor ?? colorScheme.onPrimaryContainer;
        effectiveIcon = icon ?? Icons.check_circle_outline;
        break;
      case SnackBarType.error:
        effectiveBgColor = backgroundColor ?? colorScheme.errorContainer;
        effectiveTextColor = textColor ?? colorScheme.onErrorContainer;
        effectiveIcon = icon ?? Icons.error_outline;
        break;
      case SnackBarType.warning:
        effectiveBgColor = backgroundColor ?? Colors.orange.shade100;
        effectiveTextColor = textColor ?? Colors.orange.shade900;
        effectiveIcon = icon ?? Icons.warning_amber_outlined;
        break;
      case SnackBarType.info:
        effectiveBgColor = backgroundColor ?? colorScheme.secondaryContainer;
        effectiveTextColor = textColor ?? colorScheme.onSecondaryContainer;
        effectiveIcon = icon ?? Icons.info_outline;
        break;
      case SnackBarType.standard:
        effectiveBgColor =
            backgroundColor ??
            snackBarTheme.backgroundColor ??
            colorScheme.surfaceContainerHighest;
        effectiveTextColor =
            textColor ??
            snackBarTheme.contentTextStyle?.color ??
            colorScheme.onSurface;
        effectiveIcon = icon ?? Icons.info_outline;
        break;
    }

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            Icon(
              effectiveIcon,
              color: effectiveTextColor,
              size: AppDimens.iconM,
            ),
            const SizedBox(width: AppDimens.spaceM),
            Expanded(
              child: Text(
                message,
                style:
                    snackBarTheme.contentTextStyle?.copyWith(
                      color: effectiveTextColor,
                    ) ??
                    TextStyle(color: effectiveTextColor),
              ),
            ),
          ],
        ),
        backgroundColor: effectiveBgColor,
        duration: duration,
        action: action,
        behavior: behavior,
        shape:
            shape ??
            snackBarTheme.shape ??
            RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(AppDimens.radiusS),
            ),
        margin:
            margin ??
            (behavior == SnackBarBehavior.floating
                ? const EdgeInsets.all(AppDimens.paddingS)
                : null),
        padding: padding,
        elevation: elevation ?? snackBarTheme.elevation,
      ),
    );
  }

  static void success(
    BuildContext context,
    String message, {
    Duration? duration,
    SnackBarAction? action,
  }) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;
    show(
      context,
      message,
      type: SnackBarType.success,
      duration: duration ?? const Duration(seconds: 3),
      action: action,
    );
  }

  static void error(
    BuildContext context,
    String message, {
    Duration? duration,
    SnackBarAction? action,
  }) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;
    show(
      context,
      message,
      type: SnackBarType.error,
      duration: duration ?? const Duration(seconds: 5),
      action: action,
    );
  }

  static void warning(
    BuildContext context,
    String message, {
    Duration? duration,
    SnackBarAction? action,
  }) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;
    show(
      context,
      message,
      type: SnackBarType.warning,
      duration: duration ?? const Duration(seconds: 4),
      action: action,
    );
  }

  static void info(
    BuildContext context,
    String message, {
    Duration? duration,
    SnackBarAction? action,
  }) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;
    show(
      context,
      message,
      type: SnackBarType.info,
      duration: duration ?? const Duration(seconds: 3),
      action: action,
    );
  }

  static void hideCurrent(BuildContext context) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;
    ScaffoldMessenger.of(context).hideCurrentSnackBar();
  }

  static void clearAll(BuildContext context) {
    // Guard against using BuildContext across async gaps
    if (!context.mounted) return;
    ScaffoldMessenger.of(context).clearSnackBars();
  }
}
