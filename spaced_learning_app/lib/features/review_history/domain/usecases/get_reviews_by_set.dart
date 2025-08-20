import '../entities/review_history.dart';
import '../repositories/review_history_repository.dart';
import '../../../../core/pagination/page.dart';

class GetReviewsBySet {
  final ReviewHistoryRepository _repository;
  GetReviewsBySet(this._repository);

  Future<Page<ReviewHistory>> call(String setId, {int page = 0, int size = 20}) {
    return _repository.getBySet(setId, page: page, size: size);
  }
}
