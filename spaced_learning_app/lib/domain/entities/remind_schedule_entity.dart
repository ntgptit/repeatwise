import '../models/enums/remind_status.dart';

/// RemindSchedule entity representing a reminder schedule in the domain
class RemindScheduleEntity {
  final String id;
  final String userId;
  final String learningSetId;
  final DateTime scheduledTime;
  final RemindStatus status;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const RemindScheduleEntity({
    required this.id,
    required this.userId,
    required this.learningSetId,
    required this.scheduledTime,
    required this.status,
    this.createdAt,
    this.updatedAt,
  });

  /// Create RemindScheduleEntity from JSON
  factory RemindScheduleEntity.fromJson(Map<String, dynamic> json) {
    return RemindScheduleEntity(
      id: json['id'] as String,
      userId: json['userId'] as String,
      learningSetId: json['learningSetId'] as String,
      scheduledTime: DateTime.parse(json['scheduledTime'] as String),
      status: RemindStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => RemindStatus.pending,
      ),
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String)
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
    );
  }

  /// Convert RemindScheduleEntity to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'learningSetId': learningSetId,
      'scheduledTime': scheduledTime.toIso8601String(),
      'status': status.name,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  /// Create a copy of RemindScheduleEntity with updated fields
  RemindScheduleEntity copyWith({
    String? id,
    String? userId,
    String? learningSetId,
    DateTime? scheduledTime,
    RemindStatus? status,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return RemindScheduleEntity(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      learningSetId: learningSetId ?? this.learningSetId,
      scheduledTime: scheduledTime ?? this.scheduledTime,
      status: status ?? this.status,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is RemindScheduleEntity &&
        other.id == id &&
        other.userId == userId &&
        other.learningSetId == learningSetId;
  }

  @override
  int get hashCode {
    return id.hashCode ^ userId.hashCode ^ learningSetId.hashCode;
  }

  @override
  String toString() {
    return 'RemindScheduleEntity(id: $id, scheduledTime: $scheduledTime, status: $status)';
  }
}
