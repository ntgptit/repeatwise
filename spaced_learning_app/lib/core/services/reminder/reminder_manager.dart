// lib/core/services/reminder/reminder_manager.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:spaced_learning_app/core/services/reminder/notification_service.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';

import '../../di/providers.dart';

part 'reminder_manager.g.dart';

@riverpod
class ReminderManager extends _$ReminderManager {
  static const String _enabledKey = 'reminders_enabled';
  static const String _noonReminderKey = 'noon_reminder_enabled';
  static const String _eveningFirstReminderKey =
      'evening_first_reminder_enabled';
  static const String _eveningSecondReminderKey =
      'evening_second_reminder_enabled';
  static const String _endOfDayReminderKey = 'end_of_day_reminder_enabled';

  bool _isInitialized = false;

  @override
  Future<Map<String, dynamic>> build() async {
    // Initialize on first build
    if (!_isInitialized) {
      await initialize();
    }

    return _loadCurrentSettings();
  }

  Future<Map<String, dynamic>> _loadCurrentSettings() async {
    final storageService = ref.read(storageServiceProvider);

    return {
      'remindersEnabled': await storageService.getBool(_enabledKey) ?? true,
      'noonReminderEnabled':
          await storageService.getBool(_noonReminderKey) ?? true,
      'eveningFirstReminderEnabled':
          await storageService.getBool(_eveningFirstReminderKey) ?? true,
      'eveningSecondReminderEnabled':
          await storageService.getBool(_eveningSecondReminderKey) ?? true,
      'endOfDayReminderEnabled':
          await storageService.getBool(_endOfDayReminderKey) ?? true,
      'isInitialized': _isInitialized,
    };
  }

  Future<bool> initialize() async {
    if (_isInitialized) return true;

    try {
      // Load preferences first
      await _loadPreferences();

      // Initialize notification service
      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      final bool notificationsInitialized = await notificationService
          .initialize();
      if (!notificationsInitialized) {
        debugPrint('Warning: Failed to initialize notification service');
      }

      // Initialize device specific service
      final deviceSpecificService = await ref.read(
        deviceSpecificServiceProvider.future,
      );
      final bool deviceServicesInitialized = await deviceSpecificService
          .initialize();
      if (!deviceServicesInitialized) {
        debugPrint('Warning: Failed to initialize device-specific services');
      }

      // Schedule reminders if needed
      final settings = state.valueOrNull ?? {};
      if (settings['remindersEnabled'] == true) {
        await scheduleAllReminders();
      }

      _isInitialized = true;
      state = AsyncValue.data(await _loadCurrentSettings());
      return true;
    } catch (e) {
      debugPrint('Error initializing ReminderManager: $e');
      state = AsyncValue.error(e, StackTrace.current);
      return false;
    }
  }

  Future<void> _loadPreferences() async {
    try {
      final _ = await SharedPreferences.getInstance();
      final storageService = ref.read(storageServiceProvider);

      final remindersEnabled =
          await storageService.getBool(_enabledKey) ?? true;
      final noonReminderEnabled =
          await storageService.getBool(_noonReminderKey) ?? true;
      final eveningFirstReminderEnabled =
          await storageService.getBool(_eveningFirstReminderKey) ?? true;
      final eveningSecondReminderEnabled =
          await storageService.getBool(_eveningSecondReminderKey) ?? true;
      final endOfDayReminderEnabled =
          await storageService.getBool(_endOfDayReminderKey) ?? true;

      debugPrint('Loaded reminder preferences:');
      debugPrint('- Reminders enabled: $remindersEnabled');
      debugPrint('- Noon reminder: $noonReminderEnabled');
      debugPrint('- Evening first: $eveningFirstReminderEnabled');
      debugPrint('- Evening second: $eveningSecondReminderEnabled');
      debugPrint('- End of day: $endOfDayReminderEnabled');
    } catch (e) {
      debugPrint('Error loading reminder preferences: $e');
    }
  }

  Future<bool> _savePreferences(Map<String, dynamic> settings) async {
    try {
      final storageService = ref.read(storageServiceProvider);
      await storageService.setBool(_enabledKey, settings['remindersEnabled']);
      await storageService.setBool(
        _noonReminderKey,
        settings['noonReminderEnabled'],
      );
      await storageService.setBool(
        _eveningFirstReminderKey,
        settings['eveningFirstReminderEnabled'],
      );
      await storageService.setBool(
        _eveningSecondReminderKey,
        settings['eveningSecondReminderEnabled'],
      );
      await storageService.setBool(
        _endOfDayReminderKey,
        settings['endOfDayReminderEnabled'],
      );
      return true;
    } catch (e) {
      debugPrint('Error saving reminder preferences: $e');
      return false;
    }
  }

