import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/core/services/reminder/base_notification_service.dart';
import 'package:spaced_learning_app/core/services/reminder/notification_channel_manager.dart';

class ReminderNotificationService extends BaseNotificationService {
  final NotificationChannelManager _channelManager;

  static const int noonReminderId = 1001;
  static const int eveningFirstReminderId = 1002;
  static const int eveningSecondReminderId = 1003;
  static const int endOfDayReminderId = 1004;

  ReminderNotificationService({
    required super.deviceSpecificService,
    required super.notificationsPlugin,
  }) : _channelManager = NotificationChannelManager(deviceSpecificService);

  @override
  Future<bool> initialize() async {
    final initialized = await super.initialize();

    if (!initialized) return false;

    if (deviceSpecificService.isAndroid) {
      final androidPlugin = notificationsPlugin
          .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin
          >();

      if (!await _channelManager.createNotificationChannels(androidPlugin)) {
        debugPrint('Warning: Failed to create notification channels');
      }
    }

    if (!await _requestPermissions()) {
      debugPrint('Warning: Notification permissions not granted');
    }

    return true;
  }

  Future<bool> _requestPermissions() async {
    try {
      if (!deviceSpecificService.isAndroid) {
        final ios = notificationsPlugin
            .resolvePlatformSpecificImplementation<
              IOSFlutterLocalNotificationsPlugin
            >();

        if (ios == null) {
          return false;
        }

        final bool? result = await ios.requestPermissions(
          alert: true,
          badge: true,
          sound: true,
        );
        return result ?? false;
      }

      return true;
    } catch (e) {
      debugPrint('Error requesting notification permissions: $e');
      return false;
    }
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
    if (kIsWeb) {
      debugPrint('Scheduled notifications not supported on web platform');
      return false;
    }

    if (!isInitialized) {
      final bool initialized = await initialize();
      if (!initialized) return false;
    }

    if (!timezonesInitialized) {
      debugPrint('Timezones not initialized, cannot schedule notification');
      return false;
    }

    try {
      final notificationDetails = _channelManager.getNotificationDetails(
        isImportant: isImportant,
        isAlarmStyle: isAlarmStyle,
      );
      final tzScheduledTime = convertToTZDateTime(scheduledTime);

      await notificationsPlugin.zonedSchedule(
        id,
        title,
        body,
        tzScheduledTime,
        notificationDetails,
        androidScheduleMode: _getAndroidScheduleMode(isAlarmStyle),
        matchDateTimeComponents: DateTimeComponents.time,
        payload: payload,
      );

      debugPrint(
        'Notification scheduled with id: $id for time: $scheduledTime',
      );
      return true;
    } catch (e) {
      debugPrint('Error scheduling notification: $e');
      return false;
    }
  }

  AndroidScheduleMode _getAndroidScheduleMode(bool isAlarmStyle) {
    return isAlarmStyle
        ? AndroidScheduleMode.exactAllowWhileIdle
        : AndroidScheduleMode.exactAllowWhileIdle;
  }

  Future<bool> scheduleNoonReminder() async {
    final now = DateTime.now();
    final scheduledTime = _getScheduledTime(
      now,
      AppConstants.noonReminderHour,
      AppConstants.noonReminderMinute,
    );

    return scheduleNotification(
      id: noonReminderId,
      title: 'Check Your Learning Schedule',
      body: 'It\'s time to review today\'s learning schedule',
      scheduledTime: scheduledTime,
      isImportant: false,
    );
  }

  DateTime _getScheduledTime(DateTime now, int hour, int minute) {
    final scheduledTime = DateTime(now.year, now.month, now.day, hour, minute);

    return scheduledTime.isBefore(now)
        ? scheduledTime.add(const Duration(days: 1))
        : scheduledTime;
  }

  Future<bool> scheduleEveningFirstReminder() async {
    final now = DateTime.now();
    final scheduledTime = _getScheduledTime(
      now,
      AppConstants.eveningFirstReminderHour,
      AppConstants.eveningFirstReminderMinute,
    );

    return scheduleNotification(
      id: eveningFirstReminderId,
      title: 'Unfinished Learning Tasks',
      body: 'You still have some lessons to complete today',
      scheduledTime: scheduledTime,
      isImportant: true,
    );
  }

  Future<bool> scheduleEveningSecondReminder() async {
    final now = DateTime.now();
    final scheduledTime = _getScheduledTime(
      now,
      AppConstants.eveningSecondReminderHour,
      AppConstants.eveningSecondReminderMinute,
    );

    return scheduleNotification(
      id: eveningSecondReminderId,
      title: 'Tasks Still Pending!',
      body: 'Complete your learning before bedtime',
      scheduledTime: scheduledTime,
      isImportant: true,
    );
  }

  Future<bool> scheduleEndOfDayReminder({bool useAlarmStyle = false}) async {
    final now = DateTime.now();
    final scheduledTime = _getScheduledTime(
      now,
      AppConstants.endOfDayReminderHour,
      AppConstants.endOfDayReminderMinute,
    );

    return scheduleNotification(
      id: endOfDayReminderId,
      title: 'Don\'t Miss Today\'s Learning!',
      body: 'This is the final reminder for unfinished tasks',
      scheduledTime: scheduledTime,
      isImportant: true,
      isAlarmStyle: useAlarmStyle,
    );
  }
}
