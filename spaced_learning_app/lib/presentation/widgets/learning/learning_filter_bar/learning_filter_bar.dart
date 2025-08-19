// lib/presentation/widgets/learning/learning_filter_bar/learning_filter_bar.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/viewmodels/learning_progress_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/learning/learning_filter_bar/filter_book_selector.dart';
import 'package:spaced_learning_app/presentation/widgets/learning/learning_filter_bar/filter_date_selector.dart';
import 'package:spaced_learning_app/presentation/widgets/learning/learning_filter_bar/filter_stats_row.dart';

class LearningFilterBar extends ConsumerWidget {
  final int totalCount;
  final int dueCount;
  final int completeCount;

  const LearningFilterBar({
    super.key,
    required this.totalCount,
    required this.dueCount,
    required this.completeCount,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final selectedBook = ref.watch(selectedBookFilterProvider);
    final selectedDate = ref.watch(selectedDateFilterProvider);
    final books = ref.watch(
      learningProgressStateProvider.select(
        (value) => value.valueOrNull != null
            ? ref.read(learningProgressStateProvider.notifier).getUniqueBooks()
            : ['All'],
      ),
    );

    return _FilterBarContent(
      selectedBook: selectedBook,
      selectedDate: selectedDate,
      books: books,
      onBookChanged: (book) => ref
          .read(selectedBookFilterProvider.notifier)
          .setSelectedBook(book ?? 'All'),
      onDateSelected: () => _selectDate(context, ref),
      onDateCleared: () =>
          ref.read(selectedDateFilterProvider.notifier).clearDateFilter(),
      totalCount: totalCount,
      dueCount: dueCount,
      completeCount: completeCount,
      hasActiveFilters: selectedBook != 'All' || selectedDate != null,
    );
  }

  Future<void> _selectDate(BuildContext context, WidgetRef ref) async {
    final selectedDate = ref.read(selectedDateFilterProvider);

    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate ?? DateTime.now(),
      firstDate: DateTime(2020),
      lastDate: DateTime(2030),
      builder: (BuildContext context, Widget? child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: Theme.of(context).colorScheme,
            dialogTheme: DialogThemeData(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(AppDimens.radiusL),
              ),
            ),
          ),
          child: child!,
        );
      },
    );

    if (picked != null) {
      ref.read(selectedDateFilterProvider.notifier).setSelectedDate(picked);
    }
  }
}

class _FilterBarContent extends StatefulWidget {
  final String selectedBook;
  final DateTime? selectedDate;
  final List<String> books;
  final Function(String?) onBookChanged;
  final Function() onDateSelected;
  final Function() onDateCleared;
  final int totalCount;
  final int dueCount;
  final int completeCount;
  final bool hasActiveFilters;

  const _FilterBarContent({
    required this.selectedBook,
    required this.selectedDate,
    required this.books,
    required this.onBookChanged,
    required this.onDateSelected,
    required this.onDateCleared,
    required this.totalCount,
    required this.dueCount,
    required this.completeCount,
    required this.hasActiveFilters,
  });

  @override
  State<_FilterBarContent> createState() => _FilterBarContentState();
}

class _FilterBarContentState extends State<_FilterBarContent> {
  bool _isExpanded = false;

  @override
  void initState() {
    super.initState();
    _isExpanded = widget.hasActiveFilters;
  }

  @override
  void didUpdateWidget(_FilterBarContent oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.hasActiveFilters != oldWidget.hasActiveFilters) {
      setState(() {
        _isExpanded = widget.hasActiveFilters;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      elevation: AppDimens.elevationS,
      margin: const EdgeInsets.symmetric(
        vertical: AppDimens.paddingS,
        horizontal: AppDimens.paddingXS,
      ),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        side: BorderSide(
          color: colorScheme.outlineVariant.withValues(
            alpha: AppDimens.opacityMedium,
          ),
          width: 1.0,
        ),
      ),
      child: Column(
        children: [
          GestureDetector(
            onTap: () {
              setState(() {
                _isExpanded = !_isExpanded;
              });
            },
            child: FilterStatsRow(
              totalCount: widget.totalCount,
              dueCount: widget.dueCount,
              completeCount: widget.completeCount,
              activeFilterCount: _getActiveFilterCount(),
              showFilter: _isExpanded,
              onToggleFilter: () {
                setState(() {
                  _isExpanded = !_isExpanded;
                });
              },
            ),
          ),

          AnimatedContainer(
            duration: const Duration(milliseconds: AppDimens.durationM),
            height: _isExpanded ? null : 0,
            curve: Curves.easeInOut,
            child: ClipRect(
              child: Visibility(
                visible: _isExpanded,
                maintainState: true,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Divider(
                      height: AppDimens.dividerThickness,
                      thickness: AppDimens.dividerThickness,
                    ),
                    Padding(
                      padding: const EdgeInsets.all(AppDimens.paddingM),
                      child: Row(
                        children: [
                          Expanded(
                            child: FilterBookSelector(
                              selectedBook: widget.selectedBook,
                              books: widget.books,
                              onBookChanged: widget.onBookChanged,
                            ),
                          ),
                          const SizedBox(width: AppDimens.spaceM),
                          Expanded(
                            child: FilterDateSelector(
                              selectedDate: widget.selectedDate,
                              onDateSelected: widget.onDateSelected,
                              onDateCleared: widget.onDateCleared,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  int _getActiveFilterCount() {
    int count = 0;
    if (widget.selectedBook != 'All') count++;
    if (widget.selectedDate != null) count++;
    return count;
  }
}
