import 'package:freezed_annotation/freezed_annotation.dart';

part 'user.freezed.dart';
part 'user.g.dart';

@freezed
abstract class User with _$User {
  const factory User({
    required String id,
    required String name,
    required String username,
    required String email,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _User;

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
}

// Extension để thêm computed properties
extension UserExtension on User {
  String get displayName => name.isNotEmpty ? name : email.split('@').first;
  String get initials => name
      .split(' ')
      .map((e) => e.isNotEmpty ? e[0] : '')
      .join('')
      .toUpperCase();
  bool get isRecentlyCreated => DateTime.now().difference(createdAt).inDays < 7;
  bool get isRecentlyUpdated => DateTime.now().difference(updatedAt).inDays < 1;
  bool get isValid => id.isNotEmpty && name.isNotEmpty && email.isNotEmpty;
  bool get hasValidEmail =>
      RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(email);
}
