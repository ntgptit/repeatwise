import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:repeatwise_ui/core/models/api_response.dart';
import 'package:repeatwise_ui/core/models/set.dart';
import 'package:repeatwise_ui/features/sets/domain/repositories/set_repository.dart';
import 'package:repeatwise_ui/features/sets/domain/usecases/create_set_usecase.dart';

import 'create_set_usecase_test.mocks.dart';

@GenerateMocks([SetRepository])
void main() {
  late CreateSetUseCase useCase;
  late MockSetRepository mockRepository;

  setUp(() {
    mockRepository = MockSetRepository();
    useCase = CreateSetUseCase(mockRepository);
  });

  group('CreateSetUseCase', () {
    const userId = 'test-user-id';
    final validRequest = SetCreateRequest(
      name: 'Test Set',
      description: 'Test Description',
    );

    test('should create set successfully with valid request', () async {
      // Arrange
      final expectedSet = Set(
        id: 'set-1',
        name: 'Test Set',
        description: 'Test Description',
        status: SetStatus.active,
        userId: userId,
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
      );

      when(
        mockRepository.createSet(userId, validRequest),
      ).thenAnswer((_) async => ApiResponse.success(expectedSet));

      // Act
      final result = await useCase.execute(userId, validRequest);

      // Assert
      expect(result.isSuccess, true);
      expect(result.data, expectedSet);
      verify(mockRepository.createSet(userId, validRequest)).called(1);
    });

    test('should return error when set name is empty', () async {
      // Arrange
      final invalidRequest = SetCreateRequest(
        name: '',
        description: 'Test Description',
      );

      // Act
      final result = await useCase.execute(userId, invalidRequest);

      // Assert
      expect(result.isSuccess, false);
      expect(result.error, 'Set name cannot be empty');
      verifyNever(mockRepository.createSet(any, any));
    });

    test('should return error when set name exceeds 100 characters', () async {
      // Arrange
      final invalidRequest = SetCreateRequest(
        name: 'A' * 101, // 101 characters
        description: 'Test Description',
      );

      // Act
      final result = await useCase.execute(userId, invalidRequest);

      // Assert
      expect(result.isSuccess, false);
      expect(result.error, 'Set name cannot exceed 100 characters');
      verifyNever(mockRepository.createSet(any, any));
    });

    test(
      'should return error when description exceeds 500 characters',
      () async {
        // Arrange
        final invalidRequest = SetCreateRequest(
          name: 'Test Set',
          description: 'A' * 501, // 501 characters
        );

        // Act
        final result = await useCase.execute(userId, invalidRequest);

        // Assert
        expect(result.isSuccess, false);
        expect(result.error, 'Description cannot exceed 500 characters');
        verifyNever(mockRepository.createSet(any, any));
      },
    );

    test('should propagate repository error', () async {
      // Arrange
      const errorMessage = 'Network error';
      when(
        mockRepository.createSet(userId, validRequest),
      ).thenAnswer((_) async => ApiResponse.error(errorMessage));

      // Act
      final result = await useCase.execute(userId, validRequest);

      // Assert
      expect(result.isSuccess, false);
      expect(result.error, errorMessage);
      verify(mockRepository.createSet(userId, validRequest)).called(1);
    });
  });
}
