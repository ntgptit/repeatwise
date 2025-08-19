import 'package:android_alarm_manager_plus/android_alarm_manager_plus.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/core/services/reminder/device_specific_service.dart';

import '../../di/providers.dart';

part 'alarm_manager_service.g.dart';

@pragma('vm:entry-point')
void noonReminderCallback() {
  _triggerBackgroundCheck(AppConstants.noonReminderCallbackId);
}

@pragma('vm:entry-point')
void eveningFirstReminderCallback() {
  _triggerBackgroundCheck(AppConstants.eveningFirstReminderCallbackId);
}

@pragma('vm:entry-point')
void eveningSecondReminderCallback() {
  _triggerBackgroundCheck(AppConstants.eveningSecondReminderCallbackId);
}

@pragma('vm:entry-point')
void endOfDayReminderCallback() {
  _triggerBackgroundCheck(AppConstants.endOfDayReminderCallbackId);
}

@pragma('vm:entry-point')
Future<void> _triggerBackgroundCheck(int callbackId) async {
  try {
    final prefs = await SharedPreferences.getInstance();

    await prefs.setInt('last_triggered_callback', callbackId);
    await prefs.setInt(
      'last_triggered_time',
      DateTime.now().millisecondsSinceEpoch,
    );

    debugPrint('Alarm callback triggered: $callbackId at ${DateTime.now()}');
  } catch (e) {
    debugPrint(
      'Error in _triggerBackgroundCheck: $e',
    ); // Use print since debugPrint might not work in background
  }
}

class AlarmManagerService {
  final DeviceSpecificService _deviceSpecificService;
  bool _isInitialized = false;

  AlarmManagerService({required DeviceSpecificService deviceSpecificService})
    : _deviceSpecificService = deviceSpecificService;

  Future<bool> initialize() async {
    if (_isInitialized) return true;

    try {
      if (!_deviceSpecificService.isAndroid) {
        debugPrint('Not on Android, using local notifications only');
        _isInitialized = true;
        return true;
      }

      final bool initialized = await AndroidAlarmManager.initialize();
      if (!initialized) {
        debugPrint('Failed to initialize AndroidAlarmManager');
        return false;
      }

      debugPrint('AlarmManager initialized successfully');

      if (_deviceSpecificService.sdkVersion >= 31) {
        final bool permissionGranted = await _requestExactAlarmPermission();
        debugPrint('Exact alarm permission granted: $permissionGranted');
      }

      _isInitialized = true;
      return true;
    } catch (e) {
      debugPrint('Error initializing AlarmManager: $e');
      debugPrint('Falling back to local notifications only');
      return false;
    }
  }

  Future<bool> _requestExactAlarmPermission() async {
    try {
      const methodChannel = MethodChannel('com.yourapp.device/optimization');
      final bool result = await methodChannel.invokeMethod(
        'requestExactAlarmPermission',
      );
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error requesting exact alarm permission: ${e.message}');
      return false;
    } catch (e) {
      debugPrint('Unexpected error requesting exact alarm permission: $e');
      return false;
    }
  }

  Future<bool> scheduleFixedTimeAlarms() async {
    if (!await _ensureInitialized()) {
      return false;
    }

    if (!_deviceSpecificService.isAndroid) {
      debugPrint('Not on Android, cannot schedule alarms');
      return false;
    }

    try {
      final List<bool> results = await Future.wait([
        _scheduleNoonReminder(),
        _scheduleEveningFirstReminder(),
        _scheduleEveningSecondReminder(),
        _scheduleEndOfDayReminder(),
      ]);

      final bool allSuccessful = results.every((result) => result);
      debugPrint(
        'All fixed-time alarms scheduled successfully: $allSuccessful',
      );
      return allSuccessful;
    } catch (e) {
      debugPrint('Error scheduling fixed-time alarms: $e');
      return false;
    }
  }

  Future<bool> _ensureInitialized() async {
    if (_isInitialized) return true;

    final bool initialized = await initialize();
    return initialized;
  }

  Future<bool> _scheduleNoonReminder() async {
    try {
      final alarmTime = _createTime(
        AppConstants.noonReminderHour,
        AppConstants.noonReminderMinute,
      );

      final bool result = await AndroidAlarmManager.periodic(
        const Duration(days: 1),
        AppConstants.noonReminderCallbackId,
        noonReminderCallback,
        startAt: alarmTime,
        exact: true,
        wakeup: true,
        rescheduleOnReboot: true,
      );

      debugPrint('Noon reminder scheduled: $result at $alarmTime');
      return result;
    } catch (e) {
      debugPrint('Error scheduling noon reminder: $e');
      return false;
    }
  }

  Future<bool> _scheduleEveningFirstReminder() async {
    try {
      final alarmTime = _createTime(
        AppConstants.eveningFirstReminderHour,
        AppConstants.eveningFirstReminderMinute,
      );

      final bool result = await AndroidAlarmManager.periodic(
        const Duration(days: 1),
        AppConstants.eveningFirstReminderCallbackId,
        eveningFirstReminderCallback,
        startAt: alarmTime,
        exact: true,
        wakeup: true,
        rescheduleOnReboot: true,
      );

      debugPrint('Evening first reminder scheduled: $result at $alarmTime');
      return result;
    } catch (e) {
      debugPrint('Error scheduling evening first reminder: $e');
      return false;
    }
  }

