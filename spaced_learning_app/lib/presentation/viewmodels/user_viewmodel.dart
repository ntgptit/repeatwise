// lib/presentation/viewmodels/user_viewmodel.dart
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/domain/models/user.dart';

import '../../core/di/providers.dart';

part 'user_viewmodel.g.dart';

@riverpod
class UserState extends _$UserState {
  @override
  Future<User?> build() async {
    return loadCurrentUser();
  }

  Future<User?> loadCurrentUser() async {
    state = const AsyncValue.loading();
    try {
      final user = await ref.read(userRepositoryProvider).getCurrentUser();
      state = AsyncValue.data(user);
      return user;
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
      return null;
    }
  }

  Future<bool> updateProfile({String? displayName, String? password}) async {
    if (state.value == null) {
      state = AsyncValue.error('User is not loaded', StackTrace.current);
      return false;
    }

    state = const AsyncValue.loading();
    try {
      final result = await ref
          .read(userRepositoryProvider)
          .updateUser(
            state.value!.id,
            displayName: displayName,
            password: password,
          );
      state = AsyncValue.data(result);
      return true;
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
      return false;
    }
  }

  // Thêm phương thức clearError để xử lý lỗi
  void clearError() {
    // Nếu state hiện tại đang có lỗi, chuyển về trạng thái loading
    // và sau đó gọi lại loadCurrentUser để refresh
    if (state.hasError) {
      state = const AsyncValue.loading();
      loadCurrentUser();
    }
  }
}

@riverpod
class UserError extends _$UserError {
  @override
  String? build() {
    final userState = ref.watch(userStateProvider);
    return userState.hasError ? userState.error.toString() : null;
  }

  void clearError() {
    state = null;
  }
}
