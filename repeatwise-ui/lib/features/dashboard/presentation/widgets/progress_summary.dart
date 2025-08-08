import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_dimens.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/models/set.dart';
import '../../../sets/providers/set_providers.dart';

class ProgressSummary extends ConsumerWidget {
  const ProgressSummary({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final setsAsync = ref.watch(setsNotifierProvider);

    return setsAsync.when(
      data: (sets) {
        final totalSets = sets.length;
        final activeSets = sets.where((set) => set.isActive).length;
        final completedSets = sets.where((set) => set.isCompleted).length;
        final learningSets = sets.where((set) => set.isLearning).length;

        return RepeatWiseCard(
          child: Padding(
            padding: AppDimens.paddingAll16,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Learning Progress',
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        context.go('/sets/statistics');
                      },
                      child: const Text('View Details'),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: _ProgressItem(
                        title: 'Total Sets',
                        value: totalSets.toString(),
                        color: AppColors.primary,
                        icon: Icons.list,
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: _ProgressItem(
                        title: 'Active',
                        value: activeSets.toString(),
                        color: AppColors.success,
                        icon: Icons.play_circle,
                      ),
                    ),
                    const SizedBox(width: AppDimens.spacing12),
                    Expanded(
                      child: _ProgressItem(
                        title: 'Learning',
                        value: learningSets.toString(),
                        color: AppColors.warning,
                        icon: Icons.school,
                      ),
                    ),
                    const SizedBox(width: AppDimens.spacing12),
                    Expanded(
                      child: _ProgressItem(
                        title: 'Completed',
                        value: completedSets.toString(),
                        color: AppColors.secondary,
                        icon: Icons.check_circle,
                      ),
                    ),
                  ],
                ),
                if (totalSets > 0) ...[
                  const SizedBox(height: 16),
                  LinearProgressIndicator(
                    value: totalSets > 0 ? completedSets / totalSets : 0,
                    backgroundColor: Colors.grey[300],
                    valueColor: AlwaysStoppedAnimation<Color>(
                      AppColors.primary,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '${((completedSets / totalSets) * 100).toInt()}% Complete',
                    style: Theme.of(
                      context,
                    ).textTheme.bodySmall?.copyWith(color: Colors.grey[600]),
                  ),
                ],
              ],
            ),
          ),
        );
      },
      loading: () => const Card(
        child: Padding(
          padding: EdgeInsets.all(16.0),
          child: Center(child: CircularProgressIndicator()),
        ),
      ),
      error: (error, stack) => Card(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Text('Error loading progress: $error'),
        ),
      ),
    );
  }
}

class _ProgressItem extends StatelessWidget {
  final String title;
  final String value;
  final Color color;
  final IconData icon;

  const _ProgressItem({
    required this.title,
    required this.value,
    required this.color,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          padding: AppDimens.paddingAll8,
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            borderRadius: AppDimens.borderRadius8,
          ),
          child: Icon(icon, color: color, size: AppDimens.iconSize20),
        ),
        const SizedBox(height: AppDimens.spacing8),
        Text(
          value,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
        const SizedBox(height: AppDimens.spacing4),
        Text(
          title,
          style: Theme.of(
            context,
          ).textTheme.bodySmall?.copyWith(color: AppColors.gray600),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
