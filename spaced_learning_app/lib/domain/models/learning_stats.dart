import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/domain/models/learning_insight.dart';

part 'learning_stats.freezed.dart';
part 'learning_stats.g.dart';

@freezed
abstract class LearningStatsDTO with _$LearningStatsDTO {
  const factory LearningStatsDTO({
    @Default(0) int totalModules,
    @Default({
      'FIRST_TIME': 0,
      'FIRST_REVIEW': 0,
      'SECOND_REVIEW': 0,
      'THIRD_REVIEW': 0,
      'MORE_THAN_THREE_REVIEWS': 0,
    })
    Map<String, int> cycleStats,

    @Default(0) int dueToday,
    @Default(0) int dueThisWeek,
    @Default(0) int dueThisMonth,

    @Default(0) int wordsDueToday,
    @Default(0) int wordsDueThisWeek,
    @Default(0) int wordsDueThisMonth,

    @Default(0) int completedToday,
    @Default(0) int completedThisWeek,
    @Default(0) int completedThisMonth,

    @Default(0) int wordsCompletedToday,
    @Default(0) int wordsCompletedThisWeek,
    @Default(0) int wordsCompletedThisMonth,

    @Default(0) int streakDays,
    @Default(0) int streakWeeks,
    @Default(0) int longestStreakDays,

    @Default(0) int totalWords,
    @Default(0) int totalCompletedModules,
    @Default(0) int totalInProgressModules,
    @Default(0) int learnedWords,
    @Default(0) int pendingWords,
    @Default(0.0) double vocabularyCompletionRate,
    @Default(0.0) double weeklyNewWordsRate,

    @Default([]) List<LearningInsightRespone> learningInsights,

    DateTime? lastUpdated,
  }) = _LearningStatsDTO;

  factory LearningStatsDTO.fromJson(Map<String, dynamic> json) =>
      _$LearningStatsDTOFromJson(json);
}
