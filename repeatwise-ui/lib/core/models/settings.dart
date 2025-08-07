import 'package:freezed_annotation/freezed_annotation.dart';

part 'settings.freezed.dart';
part 'settings.g.dart';

enum ThemeMode {
  @JsonValue('LIGHT')
  light,
  @JsonValue('DARK')
  dark,
  @JsonValue('SYSTEM')
  system,
}

enum NotificationPreference {
  @JsonValue('ALL')
  all,
  @JsonValue('IMPORTANT_ONLY')
  importantOnly,
  @JsonValue('NONE')
  none,
}

@freezed
abstract class UserSettings with _$UserSettings {
  const factory UserSettings({
    required String userId,
    required ThemeMode themeMode,
    required NotificationPreference notificationPreference,
    required bool emailNotifications,
    required bool pushNotifications,
    required String language,
    required String timezone,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _UserSettings;

  factory UserSettings.fromJson(Map<String, dynamic> json) =>
      _$UserSettingsFromJson(json);
}

@freezed
abstract class SettingsUpdateRequest with _$SettingsUpdateRequest {
  const factory SettingsUpdateRequest({
    ThemeMode? themeMode,
    NotificationPreference? notificationPreference,
    bool? emailNotifications,
    bool? pushNotifications,
    String? language,
    String? timezone,
  }) = _SettingsUpdateRequest;

  factory SettingsUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$SettingsUpdateRequestFromJson(json);
}

// Extensions for computed properties
extension UserSettingsExtension on UserSettings {
  bool get isDarkMode => themeMode == ThemeMode.dark;
  bool get isLightMode => themeMode == ThemeMode.light;
  bool get isSystemTheme => themeMode == ThemeMode.system;
  bool get hasNotifications => notificationPreference != NotificationPreference.none;
  bool get hasAllNotifications => notificationPreference == NotificationPreference.all;
  bool get hasImportantNotifications => notificationPreference == NotificationPreference.importantOnly;
  bool get hasEmailNotifications => emailNotifications;
  bool get hasPushNotifications => pushNotifications;
  bool get isRecentlyUpdated => DateTime.now().difference(updatedAt).inDays < 1;
  
  String get themeModeDisplayName {
    switch (themeMode) {
      case ThemeMode.light:
        return 'Light';
      case ThemeMode.dark:
        return 'Dark';
      case ThemeMode.system:
        return 'System';
    }
  }
  
  String get notificationPreferenceDisplayName {
    switch (notificationPreference) {
      case NotificationPreference.all:
        return 'All Notifications';
      case NotificationPreference.importantOnly:
        return 'Important Only';
      case NotificationPreference.none:
        return 'No Notifications';
    }
  }
  
  bool get isValid => userId.isNotEmpty && language.isNotEmpty && timezone.isNotEmpty;
  bool get hasValidLanguage => language.length >= 2 && language.length <= 5;
  bool get hasValidTimezone => timezone.isNotEmpty;
}

extension SettingsUpdateRequestExtension on SettingsUpdateRequest {
  bool get hasChanges => themeMode != null || 
                        notificationPreference != null || 
                        emailNotifications != null || 
                        pushNotifications != null || 
                        language != null || 
                        timezone != null;
  bool get hasValidLanguage => language == null || (language!.length >= 2 && language!.length <= 5);
  bool get hasValidTimezone => timezone == null || timezone!.isNotEmpty;
}
