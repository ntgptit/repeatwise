// lib/presentation/viewmodels/daily_task_report_viewmodel.dart
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/di/providers.dart';

part 'daily_task_report_viewmodel.g.dart';

class LogEntry {
  final DateTime timestamp;
  final String message;
  final String? detail;
  final bool isSuccess;

  LogEntry({
    required this.timestamp,
    required this.message,
    this.detail,
    required this.isSuccess,
  });

  Map<String, dynamic> toJson() {
    return {
      'timestamp': timestamp.toIso8601String(),
      'message': message,
      'detail': detail,
      'isSuccess': isSuccess,
    };
  }

  factory LogEntry.fromJson(Map<String, dynamic> json) {
    return LogEntry(
      timestamp: DateTime.parse(json['timestamp']),
      message: json['message'],
      detail: json['detail'],
      isSuccess: json['isSuccess'],
    );
  }
}

class CheckResult {
  final DateTime timestamp;
  final bool isSuccess;
  final bool hasDueTasks;
  final int taskCount;
  final String? errorMessage;

  CheckResult({
    required this.timestamp,
    required this.isSuccess,
    required this.hasDueTasks,
    required this.taskCount,
    this.errorMessage,
  });
}

@riverpod
class DailyTaskReportState extends _$DailyTaskReportState {
  static const String _isActiveKey = 'daily_task_checker_active';
  static const String _logEntriesKey = 'daily_task_log_entries';

  @override
  Future<Map<String, dynamic>> build() async {
    return loadReportData();
  }

  Future<Map<String, dynamic>> loadReportData() async {
    try {
      // Get active state
      final storageService = ref.read(storageServiceProvider);
      final isCheckerActive =
          await storageService.getBool(_isActiveKey) ?? false;

      // Get latest check report
      final dailyTaskChecker = await ref.read(dailyTaskCheckerProvider.future);
      final report = await dailyTaskChecker.getLastCheckReport();

      // Get logs
      final logs = await _loadLogs();

      return {
        'isCheckerActive': isCheckerActive,
        'lastCheckTime': report['lastCheckTime'],
        'lastCheckResult': report['lastCheckResult'],
        'lastCheckTaskCount': report['lastCheckTaskCount'],
        'lastCheckError': report['lastCheckError'],
        'logEntries': logs,
      };
    } catch (e) {
      throw Exception('Error loading report data: $e');
    }
  }

  Future<List<LogEntry>> _loadLogs() async {
    try {
      final storageService = ref.read(storageServiceProvider);
      final logsJson = await storageService.getString(_logEntriesKey);
      if (logsJson == null) {
        return [];
      }

      // Parse JSON
      final List<dynamic> logsData = jsonDecode(logsJson);
      return logsData.map((json) => LogEntry.fromJson(json)).toList();
    } catch (e) {
      debugPrint('Error loading logs: $e');
      return [];
    }
  }

  Future<void> toggleChecker(bool value) async {
    if (value == state.valueOrNull?['isCheckerActive']) return;

    try {
      final storageService = ref.read(storageServiceProvider);
      await storageService.setBool(_isActiveKey, value);

      if (value) {
        // Activate checker
        final dailyTaskChecker = await ref.read(
          dailyTaskCheckerProvider.future,
        );
        final success = await dailyTaskChecker.initialize();
        if (success) {
          await _addLogEntry(
            message: 'Daily automated check activated',
            isSuccess: true,
          );
        } else {
          await _addLogEntry(
            message: 'Failed to activate automated check',
            isSuccess: false,
          );
          await storageService.setBool(_isActiveKey, false);
        }
      } else {
        // Deactivate checker
        final dailyTaskChecker = await ref.read(
          dailyTaskCheckerProvider.future,
        );
        final success = await dailyTaskChecker.cancelDailyCheck();
        await _addLogEntry(
          message: 'Daily automated check deactivated',
          isSuccess: success,
        );
      }

      state = await AsyncValue.guard(() => loadReportData());
    } catch (e) {
      throw Exception('Error changing checker state: $e');
    }
  }

