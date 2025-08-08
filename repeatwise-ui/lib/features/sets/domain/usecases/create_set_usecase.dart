import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class CreateSetUseCase {
  final SetRepository _repository;

  CreateSetUseCase(this._repository);

  Future<ApiResponse<Set>> execute(String userId, SetCreateRequest request) {
    // Business logic validation
    if (request.name.trim().isEmpty) {
      return Future.value(ApiResponse.error('Set name cannot be empty'));
    }

    if (request.name.length > 100) {
      return Future.value(
        ApiResponse.error('Set name cannot exceed 100 characters'),
      );
    }

    if (request.description != null && request.description!.length > 500) {
      return Future.value(
        ApiResponse.error('Description cannot exceed 500 characters'),
      );
    }

    // Additional business rules can be added here
    // For example: check if user has reached set limit, validate tags, etc.

    return _repository.createSet(userId, request);
  }
}
