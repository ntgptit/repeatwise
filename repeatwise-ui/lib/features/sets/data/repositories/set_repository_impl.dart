import '../../../../core/services/api_repository.dart';
import '../../domain/repositories/set_repository.dart';
import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';

class SetRepositoryImpl implements SetRepository {
  final ApiRepository _apiRepository;

  SetRepositoryImpl(this._apiRepository);

  @override
  Future<ApiResponse<List<Set>>> getSetsByUser(String userId) {
    return _apiRepository.getSetsByUser(userId);
  }

  @override
  Future<ApiResponse<Set>> getSetById(String setId) {
    return _apiRepository.getSetById(setId);
  }

  @override
  Future<ApiResponse<Set>> createSet(String userId, SetCreateRequest request) {
    return _apiRepository.createSet(userId, request);
  }

  @override
  Future<ApiResponse<Set>> updateSet(
    String id,
    String userId,
    SetUpdateRequest request,
  ) {
    return _apiRepository.updateSet(id, userId, request);
  }

  @override
  Future<ApiResponse<void>> deleteSet(String id, String userId) {
    return _apiRepository.deleteSet(id, userId);
  }

  @override
  Future<ApiResponse<Set>> startLearning(String id, String userId) {
    return _apiRepository.startLearning(id, userId);
  }

  @override
  Future<ApiResponse<Set>> markAsMastered(String id, String userId) {
    return _apiRepository.markAsMastered(id, userId);
  }

  @override
  Future<ApiResponse<Map<String, dynamic>>> getSetStatistics(
    String id,
    String userId,
  ) {
    return _apiRepository.getSetStatistics(id, userId);
  }

  @override
  Future<ApiResponse<List<Set>>> getDailyReviewSets(
    String userId, {
    String? date,
  }) {
    return _apiRepository.getDailyReviewSets(userId, date: date);
  }
}
