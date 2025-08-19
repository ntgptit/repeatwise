import 'package:freezed_annotation/freezed_annotation.dart';

part 'due_stats.freezed.dart';

@freezed
abstract class DueStats with _$DueStats {
  const factory DueStats({
    required int dueToday,
    required int dueThisWeek,
    required int dueThisMonth,
    required int wordsDueToday,
    required int wordsDueThisWeek,
    required int wordsDueThisMonth,
  }) = _DueStats;
}
