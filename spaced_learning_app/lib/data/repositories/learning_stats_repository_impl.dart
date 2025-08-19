import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/learning_insight.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';
import 'package:spaced_learning_app/domain/repositories/learning_stats_repository.dart';

class LearningStatsRepositoryImpl implements LearningStatsRepository {
  final ApiClient _apiClient;

  LearningStatsRepositoryImpl(this._apiClient);

  @override
  Future<LearningStatsDTO> getDashboardStats({
    bool refreshCache = false,
  }) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.dashboardStats,
        queryParameters: {'refreshCache': refreshCache},
      );

      if (response['success'] != true || response['data'] == null) {
        throw BadRequestException(
          'Failed to get dashboard stats: ${response['message']}',
        );
      }

      return LearningStatsDTO.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get dashboard stats: $e');
    }
  }

  @override
  Future<List<LearningInsightRespone>> getLearningInsights() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.learningInsights);

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> insightsList = response['data'];
      return insightsList
          .map((item) => LearningInsightRespone.fromJson(item))
          .toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get learning insights: $e');
    }
  }
}
