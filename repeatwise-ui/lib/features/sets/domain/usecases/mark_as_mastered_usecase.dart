import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class MarkAsMasteredUseCase {
  final SetRepository _repository;

  MarkAsMasteredUseCase(this._repository);

  Future<ApiResponse<Set>> execute(String setId, String userId) {
    // Business logic validation
    // For example: check if set meets mastery criteria
    // This might involve checking completion percentage, review count, etc.

    // Additional business rules can be added here
    // For example: ensure minimum learning time before marking as mastered
    // if (!meetsMasteryCriteria) {
    //   return Future.value(ApiResponse.error('Set does not meet mastery criteria'));
    // }

    return _repository.markAsMastered(setId, userId);
  }
}
