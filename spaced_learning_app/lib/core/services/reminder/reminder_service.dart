import 'dart:async';

import 'package:event_bus/event_bus.dart';
import 'package:flutter/material.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/events/app_events.dart';
import 'package:spaced_learning_app/core/services/reminder/device_specific_service.dart';
import 'package:spaced_learning_app/core/services/reminder/notification_service.dart';
import 'package:spaced_learning_app/core/services/storage_service.dart';
import 'package:spaced_learning_app/domain/repositories/progress_repository.dart';

import '../../di/providers.dart';

part 'reminder_service.g.dart';

class ReminderService {
  final NotificationService _notificationService;
  final StorageService _storageService;
  final DeviceSpecificService _deviceSpecificService;
  final ProgressRepository _progressRepository;
  final EventBus _eventBus;

  final List<StreamSubscription> _subscriptions = [];

  bool _isInitialized = false;
  bool _isScheduling = false;

  ReminderService({
    required NotificationService notificationService,
    required StorageService storageService,
    required DeviceSpecificService deviceSpecificService,
    required ProgressRepository progressRepository,
    required EventBus eventBus,
  }) : _notificationService = notificationService,
       _storageService = storageService,
       _deviceSpecificService = deviceSpecificService,
       _progressRepository = progressRepository,
       _eventBus = eventBus {
    _listenToEvents();
  }

  void _listenToEvents() {
    _subscriptions.add(
      _eventBus.on<ProgressChangedEvent>().listen(_handleProgressChanged),
    );

    _subscriptions.add(
      _eventBus.on<TaskCompletedEvent>().listen(_handleTaskCompleted),
    );

    _subscriptions.add(
      _eventBus.on<ReminderSettingsChangedEvent>().listen(
        _handleSettingsChanged,
      ),
    );
  }

  void dispose() {
    for (var subscription in _subscriptions) {
      subscription.cancel();
    }
    _subscriptions.clear();
  }

  void _handleProgressChanged(ProgressChangedEvent event) {
    if (event.hasDueTasks) {
      scheduleAllReminders();
    }
  }

  void _handleTaskCompleted(TaskCompletedEvent event) {
    updateRemindersAfterTaskCompletion();
  }

  void _handleSettingsChanged(ReminderSettingsChangedEvent event) {
    if (event.enabled) {
      scheduleAllReminders();
    } else {
      _notificationService.cancelAllNotifications();
    }
  }

  Future<bool> initialize() async {
    if (_isInitialized) return true;

    try {
      await _loadPreferences();

      final bool notificationsInitialized = await _notificationService
          .initialize();
      if (!notificationsInitialized) {
        debugPrint('Warning: Failed to initialize notification service');
      }

      final bool deviceServicesInitialized = await _deviceSpecificService
          .initialize();
      if (!deviceServicesInitialized) {
        debugPrint('Warning: Failed to initialize device-specific services');
      }

      final remindersEnabled =
          await _storageService.getBool('reminders_enabled') ?? true;
      if (remindersEnabled) {
        await scheduleAllReminders();
      }

      _isInitialized = true;
      return true;
    } catch (e) {
      debugPrint('Error initializing ReminderService: $e');
      return false;
    }
  }

  Future<void> _loadPreferences() async {
    try {
      debugPrint('Reminder preferences loaded');
    } catch (e) {
      debugPrint('Error loading reminder preferences: $e');
    }
  }

  Future<bool> hasPendingTasksToday() async {
    final userData = await _storageService.getUserData();
    final userId = userData?['id'];

    if (userId == null) {
      debugPrint('Cannot check pending tasks: User not logged in');
      return false;
    }

    try {
      final today = DateTime.now();
      final progress = await _progressRepository.getDueProgress(
        userId.toString(),
        studyDate: today,
      );

      final hasTasks = progress.isNotEmpty;
      debugPrint('Pending tasks check: ${hasTasks ? 'Has tasks' : 'No tasks'}');
      return hasTasks;
    } catch (e) {
      debugPrint('Error checking pending tasks: $e');
      return false;
    }
  }

  Future<bool> scheduleAllReminders() async {
    if (_isScheduling) return false;
    _isScheduling = true;

    try {
      if (!await _prepareForScheduling()) {
        _isScheduling = false;
        return false;
      }

      final remindersEnabled =
          await _storageService.getBool('reminders_enabled') ?? true;
      if (!remindersEnabled) {
        debugPrint('Reminders are disabled, not scheduling any reminders');
        _isScheduling = false;
        return true;
      }

      await _checkAndLogPermissions();

      if (!await _scheduleNoonReminder()) {
        debugPrint('Failed to schedule noon reminder');
      }

      final hasPendingTasks = await hasPendingTasksToday();
      if (!hasPendingTasks) {
        debugPrint(
          'No pending tasks today, skipping evening and end-of-day reminders',
        );
        _isScheduling = false;
        return true;
      }

      await _scheduleEveningReminders();
      await _scheduleEndOfDayReminder();

      _isScheduling = false;
      return true;
    } catch (e) {
      debugPrint('Error scheduling reminders: $e');
      _isScheduling = false;
      return false;
    }
  }

