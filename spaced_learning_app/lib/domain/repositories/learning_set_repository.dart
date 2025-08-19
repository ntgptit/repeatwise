import 'package:spaced_learning_app/domain/models/learning_set.dart';

abstract class LearningSetRepository {
  Future<List<LearningSet>> getAllLearningSets();
  Future<LearningSet?> getLearningSetById(String id);
  Future<LearningSet> createLearningSet(LearningSet learningSet);
  Future<LearningSet> updateLearningSet(String id, LearningSet learningSet);
  Future<void> deleteLearningSet(String id);
  Future<List<LearningSet>> getLearningSetsByCategory(String category);
}
