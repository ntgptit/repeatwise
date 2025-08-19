// lib/presentation/screens/modules/module_grammar_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/presentation/viewmodels/grammar_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/module_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_bar_with_back.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_empty_state.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';
import 'package:spaced_learning_app/presentation/widgets/grammars/grammar_list_view.dart';
import 'package:spaced_learning_app/presentation/widgets/grammars/grammar_search_section.dart';
import 'package:spaced_learning_app/presentation/widgets/grammars/grammar_stats_card.dart';

class ModuleGrammarScreen extends ConsumerStatefulWidget {
  final String moduleId;

  const ModuleGrammarScreen({super.key, required this.moduleId});

  @override
  ConsumerState<ModuleGrammarScreen> createState() =>
      _ModuleGrammarScreenState();
}

class _ModuleGrammarScreenState extends ConsumerState<ModuleGrammarScreen>
    with TickerProviderStateMixin {
  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  final TextEditingController _searchController = TextEditingController();
  final FocusNode _searchFocusNode = FocusNode();

  bool _isInitialLoad = true;
  bool _isRefreshing = false;
  bool _isSearching = false;
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _loadInitialData();
  }

  @override
  void dispose() {
    _fadeController.dispose();
    _slideController.dispose();
    _searchController.dispose();
    _searchFocusNode.dispose();
    super.dispose();
  }

  void _initializeAnimations() {
    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _slideController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );

    _fadeAnimation = CurvedAnimation(
      parent: _fadeController,
      curve: const Interval(0.0, 0.7, curve: Curves.easeOut),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0, 0.1), end: Offset.zero).animate(
          CurvedAnimation(
            parent: _slideController,
            curve: const Interval(0.3, 1.0, curve: Curves.easeOutCubic),
          ),
        );
  }

  void _loadInitialData() {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await _performDataLoad();

      if (!mounted) return;

      setState(() => _isInitialLoad = false);
      _fadeController.forward();

      await Future.delayed(const Duration(milliseconds: 200));
      if (!mounted) return;

      _slideController.forward();
    });
  }

  Future<void> _performDataLoad() async {
    if (_isRefreshing) return;

    setState(() => _isRefreshing = true);

    try {
      await Future.wait([
        ref
            .read(selectedModuleProvider.notifier)
            .loadModuleDetails(widget.moduleId),
        ref
            .read(grammarsStateProvider.notifier)
            .loadGrammarsByModuleId(widget.moduleId),
      ]);
    } catch (error) {
      debugPrint('Error loading grammar data: $error');
    } finally {
      if (mounted) {
        setState(() => _isRefreshing = false);
      }
    }
  }

  void _handleSearch(String query) {
    final trimmedQuery = query.trim();
    setState(() {
      _searchQuery = trimmedQuery;
      _isSearching = trimmedQuery.isNotEmpty;
    });

    if (trimmedQuery.isEmpty) {
      ref
          .read(grammarsStateProvider.notifier)
          .loadGrammarsByModuleId(widget.moduleId);
      return;
    }

    ref.read(grammarsStateProvider.notifier).searchGrammars(trimmedQuery);
  }

  void _clearSearch() {
    _searchController.clear();
    _searchFocusNode.unfocus();
    _handleSearch('');
  }

  Future<void> _handleRefresh() async {
    setState(() => _isInitialLoad = true);
    await _performDataLoad();
  }

  void _navigateToGrammarDetail(String grammarId) {
    final module = ref.read(selectedModuleProvider).valueOrNull;

    if (module?.bookId.isEmpty ?? true) {
      _showErrorSnackBar('Module information not available');
      return;
    }

    context.push(
      '/books/${module!.bookId}/modules/${widget.moduleId}/grammars/$grammarId',
    );
  }

  void _showErrorSnackBar(String message) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
        ),
      ),
    );
  }

  Widget _buildLoadingState() {
    return const Center(
      child: SLLoadingIndicator(
        type: LoadingIndicatorType.fadingCircle,
        size: AppDimens.circularProgressSizeL,
      ),
    );
  }

  Widget _buildErrorState(String error) {
    return Center(
      child: SLErrorView(
        message: 'Failed to load grammar rules: $error',
        onRetry: _handleRefresh,
        icon: Icons.menu_book_outlined,
      ),
    );
  }

  Widget _buildEmptyState() {
    return SLEmptyState(
      icon: _isSearching ? Icons.search_off_rounded : Icons.menu_book_outlined,
      title: _isSearching
          ? 'No grammar rules found'
          : 'No grammar rules available',
      message: _isSearching
          ? 'Try different search terms or clear the search to see all rules.'
          : 'This module doesn\'t have any grammar rules yet. Try refreshing or check back later.',
      buttonText: _isSearching ? 'Clear Search' : 'Refresh',
      onButtonPressed: _isSearching ? _clearSearch : _handleRefresh,
    );
  }

  Widget _buildContent(List<GrammarSummary> grammars) {
    final module = ref.watch(selectedModuleProvider).valueOrNull;

    return RefreshIndicator(
      onRefresh: _handleRefresh,
      color: Theme.of(context).colorScheme.primary,
      backgroundColor: Theme.of(context).colorScheme.surfaceContainerHighest,
      child: CustomScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        slivers: [
          // Stats Card
          if (!_isSearching && grammars.isNotEmpty) ...[
            SliverToBoxAdapter(
              child: GrammarStatsCard(
                totalCount: grammars.length,
                moduleTitle: module?.title,
                onRefresh: _handleRefresh,
                isRefreshing: _isRefreshing,
              ),
            ),
          ],

          // Grammar List
          if (grammars.isEmpty)
            SliverFillRemaining(child: _buildEmptyState())
          else
            GrammarListView(
              grammars: grammars,
              onGrammarTap: _navigateToGrammarDetail,
              isSearchMode: _isSearching,
              searchQuery: _searchQuery,
            ),

          // Bottom spacing
          const SliverToBoxAdapter(child: SizedBox(height: AppDimens.spaceXXL)),
        ],
      ),
    );
  }

  PreferredSizeWidget _buildAppBar() {
    final module = ref.watch(selectedModuleProvider).valueOrNull;
    final screenTitle = _isSearching
        ? 'Search Results'
        : '${module?.title ?? 'Module'} Grammar';

    return AppBarWithBack(
      title: screenTitle,
      actions: [
        AnimatedSwitcher(
          duration: const Duration(milliseconds: 300),
          child: _isRefreshing
              ? const Padding(
                  padding: EdgeInsets.all(AppDimens.paddingM),
                  child: SizedBox(
                    width: AppDimens.iconM,
                    height: AppDimens.iconM,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  ),
                )
              : IconButton(
                  key: const ValueKey('refresh'),
                  icon: const Icon(Icons.refresh_rounded),
                  onPressed: _handleRefresh,
                  tooltip: 'Refresh',
                ),
        ),
      ],
    );
  }

  Widget _buildBody() {
    if (_isInitialLoad) {
      return _buildLoadingState();
    }

    return FadeTransition(
      opacity: _fadeAnimation,
      child: SlideTransition(
        position: _slideAnimation,
        child: Column(
          children: [
            // Search Section
            GrammarSearchSection(
              controller: _searchController,
              focusNode: _searchFocusNode,
              onChanged: _handleSearch,
              onClear: _clearSearch,
              isSearching: _isSearching,
            ),

            // Content Section
            Expanded(
              child: Consumer(
                builder: (context, ref, child) {
                  final grammarsAsync = ref.watch(grammarsStateProvider);

                  return grammarsAsync.when(
                    data: _buildContent,
                    loading: _buildLoadingState,
                    error: (error, stack) => _buildErrorState(error.toString()),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.surface,
      appBar: _buildAppBar(),
      body: _buildBody(),
    );
  }
}
