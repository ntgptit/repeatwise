// lib/presentation/widgets/grammars/grammar_stats_card.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';

class GrammarStatsCard extends StatefulWidget {
  final int totalCount;
  final String? moduleTitle;
  final VoidCallback? onRefresh;
  final bool isRefreshing;
  final bool showAnimation;

  const GrammarStatsCard({
    super.key,
    required this.totalCount,
    this.moduleTitle,
    this.onRefresh,
    this.isRefreshing = false,
    this.showAnimation = true,
  });

  @override
  State<GrammarStatsCard> createState() => _GrammarStatsCardState();
}

class _GrammarStatsCardState extends State<GrammarStatsCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    if (widget.showAnimation) {
      _initializeAnimations();
      _startAnimation();
    }
  }

  @override
  void dispose() {
    if (widget.showAnimation) {
      _animationController.dispose();
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: const Interval(0.0, 0.6, curve: Curves.easeOut),
      ),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0.0, -0.2), end: Offset.zero).animate(
          CurvedAnimation(
            parent: _animationController,
            curve: const Interval(0.2, 0.8, curve: Curves.easeOutCubic),
          ),
        );

    _scaleAnimation = Tween<double>(begin: 0.95, end: 1.0).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: const Interval(0.4, 1.0, curve: Curves.easeOutBack),
      ),
    );
  }

  void _startAnimation() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        _animationController.forward();
      }
    });
  }

  Widget _buildHeaderIcon(ColorScheme colorScheme) {
    return Container(
      width: AppDimens.avatarSizeXL,
      height: AppDimens.avatarSizeXL,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [colorScheme.primary, colorScheme.secondary],
        ),
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        boxShadow: [
          BoxShadow(
            color: colorScheme.primary.withValues(alpha: 0.25),
            blurRadius: AppDimens.shadowRadiusL,
            offset: const Offset(0, AppDimens.shadowOffsetM),
          ),
        ],
      ),
      child: Icon(
        Icons.auto_stories_rounded,
        color: colorScheme.onPrimary,
        size: AppDimens.iconXL,
      ),
    );
  }

  Widget _buildHeaderContent(ThemeData theme, ColorScheme colorScheme) {
    return Expanded(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Grammar Rules',
            style: theme.textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: colorScheme.onSurface,
              letterSpacing: -0.3,
            ),
          ),
          if (widget.moduleTitle != null) ...[
            const SizedBox(height: AppDimens.spaceXS),
            Text(
              widget.moduleTitle!,
              style: theme.textTheme.bodyLarge?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          ],
          const SizedBox(height: AppDimens.spaceS),
          _buildCountBadge(theme, colorScheme),
        ],
      ),
    );
  }

  Widget _buildCountBadge(ThemeData theme, ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
        vertical: AppDimens.paddingS,
      ),
      decoration: BoxDecoration(
        color: colorScheme.primaryContainer,
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        border: Border.all(color: colorScheme.primary.withValues(alpha: 0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            Icons.rule_rounded,
            size: AppDimens.iconS,
            color: colorScheme.onPrimaryContainer,
          ),
          const SizedBox(width: AppDimens.spaceXS),
          Text(
            '$_displayCount Grammar Rule${widget.totalCount == 1 ? '' : 's'}',
            style: theme.textTheme.labelMedium?.copyWith(
              color: colorScheme.onPrimaryContainer,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActionButton(ColorScheme colorScheme) {
    if (widget.onRefresh == null) return const SizedBox.shrink();

    return AnimatedSwitcher(
      duration: const Duration(milliseconds: 300),
      child: widget.isRefreshing
          ? Container(
              key: const ValueKey('loading'),
              width: AppDimens.iconL + AppDimens.paddingM * 2,
              height: AppDimens.iconL + AppDimens.paddingM * 2,
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer.withValues(alpha: 0.5),
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
              child: Center(
                child: SizedBox(
                  width: AppDimens.iconM,
                  height: AppDimens.iconM,
                  child: CircularProgressIndicator(
                    strokeWidth: 2.0,
                    valueColor: AlwaysStoppedAnimation<Color>(
                      colorScheme.primary,
                    ),
                  ),
                ),
              ),
            )
          : Container(
              key: const ValueKey('refresh'),
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer.withValues(alpha: 0.8),
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                border: Border.all(
                  color: colorScheme.primary.withValues(alpha: 0.3),
                ),
              ),
              child: IconButton(
                onPressed: widget.onRefresh,
                icon: Icon(
                  Icons.refresh_rounded,
                  color: colorScheme.primary,
                  size: AppDimens.iconL,
                ),
                tooltip: 'Refresh grammar list',
              ),
            ),
    );
  }

  Widget _buildStatsGrid(ThemeData theme, ColorScheme colorScheme) {
    final estimatedReadingTime = _calculateEstimatedTime();
    final difficultyLevel = _getDifficultyLevel();

    return Row(
      children: [
        Expanded(
          child: _buildStatItem(
            'Total Rules',
            _displayCount.toString(),
            Icons.format_list_numbered_rounded,
            colorScheme.primary,
            theme,
          ),
        ),
        const SizedBox(width: AppDimens.spaceL),
        Expanded(
          child: _buildStatItem(
            'Est. Study Time',
            estimatedReadingTime,
            Icons.schedule_rounded,
            colorScheme.secondary,
            theme,
          ),
        ),
        const SizedBox(width: AppDimens.spaceL),
        Expanded(
          child: _buildStatItem(
            'Difficulty',
            difficultyLevel,
            Icons.trending_up_rounded,
            colorScheme.tertiary,
            theme,
          ),
        ),
      ],
    );
  }

  Widget _buildStatItem(
    String label,
    String value,
    IconData icon,
    Color color,
    ThemeData theme,
  ) {
    return Container(
      padding: const EdgeInsets.all(AppDimens.paddingM),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.08),
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        border: Border.all(color: color.withValues(alpha: 0.2)),
      ),
      child: Column(
        children: [
          Icon(icon, color: color, size: AppDimens.iconM),
          const SizedBox(height: AppDimens.spaceS),
          Text(
            value,
            style: theme.textTheme.titleMedium?.copyWith(
              color: color,
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
          ),
          Text(
            label,
            style: theme.textTheme.labelSmall?.copyWith(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
              fontWeight: FontWeight.w500,
            ),
            textAlign: TextAlign.center,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }

  Widget _buildContent(ThemeData theme, ColorScheme colorScheme) {
    return SLCard(
      type: SLCardType.filled,
      padding: const EdgeInsets.all(AppDimens.paddingL),
      backgroundColor: colorScheme.surfaceContainer,
      elevation: AppDimens.elevationS,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        side: BorderSide(
          color: colorScheme.outlineVariant.withValues(alpha: 0.5),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header with icon, title, and action
          Row(
            children: [
              _buildHeaderIcon(colorScheme),
              const SizedBox(width: AppDimens.spaceL),
              _buildHeaderContent(theme, colorScheme),
              const SizedBox(width: AppDimens.spaceM),
              _buildActionButton(colorScheme),
            ],
          ),

          const SizedBox(height: AppDimens.spaceXL),

          // Stats grid
          _buildStatsGrid(theme, colorScheme),
        ],
      ),
    );
  }

  int get _displayCount => widget.totalCount;

  String _calculateEstimatedTime() {
    if (widget.totalCount == 0) return '0 min';

    // Estimate 2-3 minutes per grammar rule for study
    final minutes = (widget.totalCount * 2.5).ceil();

    if (minutes < 60) return '${minutes}m';

    final hours = minutes ~/ 60;
    final remainingMinutes = minutes % 60;

    if (remainingMinutes == 0) return '${hours}h';

    return '${hours}h ${remainingMinutes}m';
  }

  String _getDifficultyLevel() {
    if (widget.totalCount == 0) return 'None';
    if (widget.totalCount <= 3) return 'Basic';
    if (widget.totalCount <= 8) return 'Medium';
    if (widget.totalCount <= 15) return 'Advanced';
    return 'Expert';
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (!widget.showAnimation) {
      return Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: _buildContent(theme, colorScheme),
      );
    }

    return AnimatedBuilder(
      animation: _animationController,
      builder: (context, child) {
        return SlideTransition(
          position: _slideAnimation,
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Transform.scale(
              scale: _scaleAnimation.value,
              child: Padding(
                padding: const EdgeInsets.all(AppDimens.paddingL),
                child: _buildContent(theme, colorScheme),
              ),
            ),
          ),
        );
      },
    );
  }
}

// Extension for stats card variations
extension GrammarStatsCardVariations on GrammarStatsCard {
  /// Creates a compact version for smaller screens
  static Widget compact({
    required int totalCount,
    String? moduleTitle,
    VoidCallback? onRefresh,
    bool isRefreshing = false,
  }) {
    return GrammarStatsCard(
      totalCount: totalCount,
      moduleTitle: moduleTitle,
      onRefresh: onRefresh,
      isRefreshing: isRefreshing,
      showAnimation: false,
    );
  }

  /// Creates an animated version with entrance effects
  static Widget animated({
    required int totalCount,
    String? moduleTitle,
    VoidCallback? onRefresh,
    bool isRefreshing = false,
  }) {
    return GrammarStatsCard(
      totalCount: totalCount,
      moduleTitle: moduleTitle,
      onRefresh: onRefresh,
      isRefreshing: isRefreshing,
      showAnimation: true,
    );
  }
}
