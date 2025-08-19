// lib/presentation/widgets/modules/detail/module_progress_card.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/progress.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_progress_indicator.dart';

class ModuleProgressCard extends StatefulWidget {
  final ProgressDetail progress;
  final String moduleTitle;
  final void Function(String) onTap;
  final bool showAnimation;
  final VoidCallback? onViewDetails;
  final VoidCallback? onStartStudy;
  final bool isCompact;
  final bool showActions;

  const ModuleProgressCard({
    super.key,
    required this.progress,
    required this.moduleTitle,
    required this.onTap,
    this.showAnimation = true,
    this.onViewDetails,
    this.onStartStudy,
    this.isCompact = false,
    this.showActions = true,
  });

  @override
  State<ModuleProgressCard> createState() => _ModuleProgressCardState();
}

class _ModuleProgressCardState extends State<ModuleProgressCard>
    with TickerProviderStateMixin {
  late AnimationController _slideController;
  late AnimationController _scaleController;
  late AnimationController _pulseController;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;
  late Animation<double> _pulseAnimation;

  bool _isPressed = false;

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
      _scaleController.dispose();
      _pulseController.dispose();
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _slideController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _scaleController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 150),
    );

    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 2000),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0.0, 0.3), end: Offset.zero).animate(
          CurvedAnimation(parent: _slideController, curve: Curves.easeOutCubic),
        );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _slideController, curve: Curves.easeOut));

    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.97).animate(
      CurvedAnimation(parent: _scaleController, curve: Curves.easeInOut),
    );

    _pulseAnimation = Tween<double>(begin: 1.0, end: 1.05).animate(
      CurvedAnimation(parent: _pulseController, curve: Curves.easeInOut),
    );
  }

  void _startAnimations() {
    Future.delayed(const Duration(milliseconds: 300), () {
      if (!mounted) return;
      _slideController.forward();
    });

    // Start pulsing for overdue items
    if (_isOverdue()) {
      Future.delayed(const Duration(milliseconds: 1200), () {
        if (!mounted) return;
        _pulseController.repeat(reverse: true);
      });
    }
  }

  void _handleTapDown() {
    if (!widget.showAnimation) return;
    setState(() => _isPressed = true);
    _scaleController.forward();
  }

  void _handleTapUp() {
    if (!widget.showAnimation) return;
    setState(() => _isPressed = false);
    _scaleController.reverse();
  }

  bool _isDue() {
    if (widget.progress.nextStudyDate == null) return false;

    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final nextDate = DateTime(
      widget.progress.nextStudyDate!.year,
      widget.progress.nextStudyDate!.month,
      widget.progress.nextStudyDate!.day,
    );

    return !nextDate.isAfter(today);
  }

  bool _isOverdue() {
    if (widget.progress.nextStudyDate == null) return false;

    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final nextDate = DateTime(
      widget.progress.nextStudyDate!.year,
      widget.progress.nextStudyDate!.month,
      widget.progress.nextStudyDate!.day,
    );

    return nextDate.isBefore(today);
  }

  bool _isCompleted() {
    return widget.progress.percentComplete >= 100;
  }

  String _getStatusText() {
    if (_isOverdue()) return 'Overdue';
    if (_isDue()) return 'Due Today';
    if (_isCompleted()) return 'Completed';

    final nextDate = widget.progress.nextStudyDate;
    if (nextDate == null) return 'Not Scheduled';

    final now = DateTime.now();
    final difference = nextDate.difference(now).inDays;

    if (difference == 0) return 'Due Today';
    if (difference == 1) return 'Due Tomorrow';
    if (difference > 1) return 'Due in $difference days';

    return 'In Progress';
  }

  Color _getStatusColor(ColorScheme colorScheme) {
    if (_isOverdue()) return colorScheme.error;
    if (_isDue()) return colorScheme.warning;
    if (_isCompleted()) return colorScheme.success;

    return colorScheme.primary;
  }

  IconData _getStatusIcon() {
    if (_isOverdue()) return Icons.warning_rounded;
    if (_isDue()) return Icons.today_rounded;
    if (_isCompleted()) return Icons.check_circle_rounded;

    return Icons.schedule_rounded;
  }

  String _getCycleText() {
    switch (widget.progress.cyclesStudied) {
      case CycleStudied.firstTime:
        return 'First Learning';
      case CycleStudied.firstReview:
        return '1st Review';
      case CycleStudied.secondReview:
        return '2nd Review';
      case CycleStudied.thirdReview:
        return '3rd Review';
      case CycleStudied.moreThanThreeReviews:
        return 'Advanced';
    }
  }

  Widget _buildProgressHeader(ThemeData theme, ColorScheme colorScheme) {
    if (widget.isCompact) {
      return _buildCompactHeader(theme, colorScheme);
    }

    return Row(
      children: [
        _buildProgressIcon(colorScheme),
        const SizedBox(width: AppDimens.spaceL),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Learning Progress',
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: colorScheme.onSurface,
                ),
              ),
              const SizedBox(height: AppDimens.spaceXS),
              Text(
                widget.moduleTitle,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
        ),
        _buildStatusBadge(theme, colorScheme),
      ],
    );
  }

  Widget _buildCompactHeader(ThemeData theme, ColorScheme colorScheme) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingS),
          decoration: BoxDecoration(
            color: theme
                .getCycleColor(widget.progress.cyclesStudied)
                .withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(AppDimens.radiusS),
          ),
          child: Icon(
            Icons.trending_up_rounded,
            color: theme.getCycleColor(widget.progress.cyclesStudied),
            size: AppDimens.iconM,
          ),
        ),
        const SizedBox(width: AppDimens.spaceM),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Progress',
                style: theme.textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                  color: colorScheme.onSurface,
                ),
              ),
              Text(
                '${widget.progress.percentComplete.round()}% • ${_getCycleText()}',
                style: theme.textTheme.bodySmall?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
            ],
          ),
        ),
        _buildCompactStatusBadge(theme, colorScheme),
      ],
    );
  }

  Widget _buildProgressIcon(ColorScheme colorScheme) {
    final cycleColor = Theme.of(
      context,
    ).getCycleColor(widget.progress.cyclesStudied);

    final Widget iconWidget = Container(
      padding: const EdgeInsets.all(AppDimens.paddingM),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [cycleColor, cycleColor.withValues(alpha: 0.8)],
        ),
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        boxShadow: [
          BoxShadow(
            color: cycleColor.withValues(alpha: 0.3),
            blurRadius: AppDimens.shadowRadiusM,
            offset: const Offset(0, AppDimens.shadowOffsetS),
          ),
        ],
      ),
      child: Icon(
        Icons.trending_up_rounded,
        color: colorScheme.onPrimary,
        size: AppDimens.iconL,
      ),
    );

    if (_isOverdue() && widget.showAnimation) {
      return AnimatedBuilder(
        animation: _pulseAnimation,
        builder: (context, child) {
          return Transform.scale(
            scale: _pulseAnimation.value,
            child: iconWidget,
          );
        },
      );
    }

    return iconWidget;
  }

  Widget _buildStatusBadge(ThemeData theme, ColorScheme colorScheme) {
    final statusColor = _getStatusColor(colorScheme);

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
        vertical: AppDimens.paddingS,
      ),
      decoration: BoxDecoration(
        color: statusColor.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        border: Border.all(color: statusColor.withValues(alpha: 0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(_getStatusIcon(), size: AppDimens.iconS, color: statusColor),
          const SizedBox(width: AppDimens.spaceXS),
          Text(
            _getStatusText(),
            style: theme.textTheme.labelMedium?.copyWith(
              color: statusColor,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCompactStatusBadge(ThemeData theme, ColorScheme colorScheme) {
    final statusColor = _getStatusColor(colorScheme);

    return Container(
      padding: const EdgeInsets.all(AppDimens.paddingS),
      decoration: BoxDecoration(
        color: statusColor.withValues(alpha: 0.1),
        shape: BoxShape.circle,
      ),
      child: Icon(_getStatusIcon(), size: AppDimens.iconS, color: statusColor),
    );
  }

  Widget _buildProgressDetails(ThemeData theme, ColorScheme colorScheme) {
    if (widget.isCompact) {
      return _buildCompactProgressDetails(theme, colorScheme);
    }

    return Column(
      children: [
        _buildProgressBar(theme, colorScheme),
        const SizedBox(height: AppDimens.spaceL),
        _buildInfoGrid(theme, colorScheme),
      ],
    );
  }

  Widget _buildCompactProgressDetails(
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return Column(
      children: [
        _buildProgressBar(theme, colorScheme),
        const SizedBox(height: AppDimens.spaceM),
        Row(
          children: [
            Expanded(
              child: _buildCompactInfoItem(
                'Next Study',
                _formatNextStudyDate(),
                _getStatusColor(colorScheme),
                theme,
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildProgressBar(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Completion',
              style: theme.textTheme.labelMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
                fontWeight: FontWeight.w500,
              ),
            ),
            Text(
              '${widget.progress.percentComplete.round()}%',
              style: theme.textTheme.titleMedium?.copyWith(
                color: theme.getProgressColor(widget.progress.percentComplete),
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceS),
        SLProgressIndicator(
          type: ProgressType.linear,
          value: widget.progress.percentComplete / 100,
          color: theme.getProgressColor(widget.progress.percentComplete),
          size: widget.isCompact
              ? AppDimens.lineProgressHeight
              : AppDimens.lineProgressHeightL,
          animate: widget.showAnimation,
        ),
      ],
    );
  }

  Widget _buildInfoGrid(ThemeData theme, ColorScheme colorScheme) {
    return Row(
      children: [
        Expanded(
          child: _buildInfoItem(
            'Cycle',
            _getCycleText(),
            Icons.repeat_rounded,
            theme.getCycleColor(widget.progress.cyclesStudied),
            theme,
          ),
        ),
        const SizedBox(width: AppDimens.spaceL),
        Expanded(
          child: _buildInfoItem(
            'Next Study',
            _formatNextStudyDate(),
            Icons.schedule_rounded,
            _getStatusColor(colorScheme),
            theme,
          ),
        ),
      ],
    );
  }

  Widget _buildInfoItem(
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
            label,
            style: theme.textTheme.labelSmall?.copyWith(
              color: Theme.of(context).colorScheme.onSurfaceVariant,
              fontWeight: FontWeight.w500,
            ),
          ),
          Text(
            value,
            style: theme.textTheme.titleSmall?.copyWith(
              color: color,
              fontWeight: FontWeight.w600,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildCompactInfoItem(
    String label,
    String value,
    Color color,
    ThemeData theme,
  ) {
    return Text(
      '$label: $value',
      style: theme.textTheme.bodySmall?.copyWith(
        color: color,
        fontWeight: FontWeight.w500,
      ),
    );
  }

  Widget _buildActionButtons(ThemeData theme, ColorScheme colorScheme) {
    if (!widget.showActions) return const SizedBox.shrink();

    if (widget.isCompact) {
      return _buildCompactActionButtons(theme, colorScheme);
    }

    return Row(
      children: [
        Expanded(
          child: SLButton(
            text: 'View Details',
            type: SLButtonType.outline,
            prefixIcon: Icons.visibility_outlined,
            onPressed: () => widget.onTap(widget.progress.id),
            size: SLButtonSize.medium,
          ),
        ),
        if (_isDue() || _isOverdue()) ...[
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: SLButton(
              text: _isOverdue() ? 'Study Now' : 'Start Study',
              type: SLButtonType.primary,
              prefixIcon: Icons.play_arrow_rounded,
              onPressed:
                  widget.onStartStudy ?? () => widget.onTap(widget.progress.id),
              size: SLButtonSize.medium,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildCompactActionButtons(ThemeData theme, ColorScheme colorScheme) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        if (_isDue() || _isOverdue())
          SLButton(
            text: _isOverdue() ? 'Study' : 'Start',
            type: SLButtonType.primary,
            prefixIcon: Icons.play_arrow_rounded,
            onPressed:
                widget.onStartStudy ?? () => widget.onTap(widget.progress.id),
            size: SLButtonSize.small,
          ),
        const SizedBox(width: AppDimens.spaceS),
        SLButton(
          text: 'Details',
          type: SLButtonType.text,
          prefixIcon: Icons.visibility_outlined,
          onPressed: () => widget.onTap(widget.progress.id),
          size: SLButtonSize.small,
        ),
      ],
    );
  }

  String _formatNextStudyDate() {
    final nextDate = widget.progress.nextStudyDate;
    if (nextDate == null) return 'Not set';

    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final targetDate = DateTime(nextDate.year, nextDate.month, nextDate.day);

    final difference = targetDate.difference(today).inDays;

    if (difference == 0) return 'Today';
    if (difference == 1) return 'Tomorrow';
    if (difference == -1) return 'Yesterday';
    if (difference < -1) return '${difference.abs()} days ago';
    if (difference > 1) return 'In $difference days';

    return 'Unknown';
  }

  Widget _buildContent(ThemeData theme, ColorScheme colorScheme) {
    return GestureDetector(
      onTapDown: (_) => _handleTapDown(),
      onTapUp: (_) => _handleTapUp(),
      onTapCancel: _handleTapUp,
      child: SLCard(
        type: SLCardType.filled,
        padding: EdgeInsets.all(
          widget.isCompact ? AppDimens.paddingM : AppDimens.paddingL,
        ),
        backgroundColor: colorScheme.surfaceContainer,
        elevation: _isPressed ? AppDimens.elevationM : AppDimens.elevationS,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
          side: BorderSide(
            color: _getStatusColor(
              colorScheme,
            ).withValues(alpha: _isPressed ? 0.3 : 0.2),
            width: _isPressed ? 1.5 : 1.0,
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildProgressHeader(theme, colorScheme),
            SizedBox(
              height: widget.isCompact ? AppDimens.spaceL : AppDimens.spaceXL,
            ),
            _buildProgressDetails(theme, colorScheme),
            if (widget.showActions) ...[
              SizedBox(
                height: widget.isCompact ? AppDimens.spaceM : AppDimens.spaceL,
              ),
              _buildActionButtons(theme, colorScheme),
            ],
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (!widget.showAnimation) {
      return _buildContent(theme, colorScheme);
    }

    return AnimatedBuilder(
      animation: Listenable.merge([_slideController, _scaleController]),
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

// Extension for progress card variations
extension ModuleProgressCardVariations on ModuleProgressCard {
  /// Creates a compact version for smaller displays
  static Widget compact({
    required ProgressDetail progress,
    required String moduleTitle,
    required void Function(String) onTap,
    VoidCallback? onStartStudy,
    bool showActions = true,
  }) {
    return ModuleProgressCard(
      progress: progress,
      moduleTitle: moduleTitle,
      onTap: onTap,
      onStartStudy: onStartStudy,
      showAnimation: false,
      isCompact: true,
      showActions: showActions,
    );
  }

  /// Creates a minimal version without actions
  static Widget minimal({
    required ProgressDetail progress,
    required String moduleTitle,
    required void Function(String) onTap,
  }) {
    return ModuleProgressCard(
      progress: progress,
      moduleTitle: moduleTitle,
      onTap: onTap,
      showAnimation: false,
      isCompact: true,
      showActions: false,
    );
  }

  /// Creates an animated version with custom delay
  static Widget animated({
    required ProgressDetail progress,
    required String moduleTitle,
    required void Function(String) onTap,
    VoidCallback? onViewDetails,
    VoidCallback? onStartStudy,
  }) {
    return ModuleProgressCard(
      progress: progress,
      moduleTitle: moduleTitle,
      onTap: onTap,
      showAnimation: true,
      onViewDetails: onViewDetails,
      onStartStudy: onStartStudy,
    );
  }

  /// Creates a card optimized for list display
  static Widget forList({
    required ProgressDetail progress,
    required String moduleTitle,
    required void Function(String) onTap,
    VoidCallback? onStartStudy,
  }) {
    return ModuleProgressCard(
      progress: progress,
      moduleTitle: moduleTitle,
      onTap: onTap,
      onStartStudy: onStartStudy,
      showAnimation: false,
      isCompact: true,
      showActions: true,
    );
  }

  /// Creates a card for dashboard display
  static Widget forDashboard({
    required ProgressDetail progress,
    required String moduleTitle,
    required void Function(String) onTap,
  }) {
    return ModuleProgressCard(
      progress: progress,
      moduleTitle: moduleTitle,
      onTap: onTap,
      showAnimation: true,
      isCompact: false,
      showActions: true,
    );
  }
}
