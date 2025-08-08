import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';

abstract class SetRepository {
  Future<ApiResponse<List<Set>>> getSetsByUser(String userId);
  Future<ApiResponse<Set>> getSetById(String setId);
  Future<ApiResponse<Set>> createSet(String userId, SetCreateRequest request);
  Future<ApiResponse<Set>> updateSet(
    String id,
    String userId,
    SetUpdateRequest request,
  );
  Future<ApiResponse<void>> deleteSet(String id, String userId);
  Future<ApiResponse<Set>> startLearning(String id, String userId);
  Future<ApiResponse<Set>> markAsMastered(String id, String userId);
  Future<ApiResponse<Map<String, dynamic>>> getSetStatistics(
    String id,
    String userId,
  );
  Future<ApiResponse<List<Set>>> getDailyReviewSets(
    String userId, {
    String? date,
  });
}
