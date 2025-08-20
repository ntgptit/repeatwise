import 'package:dio/dio.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/exceptions/parse_exception.dart';
import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/features/review_history/data/datasources/review_history_remote_data_source.dart';
import 'package:spaced_learning_app/features/review_history/data/mappers/review_history_mapper.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history_create.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history_update.dart';
import 'package:spaced_learning_app/features/review_history/domain/repositories/review_history_repository.dart';

class ReviewHistoryRepositoryImpl implements ReviewHistoryRepository {
  final ReviewHistoryRemoteDataSource _remote;

  ReviewHistoryRepositoryImpl(this._remote);

  @override
  Future<ReviewHistory> create(ReviewHistoryCreate request) async {
    try {
      final dto = ReviewHistoryMapper.toCreateDto(request);
      final result = await _remote.create(dto);
      return ReviewHistoryMapper.fromDto(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<ReviewHistory> update(String id, ReviewHistoryUpdate request) async {
    try {
      final dto = ReviewHistoryMapper.toUpdateDto(request);
      final result = await _remote.update(id, dto);
      return ReviewHistoryMapper.fromDto(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<ReviewHistory> getById(String id) async {
    try {
      final result = await _remote.getById(id);
      return ReviewHistoryMapper.fromDto(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }

  @override
  Future<Page<ReviewHistory>> getBySet(String setId,
      {int page = 0, int size = 20}) async {
    try {
      final result = await _remote.getBySet(setId, page, size);
      return ReviewHistoryMapper.mapPage(result);
    } on DioException catch (e) {
      throw ServerException(e.message);
    } on ParseException {
      rethrow;
    }
  }
}
