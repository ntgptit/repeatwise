import 'package:freezed_annotation/freezed_annotation.dart';

@JsonEnum(unknownEnumValue: SkipReason.unknown)
enum SkipReason {
  @JsonValue('forgot') forgot,
  @JsonValue('busy') busy,
  @JsonValue('other') other,
  unknown,
}
