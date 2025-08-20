import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';

class GetLearningSet {
  final LearningSetRepository _repository;
  GetLearningSet(this._repository);

  Future<LearningSet> call(String id) {
    return _repository.getById(id);
  }
}
