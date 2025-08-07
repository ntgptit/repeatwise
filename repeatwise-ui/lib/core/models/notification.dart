import 'package:freezed_annotation/freezed_annotation.dart';

part 'notification.freezed.dart';
part 'notification.g.dart';

enum NotificationType {
  @JsonValue('SET_REMINDER')
  setReminder,
  @JsonValue('CYCLE_COMPLETED')
  cycleCompleted,
  @JsonValue('SET_COMPLETED')
  setCompleted,
  @JsonValue('GENERAL')
  general,
}

enum NotificationStatus {
  @JsonValue('UNREAD')
  unread,
  @JsonValue('READ')
  read,
  @JsonValue('ARCHIVED')
  archived,
}

@freezed
abstract class Notification with _$Notification {
  const factory Notification({
    required String id,
    required String userId,
    required String title,
    required String message,
    required NotificationType type,
    required NotificationStatus status,
    String? setId,
    String? cycleId,
    Map<String, dynamic>? metadata,
    required DateTime createdAt,
    DateTime? readAt,
  }) = _Notification;

  factory Notification.fromJson(Map<String, dynamic> json) =>
      _$NotificationFromJson(json);
}

// Extension để thêm computed properties
extension NotificationExtension on Notification {
  bool get isUnread => status == NotificationStatus.unread;
  bool get isRead => status == NotificationStatus.read;
  bool get isArchived => status == NotificationStatus.archived;
  bool get isRecentlyCreated => DateTime.now().difference(createdAt).inDays < 1;
  bool get hasSetId => setId != null && setId!.isNotEmpty;
  bool get hasCycleId => cycleId != null && cycleId!.isNotEmpty;
  bool get hasMetadata => metadata != null && metadata!.isNotEmpty;
  
  String get typeDisplayName {
    switch (type) {
      case NotificationType.setReminder:
        return 'Set Reminder';
      case NotificationType.cycleCompleted:
        return 'Cycle Completed';
      case NotificationType.setCompleted:
        return 'Set Completed';
      case NotificationType.general:
        return 'General';
    }
  }
  
  String get statusDisplayName {
    switch (status) {
      case NotificationStatus.unread:
        return 'Unread';
      case NotificationStatus.read:
        return 'Read';
      case NotificationStatus.archived:
        return 'Archived';
    }
  }
  
  bool get isValid => id.isNotEmpty && userId.isNotEmpty && 
                     title.isNotEmpty && message.isNotEmpty;
  bool get canMarkAsRead => isUnread;
  bool get canArchive => isRead || isUnread;
}

@freezed
abstract class NotificationCreateRequest with _$NotificationCreateRequest {
  const factory NotificationCreateRequest({
    required String userId,
    required String title,
    required String message,
    required NotificationType type,
    String? setId,
    String? cycleId,
    Map<String, dynamic>? metadata,
  }) = _NotificationCreateRequest;

  factory NotificationCreateRequest.fromJson(Map<String, dynamic> json) =>
      _$NotificationCreateRequestFromJson(json);
}

@freezed
abstract class NotificationUpdateRequest with _$NotificationUpdateRequest {
  const factory NotificationUpdateRequest({
    NotificationStatus? status,
    DateTime? readAt,
  }) = _NotificationUpdateRequest;

  factory NotificationUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$NotificationUpdateRequestFromJson(json);
}

// Extensions for validation
extension NotificationCreateRequestExtension on NotificationCreateRequest {
  bool get isValid => userId.isNotEmpty && title.isNotEmpty && message.isNotEmpty;
  bool get hasValidTitle => title.length >= 3 && title.length <= 100;
  bool get hasValidMessage => message.length <= 500;
  bool get hasValidSetId => setId == null || setId!.isNotEmpty;
  bool get hasValidCycleId => cycleId == null || cycleId!.isNotEmpty;
}

extension NotificationUpdateRequestExtension on NotificationUpdateRequest {
  bool get hasChanges => status != null || readAt != null;
  bool get isValidReadAt => readAt == null || readAt!.isBefore(DateTime.now());
}
