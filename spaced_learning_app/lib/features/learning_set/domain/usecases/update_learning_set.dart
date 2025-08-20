import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_update.dart';
import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';

class UpdateLearningSet {
  final LearningSetRepository _repository;
  UpdateLearningSet(this._repository);

  Future<LearningSet> call(String id, LearningSetUpdate request) {
    return _repository.update(id, request);
  }
}
