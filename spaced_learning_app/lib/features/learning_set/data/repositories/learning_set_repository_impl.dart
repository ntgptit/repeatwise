import 'package:dio/dio.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/exceptions/parse_exception.dart';
import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/features/learning_set/data/datasources/learning_set_remote_data_source.dart';
import 'package:spaced_learning_app/features/learning_set/data/mappers/learning_set_mapper.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_create.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_update.dart';
import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';

class LearningSetRepositoryImpl implements LearningSetRepository {
  final LearningSetRemoteDataSource _remote;

  LearningSetRepositoryImpl(this._remote);

  @override
  Future<Page<LearningSet>> getUserSets(int page, int size) async {
    try {
      final result = await _remote.getUserSets(page, size);
      return LearningSetMapper.mapPage(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<LearningSet> getById(String id) async {
    try {
      final result = await _remote.getById(id);
      return LearningSetMapper.fromDto(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<LearningSet> create(LearningSetCreate request) async {
    try {
      final dto = LearningSetMapper.toCreateDto(request);
      final result = await _remote.create(dto);
      return LearningSetMapper.fromDto(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<LearningSet> update(String id, LearningSetUpdate request) async {
    try {
      final dto = LearningSetMapper.toUpdateDto(request);
      final result = await _remote.update(id, dto);
      return LearningSetMapper.fromDto(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<void> delete(String id) async {
    try {
      await _remote.delete(id);
    } on DioException catch (e) {
      throw ServerException(e.message);
    }
  }
}
