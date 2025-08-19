import 'package:freezed_annotation/freezed_annotation.dart';

part 'completion_stats.freezed.dart';

@freezed
abstract class CompletionStats with _$CompletionStats {
  const factory CompletionStats({
    required int completedToday,
    required int completedThisWeek,
    required int completedThisMonth,
    required int wordsCompletedToday,
    required int wordsCompletedThisWeek,
    required int wordsCompletedThisMonth,
  }) = _CompletionStats;
}
