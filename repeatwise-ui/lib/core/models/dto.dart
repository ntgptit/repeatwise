import 'package:freezed_annotation/freezed_annotation.dart';

part 'dto.freezed.dart';
part 'dto.g.dart';

// Common DTOs for API requests/responses
@freezed
abstract class PaginationRequest with _$PaginationRequest {
  const factory PaginationRequest({
    @Default(1) int page,
    @Default(10) int limit,
    String? search,
    String? sortBy,
    @Default('desc') String sortOrder,
  }) = _PaginationRequest;

  factory PaginationRequest.fromJson(Map<String, dynamic> json) =>
      _$PaginationRequestFromJson(json);
}

@freezed
abstract class PaginationResponse with _$PaginationResponse {
  const factory PaginationResponse({
    required List<dynamic> data,
    required int total,
    required int page,
    required int limit,
    required int totalPages,
    required bool hasNext,
    required bool hasPrev,
  }) = _PaginationResponse;

  factory PaginationResponse.fromJson(Map<String, dynamic> json) =>
      _$PaginationResponseFromJson(json);
}

@freezed
abstract class ApiSuccessResponse with _$ApiSuccessResponse {
  const factory ApiSuccessResponse({
    required bool success,
    required dynamic data,
    String? message,
  }) = _ApiSuccessResponse;

  factory ApiSuccessResponse.fromJson(Map<String, dynamic> json) =>
      _$ApiSuccessResponseFromJson(json);
}

@freezed
abstract class ApiErrorResponse with _$ApiErrorResponse {
  const factory ApiErrorResponse({
    required bool success,
    required String message,
    String? error,
    Map<String, dynamic>? details,
  }) = _ApiErrorResponse;

  factory ApiErrorResponse.fromJson(Map<String, dynamic> json) =>
      _$ApiErrorResponseFromJson(json);
}

// Extensions for validation
extension PaginationRequestExtension on PaginationRequest {
  bool get isValid => page > 0 && limit > 0 && limit <= 100;
  bool get hasSearch => search != null && search!.isNotEmpty;
  bool get hasSorting => sortBy != null && sortBy!.isNotEmpty;
  bool get isValidSortOrder => sortOrder == 'asc' || sortOrder == 'desc';
}

extension PaginationResponseExtension on PaginationResponse {
  bool get isEmpty => data.isEmpty;
  bool get isNotEmpty => data.isNotEmpty;
  bool get isFirstPage => page == 1;
  bool get isLastPage => page >= totalPages;
  int get nextPage => hasNext ? page + 1 : page;
  int get prevPage => hasPrev ? page - 1 : page;
}
