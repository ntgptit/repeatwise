import 'enums/set_category.dart';
import 'enums/set_status.dart';

/// Learning set model representing a collection of words or grammar items
class LearningSet {
  final String id;
  final String name;
  final String? description;
  final SetCategory category;
  final int wordCount;
  final SetStatus status;
  final int currentCycle;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? deletedAt;

  const LearningSet({
    required this.id,
    required this.name,
    this.description,
    required this.category,
    required this.wordCount,
    required this.status,
    required this.currentCycle,
    required this.createdAt,
    required this.updatedAt,
    this.deletedAt,
  });

  LearningSet copyWith({
    String? id,
    String? name,
    String? description,
    SetCategory? category,
    int? wordCount,
    SetStatus? status,
    int? currentCycle,
    DateTime? createdAt,
    DateTime? updatedAt,
    DateTime? deletedAt,
  }) {
    return LearningSet(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      category: category ?? this.category,
      wordCount: wordCount ?? this.wordCount,
      status: status ?? this.status,
      currentCycle: currentCycle ?? this.currentCycle,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      deletedAt: deletedAt ?? this.deletedAt,
    );
  }
}