  Future<CheckResult> performManualCheck() async {
    if (state.valueOrNull?['isManualCheckInProgress'] == true) {
      return CheckResult(
        timestamp: DateTime.now(),
        isSuccess: false,
        hasDueTasks: false,
        taskCount: 0,
        errorMessage: 'Check in progress, please wait',
      );
    }

    // Update state to show loading
    state = AsyncValue.data({
      ...state.valueOrNull ?? {},
      'isManualCheckInProgress': true,
    });

    try {
      await _addLogEntry(message: 'Starting manual check', isSuccess: true);

      // Perform check
      final dailyTaskChecker = await ref.read(dailyTaskCheckerProvider.future);
      final event = await dailyTaskChecker.manualCheck();

      // Add log entry
      await _addLogEntry(
        message: event.hasDueTasks
            ? 'Manual check: Found ${event.taskCount} due tasks'
            : 'Manual check: No tasks due today',
        detail: event.isSuccess ? null : event.errorMessage,
        isSuccess: event.isSuccess,
      );

      // Refresh state
      state = await AsyncValue.guard(() => loadReportData());

      return CheckResult(
        timestamp: event.checkTime,
        isSuccess: event.isSuccess,
        hasDueTasks: event.hasDueTasks,
        taskCount: event.taskCount,
        errorMessage: event.errorMessage,
      );
    } catch (e) {
      // Add error log entry
      await _addLogEntry(
        message: 'Manual check failed',
        detail: e.toString(),
        isSuccess: false,
      );

      // Refresh state
      state = await AsyncValue.guard(() => loadReportData());

      return CheckResult(
        timestamp: DateTime.now(),
        isSuccess: false,
        hasDueTasks: false,
        taskCount: 0,
        errorMessage: e.toString(),
      );
    } finally {
      // Update state to show not loading
      state = AsyncValue.data({
        ...state.valueOrNull ?? {},
        'isManualCheckInProgress': false,
      });
    }
  }

  Future<void> _addLogEntry({
    required String message,
    String? detail,
    required bool isSuccess,
  }) async {
    try {
      final storageService = ref.read(storageServiceProvider);

      final entry = LogEntry(
        timestamp: DateTime.now(),
        message: message,
        detail: detail,
        isSuccess: isSuccess,
      );

      // Get existing logs
      final currentStateValue = state.valueOrNull;
      List<LogEntry> existingLogs = [];

      if (currentStateValue != null &&
          currentStateValue['logEntries'] != null &&
          currentStateValue['logEntries'] is List<LogEntry>) {
        existingLogs = currentStateValue['logEntries'] as List<LogEntry>;
      } else {
        existingLogs = await _loadLogs();
      }

      // Add to start of list
      final updatedLogs = [entry, ...existingLogs];

      // Limit number of entries
      final limitedLogs = updatedLogs.length > 100
          ? updatedLogs.sublist(0, 100)
          : updatedLogs;

      // Save logs
      final logsJson = jsonEncode(limitedLogs.map((e) => e.toJson()).toList());
      await storageService.setString(_logEntriesKey, logsJson);
    } catch (e) {
      debugPrint('Error adding log entry: $e');
    }
  }

  Future<void> clearLogs() async {
    try {
      final storageService = ref.read(storageServiceProvider);
      // Đặt danh sách logs về mảng trống và không thêm log mới
      await storageService.setString(_logEntriesKey, '[]');
      // Tải lại dữ liệu mà không thêm log mới
      state = await AsyncValue.guard(() => loadReportData());
    } catch (e) {
      throw Exception('Error clearing logs: $e');
    }
  }
}

@riverpod
bool isManualCheckInProgress(Ref ref) {
  final reportState = ref.watch(dailyTaskReportStateProvider);
  return reportState.valueOrNull?['isManualCheckInProgress'] ?? false;
}
