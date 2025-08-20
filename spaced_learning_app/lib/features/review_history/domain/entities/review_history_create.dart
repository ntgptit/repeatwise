import 'package:freezed_annotation/freezed_annotation.dart';

import 'review_status.dart';
import 'skip_reason.dart';

part 'review_history_create.freezed.dart';

@freezed
class ReviewHistoryCreate with _$ReviewHistoryCreate {
  const factory ReviewHistoryCreate({
    required String setId,
    required int cycleNo,
    required int reviewNo,
    int? score,
    required ReviewStatus status,
    SkipReason? skipReason,
    String? note,
  }) = _ReviewHistoryCreate;
}
