import 'package:flutter/foundation.dart';
import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/core/utils/string_utils.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/domain/repositories/grammar_repository.dart';

class GrammarRepositoryImpl implements GrammarRepository {
  final ApiClient _apiClient;

  GrammarRepositoryImpl(this._apiClient);

  @override
  Future<List<GrammarSummary>> getAllGrammars({
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.grammars,
        queryParameters: {'page': page, 'size': size},
      );

      final content = response['content'];
      if (content == null || content is! List) {
        return [];
      }

      return content.map((item) => GrammarSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      debugPrint('Error fetching grammars: $e');
      throw UnexpectedException('Failed to get grammars: $e');
    }
  }

  @override
  Future<GrammarDetail> getGrammarById(String id) async {
    final sanitizedId = StringUtils.sanitizeId(id, source: 'GrammarRepository');
    if (sanitizedId == null) {
      throw BadRequestException('Invalid grammar ID');
    }

    try {
      final response = await _apiClient.get(
        '${ApiEndpoints.grammars}/$sanitizedId',
      );

      if (response['success'] != true || response['data'] == null) {
        throw NotFoundException('Grammar not found: ${response['message']}');
      }

      return GrammarDetail.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      debugPrint('Error fetching grammar detail: $e');
      throw UnexpectedException('Failed to get grammar: $e');
    }
  }

  @override
  Future<List<GrammarSummary>> getGrammarsByModuleId(String moduleId) async {
    final sanitizedId = StringUtils.sanitizeId(
      moduleId,
      source: 'GrammarRepository',
    );
    if (sanitizedId == null) {
      throw BadRequestException('Invalid module ID');
    }

    try {
      final response = await _apiClient.get(
        ApiEndpoints.grammarsByModule(sanitizedId),
      );

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> grammarList = response['data'];
      return grammarList.map((item) => GrammarSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      debugPrint('Error fetching grammars by module: $e');
      throw UnexpectedException('Failed to get grammars by module: $e');
    }
  }

  @override
  Future<List<GrammarSummary>> searchGrammars(
    String query, {
    int page = 0,
    int size = 20,
  }) async {
    if (query.trim().isEmpty) {
      return [];
    }

    try {
      final response = await _apiClient.get(
        ApiEndpoints.grammarSearch,
        queryParameters: {'title': query, 'page': page, 'size': size},
      );

      if (response['success'] != true || response['content'] == null) {
        return [];
      }

      final List<dynamic> grammarList = response['content'];
      return grammarList.map((item) => GrammarSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      debugPrint('Error searching grammars: $e');
      throw UnexpectedException('Failed to search grammars: $e');
    }
  }
}
