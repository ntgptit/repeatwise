import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/remind_schedule.dart';
import 'package:spaced_learning_app/domain/repositories/remind_schedule_repository.dart';

class RemindScheduleRepositoryImpl implements RemindScheduleRepository {
  final ApiClient _apiClient;

  RemindScheduleRepositoryImpl(this._apiClient);

  @override
  Future<List<RemindSchedule>> getAllRemindSchedules() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.remindSchedules);
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => RemindSchedule.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch remind schedules: $e');
    }
  }

  @override
  Future<RemindSchedule?> getRemindScheduleById(String id) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.remindScheduleById(id),
      );
      final data = response['data'];
      return data != null ? RemindSchedule.fromJson(data) : null;
    } catch (e) {
      throw Exception('Failed to fetch remind schedule: $e');
    }
  }

  @override
  Future<RemindSchedule> createRemindSchedule(
    RemindSchedule remindSchedule,
  ) async {
    try {
      final response = await _apiClient.post(
        ApiEndpoints.remindSchedules,
        data: remindSchedule.toJson(),
      );
      final data = response['data'];
      return RemindSchedule.fromJson(data);
    } catch (e) {
      throw Exception('Failed to create remind schedule: $e');
    }
  }

  @override
  Future<RemindSchedule> updateRemindSchedule(
    String id,
    RemindSchedule remindSchedule,
  ) async {
    try {
      final response = await _apiClient.put(
        ApiEndpoints.remindScheduleById(id),
        data: remindSchedule.toJson(),
      );
      final data = response['data'];
      return RemindSchedule.fromJson(data);
    } catch (e) {
      throw Exception('Failed to update remind schedule: $e');
    }
  }

  @override
  Future<void> deleteRemindSchedule(String id) async {
    try {
      await _apiClient.delete(ApiEndpoints.remindScheduleById(id));
    } catch (e) {
      throw Exception('Failed to delete remind schedule: $e');
    }
  }

  @override
  Future<List<RemindSchedule>> getRemindSchedulesByUser(String userId) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.remindSchedulesByUser(userId),
      );
      final List<dynamic> data = response['data'] ?? [];
      return data.map((json) => RemindSchedule.fromJson(json)).toList();
    } catch (e) {
      throw Exception('Failed to fetch remind schedules by user: $e');
    }
  }
}
