/// Application-wide constants
class AppConstants {
  // API related constants
  static const String baseUrl =
      // 'http://192.168.1.9:8080'; // Docker local
      // 'http://localhost:8088'; // Local
      'http://18.142.53.156:8080'; // AWS EC2 instance
  // 'https://spaced-learning-api.onrender.com'; // For Android emulator
  static const String apiPrefix = '/api/v1';
  static const int connectTimeout = 15000; // milliseconds
  static const int receiveTimeout = 15000; // milliseconds

  // Local storage keys
  static const String tokenKey = 'auth_token';
  static const String refreshTokenKey = 'refresh_token';
  static const String userKey = 'user_data';
  static const String darkModeKey = 'dark_mode';

  // App settings
  static const String appName = 'Spaced Learning';

  // Animation durations
  static const Duration quickDuration = Duration(milliseconds: 200);
  static const Duration mediumDuration = Duration(milliseconds: 350);
  static const Duration longDuration = Duration(milliseconds: 500);

  // Reminder settings
  // Noon reminder (12:30 PM)
  static const int noonReminderHour = 12;
  static const int noonReminderMinute = 30;

  // Evening first reminder (9:00 PM)
  static const int eveningFirstReminderHour = 21;
  static const int eveningFirstReminderMinute = 0;

  // Evening second reminder (10:30 PM)
  static const int eveningSecondReminderHour = 22;
  static const int eveningSecondReminderMinute = 30;

  // End of day reminder (11:30 PM)
  static const int endOfDayReminderHour = 23;
  static const int endOfDayReminderMinute = 30;

  // Alarm Manager callback IDs
  static const int noonReminderCallbackId = 1001;
  static const int eveningFirstReminderCallbackId = 1002;
  static const int eveningSecondReminderCallbackId = 1003;
  static const int endOfDayReminderCallbackId = 1004;
}
