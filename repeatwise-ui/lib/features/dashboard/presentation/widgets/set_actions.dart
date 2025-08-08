import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_dimens.dart';
import '../../../../core/widgets/widgets.dart';

class SetActions extends StatelessWidget {
  const SetActions({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Quick Actions',
          style: Theme.of(
            context,
          ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: AppDimens.spacing16),
        Row(
          children: [
            Expanded(
              child: _ActionCard(
                title: 'My Sets',
                subtitle: 'View all your learning sets',
                icon: Icons.list,
                color: AppColors.primary,
                onTap: () {
                  context.go('/sets');
                },
              ),
            ),
            const SizedBox(width: AppDimens.spacing12),
            Expanded(
              child: _ActionCard(
                title: 'Create Set',
                subtitle: 'Start a new learning set',
                icon: Icons.add,
                color: AppColors.success,
                onTap: () {
                  context.go('/sets/create');
                },
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spacing12),
        Row(
          children: [
            Expanded(
              child: _ActionCard(
                title: 'Daily Review',
                subtitle: 'Review today\'s sets',
                icon: Icons.today,
                color: AppColors.warning,
                onTap: () {
                  context.go('/sets/review');
                },
              ),
            ),
            const SizedBox(width: AppDimens.spacing12),
            Expanded(
              child: _ActionCard(
                title: 'Statistics',
                subtitle: 'View your learning progress',
                icon: Icons.analytics,
                color: AppColors.secondary,
                onTap: () {
                  context.go('/sets/statistics');
                },
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class _ActionCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  const _ActionCard({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return RepeatWiseCard(
      onTap: onTap,
      child: Padding(
        padding: AppDimens.paddingAll16,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  padding: AppDimens.paddingAll8,
                  decoration: BoxDecoration(
                    color: color.withOpacity(0.1),
                    borderRadius: AppDimens.borderRadius8,
                  ),
                  child: Icon(icon, color: color, size: AppDimens.iconSize20),
                ),
                const Spacer(),
                Icon(
                  Icons.arrow_forward_ios,
                  size: AppDimens.iconSize16,
                  color: AppColors.gray400,
                ),
              ],
            ),
            const SizedBox(height: AppDimens.spacing12),
            Text(
              title,
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: AppDimens.textSize16,
              ),
            ),
            const SizedBox(height: AppDimens.spacing4),
            Text(
              subtitle,
              style: TextStyle(
                fontSize: AppDimens.textSize12,
                color: AppColors.gray600,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
