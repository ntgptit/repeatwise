import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/learning_set.dart';
import 'package:spaced_learning_app/domain/repositories/learning_set_repository.dart';

class LearningSetRepositoryImpl implements LearningSetRepository {
  final ApiClient _apiClient;

  LearningSetRepositoryImpl(this._apiClient);

  @override
  Future<List<LearningSet>> getAllLearningSets() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.learningSets);
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => LearningSet.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch learning sets: $e');
    }
  }

  @override
  Future<LearningSet?> getLearningSetById(String id) async {
    try {
      final response = await _apiClient.get(ApiEndpoints.learningSetById(id));
      final data = response['data'];
      return data != null ? LearningSet.fromJson(data) : null;
    } catch (e) {
      throw Exception('Failed to fetch learning set: $e');
    }
  }

  @override
  Future<LearningSet> createLearningSet(LearningSet learningSet) async {
    try {
      final response = await _apiClient.post(
        ApiEndpoints.learningSets,
        data: learningSet.toJson(),
      );
      final data = response['data'];
      return LearningSet.fromJson(data);
    } catch (e) {
      throw Exception('Failed to create learning set: $e');
    }
  }

  @override
  Future<LearningSet> updateLearningSet(
    String id,
    LearningSet learningSet,
  ) async {
    try {
      final response = await _apiClient.put(
        ApiEndpoints.learningSetById(id),
        data: learningSet.toJson(),
      );
      final data = response['data'];
      return LearningSet.fromJson(data);
    } catch (e) {
      throw Exception('Failed to update learning set: $e');
    }
  }

  @override
  Future<void> deleteLearningSet(String id) async {
    try {
      await _apiClient.delete(ApiEndpoints.learningSetById(id));
    } catch (e) {
      throw Exception('Failed to delete learning set: $e');
    }
  }

  @override
  Future<List<LearningSet>> getLearningSetsByCategory(String category) async {
    try {
      final response = await _apiClient.get(
        '${ApiEndpoints.learningSets}?category=$category',
      );
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => LearningSet.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch learning sets by category: $e');
    }
  }
}
