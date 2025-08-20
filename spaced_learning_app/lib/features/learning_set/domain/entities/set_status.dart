import 'package:freezed_annotation/freezed_annotation.dart';

@JsonEnum(unknownEnumValue: SetStatus.unknown)
enum SetStatus {
  @JsonValue('not_started') notStarted,
  @JsonValue('learning') learning,
  @JsonValue('reviewing') reviewing,
  @JsonValue('mastered') mastered,
  unknown,
}
