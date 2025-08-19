import '../../domain/models/auth_response.dart';
import '../../domain/models/user.dart';

/// Mapper class for authentication-related data transformations
class AuthMapper {
  /// Convert API response to AuthResponse domain model
  static AuthResponse fromApiResponse(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] as String,
      refreshToken: json['refreshToken'] as String?,
      user: UserMapper.fromJson(json['user'] as Map<String, dynamic>),
    );
  }

  /// Convert AuthResponse to API request format for token refresh
  static Map<String, dynamic> toRefreshTokenRequest(String refreshToken) {
    return {'refreshToken': refreshToken};
  }

  /// Convert login credentials to API request format
  static Map<String, dynamic> toLoginRequest(
    String usernameOrEmail,
    String password,
  ) {
    return {'usernameOrEmail': usernameOrEmail, 'password': password};
  }

  /// Convert registration data to API request format
  static Map<String, dynamic> toRegisterRequest({
    required String username,
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) {
    return {
      'username': username,
      'email': email,
      'password': password,
      'firstName': firstName,
      'lastName': lastName,
    };
  }
}

/// Mapper class for User entity
class UserMapper {
  /// Convert JSON to User domain model
  static User fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as String,
      email: json['email'] as String,
      username: json['username'] as String,
      firstName: json['firstName'] as String?,
      lastName: json['lastName'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
    );
  }

  /// Convert User domain model to JSON
  static Map<String, dynamic> toJson(User user) {
    return {
      'id': user.id,
      'email': user.email,
      'username': user.username,
      'firstName': user.firstName,
      'lastName': user.lastName,
      'createdAt': user.createdAt?.toIso8601String(),
      'updatedAt': user.updatedAt?.toIso8601String(),
    };
  }
}
