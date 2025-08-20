import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/core/converters/datetime_utc_converter.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_status.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/skip_reason.dart';

part 'review_history_dto.freezed.dart';
part 'review_history_dto.g.dart';

@freezed
class ReviewHistoryDto with _$ReviewHistoryDto {
  const factory ReviewHistoryDto({
    required String id,
    required String setId,
    required String setName,
    required int cycleNo,
    required int reviewNo,
    int? score,
    required ReviewStatus status,
    SkipReason? skipReason,
    String? note,
    @DateTimeUtcConverter() required DateTime createdAt,
    @DateTimeUtcConverter() required DateTime updatedAt,
  }) = _ReviewHistoryDto;

  factory ReviewHistoryDto.fromJson(Map<String, dynamic> json) =>
      _$ReviewHistoryDtoFromJson(json);
}
