// Không cần thay đổi nhiều, nhưng đã cập nhật imports
import 'package:spaced_learning_app/core/services/learning_data_service.dart';
import 'package:spaced_learning_app/domain/models/learning_module.dart';
import 'package:spaced_learning_app/domain/repositories/learning_progress_repository.dart';

class LearningDataServiceImpl implements LearningDataService {
  final LearningProgressRepository _repository;

  List<LearningModule>? _cachedModules;

  LearningDataServiceImpl(this._repository);

  @override
  Future<List<LearningModule>> getModules() async {
    if (_cachedModules != null) {
      return _cachedModules!;
    }

    try {
      final modules = await _repository.getAllModules();
      _cachedModules = modules;
      return modules;
    } catch (e) {
      return [];
    }
  }

  @override
  List<LearningModule> filterByBook(List<LearningModule> modules, String book) {
    if (book == 'All') {
      return modules;
    }
    return modules.where((module) => module.bookName == book).toList();
  }

  @override
  List<LearningModule> filterByDate(
    List<LearningModule> modules,
    DateTime date,
  ) {
    return modules
        .where(
          (module) =>
              module.progressNextStudyDate != null &&
              isSameDay(module.progressNextStudyDate!, date),
        )
        .toList();
  }

  @override
  bool isSameDay(DateTime date1, DateTime date2) {
    return date1.year == date2.year &&
        date1.month == date2.month &&
        date1.day == date2.day;
  }

  @override
  int countDueModules(List<LearningModule> modules, {int daysThreshold = 7}) {
    final today = DateTime.now();
    final dueDate = today.add(Duration(days: daysThreshold));

    return modules
        .where(
          (module) =>
              module.progressNextStudyDate != null &&
              module.progressNextStudyDate!.isAfter(
                today.subtract(const Duration(days: 1)),
              ) &&
              module.progressNextStudyDate!.isBefore(dueDate),
        )
        .length;
  }

  @override
  int countCompletedModules(List<LearningModule> modules) {
    return modules
        .where((module) => (module.progressLatestPercentComplete ?? 0) == 100)
        .length;
  }

  @override
  List<String> getUniqueBooks(List<LearningModule> modules) {
    if (modules.isEmpty) return ['All'];
    final books = modules.map((module) => module.bookName).toSet().toList()
      ..sort();
    return ['All', ...books];
  }

  @override
  Future<bool> exportData() async {
    try {
      final result = await _repository.exportData();
      return result.isNotEmpty && (result['success'] == true);
    } catch (e) {
      return false;
    }
  }

  @override
  int getActiveModulesCount(List<LearningModule> modules) {
    return modules
        .where((module) => (module.progressLatestPercentComplete ?? 0) < 100)
        .length;
  }

  @override
  List<LearningModule> getDueToday(List<LearningModule> modules) {
    final today = DateTime.now();
    return modules
        .where(
          (module) =>
              module.progressNextStudyDate != null &&
              isSameDay(module.progressNextStudyDate!, today),
        )
        .toList();
  }

  @override
  List<LearningModule> getDueThisWeek(List<LearningModule> modules) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final weekStart = today.subtract(Duration(days: today.weekday - 1));
    final weekEnd = weekStart.add(const Duration(days: 6));

    return modules
        .where(
          (module) =>
              module.progressNextStudyDate != null &&
              module.progressNextStudyDate!.isAfter(
                weekStart.subtract(const Duration(days: 1)),
              ) &&
              module.progressNextStudyDate!.isBefore(
                weekEnd.add(const Duration(days: 1)),
              ),
        )
        .toList();
  }

  @override
  List<LearningModule> getDueThisMonth(List<LearningModule> modules) {
    final now = DateTime.now();
    final monthStart = DateTime(now.year, now.month, 1);
    final monthEnd = (now.month < 12)
        ? DateTime(now.year, now.month + 1, 0)
        : DateTime(now.year + 1, 1, 0);

    return modules
        .where(
          (module) =>
              module.progressNextStudyDate != null &&
              module.progressNextStudyDate!.isAfter(
                monthStart.subtract(const Duration(days: 1)),
              ) &&
              module.progressNextStudyDate!.isBefore(
                monthEnd.add(const Duration(days: 1)),
              ),
        )
        .toList();
  }

  @override
  Future<Map<String, dynamic>> getDashboardStats({
    String? book,
    DateTime? date,
  }) async {
    try {
      return await _repository.getDashboardStats(book: book, date: date);
    } catch (e) {
      return {};
    }
  }

  @override
  void resetCache() {
    _cachedModules = null;
  }
}
