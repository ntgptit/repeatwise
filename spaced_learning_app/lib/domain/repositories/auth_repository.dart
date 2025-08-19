import 'package:spaced_learning_app/domain/models/auth_response.dart';

abstract class AuthRepository {
  Future<AuthResponse> login(String usernameOrEmail, String password);

  Future<AuthResponse> register(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
  );

  Future<AuthResponse> refreshToken(String refreshToken);

  Future<bool> validateToken(String token);

  String? getUsernameFromToken(String token);
}
