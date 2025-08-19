/// Authentication request model for login
class AuthRequest {
  final String usernameOrEmail;
  final String password;

  const AuthRequest({
    required this.usernameOrEmail,
    required this.password,
  });

  Map<String, dynamic> toJson() {
    return {
      'usernameOrEmail': usernameOrEmail,
      'password': password,
    };
  }
}
