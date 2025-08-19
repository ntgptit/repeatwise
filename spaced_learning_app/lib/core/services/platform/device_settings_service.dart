import 'package:flutter/foundation.dart' show kIsWeb, defaultTargetPlatform;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../di/providers.dart';

part 'device_settings_service.g.dart';

class DeviceSettingsService {
  static const MethodChannel _channel = MethodChannel(
    'com.example.spaced_learning_app.device/optimization',
  );

  bool get isAndroid {
    if (kIsWeb) return false;

    try {
      return defaultTargetPlatform == TargetPlatform.android;
    } catch (e) {
      debugPrint('Error checking platform: $e');
      return false;
    }
  }

  Future<bool> hasExactAlarmPermission() async {
    if (!isAndroid) {
      return true; // Mặc định true cho các platform khác Android
    }

    try {
      final bool hasPermission = await _channel.invokeMethod(
        'hasExactAlarmPermission',
      );
      return hasPermission;
    } on PlatformException catch (e) {
      debugPrint('Error checking exact alarm permission: ${e.message}');
      return false;
    } on MissingPluginException {
      debugPrint('Device optimization plugin not available');
      return false;
    } catch (e) {
      debugPrint('Unexpected error checking exact alarm permission: $e');
      return false;
    }
  }

  Future<bool> isIgnoringBatteryOptimizations() async {
    if (!isAndroid) {
      return true; // Mặc định true cho các platform khác Android
    }

    try {
      final bool isIgnoring = await _channel.invokeMethod(
        'isIgnoringBatteryOptimizations',
      );
      return isIgnoring;
    } on PlatformException catch (e) {
      debugPrint('Error checking battery optimization status: ${e.message}');
      return false;
    } on MissingPluginException {
      debugPrint('Device optimization plugin not available');
      return false;
    } catch (e) {
      debugPrint('Unexpected error checking battery optimization: $e');
      return false;
    }
  }

  Future<bool> requestExactAlarmPermission() async {
    if (!isAndroid) {
      return true; // Mặc định true cho các platform khác Android
    }

    try {
      final bool result = await _channel.invokeMethod(
        'requestExactAlarmPermission',
      );
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error requesting exact alarm permission: ${e.message}');
      return false;
    } on MissingPluginException {
      debugPrint('Device optimization plugin not available');
      return false;
    } catch (e) {
      debugPrint('Unexpected error requesting exact alarm permission: $e');
      return false;
    }
  }

  Future<bool> requestBatteryOptimization() async {
    if (!isAndroid) {
      return true; // Mặc định true cho các platform khác Android
    }

    try {
      final bool result = await _channel.invokeMethod(
        'requestBatteryOptimization',
      );
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error requesting battery optimization: ${e.message}');
      return false;
    } on MissingPluginException {
      debugPrint('Device optimization plugin not available');
      return false;
    } catch (e) {
      debugPrint('Unexpected error requesting battery optimization: $e');
      return false;
    }
  }

  Future<bool> disableSleepingApps() async {
    if (!isAndroid) {
      return true; // Mặc định true cho các platform khác Android
    }

    try {
      final bool result = await _channel.invokeMethod('disableSleepingApps');
      return result;
    } on PlatformException catch (e) {
      debugPrint('Error disabling sleeping apps: ${e.message}');
      return false;
    } on MissingPluginException {
      debugPrint('Device optimization plugin not available');
      return false;
    } catch (e) {
      debugPrint('Unexpected error disabling sleeping apps: $e');
      return false;
    }
  }

  Future<Map<String, dynamic>> getDeviceInfo() async {
    if (!isAndroid) {
      return {
        'sdkVersion': 0,
        'manufacturer': defaultTargetPlatform.toString(),
        'model': 'Unknown',
      };
    }

    try {
      final Map<dynamic, dynamic> info = await _channel.invokeMethod(
        'getDeviceInfo',
      );
      return Map<String, dynamic>.from(info);
    } on PlatformException catch (e) {
      debugPrint('Error getting device info: ${e.message}');
      return {'sdkVersion': 0, 'manufacturer': 'Unknown', 'model': 'Unknown'};
    } on MissingPluginException {
      debugPrint('Device optimization plugin not available');
      return {'sdkVersion': 0, 'manufacturer': 'Unknown', 'model': 'Unknown'};
    } catch (e) {
      debugPrint('Unexpected error getting device info: $e');
      return {'sdkVersion': 0, 'manufacturer': 'Unknown', 'model': 'Unknown'};
    }
  }
}

@riverpod
class DeviceSettings extends _$DeviceSettings {
  @override
  Future<Map<String, dynamic>> build() async {
    final deviceSettingsService = ref.watch(deviceSettingsServiceProvider);

    final Map<String, dynamic> settings = {
      'isAndroid': deviceSettingsService.isAndroid,
    };

    if (deviceSettingsService.isAndroid) {
      settings['isIgnoringBatteryOptimizations'] = await deviceSettingsService
          .isIgnoringBatteryOptimizations();
      settings['hasExactAlarmPermission'] = await deviceSettingsService
          .hasExactAlarmPermission();
      settings['deviceInfo'] = await deviceSettingsService.getDeviceInfo();
    }

    return settings;
  }

  Future<bool> requestBatteryOptimization() async {
    final deviceSettingsService = ref.read(deviceSettingsServiceProvider);
    final result = await deviceSettingsService.requestBatteryOptimization();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> requestExactAlarmPermission() async {
    final deviceSettingsService = ref.read(deviceSettingsServiceProvider);
    final result = await deviceSettingsService.requestExactAlarmPermission();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> disableSleepingApps() async {
    final deviceSettingsService = ref.read(deviceSettingsServiceProvider);
    final result = await deviceSettingsService.disableSleepingApps();

    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }
}
