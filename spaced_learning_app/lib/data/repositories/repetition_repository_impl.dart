import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';
import 'package:spaced_learning_app/domain/repositories/repetition_repository.dart';

class RepetitionRepositoryImpl implements RepetitionRepository {
  final ApiClient _apiClient;

  RepetitionRepositoryImpl(this._apiClient);

  @override
  Future<List<Repetition>> getRepetitionsByProgressId(String progressId) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.repetitionsByProgress(progressId),
      );

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> repetitionList = response['data'];
      return repetitionList.map((item) => Repetition.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get repetitions by progress: $e');
    }
  }

  @override
  Future<List<Repetition>> createDefaultSchedule(
    String moduleProgressId,
  ) async {
    try {
      final response = await _apiClient.post(
        ApiEndpoints.repetitionSchedule(moduleProgressId),
      );

      if (response['success'] != true || response['data'] == null) {
        throw BadRequestException(
          'Failed to create repetition schedule: ${response['message']}',
        );
      }

      final List<dynamic> repetitionList = response['data'];
      return repetitionList.map((item) => Repetition.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to create repetition schedule: $e');
    }
  }

  @override
  Future<Repetition> updateRepetition(
    String id, {
    RepetitionStatus? status,
    DateTime? reviewDate,
    bool rescheduleFollowing = false,
    double? percentComplete,
  }) async {
    try {
      // Kiểm tra đầu vào
      if (status == null && reviewDate == null) {
        throw BadRequestException(
          'Either status or reviewDate must be provided',
        );
      }

      // Gọi API cập nhật trạng thái hoàn thành nếu có status
      if (status != null) {
        return await _updateCompletionStatus(id, status, percentComplete);
      }

      // Mặc định gọi API lập lịch lại nếu không cập nhật trạng thái
      return await _rescheduleRepetition(id, reviewDate!, rescheduleFollowing);
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to update repetition: $e');
    }
  }

  /// Cập nhật trạng thái hoàn thành của một repetition
  Future<Repetition> _updateCompletionStatus(
    String id,
    RepetitionStatus status,
    double? percentComplete,
  ) async {
    final data = <String, dynamic>{
      'status': status.toString().split('.').last.toUpperCase(),
    };

    if (percentComplete != null) {
      data['score'] = percentComplete;
    }

    final response = await _apiClient.put(
      '${ApiEndpoints.repetitions}/$id/complete',
      data: data,
    );

    final bool isSuccessful =
        response['success'] == true && response['data'] != null;
    if (!isSuccessful) {
      throw BadRequestException(
        'Failed to update repetition status: ${response['message']}',
      );
    }

    return Repetition.fromJson(response['data']);
  }

  /// Lập lịch lại một repetition
  Future<Repetition> _rescheduleRepetition(
    String id,
    DateTime reviewDate,
    bool rescheduleFollowing,
  ) async {
    final data = <String, dynamic>{
      'reviewDate': _formatDate(reviewDate),
      'rescheduleFollowing': rescheduleFollowing,
    };

    final response = await _apiClient.put(
      '${ApiEndpoints.repetitions}/$id/reschedule',
      data: data,
    );

    final bool isSuccessful =
        response['success'] == true && response['data'] != null;
    if (!isSuccessful) {
      throw BadRequestException(
        'Failed to reschedule repetition: ${response['message']}',
      );
    }

    return Repetition.fromJson(response['data']);
  }

  @override
  Future<int> countByModuleProgressId(String moduleProgressId) async {
    try {
      final response = await _apiClient.get(
        '${ApiEndpoints.repetitions}/count',
        queryParameters: {'moduleProgressId': moduleProgressId},
      );

      if (response['success'] == true && response['data'] != null) {
        return response['data'] as int;
      }

      // Fallback to counting the actual repetitions
      final repetitions = await getRepetitionsByProgressId(moduleProgressId);
      return repetitions.length;
    } catch (e) {
      // Fallback to counting the actual repetitions
      final repetitions = await getRepetitionsByProgressId(moduleProgressId);
      return repetitions.length;
    }
  }

  String _formatDate(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
  }
}
