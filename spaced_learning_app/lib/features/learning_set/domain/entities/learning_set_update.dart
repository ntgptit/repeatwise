import 'package:freezed_annotation/freezed_annotation.dart';

import 'set_category.dart';

part 'learning_set_update.freezed.dart';

@freezed
class LearningSetUpdate with _$LearningSetUpdate {
  const factory LearningSetUpdate({
    String? name,
    String? description,
    SetCategory? category,
    int? wordCount,
  }) = _LearningSetUpdate;
}
