// lib/presentation/widgets/books/book_detail_tabs.dart
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/utils/date_utils.dart';
import 'package:spaced_learning_app/domain/models/book.dart';
import 'package:spaced_learning_app/domain/models/module.dart';
import 'package:spaced_learning_app/presentation/utils/book_formatter.dart';
import 'package:spaced_learning_app/presentation/widgets/books/metadata_item.dart';
import 'package:spaced_learning_app/presentation/widgets/books/module_card.dart';
import 'package:spaced_learning_app/presentation/widgets/books/stat_item.dart';

class BookOverviewTab extends StatelessWidget {
  final BookDetail book;

  const BookOverviewTab({super.key, required this.book});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppDimens.paddingL),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (book.description?.isNotEmpty ?? false) ...[
            Text(
              'Description',
              style: theme.textTheme.titleLarge?.copyWith(
                color: colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: AppDimens.spaceM),
            Text(
              book.description ?? '',
              style: theme.textTheme.bodyMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: AppDimens.spaceXL),
          ],

          _buildStatsSection(context),
          const SizedBox(height: AppDimens.spaceXL),

          _buildBookMetadataSection(context),
          const SizedBox(height: AppDimens.spaceXXL),
        ],
      ),
    );
  }

  Widget _buildStatsSection(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    // Calculate total words
    final totalWords = book.modules.fold<int>(
      0,
      (total, module) => total + (module.wordCount ?? 0),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Book Stats',
          style: theme.textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
        ),
        const SizedBox(height: AppDimens.spaceM),
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingL),
          decoration: BoxDecoration(
            color: colorScheme.surfaceContainerLow,
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              StatItemWidget(
                value: book.modules.length.toString(),
                label: 'Modules',
                icon: Icons.menu_book,
                color: colorScheme.primary,
              ),
              StatItemWidget(
                value: totalWords.toString(),
                label: 'Words',
                icon: Icons.text_fields,
                color: colorScheme.secondary,
              ),
              StatItemWidget(
                value: book.modules
                    .fold<int>(
                      0,
                      (total, module) => total + (module.progress.length),
                    )
                    .toString(),
                label: 'Students',
                icon: Icons.people,
                color: colorScheme.tertiary,
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildBookMetadataSection(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Book Details',
          style: theme.textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
        ),
        const SizedBox(height: AppDimens.spaceM),
        Card(
          elevation: 0,
          color: colorScheme.surfaceContainerLow,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
          ),
          child: Padding(
            padding: const EdgeInsets.all(AppDimens.paddingL),
            child: Column(
              children: [
                // Display metadata fields
                MetadataItemWidget(
                  label: 'Status',
                  value: BookFormatter.formatStatus(book.status),
                  icon: Icons.info_outline,
                ),
                if (book.difficultyLevel != null) ...[
                  const SizedBox(height: AppDimens.spaceM),
                  MetadataItemWidget(
                    label: 'Difficulty',
                    value: BookFormatter.formatDifficulty(book.difficultyLevel),
                    icon: Icons.signal_cellular_alt_outlined,
                  ),
                ],
                if (book.category != null) ...[
                  const SizedBox(height: AppDimens.spaceM),
                  MetadataItemWidget(
                    label: 'Category',
                    value: book.category!,
                    icon: Icons.category_outlined,
                  ),
                ],
                if (book.createdAt != null) ...[
                  const SizedBox(height: AppDimens.spaceM),
                  MetadataItemWidget(
                    label: 'Created',
                    value: AppDateUtils.formatDate(book.createdAt!),
                    icon: Icons.calendar_today,
                  ),
                ],
                if (book.updatedAt != null) ...[
                  const SizedBox(height: AppDimens.spaceM),
                  MetadataItemWidget(
                    label: 'Updated',
                    value: AppDateUtils.formatDate(book.updatedAt!),
                    icon: Icons.update,
                  ),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class BookModulesTab extends StatelessWidget {
  final List<ModuleSummary> modules;
  final String bookId;
  final VoidCallback onRetry;

  const BookModulesTab({
    super.key,
    required this.modules,
    required this.bookId,
    required this.onRetry,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (modules.isEmpty) {
      return Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.menu_book_outlined,
              size: AppDimens.iconXXL,
              color: colorScheme.onSurfaceVariant.withValues(alpha: 0.3),
            ),
            const SizedBox(height: AppDimens.spaceL),
            Text(
              'No modules available for this book',
              style: theme.textTheme.titleMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
            ),
            const SizedBox(height: AppDimens.spaceL),
            TextButton.icon(
              onPressed: onRetry,
              icon: const Icon(Icons.refresh),
              label: const Text('Refresh'),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(AppDimens.paddingL),
      itemCount: modules.length,
      itemBuilder: (context, index) {
        final module = modules[index];
        return ModuleCardWidget(
          module: module,
          index: index,
          onTap: () => _navigateToModuleDetail(context, bookId, module.id),
        );
      },
    );
  }

  void _navigateToModuleDetail(
    BuildContext context,
    String bookId,
    String moduleId,
  ) {
    GoRouter.of(context).push('/books/$bookId/modules/$moduleId');
  }
}
