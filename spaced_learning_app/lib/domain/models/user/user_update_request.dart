/// User update request model for profile modifications
class UserUpdateRequest {
  final String? username;
  final String? email;
  final String? firstName;
  final String? lastName;
  final String? displayName;
  final String? password;

  const UserUpdateRequest({
    this.username,
    this.email,
    this.firstName,
    this.lastName,
    this.displayName,
    this.password,
  });

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> json = {};
    
    if (username != null) json['username'] = username;
    if (email != null) json['email'] = email;
    if (firstName != null) json['firstName'] = firstName;
    if (lastName != null) json['lastName'] = lastName;
    if (displayName != null) json['displayName'] = displayName;
    if (password != null) json['password'] = password;
    
    return json;
  }
}