  Future<bool> _prepareForScheduling() async {
    debugPrint('========== DEBUGGING REMINDERS ==========');
    debugPrint('Initialized: $_isInitialized');

    if (!_isInitialized) {
      final bool initialized = await initialize();
      debugPrint('Initialization result: $initialized');
      if (!initialized) {
        debugPrint('Failed to initialize ReminderService');
        return false;
      }
    }

    await _notificationService.cancelAllNotifications();
    debugPrint('All previous notifications cancelled');

    return true;
  }

  Future<void> _checkAndLogPermissions() async {
    // Check permission status
    final deviceService = _deviceSpecificService;
    final hasAlarm = await deviceService.hasExactAlarmPermission();
    final ignoresBattery = await deviceService.isIgnoringBatteryOptimizations();
    debugPrint(
      'Permission check - Exact alarm: $hasAlarm, Battery optimization: $ignoresBattery',
    );
  }

  Future<bool> _scheduleNoonReminder() async {
    final noonReminderEnabled =
        await _storageService.getBool('noon_reminder_enabled') ?? true;
    if (!noonReminderEnabled) {
      return true;
    }

    final bool noonResult = await _notificationService.scheduleNoonReminder();
    debugPrint('Noon reminder scheduled: $noonResult');
    return noonResult;
  }

  Future<void> _scheduleEveningReminders() async {
    final eveningFirstReminderEnabled =
        await _storageService.getBool('evening_first_reminder_enabled') ?? true;
    if (eveningFirstReminderEnabled) {
      final bool eveningFirstResult = await _notificationService
          .scheduleEveningFirstReminder();
      debugPrint('Evening first reminder scheduled: $eveningFirstResult');
    }

    final eveningSecondReminderEnabled =
        await _storageService.getBool('evening_second_reminder_enabled') ??
        true;
    if (eveningSecondReminderEnabled) {
      final bool eveningSecondResult = await _notificationService
          .scheduleEveningSecondReminder();
      debugPrint('Evening second reminder scheduled: $eveningSecondResult');
    }
  }

  Future<void> _scheduleEndOfDayReminder() async {
    final endOfDayReminderEnabled =
        await _storageService.getBool('end_of_day_reminder_enabled') ?? true;
    if (!endOfDayReminderEnabled) {
      return;
    }

    final useAlarmStyle =
        _deviceSpecificService.isAndroid &&
        (_deviceSpecificService.isSamsungDevice ||
            _deviceSpecificService.sdkVersion >= 26);

    final bool endOfDayResult = await _notificationService
        .scheduleEndOfDayReminder(useAlarmStyle: useAlarmStyle);
    debugPrint(
      'End of day reminder scheduled: $endOfDayResult (alarm style: $useAlarmStyle)',
    );
  }

  Future<bool> updateRemindersAfterTaskCompletion() async {
    try {
      final stillHasPendingTasks = await hasPendingTasksToday();
      if (stillHasPendingTasks) {
        return true;
      }

      debugPrint(
        'All tasks completed, cancelling evening and end-of-day reminders',
      );
      await _notificationService.cancelNotification(
        NotificationService.eveningFirstReminderId,
      );
      await _notificationService.cancelNotification(
        NotificationService.eveningSecondReminderId,
      );
      await _notificationService.cancelNotification(
        NotificationService.endOfDayReminderId,
      );
      return true;
    } catch (e) {
      debugPrint('Error updating reminders after task completion: $e');
      return false;
    }
  }

  Future<bool> getRemindersEnabled() async {
    return await _storageService.getBool('reminders_enabled') ?? true;
  }

  Future<bool> getNoonReminderEnabled() async {
    return await _storageService.getBool('noon_reminder_enabled') ?? true;
  }

  Future<bool> getEveningFirstReminderEnabled() async {
    return await _storageService.getBool('evening_first_reminder_enabled') ??
        true;
  }

  Future<bool> getEveningSecondReminderEnabled() async {
    return await _storageService.getBool('evening_second_reminder_enabled') ??
        true;
  }

  Future<bool> getEndOfDayReminderEnabled() async {
    return await _storageService.getBool('end_of_day_reminder_enabled') ?? true;
  }

  Future<bool> setRemindersEnabled(bool value) async {
    try {
      await _storageService.setBool('reminders_enabled', value);
      if (value) {
        return await scheduleAllReminders();
      }
      return await _notificationService.cancelAllNotifications();
    } catch (e) {
      debugPrint('Error setting reminders enabled: $e');
      return false;
    }
  }

