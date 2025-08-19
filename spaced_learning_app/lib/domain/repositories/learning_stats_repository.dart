import 'package:spaced_learning_app/domain/models/learning_insight.dart';
import 'package:spaced_learning_app/domain/models/learning_stats.dart';

abstract class LearningStatsRepository {
  Future<LearningStatsDTO> getDashboardStats({bool refreshCache = false});

  Future<List<LearningInsightRespone>> getLearningInsights();
}
