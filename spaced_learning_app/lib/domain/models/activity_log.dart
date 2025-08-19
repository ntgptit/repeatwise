import 'enums/activity_type.dart';

/// Activity log model for tracking user activities
class ActivityLog {
  final String id;
  final String userId;
  final String? setId;
  final ActivityType activityType;
  final String description;
  final Map<String, dynamic>? metadata;
  final DateTime createdAt;

  const ActivityLog({
    required this.id,
    required this.userId,
    this.setId,
    required this.activityType,
    required this.description,
    this.metadata,
    required this.createdAt,
  });

  ActivityLog copyWith({
    String? id,
    String? userId,
    String? setId,
    ActivityType? activityType,
    String? description,
    Map<String, dynamic>? metadata,
    DateTime? createdAt,
  }) {
    return ActivityLog(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      setId: setId ?? this.setId,
      activityType: activityType ?? this.activityType,
      description: description ?? this.description,
      metadata: metadata ?? this.metadata,
      createdAt: createdAt ?? this.createdAt,
    );
  }
}
