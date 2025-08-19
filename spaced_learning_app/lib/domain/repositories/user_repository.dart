import 'package:spaced_learning_app/domain/models/user.dart';

abstract class UserRepository {
  Future<User> getCurrentUser();


  Future<User> updateUser(String id, {String? displayName, String? password});

  Future<bool> checkEmailExists(String email);
}
