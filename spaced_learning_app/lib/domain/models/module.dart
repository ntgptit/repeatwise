import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';

part 'module.freezed.dart';
part 'module.g.dart';

@freezed
abstract class ModuleSummary with _$ModuleSummary {
  const factory ModuleSummary({
    required String id,
    required String bookId,
    required int moduleNo,
    required String title,
    int? wordCount,
    String? url, // Thêm trường url
    DateTime? createdAt,
    DateTime? updatedAt,
  }) = _ModuleSummary;

  factory ModuleSummary.fromJson(Map<String, dynamic> json) =>
      _$ModuleSummaryFromJson(json);
}

@freezed
abstract class ModuleDetail with _$ModuleDetail {
  const factory ModuleDetail({
    required String id,
    required String bookId,
    String? bookName,
    required int moduleNo,
    required String title,
    int? wordCount,
    String? url, // Thêm trường url
    DateTime? createdAt,
    DateTime? updatedAt,
    @Default([]) List<ProgressSummary> progress,
  }) = _ModuleDetail;

  factory ModuleDetail.fromJson(Map<String, dynamic> json) =>
      _$ModuleDetailFromJson(json);
}
