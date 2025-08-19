import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';

class SLErrorView extends StatelessWidget {
  final String message;
  final VoidCallback? onRetry;
  final IconData? icon;
  final bool compact;
  final ThemeData? theme;

  const SLErrorView({
    super.key,
    required this.message,
    this.onRetry,
    this.icon,
    this.compact = false,
    this.theme,
  });

  @override
  Widget build(BuildContext context) {
    final currentTheme = theme ?? Theme.of(context);
    return compact
        ? _buildCompactView(currentTheme)
        : _buildFullView(currentTheme);
  }

  Widget _buildCompactView(ThemeData theme) {
    final colorScheme = theme.colorScheme;

    return Card(
      color: colorScheme.errorContainer,
      margin: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingL,
        vertical: AppDimens.paddingS,
      ),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingM),
        child: Row(
          children: [
            _buildErrorIcon(theme, size: AppDimens.iconM),
            const SizedBox(width: AppDimens.spaceM),
            Expanded(
              child: Text(
                message,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onErrorContainer,
                ),
              ),
            ),
            if (onRetry != null)
              Padding(
                padding: const EdgeInsets.only(left: AppDimens.paddingM),
                child: SLButton(
                  text: 'Retry',
                  type: SLButtonType.text,
                  onPressed: onRetry,
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildFullView(ThemeData theme) {
    final colorScheme = theme.colorScheme;

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingXL),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _buildErrorIcon(theme, size: AppDimens.iconXXL),
            const SizedBox(height: AppDimens.spaceL),
            Text(
              message,
              textAlign: TextAlign.center,
              style: theme.textTheme.titleMedium?.copyWith(
                color: colorScheme.onSurface.withValues(
                  alpha: AppDimens.opacityVeryHigh,
                ),
              ),
            ),
            if (onRetry != null)
              Padding(
                padding: const EdgeInsets.only(top: AppDimens.paddingXL),
                child: SLButton(
                  text: 'Try Again',
                  type: SLButtonType.outline,
                  prefixIcon: Icons.refresh,
                  onPressed: onRetry,
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildErrorIcon(ThemeData theme, {required double size}) {
    final colorScheme = theme.colorScheme;

    return Icon(
      icon ?? Icons.error_outline,
      color: colorScheme.error,
      size: size,
    );
  }
}

abstract class _SnackbarHelper {
  static void _showSnackBar({
    required BuildContext context,
    required String message,
    required IconData icon,
    required Color Function(ThemeData) backgroundColorBuilder,
    required Color Function(ThemeData) contentColorBuilder,
    Duration? duration,
    SnackBarAction? action,
    Color? actionColor,
  }) {
    final theme = Theme.of(context);
    final snackBarTheme = theme.snackBarTheme;

    final effectiveBackgroundColor = backgroundColorBuilder(theme);
    final effectiveContentColor = contentColorBuilder(theme);
    final effectiveTextStyle =
        snackBarTheme.contentTextStyle ??
        theme.textTheme.bodyMedium?.copyWith(color: effectiveContentColor);

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            Icon(icon, color: effectiveContentColor, size: AppDimens.iconM),
            const SizedBox(width: AppDimens.spaceM),
            Expanded(child: Text(message, style: effectiveTextStyle)),
          ],
        ),
        backgroundColor: effectiveBackgroundColor,
        duration: duration ?? const Duration(seconds: 4),
        behavior: snackBarTheme.behavior ?? SnackBarBehavior.floating,
        shape: snackBarTheme.shape,
        action: action != null
            ? SnackBarAction(
                label: action.label,
                onPressed: action.onPressed,
                textColor:
                    actionColor ??
                    snackBarTheme.actionTextColor ??
                    theme.colorScheme.primary,
              )
            : null,
      ),
    );
  }
}

class ErrorSnackbar {
  static void show(BuildContext context, String message, {Duration? duration}) {
    _SnackbarHelper._showSnackBar(
      context: context,
      message: message,
      icon: Icons.error_outline,
      backgroundColorBuilder: (theme) => theme.colorScheme.errorContainer,
      contentColorBuilder: (theme) => theme.colorScheme.onErrorContainer,
      duration: duration,
      action: SnackBarAction(
        label: 'Dismiss',
        onPressed: () => ScaffoldMessenger.of(context).hideCurrentSnackBar(),
      ),
    );
  }
}

class SuccessSnackbar {
  static Color _getSuccessBackgroundColor(ThemeData theme) {
    return theme.brightness == Brightness.light
        ? const Color(0xFFE8F5E9)
        : const Color(0xFF2E7D32);
  }

  static Color _getSuccessContentColor(ThemeData theme) {
    return theme.brightness == Brightness.light
        ? const Color(0xFF1B5E20)
        : const Color(0xFFC8E6C9);
  }

  static void show(BuildContext context, String message, {Duration? duration}) {
    _SnackbarHelper._showSnackBar(
      context: context,
      message: message,
      icon: Icons.check_circle,
      backgroundColorBuilder: _getSuccessBackgroundColor,
      contentColorBuilder: _getSuccessContentColor,
      duration: duration,
    );
  }
}
