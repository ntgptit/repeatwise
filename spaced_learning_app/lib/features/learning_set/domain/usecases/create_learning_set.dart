import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_create.dart';
import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';

class CreateLearningSet {
  final LearningSetRepository _repository;
  CreateLearningSet(this._repository);

  Future<LearningSet> call(LearningSetCreate request) {
    return _repository.create(request);
  }
}
