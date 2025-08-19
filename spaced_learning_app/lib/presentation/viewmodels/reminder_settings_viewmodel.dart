// lib/presentation/viewmodels/reminder_settings_viewmodel.dart
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/events/app_events.dart';

import '../../core/di/providers.dart';

part 'reminder_settings_viewmodel.g.dart';

@riverpod
class ReminderSettingsState extends _$ReminderSettingsState {
  @override
  Future<Map<String, bool>> build() async {
    return _loadSettings();
  }

  Future<Map<String, bool>> _loadSettings() async {
    try {
      final reminderService = await ref.read(reminderServiceProvider.future);

      return {
        'remindersEnabled': await reminderService.getRemindersEnabled(),
        'noonReminderEnabled': await reminderService.getNoonReminderEnabled(),
        'eveningFirstReminderEnabled': await reminderService
            .getEveningFirstReminderEnabled(),
        'eveningSecondReminderEnabled': await reminderService
            .getEveningSecondReminderEnabled(),
        'endOfDayReminderEnabled': await reminderService
            .getEndOfDayReminderEnabled(),
      };
    } catch (e) {
      throw Exception('Failed to load reminder settings: $e');
    }
  }

  Future<bool> setRemindersEnabled(bool value) async {
    try {
      final reminderService = await ref.read(reminderServiceProvider.future);
      final success = await reminderService.setRemindersEnabled(value);

      if (success) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'remindersEnabled': value,
        });

        ref
            .read(eventBusProvider)
            .fire(ReminderSettingsChangedEvent(enabled: value));
      }

      return success;
    } catch (e) {
      return false;
    }
  }

  Future<bool> setNoonReminderEnabled(bool value) async {
    try {
      final reminderService = await ref.read(reminderServiceProvider.future);
      final success = await reminderService.setNoonReminderEnabled(value);

      if (success) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'noonReminderEnabled': value,
        });
      }

      return success;
    } catch (e) {
      return false;
    }
  }

  Future<bool> setEveningFirstReminderEnabled(bool value) async {
    try {
      final reminderService = await ref.read(reminderServiceProvider.future);
      final success = await reminderService.setEveningFirstReminderEnabled(
        value,
      );

      if (success) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'eveningFirstReminderEnabled': value,
        });
      }

      return success;
    } catch (e) {
      return false;
    }
  }

  Future<bool> setEveningSecondReminderEnabled(bool value) async {
    try {
      final reminderService = await ref.read(reminderServiceProvider.future);
      final success = await reminderService.setEveningSecondReminderEnabled(
        value,
      );

      if (success) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'eveningSecondReminderEnabled': value,
        });
      }

      return success;
    } catch (e) {
      return false;
    }
  }

  Future<bool> setEndOfDayReminderEnabled(bool value) async {
    try {
      final reminderService = await ref.read(reminderServiceProvider.future);
      final success = await reminderService.setEndOfDayReminderEnabled(value);

      if (success) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'endOfDayReminderEnabled': value,
        });
      }

      return success;
    } catch (e) {
      return false;
    }
  }

  Future<void> refreshSettings() async {
    state = const AsyncValue.loading();
    try {
      final settings = await _loadSettings();
      state = AsyncValue.data(settings);
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
    }
  }
}

@riverpod
class DevicePermissions extends _$DevicePermissions {
  @override
  Future<Map<String, dynamic>> build() async {
    return _loadDevicePermissions();
  }

  Future<Map<String, dynamic>> _loadDevicePermissions() async {
    try {
      final deviceSettingsService = ref.read(deviceSettingsServiceProvider);

      final hasExactAlarmPermission = await deviceSettingsService
          .hasExactAlarmPermission();
      final isIgnoringBatteryOptimizations = await deviceSettingsService
          .isIgnoringBatteryOptimizations();
      final deviceInfo = await deviceSettingsService.getDeviceInfo();

      return {
        'hasExactAlarmPermission': hasExactAlarmPermission,
        'isIgnoringBatteryOptimizations': isIgnoringBatteryOptimizations,
        'deviceInfo': deviceInfo,
      };
    } catch (e) {
      return {
        'hasExactAlarmPermission': false,
        'isIgnoringBatteryOptimizations': false,
        'deviceInfo': {
          'sdkVersion': 0,
          'manufacturer': 'Unknown',
          'model': 'Unknown',
        },
      };
    }
  }

  Future<bool> requestExactAlarmPermission() async {
    try {
      final deviceSettingsService = ref.read(deviceSettingsServiceProvider);
      final result = await deviceSettingsService.requestExactAlarmPermission();

      if (result) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'hasExactAlarmPermission': true,
        });
      }

      return result;
    } catch (e) {
      return false;
    }
  }

  Future<bool> requestBatteryOptimization() async {
    try {
      final deviceSettingsService = ref.read(deviceSettingsServiceProvider);
      final result = await deviceSettingsService.requestBatteryOptimization();

      if (result) {
        state = AsyncValue.data({
          ...state.valueOrNull ?? {},
          'isIgnoringBatteryOptimizations': true,
        });
      }

      return result;
    } catch (e) {
      return false;
    }
  }

  Future<bool> disableSleepingApps() async {
    try {
      final deviceSettingsService = ref.read(deviceSettingsServiceProvider);
      return await deviceSettingsService.disableSleepingApps();
    } catch (e) {
      return false;
    }
  }
}
