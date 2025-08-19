// lib/presentation/widgets/grammars/grammar_list_view.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/presentation/widgets/grammars/modern_grammar_list_item.dart';

typedef GrammarItemTapCallback = void Function(String grammarId);

class GrammarListView extends StatefulWidget {
  final List<GrammarSummary> grammars;
  final GrammarItemTapCallback onGrammarTap;
  final bool isSearchMode;
  final String searchQuery;
  final bool showAnimation;

  const GrammarListView({
    super.key,
    required this.grammars,
    required this.onGrammarTap,
    this.isSearchMode = false,
    this.searchQuery = '',
    this.showAnimation = true,
  });

  @override
  State<GrammarListView> createState() => _GrammarListViewState();
}

class _GrammarListViewState extends State<GrammarListView>
    with TickerProviderStateMixin {
  late AnimationController _staggerController;
  late List<Animation<double>> _itemAnimations;

  @override
  void initState() {
    super.initState();
    if (widget.showAnimation) {
      _initializeStaggerAnimation();
    }
  }

  @override
  void didUpdateWidget(GrammarListView oldWidget) {
    super.didUpdateWidget(oldWidget);

    // Restart animation when grammars list changes (e.g., from search)
    if (oldWidget.grammars.length != widget.grammars.length &&
        widget.showAnimation) {
      _restartStaggerAnimation();
    }
  }

  @override
  void dispose() {
    if (widget.showAnimation) {
      _staggerController.dispose();
    }
    super.dispose();
  }

  void _initializeStaggerAnimation() {
    _staggerController = AnimationController(
      vsync: this,
      duration: Duration(milliseconds: 300 + (widget.grammars.length * 50)),
    );

    _createItemAnimations();
    _staggerController.forward();
  }

  void _restartStaggerAnimation() {
    if (!mounted) return;

    _staggerController.reset();
    _createItemAnimations();
    _staggerController.forward();
  }

  void _createItemAnimations() {
    _itemAnimations = List.generate(widget.grammars.length, (index) {
      final startTime = index * 0.1;
      final endTime = startTime + 0.3;

      return Tween<double>(begin: 0.0, end: 1.0).animate(
        CurvedAnimation(
          parent: _staggerController,
          curve: Interval(
            startTime.clamp(0.0, 1.0),
            endTime.clamp(0.0, 1.0),
            curve: Curves.easeOutCubic,
          ),
        ),
      );
    });
  }

  Widget _buildGrammarItem(GrammarSummary grammar, int index) {
    final item = ModernGrammarListItem(
      grammar: grammar,
      onTap: () => widget.onGrammarTap(grammar.id),
      showModuleName: false, // Since we're already in module context
    );

    if (!widget.showAnimation) {
      return item;
    }

    return AnimatedBuilder(
      animation: _itemAnimations[index],
      builder: (context, child) {
        return FadeTransition(
          opacity: _itemAnimations[index],
          child: SlideTransition(
            position: Tween<Offset>(
              begin: const Offset(0.3, 0.0),
              end: Offset.zero,
            ).animate(_itemAnimations[index]),
            child: item,
          ),
        );
      },
    );
  }

  Widget _buildSectionHeader() {
    if (!widget.isSearchMode) return const SizedBox.shrink();

    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Padding(
      padding: const EdgeInsets.fromLTRB(
        AppDimens.paddingL,
        AppDimens.paddingL,
        AppDimens.paddingL,
        AppDimens.paddingM,
      ),
      child: Row(
        children: [
          Icon(
            Icons.search_rounded,
            color: colorScheme.primary,
            size: AppDimens.iconM,
          ),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Text(
              'Found ${widget.grammars.length} result${widget.grammars.length == 1 ? '' : 's'} for "${widget.searchQuery}"',
              style: theme.textTheme.titleMedium?.copyWith(
                color: colorScheme.onSurface,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildListContent() {
    return SliverPadding(
      padding: const EdgeInsets.fromLTRB(
        AppDimens.paddingL,
        0,
        AppDimens.paddingL,
        AppDimens.paddingXXL,
      ),
      sliver: SliverList.separated(
        itemCount: widget.grammars.length,
        separatorBuilder: (context, index) =>
            const SizedBox(height: AppDimens.spaceM),
        itemBuilder: (context, index) {
          final grammar = widget.grammars[index];
          return _buildGrammarItem(grammar, index);
        },
      ),
    );
  }

  Widget _buildPerformanceIndicator() {
    if (widget.grammars.length < 20) return const SizedBox.shrink();

    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.all(AppDimens.paddingL),
        padding: const EdgeInsets.all(AppDimens.paddingM),
        decoration: BoxDecoration(
          color: colorScheme.surfaceContainerHighest,
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
          border: Border.all(
            color: colorScheme.outlineVariant.withValues(alpha: 0.5),
          ),
        ),
        child: Row(
          children: [
            Icon(
              Icons.info_outline_rounded,
              color: colorScheme.primary,
              size: AppDimens.iconS,
            ),
            const SizedBox(width: AppDimens.spaceS),
            Expanded(
              child: Text(
                'Showing ${widget.grammars.length} grammar rules',
                style: theme.textTheme.bodySmall?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return SliverMainAxisGroup(
      slivers: [
        // Section header for search results
        if (widget.isSearchMode)
          SliverToBoxAdapter(child: _buildSectionHeader()),

        // Performance indicator for large lists
        _buildPerformanceIndicator(),

        // Grammar list
        _buildListContent(),
      ],
    );
  }
}

// Extension for list variations
extension GrammarListViewVariations on GrammarListView {
  /// Creates a static version without animations for better performance
  static Widget static({
    required List<GrammarSummary> grammars,
    required GrammarItemTapCallback onGrammarTap,
    bool isSearchMode = false,
    String searchQuery = '',
  }) {
    return GrammarListView(
      grammars: grammars,
      onGrammarTap: onGrammarTap,
      isSearchMode: isSearchMode,
      searchQuery: searchQuery,
      showAnimation: false,
    );
  }

  /// Creates an animated version with stagger effect
  static Widget animated({
    required List<GrammarSummary> grammars,
    required GrammarItemTapCallback onGrammarTap,
    bool isSearchMode = false,
    String searchQuery = '',
  }) {
    return GrammarListView(
      grammars: grammars,
      onGrammarTap: onGrammarTap,
      isSearchMode: isSearchMode,
      searchQuery: searchQuery,
      showAnimation: true,
    );
  }
}
