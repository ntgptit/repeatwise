import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/models/api_response.dart';
import '../../../../core/models/user.dart';
import '../../../../core/services/api_repository.dart';
import '../../../../core/services/storage_service.dart';
import '../../domain/repositories/auth_repository.dart';

part 'auth_repository_impl.g.dart';

class AuthRepositoryImpl implements AuthRepository {
  final ApiRepository _apiRepository;
  final StorageService _storageService;

  AuthRepositoryImpl(this._apiRepository, this._storageService);

  @override
  Future<ApiResponse<User>> login(String emailOrUsername, String password) async {
    final response = await _apiRepository.login(emailOrUsername, password);
    
    if (response.isSuccess) {
      // Save user data to local storage
      await _storageService.saveUser(response.data!.toJson());
      // Token is already saved by ApiRepository
    }
    
    return response;
  }

  @override
  Future<ApiResponse<User>> register(String name, String email, String password) async {
    final response = await _apiRepository.register(name, email, password);
    
    if (response.isSuccess) {
      // Save user data to local storage
      await _storageService.saveUser(response.data!.toJson());
      // Token is already saved by ApiRepository
    }
    
    return response;
  }

  @override
  Future<void> logout() async {
    await _storageService.removeToken();
    await _storageService.removeUser();
  }

  @override
  Future<ApiResponse<User>> getCurrentUser() async {
    return await _apiRepository.getCurrentUser();
  }

  @override
  Future<bool> isAuthenticated() async {
    final token = await _storageService.getToken();
    return token != null;
  }
}

@riverpod
AuthRepository authRepository(Ref ref) {
  final apiRepository = ref.watch(apiRepositoryProvider);
  final storageService = ref.watch(storageServiceProvider);
  return AuthRepositoryImpl(apiRepository, storageService);
}
