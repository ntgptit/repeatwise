import 'package:riverpod_annotation/riverpod_annotation.dart';
import '../../../../core/services/api_repository.dart';
import '../data/repositories/set_repository_impl.dart';
import '../domain/repositories/set_repository.dart';
import '../domain/usecases/usecases.dart';

part 'set_dependencies.g.dart';

@riverpod
SetRepository setRepository(SetRepositoryRef ref) {
  final apiRepository = ref.watch(apiRepositoryProvider);
  return SetRepositoryImpl(apiRepository);
}

@riverpod
GetSetsUseCase getSetsUseCase(GetSetsUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return GetSetsUseCase(repository);
}

@riverpod
GetSetByIdUseCase getSetByIdUseCase(GetSetByIdUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return GetSetByIdUseCase(repository);
}

@riverpod
CreateSetUseCase createSetUseCase(CreateSetUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return CreateSetUseCase(repository);
}

@riverpod
UpdateSetUseCase updateSetUseCase(UpdateSetUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return UpdateSetUseCase(repository);
}

@riverpod
DeleteSetUseCase deleteSetUseCase(DeleteSetUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return DeleteSetUseCase(repository);
}

@riverpod
StartLearningUseCase startLearningUseCase(StartLearningUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return StartLearningUseCase(repository);
}

@riverpod
MarkAsMasteredUseCase markAsMasteredUseCase(MarkAsMasteredUseCaseRef ref) {
  final repository = ref.watch(setRepositoryProvider);
  return MarkAsMasteredUseCase(repository);
}

@riverpod
GetDailyReviewSetsUseCase getDailyReviewSetsUseCase(
  GetDailyReviewSetsUseCaseRef ref,
) {
  final repository = ref.watch(setRepositoryProvider);
  return GetDailyReviewSetsUseCase(repository);
}

@riverpod
GetSetStatisticsUseCase getSetStatisticsUseCase(
  GetSetStatisticsUseCaseRef ref,
) {
  final repository = ref.watch(setRepositoryProvider);
  return GetSetStatisticsUseCase(repository);
}
