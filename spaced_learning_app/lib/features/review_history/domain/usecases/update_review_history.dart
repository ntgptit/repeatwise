import '../entities/review_history.dart';
import '../entities/review_history_update.dart';
import '../repositories/review_history_repository.dart';

class UpdateReviewHistory {
  final ReviewHistoryRepository _repository;
  UpdateReviewHistory(this._repository);

  Future<ReviewHistory> call(String id, ReviewHistoryUpdate request) {
    return _repository.update(id, request);
  }
}
