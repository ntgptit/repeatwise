import 'package:freezed_annotation/freezed_annotation.dart';

@JsonEnum(unknownEnumValue: SetCategory.unknown)
enum SetCategory {
  @JsonValue('vocabulary') vocabulary,
  @JsonValue('grammar') grammar,
  @JsonValue('mixed') mixed,
  @JsonValue('other') other,
  unknown,
}
