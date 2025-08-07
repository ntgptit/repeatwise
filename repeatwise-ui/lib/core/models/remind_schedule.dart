import 'package:freezed_annotation/freezed_annotation.dart';

part 'remind_schedule.freezed.dart';
part 'remind_schedule.g.dart';

enum RemindStatus {
  @JsonValue('PENDING')
  pending,
  @JsonValue('SENT')
  sent,
  @JsonValue('FAILED')
  failed,
}

@freezed
abstract class RemindSchedule with _$RemindSchedule {
  const factory RemindSchedule({
    required String id,
    required String setId,
    required String userId,
    required DateTime remindTime,
    required RemindStatus status,
    String? message,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _RemindSchedule;

  factory RemindSchedule.fromJson(Map<String, dynamic> json) => 
      _$RemindScheduleFromJson(json);
}

// Extension để thêm computed properties
extension RemindScheduleExtension on RemindSchedule {
  bool get isPending => status == RemindStatus.pending;
  bool get isSent => status == RemindStatus.sent;
  bool get isFailed => status == RemindStatus.failed;
  bool get isOverdue => remindTime.isBefore(DateTime.now()) && isPending;
  bool get isUpcoming => remindTime.isAfter(DateTime.now()) && isPending;
  Duration get timeUntilRemind => remindTime.difference(DateTime.now());
  bool get isRecentlyCreated => DateTime.now().difference(createdAt).inDays < 1;
  
  String get statusDisplayName {
    switch (status) {
      case RemindStatus.pending:
        return 'Pending';
      case RemindStatus.sent:
        return 'Sent';
      case RemindStatus.failed:
        return 'Failed';
    }
  }
  
  String get displayMessage => message ?? 'Time to review your set!';
  bool get isValid => id.isNotEmpty && setId.isNotEmpty && userId.isNotEmpty;
  bool get hasValidRemindTime => remindTime.isAfter(DateTime.now());
  bool get canSend => isPending && !isOverdue;
  bool get canReschedule => isPending || isFailed;
}

@freezed
abstract class RemindScheduleCreateRequest with _$RemindScheduleCreateRequest {
  const factory RemindScheduleCreateRequest({
    required String setId,
    required DateTime remindTime,
    String? message,
  }) = _RemindScheduleCreateRequest;

  factory RemindScheduleCreateRequest.fromJson(Map<String, dynamic> json) => 
      _$RemindScheduleCreateRequestFromJson(json);
}

@freezed
abstract class RemindScheduleUpdateRequest with _$RemindScheduleUpdateRequest {
  const factory RemindScheduleUpdateRequest({
    DateTime? remindTime,
    String? message,
    RemindStatus? status,
  }) = _RemindScheduleUpdateRequest;

  factory RemindScheduleUpdateRequest.fromJson(Map<String, dynamic> json) => 
      _$RemindScheduleUpdateRequestFromJson(json);
}

// Extension cho RemindScheduleCreateRequest
extension RemindScheduleCreateRequestExtension on RemindScheduleCreateRequest {
  bool get isValid => setId.isNotEmpty && remindTime.isAfter(DateTime.now());
  bool get hasValidSetId => setId.isNotEmpty;
  bool get hasValidRemindTime => remindTime.isAfter(DateTime.now());
  bool get hasValidMessage => message == null || message!.length <= 200;
}

// Extension cho RemindScheduleUpdateRequest
extension RemindScheduleUpdateRequestExtension on RemindScheduleUpdateRequest {
  bool get hasValidRemindTime => remindTime == null || remindTime!.isAfter(DateTime.now());
  bool get hasValidMessage => message == null || message!.length <= 200;
  bool get hasChanges => remindTime != null || message != null || status != null;
}
