import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/services/reminder/device_specific_service.dart';
import 'package:spaced_learning_app/core/services/reminder/reminder_notification_service.dart';

import '../../di/providers.dart';

part 'notification_service.g.dart';

class NotificationService {
  late final ReminderNotificationService _reminderService;

  NotificationService({required DeviceSpecificService deviceSpecificService}) {
    final notificationsPlugin = FlutterLocalNotificationsPlugin();
    _reminderService = ReminderNotificationService(
      deviceSpecificService: deviceSpecificService,
      notificationsPlugin: notificationsPlugin,
    );
  }

  // Constantes públicas que mantienen compatibilidad
  static const int noonReminderId = ReminderNotificationService.noonReminderId;
  static const int eveningFirstReminderId =
      ReminderNotificationService.eveningFirstReminderId;
  static const int eveningSecondReminderId =
      ReminderNotificationService.eveningSecondReminderId;
  static const int endOfDayReminderId =
      ReminderNotificationService.endOfDayReminderId;

  Future<bool> initialize() async {
    return _reminderService.initialize();
  }

  Future<bool> showNotification({
    required int id,
    required String title,
    required String body,
    String? payload,
    bool isImportant = false,
  }) async {
    return _reminderService.showNotification(
      id: id,
      title: title,
      body: body,
      payload: payload,
      isImportant: isImportant,
    );
  }

  Future<bool> scheduleNotification({
    required int id,
    required String title,
    required String body,
    required DateTime scheduledTime,
    String? payload,
    bool isImportant = false,
    bool isAlarmStyle = false,
  }) async {
    return _reminderService.scheduleNotification(
      id: id,
      title: title,
      body: body,
      scheduledTime: scheduledTime,
      payload: payload,
      isImportant: isImportant,
      isAlarmStyle: isAlarmStyle,
    );
  }

  Future<bool> cancelNotification(int id) async {
    return _reminderService.cancelNotification(id);
  }

  Future<bool> cancelAllNotifications() async {
    return _reminderService.cancelAllNotifications();
  }

  Future<bool> scheduleNoonReminder() async {
    return _reminderService.scheduleNoonReminder();
  }

  Future<bool> scheduleEveningFirstReminder() async {
    return _reminderService.scheduleEveningFirstReminder();
  }

  Future<bool> scheduleEveningSecondReminder() async {
    return _reminderService.scheduleEveningSecondReminder();
  }

  Future<bool> scheduleEndOfDayReminder({bool useAlarmStyle = false}) async {
    return _reminderService.scheduleEndOfDayReminder(
      useAlarmStyle: useAlarmStyle,
    );
  }

  bool get isInitialized => _reminderService.isInitialized;

  bool get timezonesInitialized => _reminderService.timezonesInitialized;
}

// Riverpod provider for notification status
@riverpod
class NotificationStatus extends _$NotificationStatus {
  @override
  Future<bool> build() async {
    final notificationService = await ref.watch(
      notificationServiceProvider.future,
    );
    return notificationService.isInitialized;
  }

  Future<void> initialize() async {
    state = const AsyncValue.loading();
    final notificationService = await ref.read(
      notificationServiceProvider.future,
    );
    state = await AsyncValue.guard(() async {
      return notificationService.initialize();
    });
  }

  Future<bool> scheduleReminder(ReminderType type) async {
    final notificationService = await ref.read(
      notificationServiceProvider.future,
    );
    switch (type) {
      case ReminderType.noon:
        return notificationService.scheduleNoonReminder();
      case ReminderType.eveningFirst:
        return notificationService.scheduleEveningFirstReminder();
      case ReminderType.eveningSecond:
        return notificationService.scheduleEveningSecondReminder();
      case ReminderType.endOfDay:
        return notificationService.scheduleEndOfDayReminder();
    }
  }

  Future<bool> cancelReminder(int id) async {
    final notificationService = await ref.read(
      notificationServiceProvider.future,
    );
    return notificationService.cancelNotification(id);
  }

  Future<bool> cancelAllReminders() async {
    final notificationService = await ref.read(
      notificationServiceProvider.future,
    );
    return notificationService.cancelAllNotifications();
  }
}

enum ReminderType { noon, eveningFirst, eveningSecond, endOfDay }
