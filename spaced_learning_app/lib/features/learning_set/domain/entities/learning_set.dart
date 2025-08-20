import 'package:freezed_annotation/freezed_annotation.dart';

import '../../../../core/converters/datetime_utc_converter.dart';
import 'set_category.dart';
import 'set_status.dart';

part 'learning_set.freezed.dart';

@freezed
class LearningSet with _$LearningSet {
  const factory LearningSet({
    required String id,
    required String name,
    String? description,
    required SetCategory category,
    required int wordCount,
    required SetStatus status,
    required int currentCycle,
    @DateTimeUtcConverter() required DateTime createdAt,
    @DateTimeUtcConverter() required DateTime updatedAt,
    @DateTimeUtcConverter() DateTime? deletedAt,
  }) = _LearningSet;
}
