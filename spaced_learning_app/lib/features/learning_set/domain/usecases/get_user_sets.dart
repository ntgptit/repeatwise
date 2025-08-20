import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/repositories/learning_set_repository.dart';

class GetUserSets {
  final LearningSetRepository _repository;
  GetUserSets(this._repository);

  Future<Page<LearningSet>> call({int page = 0, int size = 20}) {
    return _repository.getUserSets(page, size);
  }
}
