import 'package:spaced_learning_app/features/learning_set/data/mappers/learning_set_mapper.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_dto.dart';

void main() {
  final validJson = {
    'id': '1',
    'name': 'Set A',
    'description': 'desc',
    'category': 'vocabulary',
    'wordCount': 100,
    'status': 'learning',
    'currentCycle': 1,
    'createdAt': '2024-01-01T00:00:00Z',
    'updatedAt': '2024-01-01T00:00:00Z'
  };
  final dto = LearningSetDto.fromJson(validJson);
  print('Valid -> ${LearningSetMapper.fromDto(dto)}');

  final missingJson = {
    'id': '',
    'name': '',
    'category': 'vocabulary',
    'wordCount': 0,
    'status': 'learning',
    'currentCycle': 1,
    'createdAt': '2024-01-01T00:00:00Z',
    'updatedAt': '2024-01-01T00:00:00Z'
  };
  try {
    LearningSetMapper.fromDto(LearningSetDto.fromJson(missingJson));
  } catch (e) {
    print('Invalid -> $e');
  }

  final invalidDateJson = {
    'id': '1',
    'name': 'Set A',
    'category': 'vocabulary',
    'wordCount': 100,
    'status': 'learning',
    'currentCycle': 1,
    'createdAt': 'invalid',
    'updatedAt': '2024-01-01T00:00:00Z'
  };
  try {
    LearningSetDto.fromJson(invalidDateJson);
  } catch (e) {
    print('Invalid date -> $e');
  }
}
