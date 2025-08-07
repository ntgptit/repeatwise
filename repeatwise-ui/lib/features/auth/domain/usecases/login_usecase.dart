import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/models/api_response.dart';
import '../../../../core/models/user.dart';
import '../../data/repositories/auth_repository_impl.dart';
import '../repositories/auth_repository.dart';

part 'login_usecase.g.dart';

class LoginUseCase {
  final AuthRepository _authRepository;

  LoginUseCase(this._authRepository);

  Future<ApiResponse<User>> call(String email, String password) async {
    return await _authRepository.login(email, password);
  }
}

@riverpod
LoginUseCase loginUseCase(Ref ref) {
  final authRepository = ref.watch(authRepositoryProvider);
  return LoginUseCase(authRepository);
}
