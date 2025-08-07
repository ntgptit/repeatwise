import 'package:freezed_annotation/freezed_annotation.dart';

part 'set.freezed.dart';
part 'set.g.dart';

enum SetStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
  @JsonValue('ARCHIVED')
  archived, paused, completed,
}

@freezed
abstract class Set with _$Set {
  const factory Set({
    required String id,
    required String name,
    required String description,
    required SetStatus status,
    required String userId,
    required DateTime createdAt,
    required DateTime updatedAt,
    DateTime? lastReviewedAt,
    @Default(0) int totalItems,
    @Default(0) int completedItems,
  }) = _Set;

  factory Set.fromJson(Map<String, dynamic> json) => _$SetFromJson(json);
}

// Extension để thêm computed properties
extension SetExtension on Set {
  double get progress => totalItems > 0 ? completedItems / totalItems : 0.0;
  int get remainingItems => totalItems - completedItems;
  bool get isActive => status == SetStatus.active;
  bool get isCompleted => completedItems >= totalItems && totalItems > 0;
  bool get needsReview => isActive && !isCompleted;
  bool get isRecentlyUpdated => DateTime.now().difference(updatedAt).inDays < 1;
  bool get hasBeenReviewed => lastReviewedAt != null;
  String get progressPercentage => '${(progress * 100).toInt()}%';
  bool get isValid => id.isNotEmpty && name.isNotEmpty && description.isNotEmpty;
  bool get hasValidProgress => completedItems <= totalItems && completedItems >= 0;
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
  bool get hasValidName => name == null || (name!.length >= 3 && name!.length <= 100);
  bool get hasValidDescription => description == null || description!.length <= 500;
  bool get hasChanges => name != null || description != null || status != null;
}
