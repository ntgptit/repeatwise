import 'package:spaced_learning_app/domain/models/user.dart';

/// Authentication response model containing token and user information
class AuthResponse {
  final String token;
  final String? refreshToken;
  final User user;

  const AuthResponse({
    required this.token,
    this.refreshToken,
    required this.user,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] as String,
      refreshToken: json['refreshToken'] as String?,
      user: User.fromJson(json['user'] as Map<String, dynamic>),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'token': token,
      'refreshToken': refreshToken,
      'user': user.toJson(),
    };
  }

  AuthResponse copyWith({
    String? token,
    String? refreshToken,
    User? user,
  }) {
    return AuthResponse(
      token: token ?? this.token,
      refreshToken: refreshToken ?? this.refreshToken,
      user: user ?? this.user,
    );
  }
}
