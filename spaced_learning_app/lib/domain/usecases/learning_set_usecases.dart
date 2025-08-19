import '../repositories/learning_set_repository.dart';
import '../models/learning_set.dart';

/// Use case for getting all learning sets
class GetLearningSetsUseCase {
  final LearningSetRepository _repository;

  GetLearningSetsUseCase(this._repository);

  Future<List<LearningSet>> execute() async {
    return await _repository.getAllLearningSets();
  }
}

/// Use case for getting learning set by ID
class GetLearningSetByIdUseCase {
  final LearningSetRepository _repository;

  GetLearningSetByIdUseCase(this._repository);

  Future<LearningSet?> execute(String id) async {
    return await _repository.getLearningSetById(id);
  }
}

/// Use case for creating a new learning set
class CreateLearningSetUseCase {
  final LearningSetRepository _repository;

  CreateLearningSetUseCase(this._repository);

  Future<LearningSet> execute(LearningSet learningSet) async {
    return await _repository.createLearningSet(learningSet);
  }
}

/// Use case for updating a learning set
class UpdateLearningSetUseCase {
  final LearningSetRepository _repository;

  UpdateLearningSetUseCase(this._repository);

  Future<LearningSet> execute(String id, LearningSet learningSet) async {
    return await _repository.updateLearningSet(id, learningSet);
  }
}

/// Use case for deleting a learning set
class DeleteLearningSetUseCase {
  final LearningSetRepository _repository;

  DeleteLearningSetUseCase(this._repository);

  Future<void> execute(String id) async {
    await _repository.deleteLearningSet(id);
  }
}
