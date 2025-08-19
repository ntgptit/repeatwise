import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/module.dart';
import 'package:spaced_learning_app/domain/repositories/module_repository.dart';

class ModuleRepositoryImpl implements ModuleRepository {
  final ApiClient _apiClient;

  ModuleRepositoryImpl(this._apiClient);

  @override
  Future<List<ModuleSummary>> getAllModules({
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.modules,
        queryParameters: {'page': page, 'size': size},
      );

      if (response['success'] != true || response['content'] == null) {
        return [];
      }

      final List<dynamic> moduleList = response['content'];
      return moduleList.map((item) => ModuleSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get modules: $e');
    }
  }

  @override
  Future<ModuleDetail> getModuleById(String id) async {
    try {
      final response = await _apiClient.get('${ApiEndpoints.modules}/$id');

      if (response['success'] != true || response['data'] == null) {
        throw NotFoundException('Module not found: ${response['message']}');
      }

      return ModuleDetail.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get module: $e');
    }
  }

  @override
  Future<List<ModuleSummary>> getModulesByBookId(
    String bookId, {
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.modulesByBook(bookId),
        queryParameters: {'page': null, 'size': null},
      );

      final content = response['content'];
      if (content == null || content is! List) {
        return [];
      }

      return content.map((item) => ModuleSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get modules by book: $e');
    }
  }
}
