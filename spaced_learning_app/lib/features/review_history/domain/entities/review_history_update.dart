import 'package:freezed_annotation/freezed_annotation.dart';

import 'review_status.dart';

part 'review_history_update.freezed.dart';

@freezed
class ReviewHistoryUpdate with _$ReviewHistoryUpdate {
  const factory ReviewHistoryUpdate({
    int? score,
    ReviewStatus? status,
    String? note,
  }) = _ReviewHistoryUpdate;
}
