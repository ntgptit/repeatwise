
/// Utility class for date formatting operations
class DateUtils {
  /// Formats a date relative to now
  static String formatRelativeDate(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date);

    if (difference.inDays == 0) {
      return 'Today';
    } else if (difference.inDays == 1) {
      return 'Yesterday';
    } else if (difference.inDays < 7) {
      return '${difference.inDays} days ago';
    } else {
      return '${date.day}/${date.month}/${date.year}';
    }
  }

  /// Formats a date in short format (dd/mm)
  static String formatShortDate(DateTime date) {
    return '${date.day}/${date.month}';
  }

  /// Formats a date in full format (dd/mm/yyyy)
  static String formatFullDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }

  /// Formats time relative to now
  static String formatRelativeTime(DateTime time) {
    final now = DateTime.now();
    final difference = time.difference(now);

    if (difference.inHours < 1) {
      return 'In ${difference.inMinutes} minutes';
    } else if (difference.inHours < 24) {
      return 'In ${difference.inHours} hours';
    } else {
      return '${time.day}/${time.month} at ${time.hour}:${time.minute.toString().padLeft(2, '0')}';
    }
  }

  /// Formats time in HH:MM format
  static String formatTime(DateTime time) {
    return '${time.hour}:${time.minute.toString().padLeft(2, '0')}';
  }

  /// Formats date and time together
  static String formatDateTime(DateTime dateTime) {
    return '${formatTime(dateTime)} - ${formatShortDate(dateTime)}';
  }
}
