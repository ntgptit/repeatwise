// lib/presentation/screens/progress/widgets/progress_repetition_list.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/viewmodels/repetition_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';
import 'package:spaced_learning_app/presentation/screens/progress/widgets/compact_repetition_list.dart';

class ProgressRepetitionList extends ConsumerStatefulWidget {
  final String progressId;
  final CycleStudied currentCycleStudied;
  final Future<void> Function(String) onMarkCompleted;
  final Future<void> Function(String, DateTime, bool) onReschedule;
  final Future<void> Function() onReload;

  const ProgressRepetitionList({
    super.key,
    required this.progressId,
    required this.currentCycleStudied,
    required this.onMarkCompleted,
    required this.onReschedule,
    required this.onReload,
  });

  @override
  ConsumerState<ProgressRepetitionList> createState() =>
      _ProgressRepetitionListState();
}

class _ProgressRepetitionListState extends ConsumerState<ProgressRepetitionList>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: AppDimens.durationM),
    );

    _fadeAnimation = CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    );

    _animationController.forward();

    // Load repetitions when widget initializes
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref
          .read(repetitionStateProvider.notifier)
          .loadRepetitionsByProgressId(widget.progressId);
    });
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  void _restartAnimation() {
    _animationController.reset();
    _animationController.forward();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final _ = theme.colorScheme;
    final repetitionsState = ref.watch(repetitionStateProvider);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Review Schedule', style: theme.textTheme.titleLarge),
        const SizedBox(height: AppDimens.spaceL),
        repetitionsState.when(
          data: (repetitions) {
            if (repetitions.isNotEmpty) {
              _restartAnimation();
            }

            return FadeTransition(
              opacity: _fadeAnimation,
              child: CompactRepetitionList(
                progressId: widget.progressId,
                currentCycleStudied: widget.currentCycleStudied,
                onMarkCompleted: widget.onMarkCompleted,
                onReschedule: widget.onReschedule,
                onReload: widget.onReload,
              ),
            );
          },
          loading: () => const Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                SLLoadingIndicator(),
                SizedBox(height: AppDimens.spaceS),
                Text('Loading repetitions...'),
              ],
            ),
          ),
          error: (error, stackTrace) => SLErrorView(
            message: error.toString(),
            onRetry: () => ref
                .read(repetitionStateProvider.notifier)
                .loadRepetitionsByProgressId(widget.progressId),
            compact: true,
          ),
        ),
      ],
    );
  }
}
