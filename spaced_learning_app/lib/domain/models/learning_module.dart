import 'package:spaced_learning_app/domain/models/progress.dart'; // Import CycleStudied enum

CycleStudied? _parseCycleStudied(String? value) {
  if (value == null) return null;
  const map = {
    'FIRST_TIME': CycleStudied.firstTime,
    'FIRST_REVIEW': CycleStudied.firstReview,
    'SECOND_REVIEW': CycleStudied.secondReview,
    'THIRD_REVIEW': CycleStudied.thirdReview,
    'MORE_THAN_THREE_REVIEWS': CycleStudied.moreThanThreeReviews,
  };
  return map[value.toUpperCase()]; // Case-insensitive matching
}

String? _serializeCycleStudied(CycleStudied? cycle) {
  if (cycle == null) return null;
  const map = {
    CycleStudied.firstTime: 'FIRST_TIME',
    CycleStudied.firstReview: 'FIRST_REVIEW',
    CycleStudied.secondReview: 'SECOND_REVIEW',
    CycleStudied.thirdReview: 'THIRD_REVIEW',
    CycleStudied.moreThanThreeReviews: 'MORE_THAN_THREE_REVIEWS',
  };
  return map[cycle];
}

class LearningModule {
  final String bookName;
  final int bookNo;
  final String moduleTitle;
  final int moduleNo;
  final int moduleWordCount;
  final CycleStudied? progressCyclesStudied; // Changed from String?
  final DateTime? progressNextStudyDate;
  final DateTime? progressFirstLearningDate;
  final int? progressLatestPercentComplete;
  final int progressDueTaskCount;
  final String moduleId;
  final List<String>
  studyHistory; // Keep as List<String> if dates are stored as strings

  const LearningModule({
    required this.bookName,
    required this.bookNo,
    required this.moduleTitle,
    required this.moduleNo,
    required this.moduleWordCount,
    this.progressCyclesStudied, // Updated type
    this.progressNextStudyDate,
    this.progressFirstLearningDate,
    this.progressLatestPercentComplete,
    required this.progressDueTaskCount,
    required this.moduleId,
    required this.studyHistory,
  });

  factory LearningModule.fromJson(Map<String, dynamic> json) {
    return LearningModule(
      bookName: json['bookName'] ?? '',
      bookNo: json['bookNo'] ?? 0,
      moduleTitle: json['moduleTitle'] ?? '',
      moduleNo: json['moduleNo'] ?? 0,
      moduleWordCount: json['moduleWordCount'] ?? 0,
      progressCyclesStudied: _parseCycleStudied(
        json['progressCyclesStudied'] as String?,
      ),
      progressNextStudyDate:
          json['progressNextStudyDate'] != null
              ? DateTime.tryParse(json['progressNextStudyDate']) // Use tryParse
              : null,
      progressFirstLearningDate:
          json['progressFirstLearningDate'] != null
              ? DateTime.tryParse(
                json['progressFirstLearningDate'],
              ) // Use tryParse
              : null,
      progressLatestPercentComplete:
          json['progressLatestPercentComplete'] as int?,
      progressDueTaskCount: json['progressDueTaskCount'] ?? 0,
      moduleId: json['moduleId'] ?? '',
      studyHistory:
          (json['studyHistory'] as List<dynamic>?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'bookName': bookName,
      'bookNo': bookNo,
      'moduleTitle': moduleTitle,
      'moduleNo': moduleNo,
      'moduleWordCount': moduleWordCount,
      'progressCyclesStudied': _serializeCycleStudied(progressCyclesStudied),
      'progressNextStudyDate': progressNextStudyDate?.toIso8601String(),
      'progressFirstLearningDate': progressFirstLearningDate?.toIso8601String(),
      'progressLatestPercentComplete': progressLatestPercentComplete,
      'progressDueTaskCount': progressDueTaskCount,
      'moduleId': moduleId,
      'studyHistory': studyHistory,
    };
  }

  bool isDueToday() {
    if (progressNextStudyDate == null) return false;
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final studyDate = DateTime(
      progressNextStudyDate!.year,
      progressNextStudyDate!.month,
      progressNextStudyDate!.day,
    );
    return today.isAtSameMomentAs(studyDate);
  }

  bool isOverdue() {
    if (progressNextStudyDate == null) return false;
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final studyDate = DateTime(
      progressNextStudyDate!.year,
      progressNextStudyDate!.month,
      progressNextStudyDate!.day,
    );
    return studyDate.isBefore(today);
  }

  bool isDueThisWeek() {
    if (progressNextStudyDate == null) return false;
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final weekStart = today.subtract(
      Duration(days: today.weekday - 1),
    ); // Assuming Monday is 1
    final weekEnd = weekStart.add(const Duration(days: 6));
    final studyDate = DateTime(
      progressNextStudyDate!.year,
      progressNextStudyDate!.month,
      progressNextStudyDate!.day,
    );
    return !studyDate.isBefore(weekStart) && !studyDate.isAfter(weekEnd);
  }

  bool isDueThisMonth() {
    if (progressNextStudyDate == null) return false;
    final now = DateTime.now();
    return progressNextStudyDate!.year == now.year &&
        progressNextStudyDate!.month == now.month;
  }

  bool isNewModule() {
    return progressFirstLearningDate == null &&
        (progressLatestPercentComplete ?? 0) == 0;
  }

  bool isCompleted() {
    return (progressLatestPercentComplete ?? 0) >= 100;
  }

  int? getDaysSinceFirstLearning() {
    if (progressFirstLearningDate == null) return null;
    return DateTime.now().difference(progressFirstLearningDate!).inDays;
  }
}
