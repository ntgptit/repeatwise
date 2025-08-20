import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_create.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_update.dart';

abstract class LearningSetRepository {
  Future<Page<LearningSet>> getUserSets(int page, int size);
  Future<LearningSet> getById(String id);
  Future<LearningSet> create(LearningSetCreate request);
  Future<LearningSet> update(String id, LearningSetUpdate request);
  Future<void> delete(String id);
}
