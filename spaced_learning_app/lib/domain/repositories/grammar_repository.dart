import 'package:spaced_learning_app/domain/models/grammar.dart';

abstract class GrammarRepository {
  /// Get all grammars with pagination
  Future<List<GrammarSummary>> getAllGrammars({int page = 0, int size = 20});

  /// Get grammar details by id
  Future<GrammarDetail> getGrammarById(String id);

  /// Get all grammars for a specific module
  Future<List<GrammarSummary>> getGrammarsByModuleId(String moduleId);

  /// Search grammars by title with pagination
  Future<List<GrammarSummary>> searchGrammars(
    String query, {
    int page = 0,
    int size = 20,
  });
}
