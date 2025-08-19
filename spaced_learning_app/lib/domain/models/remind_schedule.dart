import 'enums/remind_status.dart';

/// Reminder schedule model for managing learning reminders
class RemindSchedule {
  final String id;
  final String setId;
  final String userId;
  final DateTime remindDate;
  final RemindStatus status;
  final DateTime createdAt;
  final DateTime updatedAt;

  const RemindSchedule({
    required this.id,
    required this.setId,
    required this.userId,
    required this.remindDate,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
  });

  RemindSchedule copyWith({
    String? id,
    String? setId,
    String? userId,
    DateTime? remindDate,
    RemindStatus? status,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return RemindSchedule(
      id: id ?? this.id,
      setId: setId ?? this.setId,
      userId: userId ?? this.userId,
      remindDate: remindDate ?? this.remindDate,
      status: status ?? this.status,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}
