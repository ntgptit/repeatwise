// lib/core/services/storage_service.dart
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';

class StorageService {
  final FlutterSecureStorage _secureStorage;
  final Future<SharedPreferences> _prefs;

  StorageService({
    FlutterSecureStorage? secureStorage,
    Future<SharedPreferences>? prefs,
  }) : _secureStorage = secureStorage ?? const FlutterSecureStorage(),
       _prefs = prefs ?? SharedPreferences.getInstance();

  // Get authentication token with error handling
  Future<String?> getToken() async {
    try {
      return await _secureStorage.read(key: AppConstants.tokenKey);
    } catch (e) {
      debugPrint('Error retrieving token: $e');
      // If there's a decryption error, clear tokens to prevent future errors
      await clearTokens();
      return null;
    }
  }

  // Get refresh token with error handling
  Future<String?> getRefreshToken() async {
    try {
      return await _secureStorage.read(key: AppConstants.refreshTokenKey);
    } catch (e) {
      debugPrint('Error retrieving refresh token: $e');
      await clearTokens();
      return null;
    }
  }

  // Get user data with error handling
  Future<Map<String, dynamic>?> getUserData() async {
    try {
      final prefs = await _prefs;
      final userData = prefs.getString(AppConstants.userKey);

      if (userData == null) {
        return null;
      }

      return jsonDecode(userData) as Map<String, dynamic>;
    } catch (e) {
      debugPrint('Error retrieving user data: $e');
      return null;
    }
  }

  // Check dark mode setting
  Future<bool> isDarkMode() async {
    try {
      final prefs = await _prefs;
      return prefs.getBool(AppConstants.darkModeKey) ?? false;
    } catch (e) {
      debugPrint('Error retrieving dark mode setting: $e');
      return false;
    }
  }

  // Save token with error handling
  Future<void> saveToken(String token) async {
    try {
      await _secureStorage.write(key: AppConstants.tokenKey, value: token);
    } catch (e) {
      debugPrint('Error saving token: $e');
      // Try to reset storage and save again
      await clearTokens();
      try {
        await _secureStorage.write(key: AppConstants.tokenKey, value: token);
      } catch (e) {
        debugPrint('Failed to save token after reset: $e');
        // Final fallback: try to reset all secure storage
        await resetSecureStorage();
      }
    }
  }

  // Save refresh token with error handling
  Future<void> saveRefreshToken(String refreshToken) async {
    try {
      await _secureStorage.write(
        key: AppConstants.refreshTokenKey,
        value: refreshToken,
      );
    } catch (e) {
      debugPrint('Error saving refresh token: $e');
      // Try to reset and save again
      await clearTokens();
      try {
        await _secureStorage.write(
          key: AppConstants.refreshTokenKey,
          value: refreshToken,
        );
      } catch (e) {
        debugPrint('Failed to save refresh token after reset: $e');
        await resetSecureStorage();
      }
    }
  }

  // Save user data
  Future<void> saveUserData(Map<String, dynamic> userData) async {
    try {
      final prefs = await _prefs;
      await prefs.setString(AppConstants.userKey, jsonEncode(userData));
    } catch (e) {
      debugPrint('Error saving user data: $e');
    }
  }

  // Save dark mode setting
  Future<void> saveDarkMode(bool isDarkMode) async {
    try {
      final prefs = await _prefs;
      await prefs.setBool(AppConstants.darkModeKey, isDarkMode);
    } catch (e) {
      debugPrint('Error saving dark mode setting: $e');
    }
  }

  // Clear authentication tokens
  Future<void> clearTokens() async {
    try {
      await _secureStorage.delete(key: AppConstants.tokenKey);
      await _secureStorage.delete(key: AppConstants.refreshTokenKey);
    } catch (e) {
      debugPrint('Error clearing tokens: $e');
      // If individual deletion fails, try resetting all secure storage
      await resetSecureStorage();
    }
  }

  // Clear user data
  Future<void> clearUserData() async {
    try {
      final prefs = await _prefs;
      await prefs.remove(AppConstants.userKey);
    } catch (e) {
      debugPrint('Error clearing user data: $e');
    }
  }

  // Reset all secure storage (emergency fallback)
  Future<void> resetSecureStorage() async {
    try {
      await _secureStorage.deleteAll();
      debugPrint('Reset all secure storage due to cryptographic error');
    } catch (e) {
      debugPrint('Error resetting secure storage: $e');
    }
  }

  // String retrieval with error handling
  Future<String?> getString(String key) async {
    try {
      final prefs = await _prefs;
      return prefs.getString(key);
    } catch (e) {
      debugPrint('Error retrieving string ($key): $e');
      return null;
    }
  }

  // String storage with error handling
  Future<void> setString(String key, String value) async {
    try {
      final prefs = await _prefs;
      await prefs.setString(key, value);
    } catch (e) {
      debugPrint('Error setting string ($key): $e');
    }
  }

  // Boolean retrieval with error handling
  Future<bool?> getBool(String key) async {
    try {
      final prefs = await _prefs;
      return prefs.getBool(key);
    } catch (e) {
      debugPrint('Error retrieving boolean ($key): $e');
      return null;
    }
  }

  // Boolean storage with error handling
  Future<void> setBool(String key, bool value) async {
    try {
      final prefs = await _prefs;
      await prefs.setBool(key, value);
    } catch (e) {
      debugPrint('Error setting boolean ($key): $e');
    }
  }

  // Int retrieval with error handling
  Future<int?> getInt(String key) async {
    try {
      final prefs = await _prefs;
      return prefs.getInt(key);
    } catch (e) {
      debugPrint('Error retrieving int ($key): $e');
      return null;
    }
  }

  // Int storage with error handling
  Future<void> setInt(String key, int value) async {
    try {
      final prefs = await _prefs;
      await prefs.setInt(key, value);
    } catch (e) {
      debugPrint('Error setting int ($key): $e');
    }
  }

  // Double retrieval with error handling
  Future<double?> getDouble(String key) async {
    try {
      final prefs = await _prefs;
      return prefs.getDouble(key);
    } catch (e) {
      debugPrint('Error retrieving double ($key): $e');
      return null;
    }
  }

  // Double storage with error handling
  Future<void> setDouble(String key, double value) async {
    try {
      final prefs = await _prefs;
      await prefs.setDouble(key, value);
    } catch (e) {
      debugPrint('Error setting double ($key): $e');
    }
  }

  // String list retrieval with error handling
  Future<List<String>?> getStringList(String key) async {
    try {
      final prefs = await _prefs;
      return prefs.getStringList(key);
    } catch (e) {
      debugPrint('Error retrieving string list ($key): $e');
      return null;
    }
  }

  // String list storage with error handling
  Future<void> setStringList(String key, List<String> value) async {
    try {
      final prefs = await _prefs;
      await prefs.setStringList(key, value);
    } catch (e) {
      debugPrint('Error setting string list ($key): $e');
    }
  }
}
