import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/set_category.dart';

part 'learning_set_create_request_dto.freezed.dart';
part 'learning_set_create_request_dto.g.dart';

@freezed
class LearningSetCreateRequestDto with _$LearningSetCreateRequestDto {
  const factory LearningSetCreateRequestDto({
    required String name,
    String? description,
    required SetCategory category,
    required int wordCount,
  }) = _LearningSetCreateRequestDto;

  factory LearningSetCreateRequestDto.fromJson(Map<String, dynamic> json) =>
      _$LearningSetCreateRequestDtoFromJson(json);
}
