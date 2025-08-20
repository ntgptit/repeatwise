import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/set_category.dart';

part 'learning_set_update_request_dto.freezed.dart';
part 'learning_set_update_request_dto.g.dart';

@freezed
class LearningSetUpdateRequestDto with _$LearningSetUpdateRequestDto {
  const factory LearningSetUpdateRequestDto({
    String? name,
    String? description,
    SetCategory? category,
    int? wordCount,
  }) = _LearningSetUpdateRequestDto;

  factory LearningSetUpdateRequestDto.fromJson(Map<String, dynamic> json) =>
      _$LearningSetUpdateRequestDtoFromJson(json);
}
