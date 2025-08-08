import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../../../../core/services/api_repository.dart';
import '../data/repositories/set_repository_impl.dart';
import '../domain/repositories/set_repository.dart';
import '../domain/usecases/usecases.dart';

part 'set_dependencies.g.dart';

@riverpod
SetRepository setRepository(Ref ref) {
  final apiRepository = ref.watch(apiRepositoryProvider);
  return SetRepositoryImpl(apiRepository);
}

@riverpod
GetSetsUseCase getSetsUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return GetSetsUseCase(repository);
}

@riverpod
GetSetByIdUseCase getSetByIdUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return GetSetByIdUseCase(repository);
}

@riverpod
CreateSetUseCase createSetUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return CreateSetUseCase(repository);
}

@riverpod
UpdateSetUseCase updateSetUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return UpdateSetUseCase(repository);
}

@riverpod
DeleteSetUseCase deleteSetUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return DeleteSetUseCase(repository);
}

@riverpod
StartLearningUseCase startLearningUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return StartLearningUseCase(repository);
}

@riverpod
MarkAsMasteredUseCase markAsMasteredUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return MarkAsMasteredUseCase(repository);
}

@riverpod
GetDailyReviewSetsUseCase getDailyReviewSetsUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return GetDailyReviewSetsUseCase(repository);
}

@riverpod
GetSetStatisticsUseCase getSetStatisticsUseCase(Ref ref) {
  final repository = ref.watch(setRepositoryProvider);
  return GetSetStatisticsUseCase(repository);
}
