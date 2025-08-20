import 'package:json_annotation/json_annotation.dart';

part 'review_status.g.dart';

@JsonEnum(unknownEnumValue: ReviewStatus.unknown)
enum ReviewStatus {
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('SKIPPED')
  skipped,
  unknown,
}
