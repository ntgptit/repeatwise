import 'package:freezed_annotation/freezed_annotation.dart';

part 'error_response.freezed.dart';
part 'error_response.g.dart';

@freezed
abstract class ErrorResponse with _$ErrorResponse {
  const factory ErrorResponse({
    required bool success,
    required String message,
    Map<String, String>? errors,
    @JsonKey(name: 'timestamp') DateTime? timestamp,
  }) = _ErrorResponse;

  factory ErrorResponse.fromJson(Map<String, dynamic> json) =>
      _$ErrorResponseFromJson(json);
}
