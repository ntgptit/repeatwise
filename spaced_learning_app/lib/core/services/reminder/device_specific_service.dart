// Không cần thay đổi nhiều, chỉ cần thêm provider
import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/core/services/platform/device_settings_service.dart';

import '../../di/providers.dart';

part 'device_specific_service.g.dart';

class DeviceSpecificService {
  final DeviceInfoPlugin _deviceInfo = DeviceInfoPlugin();
  final DeviceSettingsService _deviceSettingsService;

  bool _isSamsungDevice = false;
  bool _isS23Ultra = false;
  String? _deviceModel;
  String? _manufacturer;
  int _sdkVersion = 0;
  bool _isInitialized = false;

  DeviceSpecificService({DeviceSettingsService? deviceSettingsService})
    : _deviceSettingsService = deviceSettingsService ?? DeviceSettingsService();

  Future<bool> initialize() async {
    if (_isInitialized) return true;

    try {
      if (kIsWeb) {
        _isInitialized = true;
        return true;
      }

      if (_isAndroid) {
        await _initializeAndroidDevice();
        await _applyDeviceSpecificOptimizations();

        _isInitialized = true;
        return true;
      }

      _isInitialized = true;
      return true;
    } catch (e) {
      debugPrint('Error detecting device: $e');
      return false;
    }
  }

  Future<void> _initializeAndroidDevice() async {
    final androidInfo = await _deviceInfo.androidInfo;

    _deviceModel = androidInfo.model;
    _manufacturer = androidInfo.manufacturer;
    _sdkVersion = androidInfo.version.sdkInt;

    _isSamsungDevice = _manufacturer?.toLowerCase() == 'samsung';
    _isS23Ultra =
        _isSamsungDevice &&
        (_deviceModel?.toLowerCase().contains('sm-s918') ?? false);

    debugPrint(
      'Device detected: $_manufacturer $_deviceModel (SDK: $_sdkVersion)',
    );
  }

  Future<void> _applyDeviceSpecificOptimizations() async {
    if (_isSamsungDevice) {
      await _applySamsungOptimizations();
    } else {
      await _applyGeneralOptimizations();
    }
  }

  Future<void> _applySamsungOptimizations() async {
    try {
      final bool batteryOptResult =
          await _requestBatteryOptimizationExclusion();
      debugPrint('Battery optimization request result: $batteryOptResult');

      if (_isS23Ultra) {
        final bool s23Result = await _configureS23UltraFeatures();
        debugPrint('S23 Ultra specific optimizations result: $s23Result');
      } else {
        final bool samsungResult = await _configureGeneralSamsungFeatures();
        debugPrint('General Samsung optimizations result: $samsungResult');
      }
    } catch (e) {
      debugPrint('Error applying Samsung optimizations: $e');
    }
  }

  Future<void> _applyGeneralOptimizations() async {
    try {
      final bool batteryOptResult =
          await _requestBatteryOptimizationExclusion();
      debugPrint('Battery optimization request result: $batteryOptResult');

      if (_sdkVersion >= 31) {
        final bool android12Result = await _configureAndroid12Features();
        debugPrint('Android 12+ optimizations result: $android12Result');
      } else if (_sdkVersion >= 26) {
        final bool olderAndroidResult = await _configureOlderAndroidFeatures();
        debugPrint('Android 8-11 optimizations result: $olderAndroidResult');
      }
    } catch (e) {
      debugPrint('Error applying general optimizations: $e');
    }
  }

  Future<bool> _configureOlderAndroidFeatures() async {
    debugPrint('Configuring for Android $_sdkVersion');
    return true;
  }

  Future<bool> _configureAndroid12Features() async {
    try {
      if (!_isAndroid) return false;

      const methodChannel = MethodChannel(
        'com.example.spaced_learning_app.device/optimization',
      );

      try {
        return await methodChannel.invokeMethod('requestExactAlarmPermission');
      } on PlatformException catch (e) {
        debugPrint('Failed to configure Android 12 features: ${e.message}');
        return false;
      } on MissingPluginException {
        debugPrint('Plugin not available on this platform');
        return false;
      }
    } catch (e) {
      debugPrint('Unexpected error configuring Android 12 features: $e');
      return false;
    }
  }

  Future<bool> _configureGeneralSamsungFeatures() async {
    try {
      if (!_isAndroid) return false;

      const methodChannel = MethodChannel('com.yourapp.device/optimization');

      try {
        return await methodChannel.invokeMethod('disableSleepingApps');
      } on PlatformException catch (e) {
        debugPrint('Failed to configure Samsung features: ${e.message}');
        return false;
      } on MissingPluginException {
        debugPrint('Plugin not available on this platform');
        return false;
      }
    } catch (e) {
      debugPrint('Unexpected error configuring Samsung features: $e');
      return false;
    }
  }

  Future<bool> _requestBatteryOptimizationExclusion() async {
    try {
      if (!_isAndroid) return true;

      final status = await Permission.ignoreBatteryOptimizations.status;

      if (status.isGranted) {
        debugPrint('Battery optimization already disabled');
        return true;
      }

      final result = await Permission.ignoreBatteryOptimizations.request();
      final bool isGranted = result.isGranted;

      debugPrint('Battery optimization exclusion request result: $isGranted');
      return isGranted;
    } catch (e) {
      debugPrint('Error requesting battery optimization exclusion: $e');
      return false;
    }
  }

