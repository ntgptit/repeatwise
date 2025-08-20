import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/features/review_history/presentation/states/review_history_list_state.dart';

import '../providers/review_history_providers.dart';

part 'review_history_list_notifier.g.dart';

@riverpod
class ReviewHistoryListNotifier extends _$ReviewHistoryListNotifier {
  @override
  ReviewHistoryListState build(String setId) {
    return const ReviewHistoryListState.initial();
  }

  Future<void> fetch({int page = 0, int size = 20}) async {
    state = const ReviewHistoryListState.loading();
    final usecase = ref.read(getReviewsBySetProvider);
    try {
      final result = await usecase(setId, page: page, size: size);
      if (result.content.isEmpty) {
        state = const ReviewHistoryListState.empty();
        return;
      }
      state = ReviewHistoryListState.data(result);
    } catch (e) {
      state = ReviewHistoryListState.error(e.toString());
    }
  }
}
