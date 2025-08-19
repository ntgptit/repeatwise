import 'package:flutter/material.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';

class CycleFormatter {
  static const Map<CycleStudied, String> _displayNames = {
    CycleStudied.firstTime: 'First Cycle',
    CycleStudied.firstReview: 'First Review Cycle',
    CycleStudied.secondReview: 'Second Review Cycle',
    CycleStudied.thirdReview: 'Third Review Cycle',
    CycleStudied.moreThanThreeReviews: 'Advanced Review Cycle',
  };

  static const Map<CycleStudied, String> _shortFormats = {
    CycleStudied.firstTime: 'First Time',
    CycleStudied.firstReview: 'First Review',
    CycleStudied.secondReview: 'Second Review',
    CycleStudied.thirdReview: 'Third Review',
    CycleStudied.moreThanThreeReviews: 'Advanced Review',
  };

  static const Map<CycleStudied, String> _descriptions = {
    CycleStudied.firstTime:
        'Initial learning phase. Complete all 5 repetitions to move to the next cycle.',
    CycleStudied.firstReview:
        'First review cycle. Reinforcing what you learned in the first cycle.',
    CycleStudied.secondReview:
        'Second review cycle. Consolidating knowledge with longer intervals.',
    CycleStudied.thirdReview:
        'Third review cycle. Material should be familiar at this stage.',
    CycleStudied.moreThanThreeReviews:
        'Advanced review cycle. Knowledge is well-established in long-term memory.',
  };

  static CycleStudied parseCycle(String? cycleString) {
    if (cycleString == null) return CycleStudied.firstTime;
    return CycleStudied.values.firstWhere(
      (e) => e.name.toUpperCase() == cycleString.toUpperCase(),
      orElse: () => CycleStudied.firstTime,
    );
  }

  static String format(CycleStudied cycle) => _shortFormats[cycle]!;

  static String getDescription(CycleStudied cycle) => _descriptions[cycle]!;

  static String getDisplayName(CycleStudied cycle) => _displayNames[cycle]!;

  static CycleStudied mapNumberToCycleStudied(int number) {
    switch (number) {
      case 1:
        return CycleStudied.firstTime;
      case 2:
        return CycleStudied.firstReview;
      case 3:
        return CycleStudied.secondReview;
      case 4:
        return CycleStudied.thirdReview;
      default:
        return CycleStudied.moreThanThreeReviews;
    }
  }

  static Color getColor(CycleStudied cycle, BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    switch (cycle) {
      case CycleStudied.firstTime:
        return colorScheme.primary;
      case CycleStudied.firstReview:
        return colorScheme.tertiary;
      case CycleStudied.secondReview:
        return colorScheme.secondary;
      case CycleStudied.thirdReview:
        return colorScheme.primary;
      case CycleStudied.moreThanThreeReviews:
        return colorScheme.tertiary;
    }
  }

  static IconData getIcon(CycleStudied cycle) {
    switch (cycle) {
      case CycleStudied.firstTime:
        return Icons.looks_one;
      case CycleStudied.firstReview:
        return Icons.looks_two;
      case CycleStudied.secondReview:
        return Icons.looks_3;
      case CycleStudied.thirdReview:
        return Icons.looks_4;
      case CycleStudied.moreThanThreeReviews:
        return Icons.looks_5;
    }
  }
}
