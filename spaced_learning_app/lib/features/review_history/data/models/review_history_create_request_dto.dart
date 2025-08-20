import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_status.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/skip_reason.dart';

part 'review_history_create_request_dto.freezed.dart';
part 'review_history_create_request_dto.g.dart';

@freezed
class ReviewHistoryCreateRequestDto with _$ReviewHistoryCreateRequestDto {
  const factory ReviewHistoryCreateRequestDto({
    required String setId,
    required int cycleNo,
    required int reviewNo,
    int? score,
    required ReviewStatus status,
    SkipReason? skipReason,
    String? note,
  }) = _ReviewHistoryCreateRequestDto;

  factory ReviewHistoryCreateRequestDto.fromJson(Map<String, dynamic> json) =>
      _$ReviewHistoryCreateRequestDtoFromJson(json);
}
