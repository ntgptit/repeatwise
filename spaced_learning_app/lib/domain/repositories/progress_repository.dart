import 'package:spaced_learning_app/domain/models/progress.dart';

abstract class ProgressRepository {
  Future<List<ProgressSummary>> getAllProgress({int page = 0, int size = 20});

  Future<ProgressDetail> getProgressById(String id);

  Future<List<ProgressSummary>> getProgressByModuleId(
    String moduleId, {
    int page = 0,
    int size = 20,
  });

  Future<ProgressDetail?> getCurrentUserProgressByModule(String moduleId);

  Future<List<ProgressDetail>> getDueProgress(
    String userId, {
    DateTime? studyDate,
    int page = 0,
    int size = 20,
  });

  Future<ProgressDetail> createProgress({
    required String moduleId,
    String? userId, // Changed to optional
    DateTime? firstLearningDate,
    CycleStudied? cyclesStudied,
    DateTime? nextStudyDate,
    double? percentComplete,
  });

  Future<ProgressDetail> updateProgress(
    String id, {
    DateTime? firstLearningDate,
    CycleStudied? cyclesStudied,
    DateTime? nextStudyDate,
    double? percentComplete,
  });
}
