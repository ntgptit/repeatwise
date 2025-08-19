import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/utils/date_utils.dart';
import 'package:spaced_learning_app/domain/models/book.dart';
import 'package:spaced_learning_app/presentation/utils/book_formatter.dart';

import 'book_cover.dart';

class BookListCard extends StatelessWidget {
  final BookSummary book;
  final VoidCallback onTap;

  const BookListCard({super.key, required this.book, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: AppDimens.elevationS,
      margin: const EdgeInsets.only(bottom: AppDimens.paddingM),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
      ),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingM),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              BookCover(book: book),

              const SizedBox(width: AppDimens.spaceL),

              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      book.name,
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: colorScheme.onSurface,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),

                    const SizedBox(height: AppDimens.spaceS),

                    Row(
                      children: [
                        Icon(
                          Icons.library_books_outlined,
                          size: AppDimens.iconS,
                          color: colorScheme.primary,
                        ),
                        const SizedBox(width: AppDimens.spaceXS),
                        Text(
                          '${book.moduleCount} ${book.moduleCount == 1 ? 'module' : 'modules'}',
                          style: theme.textTheme.bodyMedium?.copyWith(
                            color: colorScheme.onSurfaceVariant,
                          ),
                        ),
                      ],
                    ),

                    const SizedBox(height: AppDimens.spaceS),

                    Row(
                      children: [
                        _buildStatusBadge(book.status, theme, colorScheme),
                        if (book.difficultyLevel != null) ...[
                          const SizedBox(width: AppDimens.spaceXS),
                          _buildDifficultyBadge(
                            book.difficultyLevel!,
                            theme,
                            colorScheme,
                          ),
                        ],
                      ],
                    ),

                    if (book.createdAt != null) ...[
                      const SizedBox(height: AppDimens.spaceS),
                      Row(
                        children: [
                          Icon(
                            Icons.calendar_today_outlined,
                            size: AppDimens.iconXS,
                            color: colorScheme.onSurfaceVariant,
                          ),
                          const SizedBox(width: AppDimens.spaceXXS),
                          Text(
                            'Created: ${AppDateUtils.formatDate(book.createdAt!)}',
                            style: theme.textTheme.bodySmall?.copyWith(
                              color: colorScheme.onSurfaceVariant,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ],
                ),
              ),

              Icon(
                Icons.chevron_right,
                color: colorScheme.onSurfaceVariant,
                size: AppDimens.iconL,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatusBadge(
    BookStatus status,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final backgroundColor = BookFormatter.getStatusColor(
      status,
      colorScheme,
    ).withValues(alpha: AppDimens.opacityLight);
    final textColor = BookFormatter.getStatusColor(status, colorScheme);

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingXS,
        vertical: 2,
      ),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(AppDimens.radiusS),
      ),
      child: Text(
        BookFormatter.formatStatus(status),
        style: theme.textTheme.labelSmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildDifficultyBadge(
    DifficultyLevel level,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final backgroundColor = BookFormatter.getDifficultyColor(
      level,
      colorScheme,
    ).withValues(alpha: AppDimens.opacityLight);
    final textColor = BookFormatter.getDifficultyColor(level, colorScheme);

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingXS,
        vertical: 2,
      ),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(AppDimens.radiusS),
      ),
      child: Text(
        BookFormatter.formatDifficulty(level),
        style: theme.textTheme.labelSmall?.copyWith(
          color: textColor,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}
