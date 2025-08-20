import '../../../../core/pagination/page.dart';
import '../entities/review_history.dart';
import '../entities/review_history_create.dart';
import '../entities/review_history_update.dart';

abstract class ReviewHistoryRepository {
  Future<ReviewHistory> create(ReviewHistoryCreate request);
  Future<ReviewHistory> update(String id, ReviewHistoryUpdate request);
  Future<ReviewHistory> getById(String id);
  Future<Page<ReviewHistory>> getBySet(String setId, {int page = 0, int size = 20});
}
