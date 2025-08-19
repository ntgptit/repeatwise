import 'package:freezed_annotation/freezed_annotation.dart';

part 'streak_stats.freezed.dart';

@freezed
abstract class StreakStats with _$StreakStats {
  const factory StreakStats({
    required int streakDays,
    required int streakWeeks,
  }) = _StreakStats;
}
