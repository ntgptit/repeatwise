import 'package:flutter/foundation.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';

import '../../core/di/providers.dart';

part 'grammar_viewmodel.g.dart';

@riverpod
class GrammarsState extends _$GrammarsState {
  @override
  Future<List<GrammarSummary>> build() async {
    return [];
  }

  /// Loads all grammars for a specific module
  Future<void> loadGrammarsByModuleId(String moduleId) async {
    if (moduleId.isEmpty) {
      state = AsyncValue.error('Module ID cannot be empty', StackTrace.current);
      return;
    }

    state = const AsyncValue.loading();
    try {
      final grammars = await ref
          .read(grammarRepositoryProvider)
          .getGrammarsByModuleId(moduleId);
      state = AsyncValue.data(grammars);
    } catch (e, stack) {
      debugPrint('Error loading grammars: $e');
      state = AsyncValue.error(e, stack);
    }
  }

  /// Searches grammars by title
  Future<void> searchGrammars(String query) async {
    if (query.isEmpty) {
      return;
    }

    state = const AsyncValue.loading();
    try {
      final grammars = await ref
          .read(grammarRepositoryProvider)
          .searchGrammars(query);
      state = AsyncValue.data(grammars);
    } catch (e, stack) {
      debugPrint('Error searching grammars: $e');
      state = AsyncValue.error(e, stack);
    }
  }
}

@riverpod
class SelectedGrammar extends _$SelectedGrammar {
  @override
  Future<GrammarDetail?> build() async => null;

  /// Loads grammar details by ID
  Future<void> loadGrammarDetails(String id) async {
    if (id.isEmpty) {
      state = AsyncValue.error(
        'Grammar ID cannot be empty',
        StackTrace.current,
      );
      return;
    }

    state = const AsyncValue.loading();
    try {
      final grammar = await ref
          .read(grammarRepositoryProvider)
          .getGrammarById(id);
      state = AsyncValue.data(grammar);
    } catch (e, stack) {
      debugPrint('Error loading grammar details: $e');
      state = AsyncValue.error(e, stack);
    }
  }

  /// Clears the selected grammar
  void clearSelectedGrammar() {
    state = const AsyncValue.data(null);
  }
}
