import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class UpdateSetUseCase {
  final SetRepository _repository;

  UpdateSetUseCase(this._repository);

  Future<ApiResponse<Set>> execute(
    String setId,
    String userId,
    SetUpdateRequest request,
  ) {
    // Business logic validation
    if (request.name != null && request.name!.trim().isEmpty) {
      return Future.value(ApiResponse.error('Set name cannot be empty'));
    }

    if (request.name != null && request.name!.length > 100) {
      return Future.value(
        ApiResponse.error('Set name cannot exceed 100 characters'),
      );
    }

    if (request.description != null && request.description!.length > 500) {
      return Future.value(
        ApiResponse.error('Description cannot exceed 500 characters'),
      );
    }

    // Additional business rules
    // For example: check if set is in learning mode and prevent certain updates
    // if (set.isLearning && request.isPublic != null) {
    //   return Future.value(ApiResponse.error('Cannot change visibility while learning'));
    // }

    return _repository.updateSet(setId, userId, request);
  }
}
