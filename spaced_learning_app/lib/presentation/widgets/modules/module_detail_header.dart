// lib/presentation/widgets/modules/detail/module_detail_header.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/module.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';

class ModuleDetailHeader extends StatefulWidget {
  final ModuleDetail module;
  final bool showAnimation;
  final VoidCallback? onRefresh;
  final bool isRefreshing;

  const ModuleDetailHeader({
    super.key,
    required this.module,
    this.showAnimation = true,
    this.onRefresh,
    this.isRefreshing = false,
  });

  @override
  State<ModuleDetailHeader> createState() => _ModuleDetailHeaderState();
}

class _ModuleDetailHeaderState extends State<ModuleDetailHeader>
    with TickerProviderStateMixin {
  late AnimationController _slideController;
  late AnimationController _fadeController;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    if (widget.showAnimation) {
      _initializeAnimations();
      _startAnimations();
    }
  }

  @override
  void dispose() {
    if (widget.showAnimation) {
      _slideController.dispose();
      _fadeController.dispose();
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _slideController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0.0, -0.3), end: Offset.zero).animate(
          CurvedAnimation(parent: _slideController, curve: Curves.easeOutCubic),
        );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _fadeController, curve: Curves.easeOut));

    _scaleAnimation = Tween<double>(begin: 0.9, end: 1.0).animate(
      CurvedAnimation(parent: _slideController, curve: Curves.easeOutBack),
    );
  }

  void _startAnimations() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        _fadeController.forward();
        Future.delayed(const Duration(milliseconds: 100), () {
          if (mounted) {
            _slideController.forward();
          }
        });
      }
    });
  }

  Widget _buildModuleIcon(ColorScheme colorScheme) {
    return Container(
      width: AppDimens.avatarSizeXXL,
      height: AppDimens.avatarSizeXXL,
      decoration: BoxDecoration(
        gradient: RadialGradient(
          colors: [
            colorScheme.primary,
            colorScheme.primaryContainer,
            colorScheme.secondary,
          ],
          stops: const [0.0, 0.6, 1.0],
        ),
        borderRadius: BorderRadius.circular(AppDimens.radiusXL),
        boxShadow: [
          BoxShadow(
            color: colorScheme.primary.withValues(alpha: 0.3),
            blurRadius: AppDimens.shadowRadiusL,
            offset: const Offset(0, AppDimens.shadowOffsetM),
          ),
        ],
      ),
      child: Stack(
        alignment: Alignment.center,
        children: [
          Icon(
            Icons.library_books_rounded,
            color: colorScheme.onPrimary,
            size: AppDimens.iconXXL,
          ),
          Positioned(
            bottom: 4,
            right: 4,
            child: Container(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.paddingXS,
                vertical: AppDimens.paddingXXS,
              ),
              decoration: BoxDecoration(
                color: colorScheme.surface,
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                border: Border.all(
                  color: colorScheme.primary.withValues(alpha: 0.3),
                ),
              ),
              child: Text(
                '#${widget.module.moduleNo}',
                style: Theme.of(context).textTheme.labelSmall?.copyWith(
                  color: colorScheme.primary,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildModuleTitle(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.module.title,
          style: theme.textTheme.headlineMedium?.copyWith(
            fontWeight: FontWeight.bold,
            color: colorScheme.onSurface,
            letterSpacing: -0.5,
            height: 1.1,
          ),
          maxLines: 3,
          overflow: TextOverflow.ellipsis,
        ),
        if (widget.module.bookName != null) ...[
          const SizedBox(height: AppDimens.spaceS),
          _buildBookInfo(theme, colorScheme),
        ],
        const SizedBox(height: AppDimens.spaceS),
        _buildModuleStats(theme, colorScheme),
      ],
    );
  }

  Widget _buildBookInfo(ThemeData theme, ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
        vertical: AppDimens.paddingXS,
      ),
      decoration: BoxDecoration(
        color: colorScheme.secondaryContainer.withValues(alpha: 0.8),
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        border: Border.all(color: colorScheme.secondary.withValues(alpha: 0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            Icons.menu_book_rounded,
            size: AppDimens.iconS,
            color: colorScheme.onSecondaryContainer,
          ),
          const SizedBox(width: AppDimens.spaceXS),
          Flexible(
            child: Text(
              widget.module.bookName!,
              style: theme.textTheme.labelMedium?.copyWith(
                color: colorScheme.onSecondaryContainer,
                fontWeight: FontWeight.w600,
              ),
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildModuleStats(ThemeData theme, ColorScheme colorScheme) {
    final stats = [
      if (widget.module.wordCount != null && widget.module.wordCount! > 0)
        _StatItem(
          icon: Icons.text_fields_rounded,
          label: '${widget.module.wordCount} words',
          color: colorScheme.tertiary,
        ),
      _StatItem(
        icon: Icons.schedule_rounded,
        label: 'Module ${widget.module.moduleNo}',
        color: colorScheme.primary,
      ),
      if (widget.module.createdAt != null)
        _StatItem(
          icon: Icons.calendar_today_rounded,
          label: _formatDate(widget.module.createdAt!),
          color: colorScheme.secondary,
        ),
    ];

    if (stats.isEmpty) return const SizedBox.shrink();

    return Wrap(
      spacing: AppDimens.spaceM,
      runSpacing: AppDimens.spaceS,
      children: stats
          .map((stat) => _buildStatChip(stat, theme, colorScheme))
          .toList(),
    );
  }

  Widget _buildStatChip(
    _StatItem stat,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingS,
        vertical: AppDimens.paddingXS,
      ),
      decoration: BoxDecoration(
        color: stat.color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        border: Border.all(color: stat.color.withValues(alpha: 0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(stat.icon, size: AppDimens.iconXS, color: stat.color),
          const SizedBox(width: AppDimens.spaceXS),
          Text(
            stat.label,
            style: theme.textTheme.labelSmall?.copyWith(
              color: stat.color,
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
          ? SizedBox(
              width: AppDimens.iconL,
              height: AppDimens.iconL,
              child: CircularProgressIndicator(
                strokeWidth: 2.0,
                valueColor: AlwaysStoppedAnimation<Color>(colorScheme.primary),
              ),
            )
          : Container(
              decoration: BoxDecoration(
                color: colorScheme.primaryContainer.withValues(alpha: 0.8),
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                border: Border.all(
                  color: colorScheme.primary.withValues(alpha: 0.3),
                ),
              ),
              child: IconButton(
                key: const ValueKey('refresh-button'),
                onPressed: widget.onRefresh,
                icon: Icon(
                  Icons.refresh_rounded,
                  color: colorScheme.primary,
                  size: AppDimens.iconL,
                ),
                tooltip: 'Refresh module data',
              ),
            ),
    );
  }

  Widget _buildInfoCard(ThemeData theme, ColorScheme colorScheme) {
    return SLCard(
      type: SLCardType.filled,
      padding: const EdgeInsets.all(AppDimens.paddingL),
      backgroundColor: colorScheme.primaryContainer.withValues(alpha: 0.6),
      elevation: AppDimens.elevationNone,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        side: BorderSide(color: colorScheme.primary.withValues(alpha: 0.2)),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(AppDimens.paddingS),
            decoration: BoxDecoration(
              color: colorScheme.primary.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: Icon(
              Icons.lightbulb_outline_rounded,
              color: colorScheme.onPrimaryContainer,
              size: AppDimens.iconM,
            ),
          ),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Learning Module',
                  style: theme.textTheme.titleSmall?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onPrimaryContainer,
                  ),
                ),
                const SizedBox(height: AppDimens.spaceXS),
                Text(
                  'This module contains learning materials and exercises to help you master new concepts. Track your progress and practice regularly.',
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onPrimaryContainer,
                    height: 1.5,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildContent(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildModuleIcon(colorScheme),
            const SizedBox(width: AppDimens.spaceL),
            Expanded(child: _buildModuleTitle(theme, colorScheme)),
            const SizedBox(width: AppDimens.spaceM),
            _buildActionButton(colorScheme),
          ],
        ),
        const SizedBox(height: AppDimens.spaceXL),
        _buildInfoCard(theme, colorScheme),
      ],
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date).inDays;

    if (difference == 0) return 'Today';
    if (difference == 1) return 'Yesterday';
    if (difference < 7) return '${difference}d ago';
    if (difference < 30) return '${(difference / 7).floor()}w ago';
    if (difference < 365) return '${(difference / 30).floor()}mo ago';
    return '${(difference / 365).floor()}y ago';
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (!widget.showAnimation) {
      return _buildContent(theme, colorScheme);
    }

    return AnimatedBuilder(
      animation: Listenable.merge([_slideController, _fadeController]),
      builder: (context, child) {
        return SlideTransition(
          position: _slideAnimation,
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Transform.scale(
              scale: _scaleAnimation.value,
              child: _buildContent(theme, colorScheme),
            ),
          ),
        );
      },
    );
  }
}

// Helper class for stats
class _StatItem {
  final IconData icon;
  final String label;
  final Color color;

  const _StatItem({
    required this.icon,
    required this.label,
    required this.color,
  });
}

// Extension for header variations
extension ModuleDetailHeaderVariations on ModuleDetailHeader {
  /// Creates a static version without animations
  static Widget static(
    ModuleDetail module, {
    VoidCallback? onRefresh,
    bool isRefreshing = false,
  }) {
    return ModuleDetailHeader(
      module: module,
      showAnimation: false,
      onRefresh: onRefresh,
      isRefreshing: isRefreshing,
    );
  }

  /// Creates an animated version
  static Widget animated(
    ModuleDetail module, {
    VoidCallback? onRefresh,
    bool isRefreshing = false,
  }) {
    return ModuleDetailHeader(
      module: module,
      showAnimation: true,
      onRefresh: onRefresh,
      isRefreshing: isRefreshing,
    );
  }
}
