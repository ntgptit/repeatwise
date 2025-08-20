import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/core/converters/datetime_utc_converter.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/set_category.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/set_status.dart';

part 'learning_set_dto.freezed.dart';
part 'learning_set_dto.g.dart';

@freezed
class LearningSetDto with _$LearningSetDto {
  const factory LearningSetDto({
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
  }) = _LearningSetDto;

  factory LearningSetDto.fromJson(Map<String, dynamic> json) =>
      _$LearningSetDtoFromJson(json);
}
