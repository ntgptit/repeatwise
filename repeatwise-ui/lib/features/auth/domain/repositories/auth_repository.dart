import '../../../../core/models/api_response.dart';
import '../../../../core/models/user.dart';

abstract class AuthRepository {
  Future<ApiResponse<User>> login(String emailOrUsername, String password);
  Future<ApiResponse<User>> register(String name, String email, String password);
  Future<ApiResponse<User>> getCurrentUser();
  Future<void> logout();
  Future<bool> isAuthenticated();
}
