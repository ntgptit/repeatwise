// lib/presentation/widgets/home/home_skeleton_screen.dart
import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class HomeSkeletonScreen extends StatelessWidget {
  const HomeSkeletonScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppDimens.paddingL),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Skeleton for welcome section
          _buildSkeletonWelcome(theme, colorScheme),
          const SizedBox(height: AppDimens.spaceXL),

          // Skeleton for dashboard section
          _buildSkeletonCard(theme, colorScheme, 200),
          const SizedBox(height: AppDimens.spaceXL),

          // Skeleton for insights section
          _buildSkeletonCard(theme, colorScheme, 180),
          const SizedBox(height: AppDimens.spaceXL),

          // Skeleton for due tasks section
          _buildSkeletonCard(theme, colorScheme, 150),
          const SizedBox(height: AppDimens.spaceXL),

          // Skeleton for quick actions
          _buildSkeletonActions(theme, colorScheme),
          SizedBox(height: MediaQuery.of(context).size.height * 0.1),
        ],
      ),
    );
  }

  Widget _buildSkeletonWelcome(ThemeData theme, ColorScheme colorScheme) {
    return Center(
      child: Column(
        children: [
          Shimmer.fromColors(
            baseColor: colorScheme.surfaceContainerHighest,
            highlightColor: colorScheme.surfaceContainerLow,
            child: Container(
              width: 200,
              height: 20,
              decoration: BoxDecoration(
                color: colorScheme.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ),
          ),
          const SizedBox(height: AppDimens.spaceS),
          Shimmer.fromColors(
            baseColor: colorScheme.surfaceContainerHighest,
            highlightColor: colorScheme.surfaceContainerLow,
            child: Container(
              width: 150,
              height: 16,
              decoration: BoxDecoration(
                color: colorScheme.surfaceContainerHighest,
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSkeletonCard(
    ThemeData theme,
    ColorScheme colorScheme,
    double height,
  ) {
    return Shimmer.fromColors(
      baseColor: colorScheme.surfaceContainerHighest,
      highlightColor: colorScheme.surfaceContainerLow,
      child: Container(
        width: double.infinity,
        height: height,
        decoration: BoxDecoration(
          color: colorScheme.surfaceContainerHighest,
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
      ),
    );
  }

  Widget _buildSkeletonActions(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Shimmer.fromColors(
          baseColor: colorScheme.surfaceContainerHighest,
          highlightColor: colorScheme.surfaceContainerLow,
          child: Container(
            width: 120,
            height: 20,
            decoration: BoxDecoration(
              color: colorScheme.surfaceContainerHighest,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
          ),
        ),
        const SizedBox(height: AppDimens.spaceL),
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: AppDimens.gridSpacingL,
          mainAxisSpacing: AppDimens.gridSpacingL,
          childAspectRatio: 1.1,
          padding: const EdgeInsets.all(AppDimens.paddingS),
          children: List.generate(
            4,
            (index) => Shimmer.fromColors(
              baseColor: colorScheme.surfaceContainerHighest,
              highlightColor: colorScheme.surfaceContainerLow,
              child: Container(
                decoration: BoxDecoration(
                  color: colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(AppDimens.radiusL),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
