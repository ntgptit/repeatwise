import '../models/enums/set_category.dart';
import '../models/enums/set_status.dart';

/// LearningSet entity representing a learning set in the domain
class LearningSetEntity {
  final String id;
  final String title;
  final String description;
  final SetCategory category;
  final SetStatus status;
  final String userId;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const LearningSetEntity({
    required this.id,
    required this.title,
    required this.description,
    required this.category,
    required this.status,
    required this.userId,
    this.createdAt,
    this.updatedAt,
  });

  /// Create LearningSetEntity from JSON
  factory LearningSetEntity.fromJson(Map<String, dynamic> json) {
    return LearningSetEntity(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      category: SetCategory.values.firstWhere(
        (e) => e.name == json['category'],
        orElse: () => SetCategory.other,
      ),
      status: SetStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => SetStatus.notStarted,
      ),
      userId: json['userId'] as String,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
    );
  }

  /// Convert LearningSetEntity to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'category': category.name,
      'status': status.name,
      'userId': userId,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  /// Create a copy of LearningSetEntity with updated fields
  LearningSetEntity copyWith({
    String? id,
    String? title,
    String? description,
    SetCategory? category,
    SetStatus? status,
    String? userId,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return LearningSetEntity(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      category: category ?? this.category,
      status: status ?? this.status,
      userId: userId ?? this.userId,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is LearningSetEntity &&
        other.id == id &&
        other.title == title &&
        other.userId == userId;
  }

  @override
  int get hashCode {
    return id.hashCode ^ title.hashCode ^ userId.hashCode;
  }

  @override
  String toString() {
    return 'LearningSetEntity(id: $id, title: $title, category: $category)';
  }
}
