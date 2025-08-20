import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:spaced_learning_app/core/extensions/color_extensions.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';
import 'package:spaced_learning_app/domain/models/repetition.dart';

class RepetitionCard extends StatefulWidget {
  final Repetition repetition;
  final bool isHistory;
  final VoidCallback? onMarkCompleted;
  final Function(DateTime)? onReschedule;

  const RepetitionCard({
    super.key,
    required this.repetition,
    this.isHistory = false,
    this.onMarkCompleted,
    this.onReschedule,
  });

  @override
  State<RepetitionCard> createState() => _RepetitionCardState();
}

class _RepetitionCardState extends State<RepetitionCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;
  bool _isHovering = false;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: AppDimens.durationS),
      vsync: this,
    );

    _scaleAnimation = Tween<double>(
      begin: 1.0,
      end: 1.02,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeOutCubic));
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _onHoverChanged(bool isHovering) {
    setState(() {
      _isHovering = isHovering;
    });

    if (isHovering) {
      _controller.forward();
    } else {
      _controller.reverse();
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final isCompleted = widget.repetition.status == RepetitionStatus.completed;
    final isPending = widget.repetition.status == RepetitionStatus.notStarted;

    final isOverdue =
        isPending &&
        widget.repetition.reviewDate != null &&
        widget.repetition.reviewDate!.isBefore(DateTime.now());

    // Configure appearance based on state
    Color cardColor = colorScheme.surfaceContainerLowest;
    Color borderColor = colorScheme.outlineVariant;
    Color textColor = colorScheme.onSurface;
    Color iconColor = colorScheme.primary;

    if (isOverdue) {
      cardColor = colorScheme.errorContainer.withValues(alpha: 0.08);
      borderColor = colorScheme.error.withValues(alpha: 0.6);
      textColor = colorScheme.error;
      iconColor = colorScheme.error;
    } else if (isCompleted) {
      cardColor = colorScheme.surfaceContainerLow;
      borderColor = colorScheme.success.withValues(alpha: 0.4);
      iconColor = colorScheme.success;
    }

    // Set up status text and icon
    String statusText = 'Pending';
    IconData statusIcon = Icons.pending;

    if (isCompleted) {
      statusText = 'Completed';
      statusIcon = Icons.check_circle;
    } else if (isOverdue) {
      statusText = 'Overdue';
      statusIcon = Icons.warning_amber_rounded;
    }

    // Get color for repetition order
    final repetitionColor = theme.getRepetitionColor(
      widget.repetition.repetitionOrder.index,
      isHistory: widget.isHistory,
    );

    final reviewDateText = widget.repetition.reviewDate != null
        ? DateFormat('MMM dd, yyyy').format(widget.repetition.reviewDate!)
        : 'Not scheduled';

    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.spaceS),
      child: MouseRegion(
        onEnter: (_) => _onHoverChanged(true),
        onExit: (_) => _onHoverChanged(false),
        child: AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return Transform.scale(scale: _scaleAnimation.value, child: child);
          },
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            decoration: BoxDecoration(
              color: cardColor,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
              border: Border.all(
                color: _isHovering && !widget.isHistory
                    ? borderColor
                    : borderColor.withValues(alpha: 0.8),
                width: _isHovering && !widget.isHistory ? 2.0 : 1.0,
              ),
              boxShadow: _isHovering && !widget.isHistory
                  ? [
                      BoxShadow(
                        color: colorScheme.shadow.withValues(
                          alpha: AppDimens.opacityLight,
                        ),
                        blurRadius: 8,
                        offset: const Offset(0, 3),
                      ),
                    ]
                  : null,
            ),
            child: Material(
              type: MaterialType.transparency,
              clipBehavior: Clip.antiAlias,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
              child: InkWell(
                onTap:
                    isPending &&
                        !widget.isHistory &&
                        widget.onMarkCompleted != null
                    ? widget.onMarkCompleted
                    : null,
                splashColor: isPending && !widget.isHistory
                    ? repetitionColor.withValues(alpha: AppDimens.opacityLight)
                    : Colors.transparent,
                highlightColor: isPending && !widget.isHistory
                    ? repetitionColor.withValues(alpha: AppDimens.opacityLight)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                child: Padding(
                  padding: const EdgeInsets.all(AppDimens.paddingM),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          _buildOrderBadge(theme, repetitionColor),
                          const SizedBox(width: AppDimens.spaceM),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  widget.repetition.formatFullOrder(),
                                  style: theme.textTheme.titleMedium?.copyWith(
                                    fontWeight: FontWeight.bold,
                                    color: textColor,
                                  ),
                                ),
                                const SizedBox(height: AppDimens.spaceXXS),
                                Row(
                                  children: [
                                    Icon(
                                      statusIcon,
                                      size: AppDimens.iconS,
                                      color: iconColor,
                                    ),
                                    const SizedBox(width: AppDimens.spaceXS),
                                    Text(
                                      statusText,
                                      style: theme.textTheme.bodyMedium
                                          ?.copyWith(
                                            color: iconColor,
                                            fontWeight: FontWeight.w600,
                                          ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                          if (isPending &&
                              !widget.isHistory &&
                              widget.onMarkCompleted != null)
                            _buildCompleteButton(
                              theme,
                              colorScheme,
                              repetitionColor,
                            ),
                        ],
                      ),
                      const SizedBox(height: AppDimens.spaceM),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Expanded(
                            child: _buildDateInfo(
                              theme,
                              colorScheme,
                              reviewDateText,
                              isOverdue,
                              iconColor,
                            ),
                          ),
                          if (isPending &&
                              !widget.isHistory &&
                              widget.onReschedule != null)
                            _buildRescheduleButton(theme, colorScheme),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildOrderBadge(ThemeData theme, Color repetitionColor) {
    return Container(
      width: AppDimens.iconXL,
      height: AppDimens.iconXL,
      decoration: BoxDecoration(
        color: repetitionColor.withValues(alpha: 0.1),
        shape: BoxShape.circle,
        border: Border.all(color: repetitionColor, width: 1.5),
      ),
      child: Center(
        child: Text(
          widget.repetition.formatOrder(),
          style: theme.textTheme.titleMedium?.copyWith(
            color: repetitionColor,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }

  Widget _buildDateInfo(
    ThemeData theme,
    ColorScheme colorScheme,
    String reviewDateText,
    bool isOverdue,
    Color textColor,
  ) {
    return Row(
      children: [
        Icon(
          Icons.event,
          size: AppDimens.iconS,
          color: textColor.withValues(alpha: 0.8),
        ),
        const SizedBox(width: AppDimens.spaceXS),
        Text(
          'Review: ',
          style: theme.textTheme.bodyMedium?.copyWith(
            color: colorScheme.onSurfaceVariant,
          ),
        ),
        Flexible(
          child: Text(
            reviewDateText,
            style: theme.textTheme.bodyMedium?.copyWith(
              fontWeight: FontWeight.bold,
              color: textColor,
            ),
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }

  Widget _buildCompleteButton(
    ThemeData theme,
    ColorScheme colorScheme,
    Color repetitionColor,
  ) {
    return Material(
      color: Colors.transparent,
      child: Ink(
        decoration: BoxDecoration(
          color: _isHovering
              ? colorScheme.success.withValues(alpha: 0.1)
              : Colors.transparent,
          shape: BoxShape.circle,
        ),
        child: IconButton(
          icon: Icon(
            Icons.check_circle_outline,
            color: _isHovering ? colorScheme.success : repetitionColor,
            size: AppDimens.iconL - 4,
          ),
          onPressed: widget.onMarkCompleted,
          tooltip: 'Mark as completed',
          splashRadius: 24,
        ),
      ),
    );
  }

  Widget _buildRescheduleButton(ThemeData theme, ColorScheme colorScheme) {
    return OutlinedButton.icon(
      onPressed: widget.repetition.reviewDate != null
          ? () => widget.onReschedule?.call(widget.repetition.reviewDate!)
          : () => widget.onReschedule?.call(DateTime.now()),
      icon: Icon(
        Icons.calendar_today,
        size: AppDimens.iconS,
        color: colorScheme.primary,
      ),
      label: Text(
        'Reschedule',
        style: theme.textTheme.labelMedium?.copyWith(
          color: colorScheme.primary,
          fontWeight: FontWeight.w600,
        ),
      ),
      style: OutlinedButton.styleFrom(
        visualDensity: VisualDensity.compact,
        padding: const EdgeInsets.symmetric(
          horizontal: AppDimens.paddingS,
          vertical: AppDimens.paddingXXS,
        ),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
        ),
      ),
    );
  }
}
