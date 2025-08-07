import 'package:flutter/material.dart';
import '../theme/app_colors.dart';
import '../theme/app_dimens.dart';

/// Utility class for status color management
class StatusUtils {
  /// Gets color for set status
  static Color getSetStatusColor(String status) {
    return AppColors.getStatusColor(status);
  }

  /// Gets color for cycle status
  static Color getCycleStatusColor(String status) {
    return AppColors.getStatusColor(status);
  }

  /// Gets color for remind status
  static Color getRemindStatusColor(String status) {
    return AppColors.getStatusColor(status);
  }

  /// Creates a status badge widget
  static Widget createStatusBadge(String status, Color color) {
    return Container(
      padding: AppDimens.paddingHorizontal8 + AppDimens.paddingVertical4,
      decoration: BoxDecoration(
        color: AppColors.withOpacity(color, 0.1),
        borderRadius: AppDimens.borderRadius12,
      ),
      child: Text(
        status,
        style: TextStyle(
          fontSize: AppDimens.textSize10,
          color: color,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  /// Creates a status circle avatar
  static Widget createStatusAvatar(String status, Color color, {Widget? child}) {
    return CircleAvatar(
      backgroundColor: AppColors.withOpacity(color, 0.1),
      child: child ?? Icon(
        Icons.circle,
        color: color,
        size: AppDimens.iconSize16,
      ),
    );
  }
}