  Future<bool> _configureS23UltraFeatures() async {
    if (!_isAndroid) return false;

    bool overallSuccess = true;

    try {
      const methodChannel = MethodChannel('com.yourapp.samsung/optimization');

      try {
        final bool gameOptimizerResult = await methodChannel.invokeMethod(
          'disableGameOptimizer',
        );
        debugPrint('Game Optimizer disabled: $gameOptimizerResult');
        if (!gameOptimizerResult) overallSuccess = false;
      } on PlatformException catch (e) {
        debugPrint('Failed to disable Game Optimizer: ${e.message}');
        overallSuccess = false;
      } on MissingPluginException {
        debugPrint('Plugin not available on this platform');
        overallSuccess = false;
      }

      bool hasEdgePanel = false;
      try {
        hasEdgePanel = await methodChannel.invokeMethod('hasEdgePanel');
        debugPrint('Device has Edge Panel: $hasEdgePanel');
      } on PlatformException catch (e) {
        debugPrint('Failed to check Edge Panel availability: ${e.message}');
      } on MissingPluginException {
        debugPrint('Plugin not available on this platform');
      }

      if (hasEdgePanel) {
        try {
          final bool edgePanelResult = await methodChannel.invokeMethod(
            'registerEdgePanelProvider',
          );
          debugPrint('Edge Panel registration: $edgePanelResult');
          if (!edgePanelResult) overallSuccess = false;
        } on PlatformException catch (e) {
          debugPrint('Failed to register Edge Panel provider: ${e.message}');
          overallSuccess = false;
        } on MissingPluginException {
          debugPrint('Plugin not available on this platform');
          overallSuccess = false;
        }
      }

      return overallSuccess;
    } catch (e) {
      debugPrint('Error configuring S23 Ultra features: $e');
      return false;
    }
  }

  bool get _isAndroid {
    if (kIsWeb) return false;

    try {
      return defaultTargetPlatform == TargetPlatform.android;
    } catch (e) {
      debugPrint('Error checking platform: $e');
      return false;
    }
  }

  bool get isAndroid => _isAndroid;

  bool get isSamsungDevice => _isSamsungDevice;

  bool get isS23Ultra => _isS23Ultra;

  String? get deviceModel => _deviceModel;

  int get sdkVersion => _sdkVersion;

  String? get manufacturer => _manufacturer;

  bool get isInitialized => _isInitialized;

  // Delegate methods to DeviceSettingsService
  Future<bool> hasExactAlarmPermission() async {
    return _deviceSettingsService.hasExactAlarmPermission();
  }

  Future<bool> isIgnoringBatteryOptimizations() async {
    return _deviceSettingsService.isIgnoringBatteryOptimizations();
  }

  Future<bool> requestExactAlarmPermission() async {
    return _deviceSettingsService.requestExactAlarmPermission();
  }

  Future<bool> requestBatteryOptimization() async {
    return _deviceSettingsService.requestBatteryOptimization();
  }

  Future<bool> disableSleepingApps() async {
    return _deviceSettingsService.disableSleepingApps();
  }

  Future<Map<String, dynamic>> getDeviceInfo() async {
    return _deviceSettingsService.getDeviceInfo();
  }
}

@riverpod
class DeviceInfo extends _$DeviceInfo {
  @override
  Future<Map<String, dynamic>> build() async {
    final deviceService = await ref.watch(deviceSpecificServiceProvider.future);

    final Map<String, dynamic> info = {
      'isAndroid': deviceService.isAndroid,
      'isSamsungDevice': deviceService.isSamsungDevice,
      'isS23Ultra': deviceService.isS23Ultra,
      'deviceModel': deviceService.deviceModel,
      'manufacturer': deviceService.manufacturer,
      'sdkVersion': deviceService.sdkVersion,
      'isInitialized': deviceService.isInitialized,
    };

    // Add battery optimization status
    info['isIgnoringBatteryOptimizations'] = await deviceService
        .isIgnoringBatteryOptimizations();

    // Add alarm permission status for Android 12+
    if (deviceService.isAndroid && deviceService.sdkVersion >= 31) {
      info['hasExactAlarmPermission'] = await deviceService
          .hasExactAlarmPermission();
    }

    return info;
  }

  Future<bool> requestBatteryOptimization() async {
    final deviceService = await ref.read(deviceSpecificServiceProvider.future);
    final result = await deviceService.requestBatteryOptimization();

    // Invalidate cache to trigger rebuild with new permission status
    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> requestExactAlarmPermission() async {
    final deviceService = await ref.read(deviceSpecificServiceProvider.future);
    final result = await deviceService.requestExactAlarmPermission();

    // Invalidate cache to trigger rebuild with new permission status
    if (result) {
      ref.invalidateSelf();
    }

    return result;
  }

  Future<bool> disableSleepingApps() async {
    final deviceService = await ref.read(deviceSpecificServiceProvider.future);
    return deviceService.disableSleepingApps();
  }
}
