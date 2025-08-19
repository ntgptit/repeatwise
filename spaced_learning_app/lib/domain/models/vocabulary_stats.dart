import 'package:freezed_annotation/freezed_annotation.dart';

part 'vocabulary_stats.freezed.dart';

@freezed
abstract class VocabularyStats with _$VocabularyStats {
  const factory VocabularyStats({
    required int totalWords,
    required int learnedWords,
    required int pendingWords,
    required double vocabularyCompletionRate,
    required double weeklyNewWordsRate,
  }) = _VocabularyStats;
}
