import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/book.dart';
import 'package:spaced_learning_app/presentation/utils/book_formatter.dart';
import 'package:spaced_learning_app/presentation/widgets/books/filter_chip.dart';

class BookFilterPanel extends StatelessWidget {
  final List<String> categories;
  final String? selectedCategory;
  final BookStatus? selectedStatus;
  final DifficultyLevel? selectedDifficulty;
  final Function(String?)? onCategorySelected;
  final Function(BookStatus?)? onStatusSelected;
  final Function(DifficultyLevel?)? onDifficultySelected;
  final VoidCallback? onFiltersApplied;
  final VoidCallback? onFilterCleared;

  const BookFilterPanel({
    super.key,
    required this.categories,
    this.selectedCategory,
    this.selectedStatus,
    this.selectedDifficulty,
    this.onCategorySelected,
    this.onStatusSelected,
    this.onDifficultySelected,
    this.onFiltersApplied,
    this.onFilterCleared,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      padding: const EdgeInsets.fromLTRB(
        AppDimens.paddingL,
        AppDimens.paddingS,
        AppDimens.paddingL,
        AppDimens.paddingL,
      ),
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerLowest,
        borderRadius: const BorderRadius.only(
          bottomLeft: Radius.circular(AppDimens.radiusL),
          bottomRight: Radius.circular(AppDimens.radiusL),
        ),
        boxShadow: [
          BoxShadow(
            color: colorScheme.shadow.withValues(alpha: AppDimens.opacityLight),
            blurRadius: AppDimens.shadowRadiusM,
            offset: const Offset(0, AppDimens.shadowOffsetS),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (selectedCategory != null ||
              selectedStatus != null ||
              selectedDifficulty != null)
            Padding(
              padding: const EdgeInsets.only(bottom: AppDimens.paddingM),
              child: Wrap(
                spacing: AppDimens.spaceS,
                runSpacing: AppDimens.spaceS,
                children: [
                  if (selectedCategory != null)
                    FilterChipWidget(
                      label: selectedCategory!,
                      color: colorScheme.tertiary,
                      onDeleted: () {
                        if (onCategorySelected != null) {
                          onCategorySelected!(null);
                        }
                      },
                    ),
                  if (selectedStatus != null)
                    FilterChipWidget(
                      label: BookFormatter.formatStatus(selectedStatus!),
                      color: colorScheme.primary,
                      onDeleted: () {
                        if (onStatusSelected != null) {
                          onStatusSelected!(null);
                        }
                      },
                    ),
                  if (selectedDifficulty != null)
                    FilterChipWidget(
                      label: BookFormatter.formatDifficulty(selectedDifficulty),
                      color: colorScheme.secondary,
                      onDeleted: () {
                        if (onDifficultySelected != null) {
                          onDifficultySelected!(null);
                        }
                      },
                    ),
                  OutlinedButton.icon(
                    onPressed: onFilterCleared,
                    icon: Icon(
                      Icons.clear_all,
                      size: AppDimens.iconS,
                      color: colorScheme.primary,
                    ),
                    label: Text(
                      'Clear All',
                      style: theme.textTheme.labelMedium?.copyWith(
                        color: colorScheme.primary,
                      ),
                    ),
                    style: OutlinedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(
                        horizontal: AppDimens.paddingM,
                        vertical: AppDimens.paddingXS,
                      ),
                      visualDensity: VisualDensity.compact,
                    ),
                  ),
                ],
              ),
            ),

          _buildFilterSection(
            theme,
            colorScheme,
            'Category',
            Icons.category_outlined,
            colorScheme.primary,
            Wrap(
              spacing: AppDimens.spaceXS,
              runSpacing: AppDimens.spaceXS,
              children: [
                for (final category in categories)
                  ChoiceChip(
                    label: Text(category),
                    selected: selectedCategory == category,
                    onSelected: (selected) {
                      if (onCategorySelected != null) {
                        onCategorySelected!(selected ? category : null);
                      }
                    },
                    selectedColor: colorScheme.tertiaryContainer,
                    labelStyle: TextStyle(
                      color: selectedCategory == category
                          ? colorScheme.onTertiaryContainer
                          : colorScheme.onSurfaceVariant,
                    ),
                  ),
              ],
            ),
          ),

          _buildFilterSection(
            theme,
            colorScheme,
            'Status',
            Icons.published_with_changes_outlined,
            colorScheme.primary,
            Wrap(
              spacing: AppDimens.spaceXS,
              runSpacing: AppDimens.spaceXS,
              children: [
                for (final status in BookStatus.values)
                  ChoiceChip(
                    label: Text(BookFormatter.formatStatus(status)),
                    selected: selectedStatus == status,
                    onSelected: (selected) {
                      if (onStatusSelected != null) {
                        onStatusSelected!(selected ? status : null);
                      }
                    },
                    selectedColor: colorScheme.primaryContainer,
                    labelStyle: TextStyle(
                      color: selectedStatus == status
                          ? colorScheme.onPrimaryContainer
                          : colorScheme.onSurfaceVariant,
                    ),
                  ),
              ],
            ),
          ),

          _buildFilterSection(
            theme,
            colorScheme,
            'Difficulty',
            Icons.signal_cellular_alt_outlined,
            colorScheme.primary,
            Wrap(
              spacing: AppDimens.spaceXS,
              runSpacing: AppDimens.spaceXS,
              children: [
                for (final difficulty in DifficultyLevel.values)
                  ChoiceChip(
                    label: Text(BookFormatter.formatDifficulty(difficulty)),
                    selected: selectedDifficulty == difficulty,
                    onSelected: (selected) {
                      if (onDifficultySelected != null) {
                        onDifficultySelected!(selected ? difficulty : null);
                      }
                    },
                    selectedColor: colorScheme.secondaryContainer,
                    labelStyle: TextStyle(
                      color: selectedDifficulty == difficulty
                          ? colorScheme.onSecondaryContainer
                          : colorScheme.onSurfaceVariant,
                    ),
                  ),
              ],
            ),
          ),

          Center(
            child: Padding(
              padding: const EdgeInsets.only(top: AppDimens.paddingM),
              child: ElevatedButton.icon(
                onPressed: onFiltersApplied,
                icon: const Icon(Icons.check),
                label: Text(
                  'Apply Filters',
                  style: theme.textTheme.labelLarge?.copyWith(
                    color: colorScheme.onPrimary,
                  ),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: colorScheme.primary,
                  padding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.paddingL,
                    vertical: AppDimens.paddingM,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterSection(
    ThemeData theme,
    ColorScheme colorScheme,
    String title,
    IconData icon,
    Color iconColor,
    Widget content,
  ) {
    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.paddingM),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: iconColor, size: AppDimens.iconS),
              const SizedBox(width: AppDimens.spaceXS),
              Text(
                title,
                style: theme.textTheme.titleSmall?.copyWith(
                  color: iconColor,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: AppDimens.spaceXS),
          content,
        ],
      ),
    );
  }
}
