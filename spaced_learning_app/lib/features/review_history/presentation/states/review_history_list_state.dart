import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history.dart';

part 'review_history_list_state.freezed.dart';

@freezed
class ReviewHistoryListState with _$ReviewHistoryListState {
  const factory ReviewHistoryListState.initial() = _Initial;
  const factory ReviewHistoryListState.loading() = _Loading;
  const factory ReviewHistoryListState.data(Page<ReviewHistory> page) = _Data;
  const factory ReviewHistoryListState.empty() = _Empty;
  const factory ReviewHistoryListState.error(String message) = _Error;
}
