import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_status.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/skip_reason.dart';

part 'review_history_update_request_dto.freezed.dart';
part 'review_history_update_request_dto.g.dart';

@freezed
class ReviewHistoryUpdateRequestDto with _$ReviewHistoryUpdateRequestDto {
  const factory ReviewHistoryUpdateRequestDto({
    int? score,
    ReviewStatus? status,
    SkipReason? skipReason,
    String? note,
  }) = _ReviewHistoryUpdateRequestDto;

  factory ReviewHistoryUpdateRequestDto.fromJson(Map<String, dynamic> json) =>
      _$ReviewHistoryUpdateRequestDtoFromJson(json);
}
