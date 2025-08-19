import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/core/theme/app_color_scheme.dart';

/// Enum for cycle study states
enum CycleStudied {
  firstTime,
  firstReview,
  secondReview,
  thirdReview,
  moreThanThreeReviews,
}

/// Extension providing semantic color utilities for theming
extension SemanticColorExtension on ThemeData {
  // Colors based on score value
  Color getScoreColor(double score) {
    final colorScheme = this.colorScheme;

    // Increased contrast by using stronger colors
    if (score >= 90) return Colors.green.shade700;
    if (score >= 75) return colorScheme.primary;
    if (score >= 60) return colorScheme.secondary;
    if (score >= 40) return Colors.orange.shade700;
    return colorScheme.error;
  }

  // Colors for repetition order
  Color getRepetitionColor(int orderIndex, {bool isHistory = false}) {
    final List<Color> repetitionColors = [
      const Color(0xFF4CAF50).withValues(alpha: 0.9), // Lighter green
      const Color(0xFF2196F3).withValues(alpha: 0.9), // Lighter blue
      const Color(0xFFF57C00).withValues(alpha: 0.85), // Lighter orange
      const Color(0xFF9C27B0).withValues(alpha: 0.85), // Lighter purple
      const Color(0xFFE53935).withValues(alpha: 0.8), // Lighter red
    ];

    final baseColor =
        repetitionColors[(orderIndex - 1) % repetitionColors.length];

    // Use different opacity based on state
    return isHistory ? baseColor.withValues(alpha: 0.7) : baseColor;
  }

  // Colors based on completion percentage
  Color getProgressColor(double percent) {
    final colorScheme = this.colorScheme;

    if (percent >= 90) return colorScheme.tertiary;
    if (percent >= 60) return colorScheme.primary;
    if (percent >= 30) return colorScheme.secondary;
    return colorScheme.error;
  }

  /// Colors for CycleStudied
  Color getCycleColor(CycleStudied cycle) {
    final colorScheme = this.colorScheme;

    switch (cycle) {
      case CycleStudied.firstTime:
        return colorScheme.primary;
      case CycleStudied.firstReview:
        return colorScheme.secondary;
      case CycleStudied.secondReview:
        return colorScheme.tertiary;
      case CycleStudied.thirdReview:
        return Colors.orange;
      case CycleStudied.moreThanThreeReviews:
        return Colors.purple;
    }
  }
}

/// Extension for semantic colors on ColorScheme
extension SemanticColorSchemeExtension on ColorScheme {
  /// Get semantic color for a stat type
  Color getStatColor(String statType) {
    switch (statType) {
      case 'success':
        return AppColors.successGreen;
      case 'warning':
        return AppColors.warningOrange;
      case 'error':
        return error;
      case 'info':
        return AppColors.infoBlue;
      case 'primary':
        return primary;
      case 'secondary':
        return secondary;
      case 'tertiary':
        return tertiary;
      default:
        return onSurfaceVariant.withValues(alpha: 0.6);
    }
  }

  /// Get background color for a stat with appropriate opacity
  Color getStatBackgroundColor(String statType, {double opacity = 0.1}) {
    return getStatColor(statType).withValues(alpha: opacity);
  }
}
