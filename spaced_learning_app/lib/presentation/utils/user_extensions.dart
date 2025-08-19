// lib/presentation/screens/profile/utils/user_extensions.dart

import 'package:spaced_learning_app/domain/models/user.dart';

extension UserExtension on User {
  String get initials {
    final name = displayName ?? email.split('@').first;
    final parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts.first[0] + parts.last[0]).toUpperCase();
    }
    return name.isNotEmpty ? name[0].toUpperCase() : '';
  }
}
