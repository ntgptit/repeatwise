import 'package:freezed_annotation/freezed_annotation.dart';

import '../../../../core/converters/datetime_utc_converter.dart';
import 'review_status.dart';

part 'review_history.freezed.dart';

@freezed
class ReviewHistory with _$ReviewHistory {
  const factory ReviewHistory({
    required String id,
    required String setId,
    required String setName,
    required int cycleNo,
    required int reviewNo,
    int? score,
    required ReviewStatus status,
    String? note,
    @DateTimeUtcConverter() required DateTime createdAt,
    @DateTimeUtcConverter() required DateTime updatedAt,
  }) = _ReviewHistory;
}
