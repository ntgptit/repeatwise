class ProgressChangedEvent {
  final String userId;
  final bool hasDueTasks;

  ProgressChangedEvent({required this.userId, this.hasDueTasks = false});
}

class TaskCompletedEvent {
  final String userId;
  final String progressId;

  TaskCompletedEvent({required this.userId, required this.progressId});
}

class ReminderSettingsChangedEvent {
  final bool enabled;

  ReminderSettingsChangedEvent({required this.enabled});
}

class DailyTaskCheckEvent {
  final DateTime checkTime;
  final bool hasDueTasks;
  final int taskCount;
  final String userId;
  final bool isSuccess;
  final String? errorMessage;

  DailyTaskCheckEvent({
    required this.checkTime,
    required this.hasDueTasks,
    required this.taskCount,
    required this.userId,
    required this.isSuccess,
    this.errorMessage,
  });
}
