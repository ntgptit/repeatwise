import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/review_history.dart';
import 'package:spaced_learning_app/domain/repositories/review_history_repository.dart';

class ReviewHistoryRepositoryImpl implements ReviewHistoryRepository {
  final ApiClient _apiClient;

  ReviewHistoryRepositoryImpl(this._apiClient);

  @override
  Future<List<ReviewHistory>> getAllReviewHistories() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.reviewHistories);
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => ReviewHistory.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch review histories: $e');
    }
  }

  @override
  Future<ReviewHistory?> getReviewHistoryById(String id) async {
    try {
      final response = await _apiClient.get(ApiEndpoints.reviewHistoryById(id));
      final data = response['data'];
      return data != null ? ReviewHistory.fromJson(data) : null;
    } catch (e) {
      throw Exception('Failed to fetch review history: $e');
    }
  }

  @override
  Future<ReviewHistory> createReviewHistory(ReviewHistory reviewHistory) async {
    try {
      final response = await _apiClient.post(
        ApiEndpoints.reviewHistories,
        data: reviewHistory.toJson(),
      );
      final data = response['data'];
      return ReviewHistory.fromJson(data);
    } catch (e) {
      throw Exception('Failed to create review history: $e');
    }
  }

  @override
  Future<ReviewHistory> updateReviewHistory(
    String id,
    ReviewHistory reviewHistory,
  ) async {
    try {
      final response = await _apiClient.put(
        ApiEndpoints.reviewHistoryById(id),
        data: reviewHistory.toJson(),
      );
      final data = response['data'];
      return ReviewHistory.fromJson(data);
    } catch (e) {
      throw Exception('Failed to update review history: $e');
    }
  }

  @override
  Future<void> deleteReviewHistory(String id) async {
    try {
      await _apiClient.delete(ApiEndpoints.reviewHistoryById(id));
    } catch (e) {
      throw Exception('Failed to delete review history: $e');
    }
  }

  @override
  Future<List<ReviewHistory>> getReviewHistoriesBySet(String setId) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.reviewHistoriesBySet(setId),
      );
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => ReviewHistory.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch review histories by set: $e');
    }
  }

  @override
  Future<List<ReviewHistory>> getReviewHistoriesByUser(String userId) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.reviewHistoriesByUser(userId),
      );
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => ReviewHistory.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch review histories by user: $e');
    }
  }
}
