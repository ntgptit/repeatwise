import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:json_annotation/json_annotation.dart';

part 'set_cycle.freezed.dart';
part 'set_cycle.g.dart';

enum CycleStatus {
  @JsonValue('PENDING')
  pending,
  @JsonValue('IN_PROGRESS')
  inProgress,
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('FAILED')
  failed,
}

@freezed
abstract class SetCycle with _$SetCycle {
  const factory SetCycle({
    required String id,
    required String setId,
    required int cycleNo,
    required CycleStatus status,
    DateTime? startedAt,
    DateTime? completedAt,
    required DateTime createdAt,
    required DateTime updatedAt,
    @Default(0) int totalItems,
    @Default(0) int completedItems,
  }) = _SetCycle;

  factory SetCycle.fromJson(Map<String, dynamic> json) => _$SetCycleFromJson(json);
}

// Extension để thêm computed properties
extension SetCycleExtension on SetCycle {
  double get progress => totalItems > 0 ? completedItems / totalItems : 0.0;
  int get remainingItems => totalItems - completedItems;
  bool get isActive => status == CycleStatus.inProgress;
  bool get isCompleted => status == CycleStatus.completed;
  bool get isPending => status == CycleStatus.pending;
  bool get isFailed => status == CycleStatus.failed;
  bool get hasStarted => startedAt != null;
  bool get hasCompleted => completedAt != null;
  
  Duration? get duration {
    if (startedAt == null || completedAt == null) return null;
    return completedAt!.difference(startedAt!);
  }
  
  String get progressPercentage => '${(progress * 100).toInt()}%';
  
  String get statusDisplayName {
    switch (status) {
      case CycleStatus.pending:
        return 'Pending';
      case CycleStatus.inProgress:
        return 'In Progress';
      case CycleStatus.completed:
        return 'Completed';
      case CycleStatus.failed:
        return 'Failed';
    }
  }

  bool get isValid => id.isNotEmpty && setId.isNotEmpty && cycleNo > 0;
  bool get hasValidProgress => completedItems <= totalItems && completedItems >= 0;
  bool get canStart => status == CycleStatus.pending;
  bool get canComplete => status == CycleStatus.inProgress;
}

@freezed
abstract class SetCycleCreateRequest with _$SetCycleCreateRequest {
  const factory SetCycleCreateRequest({
    required String setId,
    int? cycleNo,
  }) = _SetCycleCreateRequest;

  factory SetCycleCreateRequest.fromJson(Map<String, dynamic> json) => 
      _$SetCycleCreateRequestFromJson(json);
}

// Extension cho SetCycleCreateRequest
extension SetCycleCreateRequestExtension on SetCycleCreateRequest {
  bool get isValid => setId.isNotEmpty;
  bool get hasValidSetId => setId.length > 0;
}
