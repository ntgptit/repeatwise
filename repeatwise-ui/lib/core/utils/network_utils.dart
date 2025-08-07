import 'dart:io';
import 'package:dio/dio.dart';

class NetworkUtils {
  /// Check if device has internet connection
  static Future<bool> hasInternetConnection() async {
    try {
      final result = await InternetAddress.lookup('google.com');
      return result.isNotEmpty && result[0].rawAddress.isNotEmpty;
    } on SocketException catch (_) {
      return false;
    }
  }

  /// Check if error is a network connectivity issue
  static bool isNetworkError(DioException error) {
    return error.type == DioExceptionType.connectionError ||
           error.type == DioExceptionType.connectionTimeout ||
           error.type == DioExceptionType.receiveTimeout ||
           error.type == DioExceptionType.sendTimeout;
  }

  /// Check if error is a server error (5xx)
  static bool isServerError(DioException error) {
    return error.response?.statusCode != null &&
           error.response!.statusCode! >= 500;
  }

  /// Check if error is a client error (4xx)
  static bool isClientError(DioException error) {
    return error.response?.statusCode != null &&
           error.response!.statusCode! >= 400 &&
           error.response!.statusCode! < 500;
  }

  /// Get user-friendly error message
  static String getUserFriendlyErrorMessage(DioException error) {
    if (isNetworkError(error)) {
      return 'No internet connection. Please check your network and try again.';
    }
    
    if (isServerError(error)) {
      return 'Server error. Please try again later.';
    }
    
    if (isClientError(error)) {
      final statusCode = error.response?.statusCode;
      switch (statusCode) {
        case 400:
          return 'Invalid request. Please check your input.';
        case 401:
          return 'Unauthorized. Please log in again.';
        case 403:
          return 'Access denied. You don\'t have permission for this action.';
        case 404:
          return 'Resource not found.';
        case 422:
          return 'Validation error. Please check your input.';
        default:
          return 'An error occurred. Please try again.';
      }
    }
    
    return 'An unexpected error occurred. Please try again.';
  }

  /// Format file size for upload/download progress
  static String formatFileSize(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    if (bytes < 1024 * 1024 * 1024) return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
    return '${(bytes / (1024 * 1024 * 1024)).toStringAsFixed(1)} GB';
  }

  /// Calculate upload/download progress percentage
  static double calculateProgress(int received, int total) {
    if (total <= 0) return 0.0;
    return (received / total) * 100;
  }

  /// Validate URL format
  static bool isValidUrl(String url) {
    try {
      Uri.parse(url);
      return true;
    } catch (e) {
      return false;
    }
  }

  /// Extract filename from URL
  static String? extractFilenameFromUrl(String url) {
    try {
      final uri = Uri.parse(url);
      final pathSegments = uri.pathSegments;
      if (pathSegments.isNotEmpty) {
        return pathSegments.last;
      }
    } catch (e) {
      // Invalid URL
    }
    return null;
  }

  /// Get file extension from URL
  static String? getFileExtension(String url) {
    final filename = extractFilenameFromUrl(url);
    if (filename != null && filename.contains('.')) {
      return filename.split('.').last.toLowerCase();
    }
    return null;
  }

  /// Check if file is an image
  static bool isImageFile(String url) {
    final extension = getFileExtension(url);
    return extension != null && 
           ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].contains(extension);
  }

  /// Check if file is a video
  static bool isVideoFile(String url) {
    final extension = getFileExtension(url);
    return extension != null && 
           ['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm'].contains(extension);
  }

  /// Check if file is a document
  static bool isDocumentFile(String url) {
    final extension = getFileExtension(url);
    return extension != null && 
           ['pdf', 'doc', 'docx', 'txt', 'rtf'].contains(extension);
  }
}
