import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:spaced_learning_app/core/services/reminder/device_specific_service.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;

abstract class BaseNotificationService {
  final FlutterLocalNotificationsPlugin notificationsPlugin;
  final DeviceSpecificService deviceSpecificService;
  bool _isInitialized = false;
  bool _timezonesInitialized = false;

  BaseNotificationService({
    required this.notificationsPlugin,
    required this.deviceSpecificService,
  });

  Future<bool> initialize() async {
    if (_isInitialized) return true;

    try {
      if (!await _initializeTimezones()) {
        debugPrint('Warning: Failed to initialize timezones');
      }

      if (kIsWeb) {
        debugPrint(
          'Running on web platform, skipping native notification setup',
        );
        _isInitialized = true;
        return true;
      }

      if (!await _initializeNotifications()) {
        debugPrint('Failed to initialize notifications plugin');
        return false;
      }

      _isInitialized = true;
      return true;
    } catch (e) {
      debugPrint('Error initializing NotificationService: $e');
      return false;
    }
  }

  Future<bool> _initializeTimezones() async {
    if (_timezonesInitialized) return true;

    try {
      tz.initializeTimeZones();
      final String timeZoneName = tz.local.name;
      debugPrint('Local timezone: $timeZoneName');
      _timezonesInitialized = true;
      return true;
    } catch (e) {
      debugPrint('Error initializing timezone data: $e');
      return false;
    }
  }

  Future<bool> _initializeNotifications() async {
    try {
      final AndroidInitializationSettings androidSettings =
          _getAndroidSettings();

      const iosSettings = DarwinInitializationSettings(
        requestAlertPermission: true,
        requestBadgePermission: true,
        requestSoundPermission: true,
      );

      final initSettings = InitializationSettings(
        android: androidSettings,
        iOS: iosSettings,
      );

      final bool? initialized = await notificationsPlugin.initialize(
        initSettings,
        onDidReceiveNotificationResponse: _onNotificationTapped,
      );

      return initialized ?? false;
    } catch (e) {
      debugPrint('Error initializing notifications: $e');
      return false;
    }
  }

  AndroidInitializationSettings _getAndroidSettings() {
    return const AndroidInitializationSettings('ic_launcher');
  }

  void _onNotificationTapped(NotificationResponse response) {
    debugPrint('Notification tapped: ${response.payload}');
  }

  Future<bool> showNotification({
    required int id,
    required String title,
    required String body,
    String? payload,
    bool isImportant = false,
  }) async {
    if (kIsWeb) {
      debugPrint('Notifications not supported on web platform');
      return false;
    }

    if (!_isInitialized) {
      final bool initialized = await initialize();
      if (!initialized) return false;
    }

    try {
      final notificationDetails = _getNotificationDetails(isImportant);

      await notificationsPlugin.show(
        id,
        title,
        body,
        notificationDetails,
        payload: payload,
      );
      debugPrint('Notification shown with id: $id');
      return true;
    } catch (e) {
      debugPrint('Error showing notification: $e');
      return false;
    }
  }

  NotificationDetails _getNotificationDetails(bool isImportant) {
    // Implementation should be provided by subclasses
    throw UnimplementedError(
      '_getNotificationDetails must be implemented by subclasses',
    );
  }

  tz.TZDateTime convertToTZDateTime(DateTime scheduledTime) {
    try {
      return tz.TZDateTime.from(scheduledTime, tz.local);
    } catch (e) {
      debugPrint('Error converting to TZ datetime: $e');
      return tz.TZDateTime.from(scheduledTime, tz.UTC);
    }
  }

  Future<bool> cancelNotification(int id) async {
    if (kIsWeb) {
      debugPrint('Notifications not supported on web platform');
      return false;
    }

    if (!_isInitialized) {
      debugPrint(
        'Notification service not initialized, skipping cancel notification',
      );
      return false;
    }

    try {
      await notificationsPlugin.cancel(id);
      debugPrint('Notification cancelled with id: $id');
      return true;
    } catch (e) {
      debugPrint('Error cancelling notification $id: $e');
      return false;
    }
  }

  Future<bool> cancelAllNotifications() async {
    if (kIsWeb) {
      debugPrint('Notifications not supported on web platform');
      return false;
    }

    if (!_isInitialized) {
      debugPrint(
        'Notification service not initialized, skipping cancel all notifications',
      );
      return false;
    }

    try {
      await notificationsPlugin.cancelAll();
      debugPrint('All notifications cancelled');
      return true;
    } catch (e) {
      debugPrint('Error cancelling all notifications: $e');
      return false;
    }
  }

  bool get isInitialized => _isInitialized;

  bool get timezonesInitialized => _timezonesInitialized;
}
