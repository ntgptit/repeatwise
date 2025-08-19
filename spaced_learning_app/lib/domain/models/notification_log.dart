import 'enums/notification_type.dart';
import 'enums/notification_status.dart';

/// Notification log model for tracking notification delivery status
class NotificationLog {
  final String id;
  final String userId;
  final String? setId;
  final NotificationType type;
  final NotificationStatus status;
  final String? errorMessage;
  final DateTime createdAt;

  const NotificationLog({
    required this.id,
    required this.userId,
    this.setId,
    required this.type,
    required this.status,
    this.errorMessage,
    required this.createdAt,
  });

  NotificationLog copyWith({
    String? id,
    String? userId,
    String? setId,
    NotificationType? type,
    NotificationStatus? status,
    String? errorMessage,
    DateTime? createdAt,
  }) {
    return NotificationLog(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      setId: setId ?? this.setId,
      type: type ?? this.type,
      status: status ?? this.status,
      errorMessage: errorMessage ?? this.errorMessage,
      createdAt: createdAt ?? this.createdAt,
    );
  }
}
