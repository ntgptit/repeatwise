import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/features/learning_set/presentation/states/learning_set_list_state.dart';

import '../providers/learning_set_providers.dart';

part 'learning_set_list_notifier.g.dart';

@riverpod
class LearningSetListNotifier extends _$LearningSetListNotifier {
  @override
  LearningSetListState build() {
    return const LearningSetListState.initial();
  }

  Future<void> fetch({int page = 0, int size = 20}) async {
    state = const LearningSetListState.loading();
    final usecase = ref.read(getUserSetsProvider);
    try {
      final result = await usecase(page: page, size: size);
      if (result.content.isEmpty) {
        state = const LearningSetListState.empty();
        return;
      }
      state = LearningSetListState.data(result);
    } catch (e) {
      state = LearningSetListState.error(e.toString());
    }
  }
}
