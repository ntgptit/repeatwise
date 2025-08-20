import 'package:json_annotation/json_annotation.dart';

/// Converter for ISO-8601 UTC strings to [DateTime] and back.
class DateTimeUtcConverter implements JsonConverter<DateTime, String> {
  const DateTimeUtcConverter();

  @override
  DateTime fromJson(String json) {
    if (json.isEmpty) {
      throw const FormatException('Empty DateTime string');
    }
    final date = DateTime.tryParse(json);
    if (date == null) {
      throw FormatException('Invalid DateTime format: $json');
    }
    return date.toUtc();
  }

  @override
  String toJson(DateTime object) {
    return object.toUtc().toIso8601String();
  }
}
