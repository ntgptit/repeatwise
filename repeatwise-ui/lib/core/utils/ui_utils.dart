import 'package:flutter/material.dart';
import '../theme/app_colors.dart';
import '../theme/app_dimens.dart';

/// Utility class for common UI patterns
class UiUtils {
  /// Creates an error message container
  static Widget createErrorMessage(String message) {
    return Container(
      padding: AppDimens.paddingAll12,
      decoration: BoxDecoration(
        color: AppColors.errorContainer,
        borderRadius: AppDimens.borderRadius8,
        border: Border.all(color: AppColors.error),
      ),
      child: Text(
        message,
        style: const TextStyle(color: AppColors.error),
        textAlign: TextAlign.center,
      ),
    );
  }

  /// Creates a loading indicator
  static Widget createLoadingIndicator({double size = AppDimens.loadingSize20, double strokeWidth = AppDimens.progressStrokeWidth2}) {
    return SizedBox(
      height: size,
      width: size,
      child: CircularProgressIndicator(strokeWidth: strokeWidth),
    );
  }

  /// Creates a progress indicator with percentage
  static Widget createProgressIndicator({
    required double value,
    required String percentageText,
    Color? backgroundColor,
    Color? valueColor,
    TextStyle? textStyle,
  }) {
    return Row(
      children: [
        Expanded(
          child: LinearProgressIndicator(
            value: value,
            backgroundColor: backgroundColor ?? AppColors.gray300,
            valueColor: AlwaysStoppedAnimation<Color>(
              valueColor ?? AppColors.primary,
            ),
          ),
        ),
        const SizedBox(width: AppDimens.spacing8),
        Text(
          percentageText,
          style: textStyle ?? const TextStyle(
            fontSize: AppDimens.textSize12,
            color: AppColors.gray600,
          ),
        ),
      ],
    );
  }

  /// Creates a section header with action button
  static Widget createSectionHeader({
    required String title,
    String? actionText,
    VoidCallback? onActionPressed,
  }) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: AppDimens.textSize18,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (actionText != null)
          TextButton(
            onPressed: onActionPressed,
            child: Text(actionText),
          ),
      ],
    );
  }

  /// Creates a popup menu item
  static PopupMenuItem<String> createPopupMenuItem({
    required String value,
    required IconData icon,
    required String text,
    Color? iconColor,
    Color? textColor,
  }) {
    return PopupMenuItem(
      value: value,
      child: Row(
        children: [
          Icon(icon, color: iconColor),
          const SizedBox(width: AppDimens.spacing8),
          Text(text, style: TextStyle(color: textColor)),
        ],
      ),
    );
  }
}
