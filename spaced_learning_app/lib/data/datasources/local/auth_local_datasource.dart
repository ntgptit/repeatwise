import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../domain/models/auth_response.dart';
import '../../../domain/models/user.dart';

abstract class AuthLocalDataSource {
  Future<void> saveAuthData(AuthResponse authResponse);
  Future<AuthResponse?> getAuthData();
  Future<void> clearAuthData();
  Future<String?> getAccessToken();
  Future<String?> getRefreshToken();
  Future<User?> getUser();
}

class AuthLocalDataSourceImpl implements AuthLocalDataSource {
  static const String _accessTokenKey = 'access_token';
  static const String _refreshTokenKey = 'refresh_token';
  static const String _userDataKey = 'user_data';

  @override
  Future<void> saveAuthData(AuthResponse authResponse) async {
    final prefs = await SharedPreferences.getInstance();
    
    await prefs.setString(_accessTokenKey, authResponse.token);
    if (authResponse.refreshToken != null) {
      await prefs.setString(_refreshTokenKey, authResponse.refreshToken!);
    }
    
    await prefs.setString(_userDataKey, jsonEncode(authResponse.user.toJson()));
  }

  @override
  Future<AuthResponse?> getAuthData() async {
    final prefs = await SharedPreferences.getInstance();
    
    final accessToken = prefs.getString(_accessTokenKey);
    final refreshToken = prefs.getString(_refreshTokenKey);
    final userData = prefs.getString(_userDataKey);
    
    if (accessToken == null || userData == null) {
      return null;
    }
    
    return AuthResponse(
      token: accessToken,
      refreshToken: refreshToken,
      user: User.fromJson(jsonDecode(userData) as Map<String, dynamic>),
    );
  }

  @override
  Future<void> clearAuthData() async {
    final prefs = await SharedPreferences.getInstance();
    
    await prefs.remove(_accessTokenKey);
    await prefs.remove(_refreshTokenKey);
    await prefs.remove(_userDataKey);
  }

  @override
  Future<String?> getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_accessTokenKey);
  }

  @override
  Future<String?> getRefreshToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_refreshTokenKey);
  }

  @override
  Future<User?> getUser() async {
    final prefs = await SharedPreferences.getInstance();
    final userData = prefs.getString(_userDataKey);
    
    if (userData == null) {
      return null;
    }
    
    return User.fromJson(jsonDecode(userData) as Map<String, dynamic>);
  }
}
