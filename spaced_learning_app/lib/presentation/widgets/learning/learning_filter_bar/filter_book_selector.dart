// lib/presentation/widgets/learning/learning_filter_bar/filter_book_selector.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class FilterBookSelector extends ConsumerWidget {
  final String selectedBook;
  final List<String> books;
  final Function(String?) onBookChanged;

  const FilterBookSelector({
    super.key,
    required this.selectedBook,
    required this.books,
    required this.onBookChanged,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final hasBookFilter = selectedBook != 'All';

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Select Book',
          style: theme.textTheme.labelMedium?.copyWith(
            color: colorScheme.onSurface.withValues(
              alpha: AppDimens.opacityHigh,
            ),
          ),
        ),
        const SizedBox(height: AppDimens.spaceXS),
        SizedBox(
          height: 48,
          child: Container(
            decoration: BoxDecoration(
              border: Border.all(
                color: hasBookFilter
                    ? colorScheme.primary
                    : colorScheme.outline.withValues(
                        alpha: AppDimens.opacitySemi,
                      ),
                width: hasBookFilter ? 2 : 1,
              ),
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
            child: DropdownButtonHideUnderline(
              child: DropdownButton<String>(
                value: selectedBook,
                isExpanded: true,
                icon: Icon(
                  Icons.arrow_drop_down,
                  color: hasBookFilter ? colorScheme.primary : null,
                  size: AppDimens.iconL,
                ),
                padding: const EdgeInsets.symmetric(
                  horizontal: AppDimens.paddingL,
                  vertical: AppDimens.paddingS,
                ),
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                dropdownColor: colorScheme.surface,
                items: books.map((book) {
                  final isSelected = book == selectedBook;
                  final isAll = book == 'All';

                  return DropdownMenuItem(
                    value: book,
                    child: Text(
                      book,
                      style: TextStyle(
                        color: isSelected && !isAll
                            ? colorScheme.primary
                            : colorScheme.onSurface,
                        fontWeight: isSelected && !isAll
                            ? FontWeight.bold
                            : null,
                      ),
                    ),
                  );
                }).toList(),
                onChanged: onBookChanged,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
