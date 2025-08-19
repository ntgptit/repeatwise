import 'package:freezed_annotation/freezed_annotation.dart';

part 'module_stats.freezed.dart';

@freezed
abstract class ModuleStats with _$ModuleStats {
  const factory ModuleStats({
    required int totalModules,
    required Map<String, int> cycleStats,
  }) = _ModuleStats;
}
