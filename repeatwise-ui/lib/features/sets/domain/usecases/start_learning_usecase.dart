import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class StartLearningUseCase {
  final SetRepository _repository;

  StartLearningUseCase(this._repository);

  Future<ApiResponse<Set>> execute(String setId, String userId) {
    // Business logic validation
    // For example: check if user can start learning this set
    // This might involve checking user's current learning sessions, time limits, etc.

    // Additional business rules can be added here
    // For example: prevent starting multiple sets simultaneously
    // if (userHasActiveLearningSession) {
    //   return Future.value(ApiResponse.error('You already have an active learning session'));
    // }

    return _repository.startLearning(setId, userId);
  }
}
