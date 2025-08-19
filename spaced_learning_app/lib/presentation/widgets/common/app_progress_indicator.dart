import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

enum ProgressType { linear, circular }

class SLProgressIndicator extends StatefulWidget {
  final ProgressType type;
  final double? value;
  final double size;
  final double strokeWidth;
  final Color? color;
  final Color? backgroundColor;
  final Color? foregroundColor;
  final Animation<Color?>? valueColor;
  final Widget? child;
  final String? label;
  final TextStyle? labelStyle;
  final bool animate;
  final Duration animationDuration;

  const SLProgressIndicator({
    super.key,
    this.type = ProgressType.circular,
    this.value,
    this.size = AppDimens.circularProgressSizeL,
    this.strokeWidth = AppDimens.lineProgressHeight,
    this.color,
    this.backgroundColor,
    this.foregroundColor,
    this.valueColor,
    this.child,
    this.label,
    this.labelStyle,
    this.animate = false,
    this.animationDuration = const Duration(milliseconds: 1500),
  });

  @override
  State<SLProgressIndicator> createState() => _SLProgressIndicatorState();
}

class _SLProgressIndicatorState extends State<SLProgressIndicator>
    with SingleTickerProviderStateMixin {
  AnimationController? _animationController;
  late Animation<double> _fadeAnimation;
  late Animation<double> _pulseAnimation;

  @override
  void initState() {
    super.initState();
    _setupAnimation();
  }

  @override
  void didUpdateWidget(SLProgressIndicator oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.animate != widget.animate) {
      _teardownAnimation();
      _setupAnimation();
    }
  }

  @override
  void dispose() {
    _teardownAnimation();
    super.dispose();
  }

  void _setupAnimation() {
    if (!widget.animate) return;

    _animationController = AnimationController(
      vsync: this,
      duration: widget.animationDuration,
    );

    _fadeAnimation = Tween<double>(begin: 0.7, end: 1.0).animate(
      CurvedAnimation(parent: _animationController!, curve: Curves.easeInOut),
    );

    _pulseAnimation = Tween<double>(begin: 0.97, end: 1.03).animate(
      CurvedAnimation(parent: _animationController!, curve: Curves.easeInOut),
    );

    _animationController!.repeat(reverse: true);
  }

  void _teardownAnimation() {
    _animationController?.dispose();
    _animationController = null;
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final effectiveForegroundColor =
        widget.foregroundColor ?? widget.color ?? colorScheme.primary;
    final effectiveBackgroundColor =
        widget.backgroundColor ?? colorScheme.surfaceContainerHighest;
    final effectiveValueColor =
        widget.valueColor ?? AlwaysStoppedAnimation(effectiveForegroundColor);

    return _buildContent(
      effectiveForegroundColor,
      effectiveBackgroundColor,
      effectiveValueColor,
      theme,
      colorScheme,
    );
  }

  Widget _buildContent(
    Color foregroundColor,
    Color backgroundColor,
    Animation<Color?> valueColor,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final progressIndicator = widget.type == ProgressType.linear
        ? _buildLinearProgress(foregroundColor, backgroundColor, valueColor)
        : _buildCircularProgress(foregroundColor, backgroundColor, valueColor);

    if (widget.label != null) {
      return Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          progressIndicator,
          const SizedBox(height: AppDimens.spaceS),
          Text(
            widget.label!,
            style:
                widget.labelStyle ??
                theme.textTheme.bodyMedium!.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
            textAlign: TextAlign.center,
          ),
        ],
      );
    }
    return progressIndicator;
  }

  Widget _buildLinearProgress(
    Color foregroundColor,
    Color backgroundColor,
    Animation<Color?> valueColor,
  ) {
    return widget.animate && _animationController != null
        ? AnimatedBuilder(
            animation: _animationController!,
            builder: (context, child) => Opacity(
              opacity: _fadeAnimation.value,
              child: LinearProgressIndicator(
                value: widget.value,
                backgroundColor: backgroundColor,
                valueColor: valueColor,
                minHeight: widget.strokeWidth,
                borderRadius: BorderRadius.circular(widget.strokeWidth / 2),
              ),
            ),
          )
        : LinearProgressIndicator(
            value: widget.value,
            backgroundColor: backgroundColor,
            valueColor: valueColor,
            minHeight: widget.strokeWidth,
            borderRadius: BorderRadius.circular(widget.strokeWidth / 2),
          );
  }

  Widget _buildCircularProgress(
    Color foregroundColor,
    Color backgroundColor,
    Animation<Color?> valueColor,
  ) {
    Widget circularProgress = SizedBox(
      width: widget.size,
      height: widget.size,
      child: CircularProgressIndicator(
        value: widget.value,
        backgroundColor: backgroundColor,
        valueColor: valueColor,
        strokeWidth: widget.strokeWidth,
        strokeCap: StrokeCap.round,
      ),
    );

    if (widget.child != null) {
      circularProgress = Stack(
        alignment: Alignment.center,
        children: [circularProgress, widget.child!],
      );
    }

    return widget.animate && _animationController != null
        ? AnimatedBuilder(
            animation: _animationController!,
            builder: (context, child) => Transform.scale(
              scale: _pulseAnimation.value,
              child: Opacity(opacity: _fadeAnimation.value, child: child),
            ),
            child: circularProgress,
          )
        : circularProgress;
  }
}