  Future<bool> setNoonReminderEnabled(bool value) async {
    try {
      await _storageService.setBool('noon_reminder_enabled', value);
      final remindersEnabled =
          await _storageService.getBool('reminders_enabled') ?? true;
      if (!remindersEnabled) {
        return true;
      }

      if (value) {
        return await _notificationService.scheduleNoonReminder();
      }
      return await _notificationService.cancelNotification(
        NotificationService.noonReminderId,
      );
    } catch (e) {
      debugPrint('Error setting noon reminder: $e');
      return false;
    }
  }

  Future<bool> setEveningFirstReminderEnabled(bool value) async {
    try {
      await _storageService.setBool('evening_first_reminder_enabled', value);
      final remindersEnabled =
          await _storageService.getBool('reminders_enabled') ?? true;
      if (!remindersEnabled) {
        return true;
      }

      if (value && await hasPendingTasksToday()) {
        return await _notificationService.scheduleEveningFirstReminder();
      }
      return await _notificationService.cancelNotification(
        NotificationService.eveningFirstReminderId,
      );
    } catch (e) {
      debugPrint('Error setting evening first reminder: $e');
      return false;
    }
  }

  Future<bool> setEveningSecondReminderEnabled(bool value) async {
    try {
      await _storageService.setBool('evening_second_reminder_enabled', value);
      final remindersEnabled =
          await _storageService.getBool('reminders_enabled') ?? true;
      if (!remindersEnabled) {
        return true;
      }

      if (value && await hasPendingTasksToday()) {
        return await _notificationService.scheduleEveningSecondReminder();
      }
      return await _notificationService.cancelNotification(
        NotificationService.eveningSecondReminderId,
      );
    } catch (e) {
      debugPrint('Error setting evening second reminder: $e');
      return false;
    }
  }

  Future<bool> setEndOfDayReminderEnabled(bool value) async {
    try {
      await _storageService.setBool('end_of_day_reminder_enabled', value);
      final remindersEnabled =
          await _storageService.getBool('reminders_enabled') ?? true;
      if (!remindersEnabled) {
        return true;
      }

      if (value && await hasPendingTasksToday()) {
        return await _notificationService.scheduleEndOfDayReminder();
      }
      return await _notificationService.cancelNotification(
        NotificationService.endOfDayReminderId,
      );
    } catch (e) {
      debugPrint('Error setting end of day reminder: $e');
      return false;
    }
  }
}

@riverpod
class ReminderSettings extends _$ReminderSettings {
  @override
  Future<Map<String, bool>> build() async {
    final reminderService = await ref.watch(reminderServiceProvider.future);

    final Map<String, bool> settings = {
      'reminders_enabled': await reminderService.getRemindersEnabled(),
      'noon_reminder_enabled': await reminderService.getNoonReminderEnabled(),
      'evening_first_reminder_enabled': await reminderService
          .getEveningFirstReminderEnabled(),
      'evening_second_reminder_enabled': await reminderService
          .getEveningSecondReminderEnabled(),
      'end_of_day_reminder_enabled': await reminderService
          .getEndOfDayReminderEnabled(),
    };

    return settings;
  }

  Future<void> setRemindersEnabled(bool value) async {
    final reminderService = await ref.read(reminderServiceProvider.future);
    final result = await reminderService.setRemindersEnabled(value);

    if (result) {
      state = AsyncValue.data({
        ...state.valueOrNull ?? {},
        'reminders_enabled': value,
      });

      // Broadcast event
      final eventBus = ref.read(eventBusProvider);
      eventBus.fire(ReminderSettingsChangedEvent(enabled: value));
    }
  }

  Future<void> setNoonReminderEnabled(bool value) async {
    final reminderService = await ref.read(reminderServiceProvider.future);
    final result = await reminderService.setNoonReminderEnabled(value);

    if (result) {
      state = AsyncValue.data({
        ...state.valueOrNull ?? {},
        'noon_reminder_enabled': value,
      });
    }
  }

  Future<void> setEveningFirstReminderEnabled(bool value) async {
    final reminderService = await ref.read(reminderServiceProvider.future);
    final result = await reminderService.setEveningFirstReminderEnabled(value);

    if (result) {
      state = AsyncValue.data({
        ...state.valueOrNull ?? {},
        'evening_first_reminder_enabled': value,
      });
    }
  }

  Future<void> setEveningSecondReminderEnabled(bool value) async {
    final reminderService = await ref.read(reminderServiceProvider.future);
    final result = await reminderService.setEveningSecondReminderEnabled(value);

    if (result) {
      state = AsyncValue.data({
        ...state.valueOrNull ?? {},
        'evening_second_reminder_enabled': value,
      });
    }
  }

  Future<void> setEndOfDayReminderEnabled(bool value) async {
    final reminderService = await ref.read(reminderServiceProvider.future);
    final result = await reminderService.setEndOfDayReminderEnabled(value);

    if (result) {
      state = AsyncValue.data({
        ...state.valueOrNull ?? {},
        'end_of_day_reminder_enabled': value,
      });
    }
  }

  Future<void> scheduleAllReminders() async {
    final reminderService = await ref.read(reminderServiceProvider.future);
    await reminderService.scheduleAllReminders();
  }
}
