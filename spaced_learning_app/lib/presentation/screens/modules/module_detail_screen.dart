// lib/presentation/screens/modules/module_detail_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/di/providers.dart';
import 'package:spaced_learning_app/core/navigation/navigation_helper.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/domain/models/module.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/utils/snackbar_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/module_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_bar_with_back.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';

import '../../widgets/modules/module_detail_content.dart';
import '../../widgets/modules/module_detail_fab_section.dart';
import '../../widgets/modules/module_detail_header.dart';
import '../../widgets/modules/module_loading_skeleton.dart';
import '../../widgets/modules/module_progress_card.dart';

class ModuleDetailScreen extends ConsumerStatefulWidget {
  final String moduleId;

  const ModuleDetailScreen({super.key, required this.moduleId});

  @override
  ConsumerState<ModuleDetailScreen> createState() => _ModuleDetailScreenState();
}

class _ModuleDetailScreenState extends ConsumerState<ModuleDetailScreen>
    with TickerProviderStateMixin {
  late AnimationController _fadeController;
  late AnimationController _slideController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  bool _isInitialLoad = true;
  bool _isRefreshing = false;
  bool _isCheckingGrammar = false;
  bool _hasGrammar = false;
  bool _isCreatingProgress = false;
  bool _grammarCheckCompleted = false;

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
      curve: Curves.easeOut,
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0, 0.05), end: Offset.zero).animate(
          CurvedAnimation(parent: _slideController, curve: Curves.easeOutCubic),
        );
  }

  void _loadInitialData() {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await _performDataLoad();
      await _checkGrammarContent();

      if (!mounted) return;

      setState(() {
        _isInitialLoad = false;
        _grammarCheckCompleted = true;
      });
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
      await ref
          .read(selectedModuleProvider.notifier)
          .loadModuleDetails(widget.moduleId);

      if (!mounted) return;

      final module = ref.read(selectedModuleProvider).valueOrNull;
      if (module == null) return;

      // Load progress details if available
      await _loadProgressData(module);
    } catch (error) {
      debugPrint('Error loading module data: $error');
    } finally {
      if (mounted) {
        setState(() => _isRefreshing = false);
      }
    }
  }

  Future<void> _loadProgressData(ModuleDetail module) async {
    if (module.progress.isNotEmpty) {
      final progressId = module.progress[0].id;
      await ref
          .read(selectedProgressProvider.notifier)
          .loadProgressDetails(progressId);
      return;
    }

    final isAuthenticated = ref.read(authStateProvider).valueOrNull ?? false;
    if (!isAuthenticated) return;

    await ref
        .read(selectedProgressProvider.notifier)
        .loadModuleProgress(widget.moduleId);
  }

  Future<void> _checkGrammarContent() async {
    if (!mounted) return;

    setState(() => _isCheckingGrammar = true);

    try {
      final grammarRepository = ref.read(grammarRepositoryProvider);
      final List<GrammarSummary> grammars = await grammarRepository
          .getGrammarsByModuleId(widget.moduleId);

      if (!mounted) return;
      setState(() {
        _hasGrammar = grammars.isNotEmpty;
        _isCheckingGrammar = false;
      });
    } catch (e) {
      debugPrint('Error checking grammar content: $e');
      if (mounted) {
        setState(() {
          _hasGrammar = false;
          _isCheckingGrammar = false;
        });
      }
    }
  }

  Future<void> _handleRefresh() async {
    setState(() => _isInitialLoad = true);
    await _performDataLoad();
    await _checkGrammarContent();
    setState(() {
      _isInitialLoad = false;
      _grammarCheckCompleted = true;
    });
  }

  Future<void> _startLearning() async {
    if (!mounted) return;

    if (!_isAuthenticated()) {
      _showLoginSnackBar();
      return;
    }

    final module = ref.read(selectedModuleProvider).valueOrNull;
    if (module == null) {
      SnackBarUtils.show(
        context,
        'Module details not available. Please refresh.',
      );
      return;
    }

    final existingProgress = ref.read(selectedProgressProvider).valueOrNull;
    if (existingProgress != null) {
      _navigateToProgress(existingProgress.id);
      return;
    }

    await _createAndNavigateToProgress();
  }

  Future<void> _createAndNavigateToProgress() async {
    if (_isCreatingProgress) return;

    setState(() => _isCreatingProgress = true);

    try {
      final newProgress = await _createNewProgress();
      if (newProgress == null) {
        if (mounted) {
          SnackBarUtils.show(context, 'Failed to start learning progress.');
        }
        return;
      }

      if (!mounted) return;
      _navigateToProgress(newProgress.id);
    } catch (error) {
      debugPrint('Error creating progress: $error');
      if (mounted) {
        SnackBarUtils.show(
          context,
          'Error starting learning: ${error.toString()}',
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isCreatingProgress = false);
      }
    }
  }

  Future<void> _navigateToGrammar() async {
    if (!mounted) return;

    setState(() => _isCheckingGrammar = true);

    final module = ref.read(selectedModuleProvider).valueOrNull;
    if (module?.bookId.isEmpty ?? true) {
      if (mounted) {
        SnackBarUtils.show(context, 'Module information is not available.');
        setState(() => _isCheckingGrammar = false);
      }
      return;
    }

    try {
      final grammarRepository = ref.read(grammarRepositoryProvider);
      final List<GrammarSummary> grammars = await grammarRepository
          .getGrammarsByModuleId(widget.moduleId);

      if (!mounted) return;

      await _handleGrammarNavigation(grammars, module);
    } catch (e) {
      if (mounted) {
        SnackBarUtils.show(
          context,
          'Failed to load grammar: ${e.toString()}',
          backgroundColor: Theme.of(context).colorScheme.errorContainer,
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isCheckingGrammar = false);
      }
    }
  }

  Future<void> _handleGrammarNavigation(
    List<GrammarSummary> grammars,
    ModuleDetail? module,
  ) async {
    if (grammars.isEmpty) {
      SnackBarUtils.show(
        context,
        'No grammar content available for this module.',
      );
      return;
    }

    if (grammars.length == 1) {
      final singleGrammar = grammars.first;
      context.push(
        '/books/${module!.bookId}/modules/${widget.moduleId}/grammars/${singleGrammar.id}',
      );
      return;
    }

    context.go('/books/${module!.bookId}/modules/${widget.moduleId}/grammar');
  }

  bool _isAuthenticated() {
    final isLoggedIn = ref.watch(authStateProvider).valueOrNull ?? false;
    final currentUser = ref.watch(currentUserProvider);
    return isLoggedIn && currentUser != null;
  }

  void _showLoginSnackBar() {
    if (!mounted) return;
    SnackBarUtils.show(
      context,
      'Please log in to start learning this module.',
      backgroundColor: Theme.of(context).colorScheme.errorContainer,
    );
  }

  Future<ProgressDetail?> _createNewProgress() async {
    if (!mounted) return null;

    final currentUser = ref.read(currentUserProvider);
    if (currentUser == null) {
      _showLoginSnackBar();
      return null;
    }

    return ref
        .read(progressStateProvider.notifier)
        .createProgress(
          moduleId: widget.moduleId,
          userId: currentUser.id,
          firstLearningDate: DateTime.now(),
          nextStudyDate: DateTime.now(),
        );
  }

  void _navigateToProgress(String progressId) {
    if (!mounted) return;

    if (progressId.isEmpty) {
      SnackBarUtils.show(context, 'Invalid progress ID. Cannot navigate.');
      return;
    }

    NavigationHelper.pushWithResult(context, '/progress/$progressId').then((_) {
      if (mounted) {
        _handleRefresh();
      }
    });
  }

  PreferredSizeWidget _buildAppBar(String? moduleTitle) {
    return AppBarWithBack(
      title: moduleTitle ?? 'Module Details',
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
                  icon: const Icon(Icons.refresh_outlined),
                  onPressed: _handleRefresh,
                  tooltip: 'Refresh module data',
                ),
        ),
      ],
    );
  }

  Widget _buildLoadingState() {
    return const ModuleLoadingSkeleton();
  }

  Widget _buildErrorState(String error) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: SLErrorView(
          message: 'Error loading module: $error',
          onRetry: _handleRefresh,
          icon: Icons.error_outline_rounded,
        ),
      ),
    );
  }

  Widget _buildModuleNotFoundState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: SLErrorView(
          message: 'Module not found or no longer available.',
          onRetry: _handleRefresh,
          icon: Icons.library_books_outlined,
        ),
      ),
    );
  }

  Widget _buildModuleContent(
    ModuleDetail module,
    ProgressDetail? userProgress,
  ) {
    final hasProgress = userProgress != null || module.progress.isNotEmpty;

    return RefreshIndicator(
      onRefresh: _handleRefresh,
      color: Theme.of(context).colorScheme.primary,
      backgroundColor: Theme.of(context).colorScheme.surfaceContainerHighest,
      child: CustomScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        slivers: [
          SliverPadding(
            padding: const EdgeInsets.all(AppDimens.paddingL),
            sliver: SliverList(
              delegate: SliverChildListDelegate([
                ModuleDetailHeader(
                  module: module,
                  showAnimation: !_isInitialLoad,
                  onRefresh: _handleRefresh,
                  isRefreshing: _isRefreshing,
                ),
                const SizedBox(height: AppDimens.spaceXL),

                if (hasProgress) ...[
                  ModuleProgressCard(
                    progress:
                        userProgress ?? module.progress[0] as ProgressDetail,
                    moduleTitle: module.title,
                    onTap: _navigateToProgress,
                    showAnimation: !_isInitialLoad,
                  ),
                  const SizedBox(height: AppDimens.spaceXL),
                ],

                ModuleDetailContent(
                  module: module,
                  showAnimation: !_isInitialLoad,
                  onViewGrammar: _navigateToGrammar,
                  hasGrammar: _hasGrammar,
                ),
                const SizedBox(height: AppDimens.spaceXXXL), // Space for FAB
              ]),
            ),
          ),
        ],
      ),
    );
  }

  Widget? _buildFloatingActionButton(
    ModuleDetail? module,
    ProgressDetail? userProgress,
  ) {
    // Always show FAB section when module is available and grammar check completed
    if (module == null || !_grammarCheckCompleted) return null;

    final hasProgress = userProgress != null || module.progress.isNotEmpty;

    return ModuleDetailFabSection(
      hasProgress: hasProgress,
      hasGrammar: _hasGrammar,
      isCheckingGrammar: _isCheckingGrammar,
      onStartLearning: _isCreatingProgress ? null : _startLearning,
      onViewGrammar: _hasGrammar ? _navigateToGrammar : null,
      onViewProgress: hasProgress
          ? () {
              final progressId =
                  userProgress?.id ??
                  (module.progress.isNotEmpty ? module.progress[0].id : null);
              if (progressId != null) {
                _navigateToProgress(progressId);
              }
            }
          : null,
      showAnimation: !_isInitialLoad,
    );
  }

  Widget _buildBody(
    AsyncValue<ModuleDetail?> moduleAsync,
    AsyncValue<ProgressDetail?> progressAsync,
  ) {
    if (_isInitialLoad) {
      return _buildLoadingState();
    }

    // Check for module loading error
    if (moduleAsync.hasError) {
      return _buildErrorState(moduleAsync.error.toString());
    }

    final module = moduleAsync.valueOrNull;
    if (module == null) {
      return _buildModuleNotFoundState();
    }

    // Progress error is not critical if we have module data
    // Only show progress error if we expected progress but failed to load
    if (progressAsync.hasError && module.progress.isNotEmpty) {
      debugPrint('Progress loading error: ${progressAsync.error}');
      // Continue with null progress rather than showing error
    }

    return FadeTransition(
      opacity: _fadeAnimation,
      child: SlideTransition(
        position: _slideAnimation,
        child: _buildModuleContent(module, progressAsync.valueOrNull),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final moduleAsync = ref.watch(selectedModuleProvider);
    final progressAsync = ref.watch(selectedProgressProvider);

    return Scaffold(
      backgroundColor: colorScheme.surface,
      appBar: _buildAppBar(moduleAsync.valueOrNull?.title),
      body: _buildBody(moduleAsync, progressAsync),
      floatingActionButton: _buildFloatingActionButton(
        moduleAsync.valueOrNull,
        progressAsync.valueOrNull,
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }
}