  Future<bool> _scheduleEveningSecondReminder() async {
    try {
      final alarmTime = _createTime(
        AppConstants.eveningSecondReminderHour,
        AppConstants.eveningSecondReminderMinute,
      );

      final bool result = await AndroidAlarmManager.periodic(
        const Duration(days: 1),
        AppConstants.eveningSecondReminderCallbackId,
        eveningSecondReminderCallback,
        startAt: alarmTime,
        exact: true,
        wakeup: true,
        rescheduleOnReboot: true,
      );

      debugPrint('Evening second reminder scheduled: $result at $alarmTime');
      return result;
    } catch (e) {
      debugPrint('Error scheduling evening second reminder: $e');
      return false;
    }
  }

  Future<bool> _scheduleEndOfDayReminder() async {
    try {
      final alarmTime = _createTime(
        AppConstants.endOfDayReminderHour,
        AppConstants.endOfDayReminderMinute,
      );

      final bool result = await AndroidAlarmManager.periodic(
        const Duration(days: 1),
        AppConstants.endOfDayReminderCallbackId,
        endOfDayReminderCallback,
        startAt: alarmTime,
        exact: true,
        wakeup: true,
        rescheduleOnReboot: true,
      );

      debugPrint('End-of-day reminder scheduled: $result at $alarmTime');
      return result;
    } catch (e) {
      debugPrint('Error scheduling end-of-day reminder: $e');
      return false;
    }
  }

  DateTime _createTime(int hour, int minute) {
    final now = DateTime.now();
    var scheduledTime = DateTime(now.year, now.month, now.day, hour, minute);

    if (scheduledTime.isBefore(now)) {
      scheduledTime = scheduledTime.add(const Duration(days: 1));
    }

    return scheduledTime;
  }

  Future<bool> cancelAlarm(int id) async {
    try {
      if (!_deviceSpecificService.isAndroid) {
        debugPrint('Not on Android, cannot cancel alarm');
        return false;
      }

      final bool result = await AndroidAlarmManager.cancel(id);
      debugPrint('Alarm $id cancelled: $result');
      return result;
    } catch (e) {
      debugPrint('Error cancelling alarm $id: $e');
      return false;
    }
  }

  Future<bool> cancelAllAlarms() async {
    try {
      if (!_deviceSpecificService.isAndroid) {
        debugPrint('Not on Android, cannot cancel alarms');
        return false;
      }

      final List<bool> results = await Future.wait([
        cancelAlarm(AppConstants.noonReminderCallbackId),
        cancelAlarm(AppConstants.eveningFirstReminderCallbackId),
        cancelAlarm(AppConstants.eveningSecondReminderCallbackId),
        cancelAlarm(AppConstants.endOfDayReminderCallbackId),
      ]);

      final bool allSuccessful = results.every((result) => result);
      debugPrint('All alarms cancelled: $allSuccessful');
      return allSuccessful;
    } catch (e) {
      debugPrint('Error cancelling all alarms: $e');
      return false;
    }
  }

  Future<int?> getLastTriggeredAlarmId() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      return prefs.getInt('last_triggered_callback');
    } catch (e) {
      debugPrint('Error getting last triggered alarm ID: $e');
      return null;
    }
  }

  Future<DateTime?> getLastTriggeredTime() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final timestamp = prefs.getInt('last_triggered_time');

      if (timestamp != null) {
        return DateTime.fromMillisecondsSinceEpoch(timestamp);
      }

      return null;
    } catch (e) {
      debugPrint('Error getting last triggered time: $e');
      return null;
    }
  }

  Future<bool> clearLastTriggeredData() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove('last_triggered_callback');
      await prefs.remove('last_triggered_time');
      return true;
    } catch (e) {
      debugPrint('Error clearing last triggered data: $e');
      return false;
    }
  }

  bool get isInitialized => _isInitialized;
}

@riverpod
class AlarmStatus extends _$AlarmStatus {
  @override
  Future<Map<String, dynamic>> build() async {
    final alarmManager = ref.watch(alarmManagerServiceProvider);

    final Map<String, dynamic> status = {
      'isInitialized': alarmManager.isInitialized,
    };

    // If initialized, get the latest alarm data
    if (alarmManager.isInitialized) {
      final lastTriggeredId = await alarmManager.getLastTriggeredAlarmId();
      final lastTriggeredTime = await alarmManager.getLastTriggeredTime();

      status['lastTriggeredId'] = lastTriggeredId;
      status['lastTriggeredTime'] = lastTriggeredTime;
    }

    return status;
  }

  Future<bool> initialize() async {
    final alarmManager = ref.read(alarmManagerServiceProvider);
    final result = await alarmManager.initialize();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> scheduleFixedTimeAlarms() async {
    final alarmManager = ref.read(alarmManagerServiceProvider);
    final result = await alarmManager.scheduleFixedTimeAlarms();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> cancelAllAlarms() async {
    final alarmManager = ref.read(alarmManagerServiceProvider);
    final result = await alarmManager.cancelAllAlarms();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> clearLastTriggeredData() async {
    final alarmManager = ref.read(alarmManagerServiceProvider);
    final result = await alarmManager.clearLastTriggeredData();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }
}
