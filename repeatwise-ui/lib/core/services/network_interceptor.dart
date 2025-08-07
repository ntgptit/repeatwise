import 'package:dio/dio.dart';
import '../config/network_config.dart';

class NetworkInterceptor extends Interceptor {
  final int maxRetries;
  final Duration retryDelay;

  NetworkInterceptor({
    this.maxRetries = NetworkConfig.maxRetries,
    this.retryDelay = NetworkConfig.retryDelay,
  });

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    // Add request timestamp for logging
    options.extra['requestTime'] = DateTime.now();
    
    // Add request ID for tracking
    options.extra['requestId'] = _generateRequestId();
    
    handler.next(options);
  }

  @override
  void onResponse(Response<dynamic> response, ResponseInterceptorHandler handler) {
    // Calculate request duration
    final requestTime = response.requestOptions.extra['requestTime'] as DateTime?;
    if (requestTime != null) {
      final duration = DateTime.now().difference(requestTime);
      response.extra['requestDuration'] = duration;
    }
    
    handler.next(response);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    // Only retry on specific error types
    if (_shouldRetry(err) && _getRetryCount(err) < maxRetries) {
      await _retryRequest(err, handler);
    } else {
      handler.next(err);
    }
  }

  bool _shouldRetry(DioException err) {
    // Retry on network errors and 5xx server errors
    return err.type == DioExceptionType.connectionError ||
           err.type == DioExceptionType.connectionTimeout ||
           err.type == DioExceptionType.receiveTimeout ||
           err.type == DioExceptionType.sendTimeout ||
           (err.response?.statusCode != null && 
            err.response!.statusCode! >= 500);
  }

  int _getRetryCount(DioException err) {
    return (err.requestOptions.extra['retryCount'] as int?) ?? 0;
  }

  Future<void> _retryRequest(DioException err, ErrorInterceptorHandler handler) async {
    final retryCount = _getRetryCount(err) + 1;
    err.requestOptions.extra['retryCount'] = retryCount;
    
    // Wait before retrying
    await Future.delayed(retryDelay * retryCount);
    
    try {
      final response = await Dio().fetch(err.requestOptions);
      handler.resolve(response);
    } catch (e) {
      handler.next(err);
    }
  }

  String _generateRequestId() {
    return DateTime.now().millisecondsSinceEpoch.toString();
  }
}
