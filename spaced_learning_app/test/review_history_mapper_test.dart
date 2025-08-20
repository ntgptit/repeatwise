import 'package:spaced_learning_app/features/review_history/data/mappers/review_history_mapper.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_dto.dart';

void main() {
  final validJson = {
    'id': '1',
    'setId': '2',
    'setName': 'Set A',
    'cycleNo': 1,
    'reviewNo': 1,
    'score': 80,
    'status': 'COMPLETED',
    'note': 'ok',
    'createdAt': '2024-01-01T00:00:00Z',
    'updatedAt': '2024-01-01T00:00:00Z'
  };
  final dto = ReviewHistoryDto.fromJson(validJson);
  print('Valid -> ${ReviewHistoryMapper.fromDto(dto)}');

  final missingJson = {
    'id': '1',
    'setId': '2',
    'setName': 'Set A',
    'cycleNo': 1,
    'reviewNo': 1,
    'status': 'SKIPPED',
    'createdAt': '2024-01-01T00:00:00Z',
    'updatedAt': '2024-01-01T00:00:00Z'
  };
  try {
    ReviewHistoryMapper.fromDto(ReviewHistoryDto.fromJson(missingJson));
  } catch (e) {
    print('Missing skipReason -> $e');
  }

  final invalidDateJson = {
    'id': '1',
    'setId': '2',
    'setName': 'Set A',
    'cycleNo': 1,
    'reviewNo': 1,
    'status': 'COMPLETED',
    'createdAt': 'invalid',
    'updatedAt': '2024-01-01T00:00:00Z'
  };
  try {
    ReviewHistoryDto.fromJson(invalidDateJson);
  } catch (e) {
    print('Invalid date -> $e');
  }
}
