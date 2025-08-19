import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:spaced_learning_app/domain/models/module.dart';

part 'book.freezed.dart';
part 'book.g.dart';

enum BookStatus {
  @JsonValue('PUBLISHED')
  published,
  @JsonValue('DRAFT')
  draft,
  @JsonValue('ARCHIVED')
  archived,
}

enum DifficultyLevel {
  @JsonValue('BEGINNER')
  beginner,
  @JsonValue('INTERMEDIATE')
  intermediate,
  @JsonValue('ADVANCED')
  advanced,
  @JsonValue('EXPERT')
  expert,
}

@freezed
abstract class BookSummary with _$BookSummary {
  const factory BookSummary({
    required String id,
    required String name,
    required BookStatus status,
    DifficultyLevel? difficultyLevel,
    String? category,
    DateTime? createdAt,
    DateTime? updatedAt,
    @Default(0) int moduleCount,
  }) = _BookSummary;

  factory BookSummary.fromJson(Map<String, dynamic> json) =>
      _$BookSummaryFromJson(json);
}

@freezed
abstract class BookDetail with _$BookDetail {
  const factory BookDetail({
    required String id,
    required String name,
    String? description,
    required BookStatus status,
    DifficultyLevel? difficultyLevel,
    String? category,
    DateTime? createdAt,
    DateTime? updatedAt,
    @Default([]) List<ModuleDetail> modules,
  }) = _BookDetail;

  factory BookDetail.fromJson(Map<String, dynamic> json) =>
      _$BookDetailFromJson(json);
}

extension BookDetailMapper on BookDetail {
  BookSummary toSummary() {
    return BookSummary(
      id: id,
      name: name,
      status: status,
      difficultyLevel: difficultyLevel,
      category: category,
      createdAt: createdAt,
      updatedAt: updatedAt,
      moduleCount: modules.length,
    );
  }
}

extension BookSummaryMapper on BookSummary {
  BookDetail toDetail({List<ModuleDetail> modules = const []}) {
    return BookDetail(
      id: id,
      name: name,
      status: status,
      difficultyLevel: difficultyLevel,
      category: category,
      createdAt: createdAt,
      updatedAt: updatedAt,
      description: null, // Vì BookSummary không có description
      modules: modules,
    );
  }
}