  Future<bool> hasPendingTasksToday() async {
    try {
      final storageService = ref.read(storageServiceProvider);
      final userData = await storageService.getUserData();
      final userId = userData?['id'];

      if (userId == null) {
        debugPrint('Cannot check pending tasks: User not logged in');
        return false; // Not logged in
      }

      // Gọi đến ProgressState thay vì ProgressViewModel
      await ref
          .read(progressStateProvider.notifier)
          .loadDueProgress(userId.toString(), studyDate: DateTime.now());

      final progressRecords = ref.read(progressStateProvider).valueOrNull ?? [];
      final hasTasks = progressRecords.isNotEmpty;
      debugPrint('Pending tasks check: ${hasTasks ? 'Has tasks' : 'No tasks'}');
      return hasTasks;
    } catch (e) {
      debugPrint('Error checking pending tasks: $e');
      return false; // Assume no tasks on error
    }
  }

  Future<bool> scheduleAllReminders() async {
    try {
      if (!_isInitialized) {
        final bool initialized = await initialize();
        if (!initialized) {
          debugPrint('Failed to initialize ReminderManager');
          return false;
        }
      }

      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      await notificationService.cancelAllNotifications();

      final settings = state.valueOrNull ?? {};
      final remindersEnabled = settings['remindersEnabled'] ?? true;

      if (!remindersEnabled) {
        debugPrint('Reminders are disabled, not scheduling any reminders');
        return true;
      }

      // Schedule noon reminder if enabled
      final noonReminderEnabled = settings['noonReminderEnabled'] ?? true;
      if (noonReminderEnabled) {
        final bool noonResult = await notificationService
            .scheduleNoonReminder();
        debugPrint('Noon reminder scheduled: $noonResult');
      }

      // Check if there are pending tasks today
      final hasPendingTasks = await hasPendingTasksToday();

      if (hasPendingTasks) {
        // Schedule evening reminders if enabled and there are tasks
        final eveningFirstReminderEnabled =
            settings['eveningFirstReminderEnabled'] ?? true;
        if (eveningFirstReminderEnabled) {
          final bool eveningFirstResult = await notificationService
              .scheduleEveningFirstReminder();
          debugPrint('Evening first reminder scheduled: $eveningFirstResult');
        }

        final eveningSecondReminderEnabled =
            settings['eveningSecondReminderEnabled'] ?? true;
        if (eveningSecondReminderEnabled) {
          final bool eveningSecondResult = await notificationService
              .scheduleEveningSecondReminder();
          debugPrint('Evening second reminder scheduled: $eveningSecondResult');
        }

        final endOfDayReminderEnabled =
            settings['endOfDayReminderEnabled'] ?? true;
        if (endOfDayReminderEnabled) {
          final deviceSpecificService = await ref.read(
            deviceSpecificServiceProvider.future,
          );
          final useAlarmStyle =
              deviceSpecificService.isAndroid &&
              (deviceSpecificService.isSamsungDevice ||
                  deviceSpecificService.sdkVersion >= 26);

          final bool endOfDayResult = await notificationService
              .scheduleEndOfDayReminder(useAlarmStyle: useAlarmStyle);
          debugPrint(
            'End of day reminder scheduled: $endOfDayResult (alarm style: $useAlarmStyle)',
          );
        }
      } else {
        debugPrint(
          'No pending tasks today, skipping evening and end-of-day reminders',
        );
      }

      return true;
    } catch (e) {
      debugPrint('Error scheduling reminders: $e');
      return false;
    }
  }

  Future<bool> updateRemindersAfterTaskCompletion() async {
    try {
      final stillHasPendingTasks = await hasPendingTasksToday();

      if (!stillHasPendingTasks) {
        debugPrint(
          'All tasks completed, cancelling evening and end-of-day reminders',
        );

        final notificationService = await ref.read(
          notificationServiceProvider.future,
        );
        await notificationService.cancelNotification(
          NotificationService.eveningFirstReminderId,
        );
        await notificationService.cancelNotification(
          NotificationService.eveningSecondReminderId,
        );
        await notificationService.cancelNotification(
          NotificationService.endOfDayReminderId,
        );
      }
      return true;
    } catch (e) {
      debugPrint('Error updating reminders after task completion: $e');
      return false;
    }
  }

  Future<bool> setRemindersEnabled(bool value) async {
    final currentSettings = state.valueOrNull ?? {};
    if (currentSettings['remindersEnabled'] == value) return true;

    final updatedSettings = {...currentSettings, 'remindersEnabled': value};
    final bool saved = await _savePreferences(updatedSettings);
    if (!saved) return false;

    if (value) {
      final result = await scheduleAllReminders();
      state = AsyncValue.data(updatedSettings);
      return result;
    } else {
      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      final result = await notificationService.cancelAllNotifications();
      state = AsyncValue.data(updatedSettings);
      return result;
    }
  }

