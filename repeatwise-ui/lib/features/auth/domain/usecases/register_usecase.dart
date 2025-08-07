import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/models/api_response.dart';
import '../../../../core/models/user.dart';
import '../../data/repositories/auth_repository_impl.dart';
import '../repositories/auth_repository.dart';

part 'register_usecase.g.dart';

class RegisterUseCase {
  final AuthRepository _authRepository;

  RegisterUseCase(this._authRepository);

  Future<ApiResponse<User>> call(String name, String email, String password) async {
    return await _authRepository.register(name, email, password);
  }
}

@riverpod
RegisterUseCase registerUseCase(Ref ref) {
  final authRepository = ref.watch(authRepositoryProvider);
  return RegisterUseCase(authRepository);
}
