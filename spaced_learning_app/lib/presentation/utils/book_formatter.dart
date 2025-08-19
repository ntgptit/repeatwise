import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/domain/models/book.dart';

class BookFormatter {
  /// Format BookStatus thành chuỗi thân thiện với người dùng
  static String formatStatus(BookStatus status) {
    switch (status) {
      case BookStatus.published:
        return 'Published';
      case BookStatus.draft:
        return 'Draft';
      case BookStatus.archived:
        return 'Archived';
    }
  }

  /// Format DifficultyLevel thành chuỗi thân thiện với người dùng
  static String formatDifficulty(DifficultyLevel? difficulty) {
    if (difficulty == null) return 'Unknown';

    switch (difficulty) {
      case DifficultyLevel.beginner:
        return 'Beginner';
      case DifficultyLevel.intermediate:
        return 'Intermediate';
      case DifficultyLevel.advanced:
        return 'Advanced';
      case DifficultyLevel.expert:
        return 'Expert';
    }
  }

  /// Lấy màu tương ứng với BookStatus
  static Color getStatusColor(BookStatus status, ColorScheme colorScheme) {
    switch (status) {
      case BookStatus.published:
        return colorScheme.success; // Định nghĩa trong color_extensions.dart
      case BookStatus.draft:
        return colorScheme.secondary;
      case BookStatus.archived:
        return colorScheme.onSurfaceVariant;
    }
  }

  /// Lấy màu tương ứng với DifficultyLevel
  static Color getDifficultyColor(
    DifficultyLevel? difficulty,
    ColorScheme colorScheme,
  ) {
    if (difficulty == null) return colorScheme.onSurfaceVariant;

    switch (difficulty) {
      case DifficultyLevel.beginner:
        return colorScheme.success; // Định nghĩa trong color_extensions.dart
      case DifficultyLevel.intermediate:
        return colorScheme.tertiary;
      case DifficultyLevel.advanced:
        return colorScheme.secondary;
      case DifficultyLevel.expert:
        return colorScheme.error;
    }
  }
}
