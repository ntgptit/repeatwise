import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:spaced_learning_app/core/services/reminder/device_specific_service.dart';

class NotificationChannelManager {
  final DeviceSpecificService _deviceSpecificService;

  static const String regularChannelId = 'spaced_learning_reminders';
  static const String importantChannelId =
      'spaced_learning_important_reminders';
  static const String alarmChannelId = 'spaced_learning_alarms';

  NotificationChannelManager(this._deviceSpecificService);

  Future<bool> createNotificationChannels(
    AndroidFlutterLocalNotificationsPlugin? androidPlugin,
  ) async {
    if (!_deviceSpecificService.isAndroid || androidPlugin == null) return true;

    try {
      // Create regular channel
      await androidPlugin.createNotificationChannel(
        const AndroidNotificationChannel(
          regularChannelId,
          'Learning Reminders',
          description: 'Reminders for your daily learning schedule',
          importance: Importance.high,
        ),
      );

      // Create important channel
      await androidPlugin.createNotificationChannel(
        const AndroidNotificationChannel(
          importantChannelId,
          'Important Reminders',
          description: 'Urgent reminders for unfinished learning tasks',
          importance: Importance.high,
          sound: RawResourceAndroidNotificationSound('notification_sound'),
        ),
      );

      // Create alarm channel
      await androidPlugin.createNotificationChannel(
        const AndroidNotificationChannel(
          alarmChannelId,
          'Learning Alarms',
          description: 'Alarm-style reminders for critical learning tasks',
          importance: Importance.max,
          sound: RawResourceAndroidNotificationSound('alarm_sound'),
          playSound: true,
          enableVibration: true,
        ),
      );

      // Create Samsung-specific channels if needed
      if (_deviceSpecificService.isSamsungDevice) {
        await _createSamsungSpecificChannels(androidPlugin);
      }

      return true;
    } catch (e) {
      debugPrint('Error creating notification channels: $e');
      return false;
    }
  }

  Future<bool> _createSamsungSpecificChannels(
    AndroidFlutterLocalNotificationsPlugin androidPlugin,
  ) async {
    try {
      await androidPlugin.createNotificationChannel(
        const AndroidNotificationChannel(
          'samsung_specific_channel',
          'Samsung Special Notifications',
          description: 'Optimized notifications for Samsung devices',
          importance: Importance.high,
        ),
      );
      return true;
    } catch (e) {
      debugPrint('Error creating Samsung notification channels: $e');
      return false;
    }
  }

  NotificationDetails getNotificationDetails({
    required bool isImportant,
    bool isAlarmStyle = false,
  }) {
    final androidDetails = AndroidNotificationDetails(
      isAlarmStyle
          ? alarmChannelId
          : (isImportant ? importantChannelId : regularChannelId),
      isAlarmStyle
          ? 'Learning Alarms'
          : (isImportant ? 'Important Reminders' : 'Learning Reminders'),
      channelDescription: isAlarmStyle
          ? 'Alarm-style reminders for critical learning tasks'
          : (isImportant
                ? 'Urgent reminders for unfinished learning tasks'
                : 'Reminders for your daily learning schedule'),
      importance: isAlarmStyle
          ? Importance.max
          : (isImportant ? Importance.max : Importance.high),
      priority: isAlarmStyle
          ? Priority.max
          : (isImportant ? Priority.max : Priority.high),
      category: isAlarmStyle
          ? AndroidNotificationCategory.alarm
          : (isImportant
                ? AndroidNotificationCategory.reminder
                : AndroidNotificationCategory.message),
      fullScreenIntent: isAlarmStyle,
      sound: isAlarmStyle
          ? const RawResourceAndroidNotificationSound('alarm_sound')
          : (isImportant
                ? const RawResourceAndroidNotificationSound(
                    'notification_sound',
                  )
                : null),
    );

    const iosDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    return NotificationDetails(android: androidDetails, iOS: iosDetails);
  }
}
