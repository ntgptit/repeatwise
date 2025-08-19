import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/services/reminder/notification_service.dart';
import 'package:spaced_learning_app/core/services/storage_service.dart';

import '../../di/providers.dart';

part 'cloud_reminder_service.g.dart';

class CloudReminderService {
  late final FirebaseMessaging _firebaseMessaging;
  final StorageService _storageService;
  final NotificationService _notificationService;

  CloudReminderService({
    required StorageService storageService,
    required NotificationService notificationService,
  }) : _storageService = storageService,
       _notificationService = notificationService {
    _firebaseMessaging = FirebaseMessaging.instance;
  }

  Future<void> initialize() async {
    try {
      final settings = await _requestNotificationPermissions();
      debugPrint('FCM permission settings: ${settings.authorizationStatus}');

      await _setupTokenHandling();
      _setupMessageHandlers();

      debugPrint('FCM initialized successfully');
    } catch (e) {
      debugPrint('Error initializing FCM: $e');
    }
  }

  Future<NotificationSettings> _requestNotificationPermissions() async {
    return _firebaseMessaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
      provisional: false,
    );
  }

  Future<void> _setupTokenHandling() async {
    final token = await _firebaseMessaging.getToken();

    if (token != null) {
      await _saveDeviceToken(token);
    } else {
      debugPrint('Error: FCM token is null');
    }

    _firebaseMessaging.onTokenRefresh.listen(_saveDeviceToken);
  }

  void _setupMessageHandlers() {
    FirebaseMessaging.onMessage.listen(_handleMessage);
    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
  }

  Future<void> _saveDeviceToken(String token) async {
    try {
      await _storageService.setString('fcm_token', token);
      debugPrint('FCM token saved locally: ${token.substring(0, 10)}...');

      final userData = await _storageService.getUserData();
      if (userData != null && userData['id'] != null) {
        debugPrint('FCM token saved for user: ${userData['id']}');
      }
    } catch (e) {
      debugPrint('Error in _saveDeviceToken: $e');
    }
  }

  void _handleMessage(RemoteMessage message) {
    debugPrint('Got FCM message: ${message.notification?.title}');

    try {
      _notificationService.showNotification(
        id: DateTime.now().millisecondsSinceEpoch ~/ 1000,
        title: message.notification?.title ?? 'Learning Reminder',
        body: message.notification?.body ?? 'Time to check your learning tasks',
        isImportant: message.data['priority'] == 'high',
      );
    } catch (e) {
      debugPrint('Error showing notification from FCM: $e');
    }
  }
}

@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  debugPrint('Handling a background message: ${message.messageId}');
}

@riverpod
class CloudNotification extends _$CloudNotification {
  @override
  Future<String?> build() async {
    final storageService = ref.watch(storageServiceProvider);
    return storageService.getString('fcm_token');
  }

  Future<void> initialize() async {
    state = const AsyncValue.loading();

    final cloudReminderService = ref.read(cloudReminderServiceProvider);
    await cloudReminderService.initialize();

    // After initialization, update the token
    final storageService = ref.read(storageServiceProvider);
    final token = await storageService.getString('fcm_token');
    state = AsyncValue.data(token);
  }
}
