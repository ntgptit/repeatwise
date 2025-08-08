import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class DeleteSetUseCase {
  final SetRepository _repository;

  DeleteSetUseCase(this._repository);

  Future<ApiResponse<void>> execute(String setId, String userId) {
    // Business logic validation
    // For example: check if set is currently being learned
    // This would require getting the set first to check its status

    // Additional business rules can be added here
    // For example: prevent deletion of sets with active learning sessions
    // if (set.isLearning) {
    //   return Future.value(ApiResponse.error('Cannot delete set while learning'));
    // }

    return _repository.deleteSet(setId, userId);
  }
}
