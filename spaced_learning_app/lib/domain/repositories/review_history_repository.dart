import 'package:spaced_learning_app/domain/models/review_history.dart';

abstract class ReviewHistoryRepository {
  Future<List<ReviewHistory>> getAllReviewHistories();
  Future<ReviewHistory?> getReviewHistoryById(String id);
  Future<ReviewHistory> createReviewHistory(ReviewHistory reviewHistory);
  Future<ReviewHistory> updateReviewHistory(
    String id,
    ReviewHistory reviewHistory,
  );
  Future<void> deleteReviewHistory(String id);
  Future<List<ReviewHistory>> getReviewHistoriesBySet(String setId);
  Future<List<ReviewHistory>> getReviewHistoriesByUser(String userId);
}
