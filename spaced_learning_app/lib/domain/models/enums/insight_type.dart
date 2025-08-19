import 'package:freezed_annotation/freezed_annotation.dart';

/// Enum for different types of learning insights
enum InsightType {
  @JsonValue('VOCABULARY_RATE')
  vocabularyRate,
  @JsonValue('STREAK')
  streak,
  @JsonValue('PENDING_WORDS')
  pendingWords,
  @JsonValue('DUE_TODAY')
  dueToday,
  @JsonValue('ACHIEVEMENT')
  achievement,
  @JsonValue('TIP')
  tip,
}
