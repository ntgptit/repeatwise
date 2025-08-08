import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class GetSetStatisticsUseCase {
  final SetRepository _repository;

  GetSetStatisticsUseCase(this._repository);

  Future<ApiResponse<Map<String, dynamic>>> execute(
    String setId,
    String userId,
  ) {
    return _repository.getSetStatistics(setId, userId);
  }
}
