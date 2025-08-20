import 'package:freezed_annotation/freezed_annotation.dart';

import 'set_category.dart';

part 'learning_set_create.freezed.dart';

@freezed
class LearningSetCreate with _$LearningSetCreate {
  const factory LearningSetCreate({
    required String name,
    String? description,
    required SetCategory category,
    required int wordCount,
  }) = _LearningSetCreate;
}
