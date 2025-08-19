abstract class AppException implements Exception {
  final String message;
  final String? prefix;
  final String? url;

  AppException(this.message, this.prefix, this.url);

  @override
  String toString() {
    return '$prefix$message';
  }
}

class AuthenticationException extends AppException {
  AuthenticationException([String? message, String? url])
    : super(message ?? 'Authentication failed', 'Authentication Error: ', url);
}

class BadRequestException extends AppException {
  final Map<String, dynamic>? errors;

  BadRequestException([String? message, String? url, this.errors])
    : super(message ?? 'Invalid request', 'Bad Request: ', url);
}

class DataFormatException extends AppException {
  DataFormatException([String? message, String? url])
    : super(message ?? 'Invalid data format', 'Data Format Error: ', url);
}

class ForbiddenException extends AppException {
  ForbiddenException([String? message, String? url])
    : super(message ?? 'Access denied', 'Forbidden: ', url);
}

class HttpException extends AppException {
  final int statusCode;

  HttpException(this.statusCode, [String? message, String? url])
    : super(
        message ?? 'Error occurred with status code: $statusCode',
        'HTTP Error: ',
        url,
      );
}

class NoInternetException extends AppException {
  NoInternetException([String? message])
    : super(message ?? 'No internet connection', 'Connection Error: ', null);
}

class NotFoundException extends AppException {
  NotFoundException([String? message, String? url])
    : super(message ?? 'Resource not found', 'Not Found: ', url);
}

class ServerException extends AppException {
  ServerException([String? message, String? url])
    : super(message ?? 'Server error occurred', 'Server Error: ', url);
}

class TimeoutException extends AppException {
  TimeoutException([String? message, String? url])
    : super(message ?? 'Operation timed out', 'Timeout: ', url);
}

class UnexpectedException extends AppException {
  UnexpectedException([String? message, String? url])
    : super(message ?? 'Unexpected error occurred', 'Unexpected Error: ', url);
}
