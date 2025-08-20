import 'package:freezed_annotation/freezed_annotation.dart';

import 'review_status.dart';
import 'skip_reason.dart';

part 'review_history_update.freezed.dart';

@freezed
class ReviewHistoryUpdate with _$ReviewHistoryUpdate {
  const factory ReviewHistoryUpdate({
    int? score,
    ReviewStatus? status,
    SkipReason? skipReason,
    String? note,
  }) = _ReviewHistoryUpdate;
}
