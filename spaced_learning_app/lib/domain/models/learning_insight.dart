import 'package:freezed_annotation/freezed_annotation.dart';
import 'enums/insight_type.dart';

part 'learning_insight.freezed.dart';
part 'learning_insight.g.dart';

@freezed
abstract class LearningInsightResponse with _$LearningInsightResponse {
  const factory LearningInsightResponse({
    required InsightType type,
    required String message,
    required String icon,
    required String color,
    @Default(0.0) double dataPoint,
    @Default(0) int priority,
  }) = _LearningInsightResponse;

  factory LearningInsightResponse.fromJson(Map<String, dynamic> json) =>
      _$LearningInsightResponseFromJson(json);
}
