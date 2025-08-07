import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:json_annotation/json_annotation.dart';

part 'statistics.freezed.dart';
part 'statistics.g.dart';

@freezed
abstract class UserStatistics with _$UserStatistics {
  const factory UserStatistics({
    required String userId,
    required int totalSets,
    required int activeSets,
    required int completedSets,
    required int totalCycles,
    required int completedCycles,
    required int totalReminders,
    required int sentReminders,
    required DateTime lastActivityAt,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _UserStatistics;

  factory UserStatistics.fromJson(Map<String, dynamic> json) =>
      _$UserStatisticsFromJson(json);
}

@freezed
abstract class SetStatistics with _$SetStatistics {
  const factory SetStatistics({
    required String setId,
    required int totalCycles,
    required int completedCycles,
    required int totalReminders,
    required int sentReminders,
    required double averageProgress,
    required Duration averageCycleDuration,
    required DateTime lastActivityAt,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _SetStatistics;

  factory SetStatistics.fromJson(Map<String, dynamic> json) =>
      _$SetStatisticsFromJson(json);
}

@freezed
abstract class CycleStatistics with _$CycleStatistics {
  const factory CycleStatistics({
    required String cycleId,
    required String setId,
    required int cycleNo,
    required Duration duration,
    required double progress,
    required int totalItems,
    required int completedItems,
    required DateTime startedAt,
    required DateTime? completedAt,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _CycleStatistics;

  factory CycleStatistics.fromJson(Map<String, dynamic> json) =>
      _$CycleStatisticsFromJson(json);
}

// Extensions for computed properties
extension UserStatisticsExtension on UserStatistics {
  double get completionRate => totalSets > 0 ? completedSets / totalSets : 0.0;
  double get cycleCompletionRate => totalCycles > 0 ? completedCycles / totalCycles : 0.0;
  double get reminderSuccessRate => totalReminders > 0 ? sentReminders / totalReminders : 0.0;
  bool get isActive => DateTime.now().difference(lastActivityAt).inDays < 7;
  bool get hasCompletedSets => completedSets > 0;
  bool get hasActiveSets => activeSets > 0;
  String get completionRatePercentage => '${(completionRate * 100).toInt()}%';
  String get cycleCompletionRatePercentage => '${(cycleCompletionRate * 100).toInt()}%';
  String get reminderSuccessRatePercentage => '${(reminderSuccessRate * 100).toInt()}%';
}

extension SetStatisticsExtension on SetStatistics {
  double get cycleCompletionRate => totalCycles > 0 ? completedCycles / totalCycles : 0.0;
  double get reminderSuccessRate => totalReminders > 0 ? sentReminders / totalReminders : 0.0;
  bool get isActive => DateTime.now().difference(lastActivityAt).inDays < 7;
  bool get hasCompletedCycles => completedCycles > 0;
  bool get hasReminders => totalReminders > 0;
  String get cycleCompletionRatePercentage => '${(cycleCompletionRate * 100).toInt()}%';
  String get reminderSuccessRatePercentage => '${(reminderSuccessRate * 100).toInt()}%';
  String get averageProgressPercentage => '${(averageProgress * 100).toInt()}%';
}

extension CycleStatisticsExtension on CycleStatistics {
  bool get isCompleted => completedAt != null;
  bool get isInProgress => startedAt != null && completedAt == null;
  Duration? get actualDuration {
    if (startedAt == null) return null;
    final endTime = completedAt ?? DateTime.now();
    return endTime.difference(startedAt);
  }
  double get completionRate => totalItems > 0 ? completedItems / totalItems : 0.0;
  int get remainingItems => totalItems - completedItems;
  String get completionRatePercentage => '${(completionRate * 100).toInt()}%';
  String get progressPercentage => '${(progress * 100).toInt()}%';
}
