import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../../../../core/models/set.dart';
import '../di/set_dependencies.dart';

part 'set_providers.g.dart';

@riverpod
class SetsNotifier extends _$SetsNotifier {
  bool _isCreating = false;
  String? _createError;

  bool get isCreating => _isCreating;
  String? get createError => _createError;

  @override
  Future<List<Set>> build() async {
    return _loadSets();
  }

  Future<List<Set>> _loadSets() async {
    final getSetsUseCase = ref.read(getSetsUseCaseProvider);
    // TODO: Get current user ID from auth state
    const userId = 'current-user-id'; // This should come from auth state
    final response = await getSetsUseCase.execute(userId);
    return response.fold(
      onSuccess: (sets) => sets,
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> refreshSets() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _loadSets());
  }

  Future<void> createSet(SetCreateRequest request) async {
    if (_isCreating) return; // Prevent multiple simultaneous requests

    _isCreating = true;
    _createError = null;
    ref.notifyListeners();

    try {
      final createSetUseCase = ref.read(createSetUseCaseProvider);
      const userId = 'current-user-id'; // This should come from auth state

      final response = await createSetUseCase.execute(userId, request);
      response.fold(
        onSuccess: (newSet) {
          state = state.whenData((sets) => [newSet, ...sets]);
        },
        onError: (message) {
          _createError = message;
          throw Exception(message);
        },
      );
    } catch (e) {
      _createError = e.toString();
      rethrow;
    } finally {
      _isCreating = false;
      ref.notifyListeners();
    }
  }

  void clearCreateError() {
    _createError = null;
    ref.notifyListeners();
  }

  Future<void> updateSet(String id, SetUpdateRequest request) async {
    final updateSetUseCase = ref.read(updateSetUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await updateSetUseCase.execute(id, userId, request);
    response.fold(
      onSuccess: (updatedSet) {
        state = state.whenData(
          (sets) => sets.map((set) => set.id == id ? updatedSet : set).toList(),
        );
      },
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> deleteSet(String id) async {
    final deleteSetUseCase = ref.read(deleteSetUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await deleteSetUseCase.execute(id, userId);
    response.fold(
      onSuccess: (_) {
        state = state.whenData(
          (sets) => sets.where((set) => set.id != id).toList(),
        );
      },
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> startLearning(String id) async {
    final startLearningUseCase = ref.read(startLearningUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await startLearningUseCase.execute(id, userId);
    response.fold(
      onSuccess: (updatedSet) {
        state = state.whenData(
          (sets) => sets.map((set) => set.id == id ? updatedSet : set).toList(),
        );
      },
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> markAsMastered(String id) async {
    final markAsMasteredUseCase = ref.read(markAsMasteredUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await markAsMasteredUseCase.execute(id, userId);
    response.fold(
      onSuccess: (updatedSet) {
        state = state.whenData(
          (sets) => sets.map((set) => set.id == id ? updatedSet : set).toList(),
        );
      },
      onError: (message) => throw Exception(message),
    );
  }
}

@riverpod
class SetDetailNotifier extends _$SetDetailNotifier {
  @override
  Future<Set?> build(String setId) async {
    return _loadSet(setId);
  }

  Future<Set?> _loadSet(String setId) async {
    final getSetByIdUseCase = ref.read(getSetByIdUseCaseProvider);
    final response = await getSetByIdUseCase.execute(setId);
    return response.fold(
      onSuccess: (set) => set,
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> refreshSet() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _loadSet(state.value?.id ?? ''));
  }

  Future<void> updateSet(SetUpdateRequest request) async {
    final currentSet = state.value;
    if (currentSet == null) return;

    final updateSetUseCase = ref.read(updateSetUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await updateSetUseCase.execute(
      currentSet.id,
      userId,
      request,
    );
    response.fold(
      onSuccess: (updatedSet) {
        state = AsyncValue.data(updatedSet);
      },
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> startLearning() async {
    final currentSet = state.value;
    if (currentSet == null) return;

    final startLearningUseCase = ref.read(startLearningUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await startLearningUseCase.execute(currentSet.id, userId);
    response.fold(
      onSuccess: (updatedSet) {
        state = AsyncValue.data(updatedSet);
      },
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> markAsMastered() async {
    final currentSet = state.value;
    if (currentSet == null) return;

    final markAsMasteredUseCase = ref.read(markAsMasteredUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await markAsMasteredUseCase.execute(currentSet.id, userId);
    response.fold(
      onSuccess: (updatedSet) {
        state = AsyncValue.data(updatedSet);
      },
      onError: (message) => throw Exception(message),
    );
  }
}

@riverpod
class SetStatisticsNotifier extends _$SetStatisticsNotifier {
  @override
  Future<Map<String, dynamic>?> build(String setId) async {
    return _loadStatistics(setId);
  }

  Future<Map<String, dynamic>?> _loadStatistics(String setId) async {
    final getSetStatisticsUseCase = ref.read(getSetStatisticsUseCaseProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await getSetStatisticsUseCase.execute(setId, userId);
    return response.fold(
      onSuccess: (statistics) => statistics,
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> refreshStatistics() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(
      () => _loadStatistics(state.value?.keys.first ?? ''),
    );
  }
}

@riverpod
class DailyReviewSetsNotifier extends _$DailyReviewSetsNotifier {
  @override
  Future<List<Set>> build() async {
    return _loadDailyReviewSets();
  }

  Future<List<Set>> _loadDailyReviewSets() async {
    final getDailyReviewSetsUseCase = ref.read(
      getDailyReviewSetsUseCaseProvider,
    );
    const userId = 'current-user-id'; // This should come from auth state

    final response = await getDailyReviewSetsUseCase.execute(userId);
    return response.fold(
      onSuccess: (sets) => sets,
      onError: (message) => throw Exception(message),
    );
  }

  Future<void> refreshDailyReviewSets() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _loadDailyReviewSets());
  }
}
