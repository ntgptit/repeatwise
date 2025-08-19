// lib/presentation/screens/books/book_detail_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/book.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/book_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/module_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/books/book_cover.dart';
import 'package:spaced_learning_app/presentation/widgets/books/book_detail_tabs.dart';
import 'package:spaced_learning_app/presentation/widgets/books/info_chip.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

class BookDetailScreen extends ConsumerStatefulWidget {
  final String bookId;

  const BookDetailScreen({super.key, required this.bookId});

  @override
  ConsumerState<BookDetailScreen> createState() => _BookDetailScreenState();
}

class _BookDetailScreenState extends ConsumerState<BookDetailScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final ScrollController _scrollController = ScrollController();
  bool _isScrolled = false;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _scrollController.addListener(_onScroll);
    WidgetsBinding.instance.addPostFrameCallback((_) => _loadData());
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
    _tabController.dispose();
    _scrollController.removeListener(_onScroll);
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    if (!mounted) return;

    await ref
        .read(selectedBookProvider.notifier)
        .loadBookDetails(widget.bookId);
    await ref
        .read(modulesStateProvider.notifier)
        .loadModulesByBookId(widget.bookId);
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final bookState = ref.watch(selectedBookProvider);
    final moduleState = ref.watch(modulesStateProvider);
    final isAdmin = ref.watch(
      authStateProvider.select(
        (value) =>
            value.valueOrNull == true &&
            (ref.read(currentUserProvider)?.roles?.contains('ADMIN') ?? false),
      ),
    );

    return bookState.when(
      data: (book) {
        if (book == null) {
          return Scaffold(
            appBar: AppBar(title: const Text('Book Details')),
            body: const Center(child: Text('Book not found')),
          );
        }

        return Scaffold(
          body: NestedScrollView(
            controller: _scrollController,
            headerSliverBuilder: (context, innerBoxIsScrolled) => [
              _buildFlexibleAppBar(
                theme,
                colorScheme,
                book,
                isAdmin,
                innerBoxIsScrolled,
              ),
            ],
            body: Column(
              children: [
                TabBar(
                  controller: _tabController,
                  labelColor: colorScheme.primary,
                  unselectedLabelColor: colorScheme.onSurfaceVariant,
                  indicatorColor: colorScheme.primary,
                  indicatorWeight: 3,
                  indicatorSize: TabBarIndicatorSize.label,
                  tabs: const [
                    Tab(icon: Icon(Icons.info_outline), text: 'Overview'),
                    Tab(
                      icon: Icon(Icons.view_module_outlined),
                      text: 'Modules',
                    ),
                  ],
                ),
                Expanded(
                  child: TabBarView(
                    controller: _tabController,
                    children: [
                      BookOverviewTab(book: book),
                      moduleState.when(
                        data: (modules) => BookModulesTab(
                          modules: modules,
                          bookId: widget.bookId,
                          onRetry: () => ref
                              .read(modulesStateProvider.notifier)
                              .loadModulesByBookId(widget.bookId),
                        ),
                        loading: () =>
                            const Center(child: SLLoadingIndicator()),
                        error: (error, stack) => Center(
                          child: SLErrorView(
                            message: error.toString(),
                            onRetry: () => ref
                                .read(modulesStateProvider.notifier)
                                .loadModulesByBookId(widget.bookId),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
      loading: () => Scaffold(
        appBar: AppBar(title: const Text('Book Details')),
        body: const Center(child: SLLoadingIndicator()),
      ),
      error: (error, stack) => Scaffold(
        appBar: AppBar(title: const Text('Book Details')),
        body: Center(
          child: SLErrorView(message: error.toString(), onRetry: _loadData),
        ),
      ),
    );
  }

  SliverAppBar _buildFlexibleAppBar(
    ThemeData theme,
    ColorScheme colorScheme,
    BookDetail book,
    bool isAdmin,
    bool innerBoxIsScrolled,
  ) {
    final statusColor = _getStatusColor(book.status);
    final difficultyData = _getDifficultyData(theme, book.difficultyLevel);

    return SliverAppBar(
      expandedHeight: AppDimens.bannerHeight,
      floating: false,
      pinned: true,
      forceElevated: _isScrolled || innerBoxIsScrolled,
      backgroundColor: _isScrolled
          ? theme.appBarTheme.backgroundColor
          : colorScheme.surfaceContainerHigh,
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () {
          final router = GoRouter.of(context);
          if (router.canPop()) {
            router.pop(true); // Trả về true để indicate cần refresh
          } else {
            router.go('/books');
          }
        },
        color: _isScrolled ? null : colorScheme.onSurfaceVariant,
      ),
      flexibleSpace: FlexibleSpaceBar(
        titlePadding: const EdgeInsets.all(0),
        expandedTitleScale: 1.0,
        title: _isScrolled
            ? Padding(
                padding: const EdgeInsets.only(left: 56.0, right: 56.0),
                child: Text(
                  book.name,
                  style: theme.textTheme.titleMedium,
                  overflow: TextOverflow.ellipsis,
                ),
              )
            : const SizedBox.shrink(),
        background: Padding(
          padding: const EdgeInsets.fromLTRB(
            AppDimens.paddingL,
            AppDimens.paddingL + kToolbarHeight,
            AppDimens.paddingL,
            AppDimens.paddingL,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Hero(
                    tag: 'book-${book.id}',
                    child: BookCover(book: book.toSummary()),
                  ),
                  const SizedBox(width: AppDimens.spaceL),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          book.name,
                          style: theme.textTheme.headlineSmall,
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: AppDimens.spaceS),
                        Row(
                          children: [
                            InfoChipWidget(
                              label: _formatStatus(book.status),
                              backgroundColor: statusColor.withValues(
                                alpha: 0.2,
                              ),
                              textColor: statusColor,
                            ),
                            if (book.difficultyLevel != null) ...[
                              const SizedBox(width: AppDimens.spaceXS),
                              InfoChipWidget(
                                label: difficultyData.text,
                                backgroundColor: difficultyData.color
                                    .withValues(alpha: 0.2),
                                textColor: difficultyData.color,
                              ),
                            ],
                          ],
                        ),
                        if (book.category != null) ...[
                          const SizedBox(height: AppDimens.spaceXS),
                          InfoChipWidget(
                            label: book.category!,
                            backgroundColor: colorScheme.tertiaryContainer,
                            textColor: colorScheme.onTertiaryContainer,
                            icon: Icons.category_outlined,
                          ),
                        ],
                        const SizedBox(height: AppDimens.spaceS),
                        Row(
                          children: [
                            Icon(
                              Icons.bookmark_border,
                              size: AppDimens.iconS,
                              color: colorScheme.onSurfaceVariant,
                            ),
                            const SizedBox(width: AppDimens.spaceXXS),
                            Text(
                              '${book.modules.length} ${book.modules.length == 1 ? 'module' : 'modules'}',
                              style: theme.textTheme.labelMedium?.copyWith(
                                color: colorScheme.onSurfaceVariant,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getStatusColor(BookStatus status) {
    switch (status) {
      case BookStatus.published:
        return Colors.green;
      case BookStatus.draft:
        return Colors.orange;
      case BookStatus.archived:
        return Colors.grey;
    }
  }

  ({Color color, String text}) _getDifficultyData(
    ThemeData theme,
    DifficultyLevel? level,
  ) {
    if (level == null) return (color: Colors.grey, text: 'Unknown');

    switch (level) {
      case DifficultyLevel.beginner:
        return (color: Colors.green, text: 'Beginner');
      case DifficultyLevel.intermediate:
        return (color: theme.colorScheme.tertiary, text: 'Intermediate');
      case DifficultyLevel.advanced:
        return (color: theme.colorScheme.secondary, text: 'Advanced');
      case DifficultyLevel.expert:
        return (color: theme.colorScheme.error, text: 'Expert');
    }
  }

  String _formatStatus(BookStatus status) {
    switch (status) {
      case BookStatus.published:
        return 'Published';
      case BookStatus.draft:
        return 'Draft';
      case BookStatus.archived:
        return 'Archived';
    }
  }
}
