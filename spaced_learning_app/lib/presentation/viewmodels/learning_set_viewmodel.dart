import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/domain/models/learning_set.dart';
import 'package:spaced_learning_app/core/di/providers.dart';
import 'package:spaced_learning_app/presentation/viewmodels/base_viewmodel.dart';

part 'learning_set_viewmodel.g.dart';

@riverpod
class LearningSetNotifier extends _$LearningSetNotifier {
  @override
  Future<List<LearningSet>> build() async {
    final repository = ref.read(learningSetRepositoryProvider);
    return await repository.getAllLearningSets();
  }

  Future<void> loadLearningSetsByCategory(String category) async {
    state = const AsyncValue.loading();
    try {
      final repository = ref.read(learningSetRepositoryProvider);
      final result = await repository.getLearningSetsByCategory(category);
      state = AsyncValue.data(result);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> loadLearningSetById(String id) async {
    state = const AsyncValue.loading();
    try {
      final repository = ref.read(learningSetRepositoryProvider);
      final result = await repository.getLearningSetById(id);
      if (result != null) {
        state = AsyncValue.data([result]);
      } else {
        state = const AsyncValue.data([]);
      }
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> createLearningSet(LearningSet learningSet) async {
    state = const AsyncValue.loading();
    try {
      final repository = ref.read(learningSetRepositoryProvider);
      await repository.createLearningSet(learningSet);
      final result = await build();
      state = AsyncValue.data(result);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> updateLearningSet(String id, LearningSet learningSet) async {
    state = const AsyncValue.loading();
    try {
      final repository = ref.read(learningSetRepositoryProvider);
      await repository.updateLearningSet(id, learningSet);
      final result = await build();
      state = AsyncValue.data(result);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> deleteLearningSet(String id) async {
    state = const AsyncValue.loading();
    try {
      final repository = ref.read(learningSetRepositoryProvider);
      await repository.deleteLearningSet(id);
      final result = await build();
      state = AsyncValue.data(result);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  List<LearningSet> getLearningSetsByStatus(SetStatus status) {
    final currentState = state.value;
    if (currentState == null) return [];
    return currentState.where((set) => set.status == status).toList();
  }

  List<LearningSet> getLearningSetsByCategory(SetCategory category) {
    final currentState = state.value;
    if (currentState == null) return [];
    return currentState.where((set) => set.category == category).toList();
  }
}
