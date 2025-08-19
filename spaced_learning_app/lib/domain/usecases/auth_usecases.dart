import '../repositories/auth_repository.dart';
import '../models/auth_response.dart';

/// Use case for user authentication
class LoginUseCase {
  final AuthRepository _authRepository;

  LoginUseCase(this._authRepository);

  Future<AuthResponse> execute(String usernameOrEmail, String password) async {
    return await _authRepository.login(usernameOrEmail, password);
  }
}

/// Use case for user registration
class RegisterUseCase {
  final AuthRepository _authRepository;

  RegisterUseCase(this._authRepository);

  Future<AuthResponse> execute({
    required String username,
    required String email,
    required String password,
    required String firstName,
    required String lastName,
  }) async {
    return await _authRepository.register(
      username,
      email,
      password,
      firstName,
      lastName,
    );
  }
}

/// Use case for refreshing authentication token
class RefreshTokenUseCase {
  final AuthRepository _authRepository;

  RefreshTokenUseCase(this._authRepository);

  Future<AuthResponse> execute(String refreshToken) async {
    return await _authRepository.refreshToken(refreshToken);
  }
}

/// Use case for validating authentication token
class ValidateTokenUseCase {
  final AuthRepository _authRepository;

  ValidateTokenUseCase(this._authRepository);

  Future<bool> execute(String token) async {
    return await _authRepository.validateToken(token);
  }
}

/// Use case for getting username from token
class GetUsernameFromTokenUseCase {
  final AuthRepository _authRepository;

  GetUsernameFromTokenUseCase(this._authRepository);

  String? execute(String token) {
    return _authRepository.getUsernameFromToken(token);
  }
}
