import 'package:spaced_learning_app/core/exceptions/parse_exception.dart';
import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/core/pagination/page_dto.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_create.dart';
import 'package:spaced_learning_app/features/learning_set/domain/entities/learning_set_update.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_create_request_dto.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_dto.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_update_request_dto.dart';

class LearningSetMapper {
  LearningSetMapper._();

  static LearningSet fromDto(LearningSetDto dto) {
    if (dto.id.isEmpty) {
      throw const ParseException('id is empty');
    }
    if (dto.name.isEmpty) {
      throw const ParseException('name is empty');
    }
    if (dto.wordCount <= 0) {
      throw const ParseException('wordCount must be positive');
    }
    return LearningSet(
      id: dto.id,
      name: dto.name,
      description: dto.description,
      category: dto.category,
      wordCount: dto.wordCount,
      status: dto.status,
      currentCycle: dto.currentCycle,
      createdAt: dto.createdAt,
      updatedAt: dto.updatedAt,
      deletedAt: dto.deletedAt,
    );
  }

  static LearningSetCreateRequestDto toCreateDto(LearningSetCreate entity) {
    return LearningSetCreateRequestDto(
      name: entity.name,
      description: entity.description,
      category: entity.category,
      wordCount: entity.wordCount,
    );
  }

  static LearningSetUpdateRequestDto toUpdateDto(LearningSetUpdate entity) {
    return LearningSetUpdateRequestDto(
      name: entity.name,
      description: entity.description,
      category: entity.category,
      wordCount: entity.wordCount,
    );
  }

  static Page<LearningSet> mapPage(PageDto<LearningSetDto> dto) {
    final items = dto.content.map(fromDto).toList();
    return Page(
      content: items,
      page: dto.page,
      size: dto.size,
      totalElements: dto.totalElements,
      totalPages: dto.totalPages,
    );
  }
}
