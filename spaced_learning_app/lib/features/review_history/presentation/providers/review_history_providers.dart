import 'package:dio/dio.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/features/review_history/data/datasources/review_history_remote_data_source.dart';
import 'package:spaced_learning_app/features/review_history/data/repositories/review_history_repository_impl.dart';
import 'package:spaced_learning_app/features/review_history/domain/repositories/review_history_repository.dart';
import 'package:spaced_learning_app/features/review_history/domain/usecases/create_review_history.dart';
import 'package:spaced_learning_app/features/review_history/domain/usecases/get_reviews_by_set.dart';
import 'package:spaced_learning_app/features/review_history/domain/usecases/update_review_history.dart';

part 'review_history_providers.g.dart';

@riverpod
ReviewHistoryRemoteDataSource reviewHistoryRemoteDataSource(Ref ref) {
  final dio = Dio(
    BaseOptions(baseUrl: AppConstants.baseUrl + AppConstants.apiPrefix),
  );
  return ReviewHistoryRemoteDataSource(dio);
}

@riverpod
ReviewHistoryRepository reviewHistoryRepository(Ref ref) {
  return ReviewHistoryRepositoryImpl(
    ref.watch(reviewHistoryRemoteDataSourceProvider),
  );
}

@riverpod
GetReviewsBySet getReviewsBySet(Ref ref) {
  return GetReviewsBySet(ref.watch(reviewHistoryRepositoryProvider));
}

@riverpod
CreateReviewHistory createReviewHistory(Ref ref) {
  return CreateReviewHistory(ref.watch(reviewHistoryRepositoryProvider));
}

@riverpod
UpdateReviewHistory updateReviewHistory(Ref ref) {
  return UpdateReviewHistory(ref.watch(reviewHistoryRepositoryProvider));
}
