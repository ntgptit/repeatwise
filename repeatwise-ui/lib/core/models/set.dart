import 'package:freezed_annotation/freezed_annotation.dart';
import 'set_cycle.dart';

part 'set.freezed.dart';
part 'set.g.dart';

enum SetStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
  @JsonValue('ARCHIVED')
  archived,
  @JsonValue('PAUSED')
  paused,
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('LEARNING')
  learning,
}

@freezed
abstract class Set with _$Set {
  const factory Set({
    required String id,
    required String name,
    String? description,
    required SetStatus status,
    required String userId,
    required DateTime createdAt,
    required DateTime updatedAt,
    DateTime? lastReviewedAt,
    @Default(0) int wordCount,
    @Default(1) int currentCycle,
    DateTime? lastCycleEndDate,
    DateTime? nextCycleStartDate,
    List<SetCycle>? cycles,
  }) = _Set;

  factory Set.fromJson(Map<String, dynamic> json) => _$SetFromJson(json);
}

// Extension để thêm computed properties
extension SetExtension on Set {
  double get progress => wordCount > 0
      ? (cycles?.where((c) => c.isCompleted).length ?? 0) / wordCount
      : 0.0;
  int get completedCycles => cycles?.where((c) => c.isCompleted).length ?? 0;
  int get remainingCycles => wordCount - completedCycles;
  bool get isActive => status == SetStatus.active;
  bool get isCompleted => completedCycles >= wordCount && wordCount > 0;
  bool get needsReview => isActive && !isCompleted;
  bool get isRecentlyUpdated => DateTime.now().difference(updatedAt).inDays < 1;
  bool get hasBeenReviewed => lastReviewedAt != null;
  String get progressPercentage => '${(progress * 100).toInt()}%';
  bool get isValid => id.isNotEmpty && name.isNotEmpty;
  bool get hasValidProgress =>
      completedCycles <= wordCount && completedCycles >= 0;
  bool get isLearning => status == SetStatus.learning;
  bool get isMastered => status == SetStatus.completed;
}

@freezed
abstract class SetCreateRequest with _$SetCreateRequest {
  const factory SetCreateRequest({
    required String name,
    required String description,
  }) = _SetCreateRequest;

  factory SetCreateRequest.fromJson(Map<String, dynamic> json) =>
      _$SetCreateRequestFromJson(json);
}

// Extension cho SetCreateRequest
extension SetCreateRequestExtension on SetCreateRequest {
  bool get isValid => name.isNotEmpty && description.isNotEmpty;
  bool get hasValidName => name.length >= 3 && name.length <= 100;
  bool get hasValidDescription => description.length <= 500;
}

@freezed
abstract class SetUpdateRequest with _$SetUpdateRequest {
  const factory SetUpdateRequest({
    String? name,
    String? description,
    SetStatus? status,
  }) = _SetUpdateRequest;

  factory SetUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$SetUpdateRequestFromJson(json);
}

// Extension cho SetUpdateRequest
extension SetUpdateRequestExtension on SetUpdateRequest {
  bool get hasValidName =>
      name == null || (name!.length >= 3 && name!.length <= 100);
  bool get hasValidDescription =>
      description == null || description!.length <= 500;
  bool get hasChanges => name != null || description != null || status != null;
}