  Future<bool> setNoonReminderEnabled(bool value) async {
    final currentSettings = state.valueOrNull ?? {};
    if (currentSettings['noonReminderEnabled'] == value) return true;

    final updatedSettings = {...currentSettings, 'noonReminderEnabled': value};
    final bool saved = await _savePreferences(updatedSettings);
    if (!saved) return false;

    if (currentSettings['remindersEnabled'] == true) {
      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      if (value) {
        final result = await notificationService.scheduleNoonReminder();
        state = AsyncValue.data(updatedSettings);
        return result;
      } else {
        final result = await notificationService.cancelNotification(
          NotificationService.noonReminderId,
        );
        state = AsyncValue.data(updatedSettings);
        return result;
      }
    }

    state = AsyncValue.data(updatedSettings);
    return true;
  }

  Future<bool> setEveningFirstReminderEnabled(bool value) async {
    final currentSettings = state.valueOrNull ?? {};
    if (currentSettings['eveningFirstReminderEnabled'] == value) return true;

    final updatedSettings = {
      ...currentSettings,
      'eveningFirstReminderEnabled': value,
    };
    final bool saved = await _savePreferences(updatedSettings);
    if (!saved) return false;

    if (currentSettings['remindersEnabled'] == true) {
      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      if (value && await hasPendingTasksToday()) {
        final result = await notificationService.scheduleEveningFirstReminder();
        state = AsyncValue.data(updatedSettings);
        return result;
      } else {
        final result = await notificationService.cancelNotification(
          NotificationService.eveningFirstReminderId,
        );
        state = AsyncValue.data(updatedSettings);
        return result;
      }
    }

    state = AsyncValue.data(updatedSettings);
    return true;
  }

  Future<bool> setEveningSecondReminderEnabled(bool value) async {
    final currentSettings = state.valueOrNull ?? {};
    if (currentSettings['eveningSecondReminderEnabled'] == value) return true;

    final updatedSettings = {
      ...currentSettings,
      'eveningSecondReminderEnabled': value,
    };
    final bool saved = await _savePreferences(updatedSettings);
    if (!saved) return false;

    if (currentSettings['remindersEnabled'] == true) {
      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      if (value && await hasPendingTasksToday()) {
        final result = await notificationService
            .scheduleEveningSecondReminder();
        state = AsyncValue.data(updatedSettings);
        return result;
      } else {
        final result = await notificationService.cancelNotification(
          NotificationService.eveningSecondReminderId,
        );
        state = AsyncValue.data(updatedSettings);
        return result;
      }
    }

    state = AsyncValue.data(updatedSettings);
    return true;
  }

  Future<bool> setEndOfDayReminderEnabled(bool value) async {
    final currentSettings = state.valueOrNull ?? {};
    if (currentSettings['endOfDayReminderEnabled'] == value) return true;

    final updatedSettings = {
      ...currentSettings,
      'endOfDayReminderEnabled': value,
    };
    final bool saved = await _savePreferences(updatedSettings);
    if (!saved) return false;

    if (currentSettings['remindersEnabled'] == true) {
      final notificationService = await ref.read(
        notificationServiceProvider.future,
      );
      if (value && await hasPendingTasksToday()) {
        final result = await notificationService.scheduleEndOfDayReminder();
        state = AsyncValue.data(updatedSettings);
        return result;
      } else {
        final result = await notificationService.cancelNotification(
          NotificationService.endOfDayReminderId,
        );
        state = AsyncValue.data(updatedSettings);
        return result;
      }
    }

    state = AsyncValue.data(updatedSettings);
    return true;
  }
}

// Convenience providers for accessing reminder settings
@riverpod
bool isReminderInitialized(Ref ref) {
  final settings = ref.watch(reminderManagerProvider).valueOrNull;
  return settings?['isInitialized'] ?? false;
}

@riverpod
bool areRemindersEnabled(Ref ref) {
  final settings = ref.watch(reminderManagerProvider).valueOrNull;
  return settings?['remindersEnabled'] ?? true;
}

@riverpod
bool isNoonReminderEnabled(Ref ref) {
  final settings = ref.watch(reminderManagerProvider).valueOrNull;
  return settings?['noonReminderEnabled'] ?? true;
}

@riverpod
bool isEveningFirstReminderEnabled(Ref ref) {
  final settings = ref.watch(reminderManagerProvider).valueOrNull;
  return settings?['eveningFirstReminderEnabled'] ?? true;
}

@riverpod
bool isEveningSecondReminderEnabled(Ref ref) {
  final settings = ref.watch(reminderManagerProvider).valueOrNull;
  return settings?['eveningSecondReminderEnabled'] ?? true;
}

@riverpod
bool isEndOfDayReminderEnabled(Ref ref) {
  final settings = ref.watch(reminderManagerProvider).valueOrNull;
  return settings?['endOfDayReminderEnabled'] ?? true;
}
