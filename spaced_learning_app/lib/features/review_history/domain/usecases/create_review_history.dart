import '../entities/review_history.dart';
import '../entities/review_history_create.dart';
import '../repositories/review_history_repository.dart';

class CreateReviewHistory {
  final ReviewHistoryRepository _repository;
  CreateReviewHistory(this._repository);

  Future<ReviewHistory> call(ReviewHistoryCreate request) {
    return _repository.create(request);
  }
}
