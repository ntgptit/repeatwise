import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';
import 'package:spaced_learning_app/presentation/utils/snackbar_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/repetition_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/cycle_completion_dialog.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/progress_header_widget.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/progress_repetition_list.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/reschedule_dialog.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/score_input_dialog.dart';

class ProgressDetailScreen extends ConsumerStatefulWidget {
  final String progressId;

  const ProgressDetailScreen({super.key, required this.progressId});

  @override
  ConsumerState<ProgressDetailScreen> createState() =>
      _ProgressDetailScreenState();
}

class _ProgressDetailScreenState extends ConsumerState<ProgressDetailScreen> {
  late Future<void> _dataLoadingFuture;
  String? _moduleUrl;

  @override
  void initState() {
    super.initState();
    Future(() => _loadInitialData());
  }

  Future<void> _loadInitialData() async {
    try {
      await ref
          .read(selectedProgressProvider.notifier)
          .loadProgressDetails(widget.progressId);

      // Lấy progress từ provider
      final progress = ref.read(selectedProgressProvider).valueOrNull;

      // Nếu có progress và có moduleId, tải thông tin module để lấy URL
      // if (progress != null && progress.moduleId.isNotEmpty) {
      //   try {
      //     await ref
      //         .read(selectedModuleProvider.notifier)
      //         .loadModuleDetails(progress.moduleId);
      //
      //     // Lấy module URL từ module details
      //     final module = ref.read(selectedModuleProvider).valueOrNull;
      //     if (module != null && module.url != null) {
      //       setState(() {
      //         _moduleUrl = module.url;
      //       });
      //     }
      //   } catch (e) {
      //     debugPrint('Error loading module details: $e');
      //   }
      // }

      await ref
          .read(repetitionStateProvider.notifier)
          .loadRepetitionsByProgressId(widget.progressId);
    } catch (e) {
      debugPrint('Error loading initial data: $e');
    }
  }

  Future<void> _reloadData() async {
    setState(() {
      _dataLoadingFuture = _loadInitialData();
    });
  }

  Future<void> _markRepetitionCompleted(String repetitionId) async {
    final score = await ScoreInputDialog.show(context);
    if (score == null) return;

    final updatedRepetition = await ref
        .read(repetitionStateProvider.notifier)
        .updateRepetition(
          repetitionId,
          status: RepetitionStatus.completed,
          percentComplete: score,
        );

    if (updatedRepetition == null) return;

    final allCompleted = await ref
        .read(repetitionStateProvider.notifier)
        .areAllRepetitionsCompleted(updatedRepetition.moduleProgressId);

    if (allCompleted) {
      _showCycleCompletionSnackBar();
    } else {
      _showCompletionSnackBar(score);
    }

    await _reloadData();
  }

  Future<void> _rescheduleRepetition(
    String repetitionId,
    DateTime currentDate,
    bool rescheduleFollowing,
  ) async {
    final repetitions = ref.read(repetitionStateProvider).valueOrNull ?? [];
    final currentRepetition = repetitions.firstWhere(
      (r) => r.id == repetitionId,
      orElse: () => throw Exception('Repetition not found'),
    );

    final result = await RescheduleDialog.show(
      context,
      initialDate: currentDate,
      title: 'Reschedule Repetition #${currentRepetition.formatOrder()}',
    );

    if (result != null) {
      final newDate = result['date'] as DateTime;
      final rescheduleFollowing = result['rescheduleFollowing'] as bool;

      final updatedRepetition = await ref
          .read(repetitionStateProvider.notifier)
          .updateRepetition(
            repetitionId,
            reviewDate: newDate,
            rescheduleFollowing: rescheduleFollowing,
          );

      if (updatedRepetition != null) {
        SnackBarUtils.show(
          context,
          'Repetition rescheduled to ${DateFormat('dd MMM').format(newDate)}',
        );

        await _reloadData();
      }
    }
  }

  void _showCompletionSnackBar(double score) {
    SnackBarUtils.show(context, 'Test score saved: ${score.toInt()}%');
  }

