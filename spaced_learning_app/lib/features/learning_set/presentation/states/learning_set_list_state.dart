import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';

part 'learning_set_list_state.freezed.dart';

@freezed
class LearningSetListState with _$LearningSetListState {
  const factory LearningSetListState.initial() = _Initial;
  const factory LearningSetListState.loading() = _Loading;
  const factory LearningSetListState.data(Page<LearningSet> page) = _Data;
  const factory LearningSetListState.empty() = _Empty;
  const factory LearningSetListState.error(String message) = _Error;
}
