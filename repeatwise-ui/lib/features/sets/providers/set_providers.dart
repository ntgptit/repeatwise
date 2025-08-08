import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../../../../core/models/set.dart';
import '../../../../core/services/api_repository.dart';

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
    final apiRepository = ref.read(apiRepositoryProvider);
    // TODO: Get current user ID from auth state
    const userId = 'current-user-id'; // This should come from auth state
    final response = await apiRepository.getSetsByUser(userId);
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
      final apiRepository = ref.read(apiRepositoryProvider);
      const userId = 'current-user-id'; // This should come from auth state

      final response = await apiRepository.createSet(userId, request);
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
    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.updateSet(id, userId, request);
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
    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.deleteSet(id, userId);
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
    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.startLearning(id, userId);
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
    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.markAsMastered(id, userId);
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
    final apiRepository = ref.read(apiRepositoryProvider);
    final response = await apiRepository.getSetById(setId);
    return response.fold(
      onSuccess: (set) => set as Set?,
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

    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.updateSet(
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

    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.startLearning(currentSet.id, userId);
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

    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.markAsMastered(currentSet.id, userId);
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
    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.getSetStatistics(setId, userId);
    return response.fold(
      onSuccess: (statistics) => statistics as Map<String, dynamic>?,
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
    final apiRepository = ref.read(apiRepositoryProvider);
    const userId = 'current-user-id'; // This should come from auth state

    final response = await apiRepository.getDailyReviewSets(userId);
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
