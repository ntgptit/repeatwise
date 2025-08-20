import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';

class DeleteLearningSet {
  final LearningSetRepository _repository;
  DeleteLearningSet(this._repository);

  Future<void> call(String id) {
    return _repository.delete(id);
  }
}
