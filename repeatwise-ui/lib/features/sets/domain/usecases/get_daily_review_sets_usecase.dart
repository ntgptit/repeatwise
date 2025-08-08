import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class GetDailyReviewSetsUseCase {
  final SetRepository _repository;

  GetDailyReviewSetsUseCase(this._repository);

  Future<ApiResponse<List<Set>>> execute(String userId, {String? date}) {
    // Business logic validation
    // For example: validate date format, check if user has any sets to review

    // Additional business rules can be added here
    // For example: filter out sets that are already mastered
    // or prioritize sets based on learning algorithm

    return _repository.getDailyReviewSets(userId, date: date);
  }
}
