import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../models/api_response.dart';
import '../models/api_exception.dart';
import '../models/user.dart';
import '../models/set.dart';
import '../models/set_cycle.dart';
import '../models/remind_schedule.dart';
import 'dio_service.dart';

part 'api_repository.g.dart';

class ApiRepository {
  final DioService _dioService;

  ApiRepository(this._dioService);

  // Auth endpoints
  Future<ApiResponse<User>> login(String email, String password) async {
    try {
      final data = await _dioService.post<Map<String, dynamic>>(
        '/auth/login',
        data: {
          'email': email,
          'password': password,
        },
      );

      if (data['success'] == true && data['user'] != null) {
        return ApiResponse.success(User.fromJson(data['user'] as Map<String, dynamic>));
      } else {
        return ApiResponse.error((data['message'] as String?) ?? 'Login failed');
      }
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<User>> register(String name, String email, String password) async {
    try {
      final data = await _dioService.post<Map<String, dynamic>>(
        '/auth/register',
        data: {
          'name': name,
          'email': email,
          'password': password,
        },
      );

      if (data['success'] == true && data['user'] != null) {
        return ApiResponse.success(User.fromJson(data['user'] as Map<String, dynamic>));
      } else {
        return ApiResponse.error((data['message'] as String?) ?? 'Registration failed');
      }
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<void>> logout() async {
    try {
      await _dioService.post('/auth/logout');
      return ApiResponse.success(null);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  // Sets endpoints
  Future<ApiResponse<List<Set>>> getSets() async {
    try {
      final data = await _dioService.get<List<Set>>(
        '/sets',
        fromJson: (json) => (json as List)
            .map((item) => Set.fromJson(item as Map<String, dynamic>))
            .toList(),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<Set>> getSetById(String id) async {
    try {
      final data = await _dioService.get<Set>(
        '/sets/$id',
        fromJson: (json) => Set.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<Set>> createSet(SetCreateRequest request) async {
    try {
      final data = await _dioService.post<Set>(
        '/sets',
        data: request.toJson(),
        fromJson: (json) => Set.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<Set>> updateSet(String id, SetUpdateRequest request) async {
    try {
      final data = await _dioService.put<Set>(
        '/sets/$id',
        data: request.toJson(),
        fromJson: (json) => Set.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<void>> deleteSet(String id) async {
    try {
      await _dioService.delete('/sets/$id');
      return ApiResponse.success(null);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  // Set Cycles endpoints
  Future<ApiResponse<List<SetCycle>>> getSetCycles(String setId) async {
    try {
      final data = await _dioService.get<List<SetCycle>>(
        '/sets/$setId/cycles',
        fromJson: (json) => (json as List)
            .map((item) => SetCycle.fromJson(item as Map<String, dynamic>))
            .toList(),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<SetCycle>> createSetCycle(String setId, SetCycleCreateRequest request) async {
    try {
      final data = await _dioService.post<SetCycle>(
        '/sets/$setId/cycles',
        data: request.toJson(),
        fromJson: (json) => SetCycle.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  // Remind Schedules endpoints
  Future<ApiResponse<List<RemindSchedule>>> getRemindSchedules() async {
    try {
      final data = await _dioService.get<List<RemindSchedule>>(
        '/remind-schedules',
        fromJson: (json) => (json as List)
            .map((item) => RemindSchedule.fromJson(item as Map<String, dynamic>))
            .toList(),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<RemindSchedule>> createRemindSchedule(RemindScheduleCreateRequest request) async {
    try {
      final data = await _dioService.post<RemindSchedule>(
        '/remind-schedules',
        data: request.toJson(),
        fromJson: (json) => RemindSchedule.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<RemindSchedule>> updateRemindSchedule(String id, RemindScheduleUpdateRequest request) async {
    try {
      final data = await _dioService.put<RemindSchedule>(
        '/remind-schedules/$id',
        data: request.toJson(),
        fromJson: (json) => RemindSchedule.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<void>> deleteRemindSchedule(String id) async {
    try {
      await _dioService.delete('/remind-schedules/$id');
      return ApiResponse.success(null);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }
}

@riverpod
ApiRepository apiRepository(Ref ref) {
  final dioService = ref.watch(dioServiceProvider);
  return ApiRepository(dioService);
}
