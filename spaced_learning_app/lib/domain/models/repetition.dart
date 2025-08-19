import 'package:freezed_annotation/freezed_annotation.dart';

part 'repetition.freezed.dart';
part 'repetition.g.dart';

enum RepetitionOrder {
  @JsonValue('FIRST_REPETITION')
  firstRepetition,
  @JsonValue('SECOND_REPETITION')
  secondRepetition,
  @JsonValue('THIRD_REPETITION')
  thirdRepetition,
  @JsonValue('FOURTH_REPETITION')
  fourthRepetition,
  @JsonValue('FIFTH_REPETITION')
  fifthRepetition,
}

enum RepetitionStatus {
  @JsonValue('NOT_STARTED')
  notStarted,
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('SKIPPED')
  skipped,
}

@freezed
abstract class Repetition with _$Repetition {
  const factory Repetition({
    required String id,
    required String moduleProgressId,
    required RepetitionOrder repetitionOrder,
    @Default(RepetitionStatus.notStarted) RepetitionStatus status,
    DateTime? reviewDate,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) = _Repetition;

  factory Repetition.fromJson(Map<String, dynamic> json) =>
      _$RepetitionFromJson(json);
}

extension RepetitionExtensions on Repetition {
  String formatOrder() {
    switch (repetitionOrder) {
      case RepetitionOrder.firstRepetition:
        return '1';
      case RepetitionOrder.secondRepetition:
        return '2';
      case RepetitionOrder.thirdRepetition:
        return '3';
      case RepetitionOrder.fourthRepetition:
        return '4';
      case RepetitionOrder.fifthRepetition:
        return '5';
    }
  }

  String formatFullOrder() {
    switch (repetitionOrder) {
      case RepetitionOrder.firstRepetition:
        return 'Repetition 1';
      case RepetitionOrder.secondRepetition:
        return 'Repetition 2';
      case RepetitionOrder.thirdRepetition:
        return 'Repetition 3';
      case RepetitionOrder.fourthRepetition:
        return 'Repetition 4';
      case RepetitionOrder.fifthRepetition:
        return 'Repetition 5';
    }
  }
}
