// lib/presentation/widgets/learning/main/module_list.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/learning/main/module_card.dart';
import 'package:spaced_learning_app/presentation/widgets/learning/main/module_list_header.dart';

class ModuleList extends ConsumerWidget {
  final List<dynamic> modules;
  final ScrollController? scrollController;
  final VoidCallback onRefresh;

  const ModuleList({
    super.key,
    required this.modules,
    this.scrollController,
    required this.onRefresh,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (modules.isEmpty) {
      return _buildEmptyState(context);
    }

    return RefreshIndicator(
      onRefresh: () async => onRefresh(),
      child: ListView.builder(
        padding: const EdgeInsets.only(bottom: AppDimens.paddingXXXL),
        physics: const AlwaysScrollableScrollPhysics(),
        controller: scrollController,
        itemCount: modules.length + 1,
        // Adding header
        itemBuilder: (context, index) {
          if (index == 0) {
            return const ModuleListHeader();
          }

          final moduleIndex = index - 1;
          final module = modules[moduleIndex];
          return ModuleCard(module: module, index: moduleIndex);
        },
      ),
    );
  }

  Widget _buildEmptyState(BuildContext context) {
    final theme = Theme.of(context);

    return Center(
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingXXL),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              width: AppDimens.avatarSizeXXL,
              height: AppDimens.avatarSizeXXL,
              decoration: BoxDecoration(
                color: theme.colorScheme.surfaceContainerHighest,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.search_off_rounded,
                size: AppDimens.iconXXL,
                color: theme.colorScheme.primary.withValues(
                  alpha: AppDimens.opacitySemi,
                ),
              ),
            ),
            const SizedBox(height: AppDimens.spaceXL),
            Text(
              'No modules found',
              style: theme.textTheme.headlineSmall,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppDimens.spaceS),
            Text(
              'Try adjusting your filters or check back later',
              style: theme.textTheme.bodyLarge?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: AppDimens.spaceXXL),
            FilledButton.icon(
              onPressed: () {
                if (scrollController != null) {
                  scrollController!.animateTo(
                    0,
                    duration: const Duration(milliseconds: AppDimens.durationM),
                    curve: Curves.easeOut,
                  );
                }
              },
              icon: const Icon(Icons.filter_alt),
              label: const Text('Change filters'),
              style: FilledButton.styleFrom(
                padding: const EdgeInsets.symmetric(
                  horizontal: AppDimens.paddingXL,
                  vertical: AppDimens.paddingM,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
