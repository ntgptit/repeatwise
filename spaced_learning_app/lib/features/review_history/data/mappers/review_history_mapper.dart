import 'package:spaced_learning_app/core/exceptions/parse_exception.dart';
import 'package:spaced_learning_app/core/pagination/page.dart';
import 'package:spaced_learning_app/core/pagination/page_dto.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history_create.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_status.dart';
import 'package:spaced_learning_app/features/review_history/domain/entities/review_history_update.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_create_request_dto.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_dto.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_update_request_dto.dart';

class ReviewHistoryMapper {
  ReviewHistoryMapper._();

  static ReviewHistory fromDto(ReviewHistoryDto dto) {
    if (dto.id.isEmpty) {
      throw const ParseException('id is empty');
    }
    if (dto.setId.isEmpty) {
      throw const ParseException('setId is empty');
    }
    if (dto.status == ReviewStatus.skipped && dto.skipReason == null) {
      throw const ParseException('skipReason is required when status is skipped');
    }
    return ReviewHistory(
      id: dto.id,
      setId: dto.setId,
      setName: dto.setName,
      cycleNo: dto.cycleNo,
      reviewNo: dto.reviewNo,
      score: dto.score,
      status: dto.status,
      skipReason: dto.skipReason,
      note: dto.note,
      createdAt: dto.createdAt,
      updatedAt: dto.updatedAt,
    );
  }

  static ReviewHistoryCreateRequestDto toCreateDto(ReviewHistoryCreate entity) {
    return ReviewHistoryCreateRequestDto(
      setId: entity.setId,
      cycleNo: entity.cycleNo,
      reviewNo: entity.reviewNo,
      score: entity.score,
      status: entity.status,
      skipReason: entity.skipReason,
      note: entity.note,
    );
  }

  static ReviewHistoryUpdateRequestDto toUpdateDto(ReviewHistoryUpdate entity) {
    return ReviewHistoryUpdateRequestDto(
      score: entity.score,
      status: entity.status,
      skipReason: entity.skipReason,
      note: entity.note,
    );
  }

  static Page<ReviewHistory> mapPage(PageDto<ReviewHistoryDto> dto) {
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
