import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/learning_module.dart';
import 'package:spaced_learning_app/domain/repositories/learning_progress_repository.dart';

class LearningProgressRepositoryImpl implements LearningProgressRepository {
  final ApiClient _apiClient;

  LearningProgressRepositoryImpl(this._apiClient);

  @override
  Future<List<LearningModule>> getAllModules() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.learningModules);

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> modulesList = response['data'];
      return modulesList.map((item) => LearningModule.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get modules: $e');
    }
  }

  @override
  Future<List<LearningModule>> getDueModules(int daysThreshold) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.dueModules,
        queryParameters: {'daysThreshold': daysThreshold},
      );

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> modulesList = response['data'];
      return modulesList.map((item) => LearningModule.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get due modules: $e');
    }
  }

  @override
  Future<List<String>> getUniqueBooks() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.uniqueBooks);

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> booksList = response['data'];
      return booksList.map((item) => item.toString()).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get unique books: $e');
    }
  }

  @override
  Future<Map<String, dynamic>> exportData() async {
    try {
      final response = await _apiClient.post(ApiEndpoints.exportData);

      if (response['success'] != true || response['data'] == null) {
        throw BadRequestException(
          'Failed to export data: ${response['message']}',
        );
      }

      return response['data'] as Map<String, dynamic>;
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to export data: $e');
    }
  }

  @override
  Future<Map<String, dynamic>> getDashboardStats({
    String? book,
    DateTime? date,
  }) async {
    try {
      final Map<String, dynamic> queryParams = {};

      if (book != null) {
        queryParams['book'] = book;
      }

      if (date != null) {
        queryParams['date'] = DateFormat('yyyy-MM-dd').format(date);
      }

      final response = await _apiClient.get(
        ApiEndpoints.dashboardStats,
        queryParameters: queryParams,
      );

      if (response['success'] != true || response['data'] == null) {
        throw BadRequestException(
          'Failed to get dashboard stats: ${response['message']}',
        );
      }

      return response['data'] as Map<String, dynamic>;
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get dashboard stats: $e');
    }
  }
}
