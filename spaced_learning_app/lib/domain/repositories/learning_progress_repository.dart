import 'package:spaced_learning_app/domain/models/learning_module.dart';

abstract class LearningProgressRepository {
  Future<List<LearningModule>> getAllModules();

  Future<List<LearningModule>> getDueModules(int daysThreshold);


  Future<List<String>> getUniqueBooks();

  Future<Map<String, dynamic>> exportData();


  Future<Map<String, dynamic>> getDashboardStats({
    String? book,
    DateTime? date,
  });
}
