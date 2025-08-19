import 'enums/review_status.dart';
import 'enums/skip_reason.dart';

/// Review history model for tracking review sessions
class ReviewHistory {
  final String id;
  final String setId;
  final String setName;
  final int cycleNo;
  final int reviewNo;
  final int score;
  final ReviewStatus status;
  final String? note;
  final SkipReason? skipReason;
  final DateTime createdAt;
  final DateTime updatedAt;

  const ReviewHistory({
    required this.id,
    required this.setId,
    required this.setName,
    required this.cycleNo,
    required this.reviewNo,
    required this.score,
    required this.status,
    this.note,
    this.skipReason,
    required this.createdAt,
    required this.updatedAt,
  });

  ReviewHistory copyWith({
    String? id,
    String? setId,
    String? setName,
    int? cycleNo,
    int? reviewNo,
    int? score,
    ReviewStatus? status,
    String? note,
    SkipReason? skipReason,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return ReviewHistory(
      id: id ?? this.id,
      setId: setId ?? this.setId,
      setName: setName ?? this.setName,
      cycleNo: cycleNo ?? this.cycleNo,
      reviewNo: reviewNo ?? this.reviewNo,
      score: score ?? this.score,
      status: status ?? this.status,
      note: note ?? this.note,
      skipReason: skipReason ?? this.skipReason,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}
