import 'package:dio/dio.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/features/learning_set/data/datasources/learning_set_remote_data_source.dart';
import 'package:spaced_learning_app/features/learning_set/data/repositories/learning_set_repository_impl.dart';
import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';
import 'package:spaced_learning_app/features/learning_set/domain/usecases/create_learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/usecases/delete_learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/usecases/get_learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/usecases/get_user_sets.dart';
import 'package:spaced_learning_app/features/learning_set/domain/usecases/update_learning_set.dart';

part 'learning_set_providers.g.dart';

@riverpod
LearningSetRemoteDataSource learningSetRemoteDataSource(Ref ref) {
  final dio = Dio(
    BaseOptions(baseUrl: AppConstants.baseUrl + AppConstants.apiPrefix),
  );
  return LearningSetRemoteDataSource(dio);
}

@riverpod
LearningSetRepository learningSetRepository(Ref ref) {
  return LearningSetRepositoryImpl(
    ref.watch(learningSetRemoteDataSourceProvider),
  );
}

@riverpod
GetUserSets getUserSets(Ref ref) {
  return GetUserSets(ref.watch(learningSetRepositoryProvider));
}

@riverpod
GetLearningSet getLearningSet(Ref ref) {
  return GetLearningSet(ref.watch(learningSetRepositoryProvider));
}

@riverpod
CreateLearningSet createLearningSet(Ref ref) {
  return CreateLearningSet(ref.watch(learningSetRepositoryProvider));
}

@riverpod
UpdateLearningSet updateLearningSet(Ref ref) {
  return UpdateLearningSet(ref.watch(learningSetRepositoryProvider));
}

@riverpod
DeleteLearningSet deleteLearningSet(Ref ref) {
  return DeleteLearningSet(ref.watch(learningSetRepositoryProvider));
}
