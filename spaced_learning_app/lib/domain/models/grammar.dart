import 'package:freezed_annotation/freezed_annotation.dart';

part 'grammar.freezed.dart';
part 'grammar.g.dart';

@freezed
abstract class GrammarSummary with _$GrammarSummary {
  const factory GrammarSummary({
    required String id,
    required String moduleId,
    String? moduleName,
    required String grammarPattern, // Thay đổi từ title
    DateTime? createdAt,
    DateTime? updatedAt,
  }) = _GrammarSummary;

  factory GrammarSummary.fromJson(Map<String, dynamic> json) =>
      _$GrammarSummaryFromJson(json);
}

@freezed
abstract class GrammarDetail with _$GrammarDetail {
  const factory GrammarDetail({
    required String id,
    required String moduleId,
    String? moduleName,
    required String grammarPattern, // Thay đổi từ title
    String? definition, // Thêm mới
    String? structure, // Thêm mới
    String? conjugation, // Thêm mới
    String? examples, // Thay đổi từ example (số nhiều)
    String? commonPhrases, // Thêm mới
    String? notes, // Thêm mới
    DateTime? createdAt,
    DateTime? updatedAt,
  }) = _GrammarDetail;

  factory GrammarDetail.fromJson(Map<String, dynamic> json) =>
      _$GrammarDetailFromJson(json);
}
