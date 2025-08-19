import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

class LoggingInterceptor extends Interceptor {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    final headers = options.headers.toString();
    final queryParameters = options.queryParameters.toString();
    final data = options.data.toString();

    debugPrint('┌───────────────────────────────────────────────────────');
    debugPrint('│ REQUEST: ${options.method} ${options.uri}');
    debugPrint('│ Headers: $headers');
    if (options.queryParameters.isNotEmpty) {
      debugPrint('│ Query Parameters: $queryParameters');
    }
    if (options.data != null) {
      debugPrint('│ Body: $data');
    }
    debugPrint('└───────────────────────────────────────────────────────');

    super.onRequest(options, handler);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    final statusCode = response.statusCode;
    final data = response.data.toString();

    debugPrint('┌───────────────────────────────────────────────────────');
    debugPrint('│ RESPONSE: $statusCode ${response.requestOptions.uri}');
    debugPrint('│ Body: $data');
    debugPrint('└───────────────────────────────────────────────────────');

    super.onResponse(response, handler);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    final statusCode = err.response?.statusCode;
    final errorMessage = err.message;
    final errorData = err.response?.data.toString();

    debugPrint('┌───────────────────────────────────────────────────────');
    debugPrint('│ ERROR: $statusCode ${err.requestOptions.uri}');
    debugPrint('│ Message: $errorMessage');
    if (errorData != null) {
      debugPrint('│ Data: $errorData');
    }
    debugPrint('└───────────────────────────────────────────────────────');

    super.onError(err, handler);
  }
}
