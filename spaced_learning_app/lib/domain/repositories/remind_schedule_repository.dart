import 'package:spaced_learning_app/domain/models/remind_schedule.dart';

abstract class RemindScheduleRepository {
  Future<List<RemindSchedule>> getAllRemindSchedules();
  Future<RemindSchedule?> getRemindScheduleById(String id);
  Future<RemindSchedule> createRemindSchedule(RemindSchedule remindSchedule);
  Future<RemindSchedule> updateRemindSchedule(
    String id,
    RemindSchedule remindSchedule,
  );
  Future<void> deleteRemindSchedule(String id);
  Future<List<RemindSchedule>> getRemindSchedulesByUser(String userId);
}
