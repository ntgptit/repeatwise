import 'package:freezed_annotation/freezed_annotation.dart';

part 'api_exception.freezed.dart';
part 'api_exception.g.dart';

@freezed
abstract class ApiException with _$ApiException {
  const factory ApiException({
    required String message,
    required int statusCode,
    Map<String, dynamic>? details,
  }) = _ApiException;

  factory ApiException.fromJson(Map<String, dynamic> json) =>
      _$ApiExceptionFromJson(json);
}
