import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../models/api_response.dart';
import '../models/api_exception.dart';
import '../models/user.dart';
import '../models/set.dart';
import '../models/set_cycle.dart';
import '../models/remind_schedule.dart';
import 'dio_service.dart';
import 'storage_service.dart';

part 'api_repository.g.dart';

class ApiRepository {
  final DioService _dioService;
  final StorageService _storageService;

  ApiRepository(this._dioService, this._storageService);

  // Auth endpoints
  Future<ApiResponse<User>> login(
    String emailOrUsername,
    String password,
  ) async {
    try {
      final data = await _dioService.post<Map<String, dynamic>>(
        '/auth/login',
        data: {'emailOrUsername': emailOrUsername, 'password': password},
      );

      if (data['success'] == true && data['user'] != null) {
        // Save token if provided
        if (data['token'] != null) {
          await _storageService.saveToken(data['token'] as String);
        }
        try {
          return ApiResponse.success(
            User.fromJson(data['user'] as Map<String, dynamic>),
          );
        } catch (e) {
          return ApiResponse.error('Failed to parse user data: $e');
        }
      } else {
        return ApiResponse.error(
          (data['message'] as String?) ?? 'Login failed',
        );
      }
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<User>> register(
    String name,
    String username,
    String email,
    String password,
  ) async {
    try {
      final data = await _dioService.post<Map<String, dynamic>>(
        '/auth/register',
        data: {
          'name': name,
          'username': username,
          'email': email,
          'password': password,
        },
      );

      if (data['success'] == true && data['user'] != null) {
        // Save token if provided
        if (data['token'] != null) {
          await _storageService.saveToken(data['token'] as String);
        }
        try {
          return ApiResponse.success(
            User.fromJson(data['user'] as Map<String, dynamic>),
          );
        } catch (e) {
          return ApiResponse.error('Failed to parse user data: $e');
        }
      } else {
        return ApiResponse.error(
          (data['message'] as String?) ?? 'Registration failed',
        );
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
      // Clear token after successful logout
      await _storageService.removeToken();
      return ApiResponse.success(null);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<User>> getCurrentUser() async {
    try {
      final data = await _dioService.get<Map<String, dynamic>>('/auth/me');
      return ApiResponse.success(User.fromJson(data));
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  // Sets endpoints
  Future<ApiResponse<List<Set>>> getSetsByUser(String userId) async {
    try {
      final data = await _dioService.get<List<Set>>(
        '/sets/user/$userId',
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

  Future<ApiResponse<List<Set>>> getSetsByUserWithFilter(
    String userId, {
    String? status,
  }) async {
    try {
      String endpoint = '/sets?userId=$userId';
      if (status != null) {
        endpoint += '&status=$status';
      }

      final data = await _dioService.get<List<Set>>(
        endpoint,
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

  Future<ApiResponse<Set>> createSet(
    String userId,
    SetCreateRequest request,
  ) async {
    try {
      final data = await _dioService.post<Set>(
        '/sets?userId=$userId',
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

  Future<ApiResponse<Set>> updateSet(
    String id,
    String userId,
    SetUpdateRequest request,
  ) async {
    try {
      final data = await _dioService.put<Set>(
        '/sets/$id?userId=$userId',
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

  Future<ApiResponse<void>> deleteSet(String id, String userId) async {
    try {
      await _dioService.delete('/sets/$id?userId=$userId');
      return ApiResponse.success(null);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<Set>> startLearning(String id, String userId) async {
    try {
      final data = await _dioService.post<Set>(
        '/sets/$id/start-learning?userId=$userId',
        fromJson: (json) => Set.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<Set>> markAsMastered(String id, String userId) async {
    try {
      final data = await _dioService.post<Set>(
        '/sets/$id/mark-mastered?userId=$userId',
        fromJson: (json) => Set.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<Map<String, dynamic>>> getSetStatistics(
    String id,
    String userId,
  ) async {
    try {
      final data = await _dioService.get<Map<String, dynamic>>(
        '/sets/$id/statistics?userId=$userId',
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<List<Set>>> getDailyReviewSets(
    String userId, {
    String? date,
  }) async {
    try {
      String endpoint = '/sets/user/$userId/daily-review';
      if (date != null) {
        endpoint += '?date=$date';
      }

      final data = await _dioService.get<List<Set>>(
        endpoint,
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

  Future<ApiResponse<SetCycle>> createSetCycle(
    String setId,
    SetCycleCreateRequest request,
  ) async {
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
            .map(
              (item) => RemindSchedule.fromJson(item as Map<String, dynamic>),
            )
            .toList(),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<RemindSchedule>> createRemindSchedule(
    RemindScheduleCreateRequest request,
  ) async {
    try {
      final data = await _dioService.post<RemindSchedule>(
        '/remind-schedules',
        data: request.toJson(),
        fromJson: (json) =>
            RemindSchedule.fromJson(json as Map<String, dynamic>),
      );
      return ApiResponse.success(data);
    } on ApiException catch (e) {
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error: $e');
    }
  }

  Future<ApiResponse<RemindSchedule>> updateRemindSchedule(
    String id,
    RemindScheduleUpdateRequest request,
  ) async {
    try {
      final data = await _dioService.put<RemindSchedule>(
        '/remind-schedules/$id',
        data: request.toJson(),
        fromJson: (json) =>
            RemindSchedule.fromJson(json as Map<String, dynamic>),
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
  final storageService = ref.watch(storageServiceProvider);
  return ApiRepository(dioService, storageService);
}