  void _showCycleCompletionSnackBar() {
    final theme = Theme.of(context);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: const Text(
          'Congratulations! You have completed this learning cycle.',
        ),
        backgroundColor: theme.colorScheme.tertiary,
        duration: const Duration(seconds: 5),
        action: SnackBarAction(
          label: 'Details',
          onPressed: _showCycleCompletionDialog,
        ),
      ),
    );
  }

  void _showCycleCompletionDialog() {
    showDialog(
      context: context,
      builder: (context) => const CycleCompletionDialog(),
    );
  }

  @override
  Widget build(BuildContext context) {
    final progressAsync = ref.watch(selectedProgressProvider);
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (widget.progressId.isEmpty) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('Progress Details'),
          backgroundColor: colorScheme.surfaceContainerHigh,
        ),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(Icons.error_outline, size: 64, color: colorScheme.error),
              const SizedBox(height: 16),
              Text('Invalid progress ID', style: theme.textTheme.titleLarge),
              const SizedBox(height: 24),
              FilledButton.tonal(
                onPressed: () => context.pop(),
                child: const Text('Go Back'),
              ),
            ],
          ),
        ),
      );
    }

    return Scaffold(
      appBar: _buildAppBar(progressAsync.valueOrNull?.moduleTitle),
      body: SafeArea(
        child: progressAsync.when(
          data: (progress) {
            if (progress == null) {
              return _buildProgressNotFoundView();
            }
            return _buildProgressView(progress);
          },
          loading: () => const Center(
            child: SLLoadingIndicator(type: LoadingIndicatorType.circle),
          ),
          error: (error, stack) => Center(
            child: SLErrorView(
              message: error.toString(),
              onRetry: _reloadData,
              icon: Icons.error_outline,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildProgressNotFoundView() {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Center(
      child: Container(
        padding: const EdgeInsets.all(AppDimens.paddingXL),
        constraints: const BoxConstraints(maxWidth: 400),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.all(AppDimens.paddingL),
              decoration: BoxDecoration(
                color: colorScheme.errorContainer,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.warning_amber_rounded,
                size: 64,
                color: colorScheme.error,
              ),
            ),
            const SizedBox(height: AppDimens.spaceL),
            Text(
              'Progress Not Found',
              style: theme.textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: colorScheme.error,
              ),
            ),
            const SizedBox(height: AppDimens.spaceM),
            Text(
              'The progress with ID ${widget.progressId} could not be found or may have been deleted.',
              textAlign: TextAlign.center,
              style: theme.textTheme.bodyLarge?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: AppDimens.spaceXL),
            FilledButton.icon(
              onPressed: _reloadData,
              icon: const Icon(Icons.refresh),
              label: const Text('Try Again'),
              style: FilledButton.styleFrom(minimumSize: const Size(200, 50)),
            ),
            const SizedBox(height: AppDimens.spaceM),
            OutlinedButton.icon(
              onPressed: () => context.go('/due-progress'),
              icon: const Icon(Icons.arrow_back),
              label: const Text('Go Back'),
              style: OutlinedButton.styleFrom(minimumSize: const Size(200, 50)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProgressView(ProgressDetail progress) {
    return RefreshIndicator(
      onRefresh: _reloadData,
      child: ListView(
        padding: const EdgeInsets.all(AppDimens.paddingM),
        children: [
          ProgressHeaderWidget(
            progress: progress,
            onCycleCompleteDialogRequested: _showCycleCompletionDialog,
          ),
          const SizedBox(height: AppDimens.spaceXL),
          ProgressRepetitionList(
            progressId: widget.progressId,
            currentCycleStudied: progress.cyclesStudied,
            onMarkCompleted: _markRepetitionCompleted,
            onReschedule: _rescheduleRepetition,
            onReload: _reloadData,
          ),
          const SizedBox(height: 100),
        ],
      ),
    );
  }

  AppBar _buildAppBar(String? moduleTitle) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return AppBar(
      title: Text(
        moduleTitle ?? 'Module Progress',
        style: theme.textTheme.titleLarge?.copyWith(
          fontWeight: FontWeight.bold,
        ),
      ),
      centerTitle: true,
      backgroundColor: colorScheme.surfaceContainerHigh,
      elevation: 0,
      scrolledUnderElevation: 4,
      actions: [
        IconButton(
          icon: const Icon(Icons.help_outline),
          tooltip: 'Learn about repetition cycles',
          onPressed: () => context.push('/help/spaced-repetition'),
        ),
        IconButton(
          icon: const Icon(Icons.refresh),
          tooltip: 'Refresh data',
          onPressed: _reloadData,
        ),
      ],
    );
  }
}
