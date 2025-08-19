import 'package:spaced_learning_app/domain/models/learning_module.dart';

abstract class LearningDataService {
  Future<List<LearningModule>> getModules();

  List<LearningModule> filterByBook(List<LearningModule> modules, String book);

  List<LearningModule> filterByDate(
    List<LearningModule> modules,
    DateTime date,
  );

  bool isSameDay(DateTime date1, DateTime date2);

  int countDueModules(List<LearningModule> modules, {int daysThreshold = 7});

  int countCompletedModules(List<LearningModule> modules);

  List<String> getUniqueBooks(List<LearningModule> modules);

  Future<bool> exportData();

  int getActiveModulesCount(List<LearningModule> modules);

  List<LearningModule> getDueToday(List<LearningModule> modules);

  List<LearningModule> getDueThisWeek(List<LearningModule> modules);

  List<LearningModule> getDueThisMonth(List<LearningModule> modules);


  Future<Map<String, dynamic>> getDashboardStats({
    String? book,
    DateTime? date,
  });

  void resetCache();
}
