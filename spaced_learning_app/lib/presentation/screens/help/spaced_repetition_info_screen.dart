import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/utils/cycle_formatter.dart';

class SpacedRepetitionInfoScreen extends ConsumerWidget {
  const SpacedRepetitionInfoScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Spaced Repetition'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            final router = GoRouter.of(context);
            if (router.canPop()) {
              router.pop();
              return;
            }
            router.go('/');
          },
          tooltip: 'Back',
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'How Spaced Repetition Works',
              style: theme.textTheme.headlineSmall,
            ),
            const SizedBox(height: AppDimens.spaceL),
            Text(
              'Spaced repetition is a learning technique that incorporates increasing intervals of time between subsequent review of previously learned material to exploit the psychological spacing effect.',
              style: theme.textTheme.bodyLarge,
            ),
            const SizedBox(height: AppDimens.spaceL),
            _buildSection(
              context,
              'The Science Behind It',
              'Spaced repetition takes advantage of the psychological spacing effect, which demonstrates that learning is more effective when study sessions are spaced out over time, rather than crammed into a single session. This technique directly addresses the "forgetting curve" - our natural tendency to forget information over time.',
              Icons.psychology,
              colorScheme.onSurfaceVariant,
            ),
            _buildSection(
              context,
              'How It Works In This App',
              'Our app schedules your learning into optimal review intervals. After initial learning, you\'ll review the material at gradually increasing intervals: 1 day, 7 days, 16 days, and 35 days. This schedule is optimized for long-term retention.',
              Icons.schedule,
              colorScheme.primary,
            ),
            Text('Learning Cycles', style: theme.textTheme.titleLarge),
            const SizedBox(height: AppDimens.spaceM),
            _buildCycleTable(context),
            const SizedBox(height: AppDimens.spaceXL),
            _buildSection(
              context,
              'Tips for Effective Learning',
              '• Complete all repetitions in a cycle to maximize learning\n'
                  '• Be consistent with your study schedule\n'
                  '• Actively recall information before checking answers\n'
                  '• Connect new information to things you already know\n'
                  '• Take brief notes during each study session\n'
                  '• Review right before sleep to improve memory consolidation',
              Icons.tips_and_updates,
              colorScheme.tertiary,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(
    BuildContext context,
    String title,
    String content,
    IconData icon,
    Color color,
  ) {
    final theme = Theme.of(context);

    return Container(
      margin: const EdgeInsets.only(bottom: AppDimens.spaceXL),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: color),
              const SizedBox(width: AppDimens.spaceM),
              Text(
                title,
                style: theme.textTheme.titleLarge?.copyWith(color: color),
              ),
            ],
          ),
          const SizedBox(height: AppDimens.spaceM),
          Text(content, style: theme.textTheme.bodyMedium),
        ],
      ),
    );
  }

  Widget _buildCycleTable(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          children: [
            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: Text(
                    'Cycle',
                    style: theme.textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Expanded(
                  flex: 3,
                  child: Text(
                    'Description',
                    style: theme.textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const Divider(height: AppDimens.spaceXL),
            ...CycleStudied.values.map(
              (cycle) => _buildCycleRow(context, cycle),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCycleRow(BuildContext context, CycleStudied cycle) {
    final theme = Theme.of(context);
    final color = CycleFormatter.getColor(cycle, context);

    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            flex: 2,
            child: Container(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.paddingS,
                vertical: AppDimens.paddingXXS,
              ),
              decoration: BoxDecoration(
                color: color.withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(AppDimens.radiusS),
                border: Border.all(color: color),
              ),
              child: Text(
                CycleFormatter.format(cycle),
                textAlign: TextAlign.center,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: color,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            flex: 3,
            child: Text(
              CycleFormatter.getDescription(cycle),
              style: theme.textTheme.bodyMedium,
            ),
          ),
        ],
      ),
    );
  }
}
