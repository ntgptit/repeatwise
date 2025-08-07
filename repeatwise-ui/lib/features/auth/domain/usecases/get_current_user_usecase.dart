import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../../../core/models/api_response.dart';
import '../../../../core/models/user.dart';
import '../../data/repositories/auth_repository_impl.dart';
import '../repositories/auth_repository.dart';

part 'get_current_user_usecase.g.dart';

class GetCurrentUserUseCase {
  final AuthRepository _authRepository;

  GetCurrentUserUseCase(this._authRepository);

  Future<ApiResponse<User>> call() async {
    return await _authRepository.getCurrentUser();
  }
}

@riverpod
GetCurrentUserUseCase getCurrentUserUseCase(Ref ref) {
  final authRepository = ref.watch(authRepositoryProvider);
  return GetCurrentUserUseCase(authRepository);
}
