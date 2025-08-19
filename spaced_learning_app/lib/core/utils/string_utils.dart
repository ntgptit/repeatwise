import 'package:flutter/foundation.dart';

class StringUtils {
  /// Kiểm tra và làm sạch ID, trả về null nếu ID không hợp lệ
  static String? sanitizeId(String id, {String? source}) {
    if (id.isEmpty) {
      final sourceInfo = source != null ? ' in $source' : '';
      debugPrint('WARNING: Empty ID detected$sourceInfo');
      return null;
    }

    final sanitizedId = id.trim();
    if (sanitizedId != id) {
      final sourceInfo = source != null ? ' in $source' : '';
      debugPrint('ID sanitized from "$id" to "$sanitizedId"$sourceInfo');
    }
    return sanitizedId;
  }

  /// Kiểm tra xem một chuỗi có phải là email hợp lệ không
  static bool isValidEmail(String email) {
    return RegExp(r'^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(email);
  }

  /// Lấy chữ cái đầu tiên của các từ trong một chuỗi
  static String getInitials(String text, {int maxInitials = 2}) {
    if (text.isEmpty) return '';

    final parts = text.trim().split(' ');
    if (parts.length >= maxInitials) {
      // Lấy chữ cái đầu tiên của từ đầu và từ cuối
      return (parts.first[0] + parts.last[0]).toUpperCase();
    }
    return text.isNotEmpty ? text[0].toUpperCase() : '';
  }
}
