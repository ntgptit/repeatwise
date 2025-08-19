import 'package:spaced_learning_app/domain/models/repetition.dart';

abstract class RepetitionRepository {
  Future<List<Repetition>> getRepetitionsByProgressId(String progressId);

  Future<List<Repetition>> createDefaultSchedule(String moduleProgressId);

  Future<Repetition> updateRepetition(
    String id, {
    RepetitionStatus? status,
    DateTime? reviewDate,
    bool rescheduleFollowing = false,
    double? percentComplete,
  });

  Future<int> countByModuleProgressId(String moduleProgressId);
}
