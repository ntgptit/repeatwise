import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';

class RepetitionUtils {
  static Map<String, List<Repetition>> groupByCycle(
    List<Repetition> repetitions,
  ) {
    final groupedByCycle = <String, List<Repetition>>{};
    final sortedRepetitions = List<Repetition>.from(repetitions)
      ..sort((a, b) {
        if (a.createdAt == null && b.createdAt == null) return 0;
        if (a.createdAt == null) return 1;
        if (b.createdAt == null) return -1;
        return a.createdAt!.compareTo(b.createdAt!);
      });

    int cycleIndex = 1;
    int currentGroupCount = 0;
    String currentKey = '';
    int repIndex = 0;

    while (repIndex < sortedRepetitions.length) {
      final rep = sortedRepetitions[repIndex];
      if (currentGroupCount < 5) {
        if (currentGroupCount == 0) {
          currentKey = 'Cycle $cycleIndex';
          groupedByCycle[currentKey] = [];
        }
        groupedByCycle[currentKey]!.add(rep);
        currentGroupCount++;
        repIndex++;
      } else {
        cycleIndex++;
        currentGroupCount = 0;
      }
    }

    for (final key in groupedByCycle.keys) {
      groupedByCycle[key]!.sort(
        (a, b) => a.repetitionOrder.index.compareTo(b.repetitionOrder.index),
      );
    }

    return groupedByCycle;
  }

  static bool isCurrentCycle(
    List<Repetition> repetitions,
    CycleStudied currentCycleStudied,
  ) {
    if (repetitions.isEmpty) return false;
    return repetitions.any((r) => r.status == RepetitionStatus.notStarted) &&
        currentCycleStudied != CycleStudied.firstTime;
  }

  static int compareReviewDates(Repetition a, Repetition b) {
    if (a.reviewDate == null && b.reviewDate == null) {
      return a.repetitionOrder.index.compareTo(b.repetitionOrder.index);
    }
    if (a.reviewDate == null) return 1;
    if (b.reviewDate == null) return -1;
    return b.reviewDate!.compareTo(a.reviewDate!);
  }
}
