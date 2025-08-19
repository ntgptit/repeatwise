import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/book.dart';
import 'package:spaced_learning_app/presentation/utils/snackbar_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/book_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/books/book_filter_panel.dart';
import 'package:spaced_learning_app/presentation/widgets/books/book_list_card.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_empty_state.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

import '../../../core/navigation/navigation_helper.dart';

class BooksScreen extends ConsumerStatefulWidget {
  const BooksScreen({super.key});

  @override
  ConsumerState<BooksScreen> createState() => _BooksScreenState();
}

class _BooksScreenState extends ConsumerState<BooksScreen>
    with TickerProviderStateMixin {
  final TextEditingController _searchController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  bool _isSearching = false;
  String? _selectedCategory;
  List<String> _categories = [];
  BookStatus? _selectedStatus;
  DifficultyLevel? _selectedDifficulty;
  bool _isFilterExpanded = false;
  bool _isScrolled = false;

  late AnimationController _filterAnimationController;
  late Animation<double> _filterAnimation;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _loadData());

    _filterAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    _filterAnimation = CurvedAnimation(
      parent: _filterAnimationController,
      curve: Curves.easeInOut,
    );

    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    final isScrolled = _scrollController.offset > 0;
    if (isScrolled != _isScrolled) {
      setState(() {
        _isScrolled = isScrolled;
      });
    }
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.removeListener(_onScroll);
    _scrollController.dispose();
    _filterAnimationController.dispose();
    super.dispose();
  }

  Future<void> _loadData({bool forceRefresh = false}) async {
    final booksNotifier = ref.read(booksStateProvider.notifier);
    final categoriesNotifier = ref.read(categoriesProvider.notifier);

    try {
      if (_categories.isEmpty || forceRefresh) {
        await categoriesNotifier.reloadCategories();
        if (mounted) {
          setState(() {
            _categories = ref.read(categoriesProvider).valueOrNull ?? [];
          });
        }
      }
      await booksNotifier.loadBooks();
    } catch (e) {
      final errorMessage = e is AppException
          ? e.message
          : 'An unexpected error occurred. Please try again.';
      if (mounted) {
        SnackBarUtils.show(
          context,
          errorMessage,
          backgroundColor: Theme.of(context).colorScheme.error,
        );
      }
    }
  }

  void _applyFilters() {
    ref
        .read(booksStateProvider.notifier)
        .filterBooks(
          status: _selectedStatus,
          difficultyLevel: _selectedDifficulty,
          category: _selectedCategory,
        );
  }

  void _resetFilters() {
    setState(() {
      _selectedCategory = null;
      _selectedStatus = null;
      _selectedDifficulty = null;
    });
    ref.read(booksStateProvider.notifier).loadBooks();
    if (_isFilterExpanded) {
      _toggleFilterPanel();
    }
  }

  Future<void> _searchBooks(String query) async {
    final booksNotifier = ref.read(booksStateProvider.notifier);
    if (query.isEmpty) {
      setState(() {
        _isSearching = false;
      });
      return booksNotifier.filterBooks(
        status: _selectedStatus,
        difficultyLevel: _selectedDifficulty,
        category: _selectedCategory,
      );
    }
    setState(() {
      _isSearching = true;
    });
    await booksNotifier.searchBooks(query);
  }

  void _toggleFilterPanel() {
    setState(() {
      _isFilterExpanded = !_isFilterExpanded;
    });

    if (_isFilterExpanded) {
      _filterAnimationController.forward();
    } else {
      _filterAnimationController.reverse();
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final currentUser = ref.watch(currentUserProvider);

    if (currentUser == null) {
      return _buildLoginPrompt(theme);
    }

    return Scaffold(
      body: NestedScrollView(
        controller: _scrollController,
        headerSliverBuilder: (context, innerBoxIsScrolled) => [
          _buildAppBar(theme, colorScheme, innerBoxIsScrolled),
        ],
        body: Column(
          children: [
            _buildSearchAndFilterBar(theme, colorScheme),
            AnimatedBuilder(
              animation: _filterAnimation,
              builder: (context, child) {
                return SizeTransition(
                  sizeFactor: _filterAnimation,
                  axisAlignment: -1.0,
                  child: child,
                );
              },
              child: _isFilterExpanded
                  ? BookFilterPanel(
                      categories: _categories,
                      selectedCategory: _selectedCategory,
                      selectedStatus: _selectedStatus,
                      selectedDifficulty: _selectedDifficulty,
                      onCategorySelected: (category) {
                        setState(() => _selectedCategory = category);
                        _applyFilters();
                      },
                      onStatusSelected: (status) {
                        setState(() => _selectedStatus = status);
                        _applyFilters();
                      },
                      onDifficultySelected: (difficulty) {
                        setState(() => _selectedDifficulty = difficulty);
                        _applyFilters();
                      },
                      onFiltersApplied: () {
                        _applyFilters();
                        _toggleFilterPanel();
                      },
                      onFilterCleared: _resetFilters,
                    )
                  : const SizedBox.shrink(),
            ),
            Expanded(
              child: Consumer(
                builder: (context, ref, child) {
                  final booksState = ref.watch(booksStateProvider);

                  return booksState.when(
                    data: (books) => _buildBooksList(
                      books,
                      theme,
                      _isSearching ? 'Search results' : 'Books Library',
                    ),
                    error: (error, stackTrace) => Center(
                      child: SLErrorView(
                        message: error.toString(),
                        onRetry: () => _loadData(forceRefresh: true),
                      ),
                    ),
                    loading: () => const Center(child: SLLoadingIndicator()),
                  );
                },
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: currentUser.roles?.contains('ADMIN') == true
          ? FloatingActionButton(
              onPressed: () => GoRouter.of(context).push('/books/create'),
              tooltip: 'Add Book',
              child: const Icon(Icons.add),
            )
          : null,
    );
  }

  Widget _buildLoginPrompt(ThemeData theme) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Books'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => GoRouter.of(context).pop(),
        ),
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              Icons.lock_outline,
              size: AppDimens.iconXXL,
              color: theme.colorScheme.primary.withValues(alpha: 0.6),
            ),
            const SizedBox(height: AppDimens.spaceL),
            Text(
              'Please log in to browse books',
              style: theme.textTheme.titleLarge?.copyWith(
                color: theme.colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: AppDimens.spaceXL),
            ElevatedButton.icon(
              onPressed: () => GoRouter.of(context).go('/login'),
              icon: const Icon(Icons.login),
              label: const Text('Log In'),
              style: ElevatedButton.styleFrom(
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

  SliverAppBar _buildAppBar(
    ThemeData theme,
    ColorScheme colorScheme,
    bool innerBoxIsScrolled,
  ) {
    return SliverAppBar(
      title: const Text('Books'),
      floating: true,
      pinned: true,
      forceElevated: _isScrolled || innerBoxIsScrolled,
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
      actions: [
        IconButton(
          icon: const Icon(Icons.refresh_outlined),
          onPressed: () => _loadData(forceRefresh: true),
          tooltip: 'Refresh',
        ),
      ],
    );
  }

  Widget _buildSearchAndFilterBar(ThemeData theme, ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.fromLTRB(
        AppDimens.paddingL,
        AppDimens.paddingM,
        AppDimens.paddingL,
        AppDimens.paddingS,
      ),
      decoration: BoxDecoration(
        color: theme.colorScheme.surface,
        boxShadow: _isScrolled || _isFilterExpanded
            ? [
                BoxShadow(
                  color: theme.colorScheme.shadow.withValues(alpha: 0.05),
                  blurRadius: 4,
                  offset: const Offset(0, 2),
                ),
              ]
            : null,
      ),
      child: Row(
        children: [
          Expanded(
            child: SizedBox(
              height: AppDimens.textFieldHeight,
              child: TextField(
                controller: _searchController,
                decoration: InputDecoration(
                  hintText: 'Search books...',
                  prefixIcon: Icon(
                    Icons.search,
                    color: theme.colorScheme.onSurfaceVariant,
                  ),
                  suffixIcon: _searchController.text.isNotEmpty
                      ? IconButton(
                          icon: Icon(
                            Icons.clear,
                            color: theme.colorScheme.onSurfaceVariant,
                          ),
                          onPressed: () {
                            _searchController.clear();
                            _searchBooks('');
                          },
                          tooltip: 'Clear search',
                        )
                      : null,
                  filled: true,
                  fillColor: theme.colorScheme.surfaceContainerLowest,
                  contentPadding: const EdgeInsets.symmetric(
                    horizontal: AppDimens.paddingM,
                  ),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(AppDimens.radiusL),
                    borderSide: BorderSide.none,
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(AppDimens.radiusL),
                    borderSide: BorderSide(
                      color: theme.colorScheme.outlineVariant.withValues(
                        alpha: 0.5,
                      ),
                    ),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(AppDimens.radiusL),
                    borderSide: BorderSide(
                      color: theme.colorScheme.primary,
                      width: 1.5,
                    ),
                  ),
                ),
                onChanged: _searchBooks,
                onSubmitted: _searchBooks,
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(left: AppDimens.paddingM),
            child: Badge(
              label: Text(_getActiveFilterCount().toString()),
              isLabelVisible: _getActiveFilterCount() > 0,
              child: IconButton.filledTonal(
                icon: Icon(
                  _isFilterExpanded ? Icons.filter_list_off : Icons.filter_list,
                ),
                onPressed: _toggleFilterPanel,
                tooltip: _isFilterExpanded ? 'Hide filters' : 'Show filters',
              ),
            ),
          ),
        ],
      ),
    );
  }

  int _getActiveFilterCount() {
    int count = 0;
    if (_selectedCategory != null) count++;
    if (_selectedStatus != null) count++;
    if (_selectedDifficulty != null) count++;
    return count;
  }

  Widget _buildBooksList(
    List<BookSummary> books,
    ThemeData theme,
    String title,
  ) {
    if (books.isEmpty) {
      return _buildEmptyState(theme);
    }

    return RefreshIndicator(
      onRefresh: () => _loadData(forceRefresh: true),
      child: CustomScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        slivers: [
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(
              AppDimens.paddingL,
              AppDimens.paddingL,
              AppDimens.paddingL,
              AppDimens.paddingS,
            ),
            sliver: SliverToBoxAdapter(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    '$title (${books.length})',
                    style: theme.textTheme.titleLarge,
                  ),
                ],
              ),
            ),
          ),
          SliverPadding(
            padding: const EdgeInsets.fromLTRB(
              AppDimens.paddingL,
              0,
              AppDimens.paddingL,
              AppDimens.paddingXXXL,
            ),
            sliver: SliverList(
              delegate: SliverChildBuilderDelegate((context, index) {
                final book = books[index];
                return BookListCard(
                  book: book,
                  onTap: () {
                    if (book.id.isEmpty) {
                      SnackBarUtils.show(context, 'Invalid book ID');
                      return;
                    }

                    NavigationHelper.pushWithResult(
                      context,
                      '/books/${book.id}',
                    ).then((result) {
                      if (result == true) {
                        _loadData(forceRefresh: true);
                      }
                    });
                  },
                );
              }, childCount: books.length),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState(ThemeData theme) {
    return SLEmptyState(
      icon: _isSearching ? Icons.search_off : Icons.book_outlined,
      title: _isSearching ? 'No books found' : 'No books available',
      message: _isSearching
          ? 'Try adjusting your search or filters.'
          : 'Check back later or refresh.',
      buttonText: _isSearching ? 'Clear Search & Filters' : 'Refresh',
      onButtonPressed: _isSearching
          ? _resetFilters
          : () => _loadData(forceRefresh: true),
    );
  }
}
