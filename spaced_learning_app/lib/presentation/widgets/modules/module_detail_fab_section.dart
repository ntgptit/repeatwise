// lib/presentation/widgets/modules/detail/module_detail_fab_section.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class ModuleDetailFabSection extends StatefulWidget {
  final bool hasProgress;
  final bool hasGrammar;
  final bool isCheckingGrammar;
  final VoidCallback? onStartLearning;
  final VoidCallback? onViewGrammar;
  final VoidCallback? onViewProgress;
  final bool showAnimation;

  const ModuleDetailFabSection({
    super.key,
    required this.hasProgress,
    required this.hasGrammar,
    this.isCheckingGrammar = false,
    this.onStartLearning,
    this.onViewGrammar,
    this.onViewProgress,
    this.showAnimation = true,
  });

  @override
  State<ModuleDetailFabSection> createState() => _ModuleDetailFabSectionState();
}

class _ModuleDetailFabSectionState extends State<ModuleDetailFabSection>
    with TickerProviderStateMixin {
  late AnimationController _slideController;
  late AnimationController _rotationController;
  late AnimationController _expandController;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _fadeAnimation;
  late Animation<double> _rotationAnimation;
  late Animation<double> _expandAnimation;

  bool _isExpanded = false;

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    if (widget.showAnimation) {
      _startEntryAnimation();
    }
  }

  @override
  void dispose() {
    _slideController.dispose();
    _rotationController.dispose();
    _expandController.dispose();
    super.dispose();
  }

  void _initializeAnimations() {
    _slideController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _rotationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );

    _expandController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0.0, 2.0), end: Offset.zero).animate(
          CurvedAnimation(parent: _slideController, curve: Curves.elasticOut),
        );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _slideController, curve: Curves.easeOut));

    _rotationAnimation = Tween<double>(begin: 0.0, end: 0.125).animate(
      CurvedAnimation(parent: _rotationController, curve: Curves.easeInOut),
    );

    _expandAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _expandController, curve: Curves.easeOutCubic),
    );
  }

  void _startEntryAnimation() {
    Future.delayed(const Duration(milliseconds: 1000), () {
      if (mounted) {
        _slideController.forward();
      }
    });
  }

  void _toggleExpansion() {
    setState(() {
      _isExpanded = !_isExpanded;
    });

    if (_isExpanded) {
      _rotationController.forward();
      _expandController.forward();
      return;
    }

    _rotationController.reverse();
    _expandController.reverse();
  }

  Widget _buildMainFAB(ColorScheme colorScheme) {
    // Show single action FAB if only one action available
    if (_getAvailableActionsCount() == 1) {
      return _buildSingleActionFAB(colorScheme);
    }

    // Show expandable FAB for multiple actions
    return FloatingActionButton(
      onPressed: _toggleExpansion,
      heroTag: 'module_main_fab',
      backgroundColor: colorScheme.primary,
      foregroundColor: colorScheme.onPrimary,
      elevation: AppDimens.elevationL,
      child: AnimatedBuilder(
        animation: _rotationAnimation,
        builder: (context, child) {
          return Transform.rotate(
            angle: _rotationAnimation.value * 3.14159 * 2,
            child: Icon(
              _isExpanded ? Icons.close_rounded : Icons.more_vert_rounded,
            ),
          );
        },
      ),
    );
  }

  Widget _buildSingleActionFAB(ColorScheme colorScheme) {
    // Determine the primary action
    if (!widget.hasProgress && widget.onStartLearning != null) {
      return FloatingActionButton.extended(
        onPressed: widget.onStartLearning,
        heroTag: 'module_start_learning_fab',
        backgroundColor: colorScheme.primary,
        foregroundColor: colorScheme.onPrimary,
        elevation: AppDimens.elevationL,
        icon: const Icon(Icons.play_arrow_rounded),
        label: const Text('Start Learning'),
      );
    }

    if (widget.hasProgress && widget.onViewProgress != null) {
      return FloatingActionButton.extended(
        onPressed: widget.onViewProgress,
        heroTag: 'module_view_progress_fab',
        backgroundColor: colorScheme.secondary,
        foregroundColor: colorScheme.onSecondary,
        elevation: AppDimens.elevationL,
        icon: const Icon(Icons.trending_up_rounded),
        label: const Text('View Progress'),
      );
    }

    if (widget.hasGrammar && widget.onViewGrammar != null) {
      return FloatingActionButton.extended(
        onPressed: widget.onViewGrammar,
        heroTag: 'module_view_grammar_fab',
        backgroundColor: colorScheme.tertiary,
        foregroundColor: colorScheme.onTertiary,
        elevation: AppDimens.elevationL,
        icon: widget.isCheckingGrammar
            ? SizedBox(
                width: AppDimens.iconM,
                height: AppDimens.iconM,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  valueColor: AlwaysStoppedAnimation<Color>(
                    colorScheme.onTertiary,
                  ),
                ),
              )
            : const Icon(Icons.auto_stories_rounded),
        label: const Text('Grammar'),
      );
    }

    // Fallback - should not reach here
    return const SizedBox.shrink();
  }

  Widget _buildSecondaryFAB({
    required IconData icon,
    required String tooltip,
    required VoidCallback? onPressed,
    required Color backgroundColor,
    required Color foregroundColor,
    required String heroTag,
    Widget? customIcon,
  }) {
    return FloatingActionButton(
      onPressed: onPressed,
      heroTag: heroTag,
      backgroundColor: backgroundColor,
      foregroundColor: foregroundColor,
      elevation: AppDimens.elevationM,
      mini: true,
      tooltip: tooltip,
      child: customIcon ?? Icon(icon),
    );
  }

  Widget _buildExpandedActions(ColorScheme colorScheme) {
    final actions = <Widget>[];

    // Start Learning Action
    if (!widget.hasProgress && widget.onStartLearning != null) {
      actions.add(
        AnimatedBuilder(
          animation: _expandAnimation,
          builder: (context, child) {
            return Transform.scale(
              scale: _expandAnimation.value,
              child: Opacity(
                opacity: _expandAnimation.value,
                child: _buildSecondaryFAB(
                  icon: Icons.play_arrow_rounded,
                  tooltip: 'Start Learning',
                  onPressed: widget.onStartLearning,
                  backgroundColor: colorScheme.primary,
                  foregroundColor: colorScheme.onPrimary,
                  heroTag: 'module_start_learning_mini_fab',
                ),
              ),
            );
          },
        ),
      );
    }

    // View Progress Action
    if (widget.hasProgress && widget.onViewProgress != null) {
      actions.add(
        AnimatedBuilder(
          animation: _expandAnimation,
          builder: (context, child) {
            return Transform.scale(
              scale: _expandAnimation.value,
              child: Opacity(
                opacity: _expandAnimation.value,
                child: _buildSecondaryFAB(
                  icon: Icons.trending_up_rounded,
                  tooltip: 'View Progress',
                  onPressed: widget.onViewProgress,
                  backgroundColor: colorScheme.secondary,
                  foregroundColor: colorScheme.onSecondary,
                  heroTag: 'module_view_progress_mini_fab',
                ),
              ),
            );
          },
        ),
      );
    }

    // View Grammar Action
    if (widget.hasGrammar && widget.onViewGrammar != null) {
      actions.add(
        AnimatedBuilder(
          animation: _expandAnimation,
          builder: (context, child) {
            return Transform.scale(
              scale: _expandAnimation.value,
              child: Opacity(
                opacity: _expandAnimation.value,
                child: _buildSecondaryFAB(
                  icon: Icons.auto_stories_rounded,
                  tooltip: 'View Grammar',
                  onPressed: widget.onViewGrammar,
                  backgroundColor: colorScheme.tertiary,
                  foregroundColor: colorScheme.onTertiary,
                  heroTag: 'module_view_grammar_mini_fab',
                  customIcon: widget.isCheckingGrammar
                      ? SizedBox(
                          width: AppDimens.iconS,
                          height: AppDimens.iconS,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            valueColor: AlwaysStoppedAnimation<Color>(
                              colorScheme.onTertiary,
                            ),
                          ),
                        )
                      : null,
                ),
              ),
            );
          },
        ),
      );
    }

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        ...actions.map(
          (action) => Padding(
            padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
            child: action,
          ),
        ),
      ],
    );
  }

  int _getAvailableActionsCount() {
    int count = 0;

    if (!widget.hasProgress && widget.onStartLearning != null) count++;
    if (widget.hasProgress && widget.onViewProgress != null) count++;
    if (widget.hasGrammar && widget.onViewGrammar != null) count++;

    return count;
  }

  Widget _buildFABContent(ColorScheme colorScheme) {
    final availableActions = _getAvailableActionsCount();

    // Don't show FAB if no actions available
    if (availableActions == 0) {
      return const SizedBox.shrink();
    }

    // Single action - show extended FAB
    if (availableActions == 1) {
      return _buildMainFAB(colorScheme);
    }

    // Multiple actions - show expandable FAB
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        if (_isExpanded) ...[
          _buildExpandedActions(colorScheme),
          const SizedBox(height: AppDimens.spaceM),
        ],
        _buildMainFAB(colorScheme),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    if (!widget.showAnimation) {
      return _buildFABContent(colorScheme);
    }

    return AnimatedBuilder(
      animation: _slideController,
      builder: (context, child) {
        return SlideTransition(
          position: _slideAnimation,
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: _buildFABContent(colorScheme),
          ),
        );
      },
    );
  }
}

// Extension for FAB variations
extension ModuleDetailFabSectionVariations on ModuleDetailFabSection {
  /// Creates a simple version for single action
  static Widget simple({
    required bool hasProgress,
    VoidCallback? onStartLearning,
    VoidCallback? onViewProgress,
    bool showAnimation = true,
  }) {
    return ModuleDetailFabSection(
      hasProgress: hasProgress,
      hasGrammar: false,
      onStartLearning: onStartLearning,
      onViewProgress: onViewProgress,
      showAnimation: showAnimation,
    );
  }

  /// Creates a full-featured version with all actions
  static Widget full({
    required bool hasProgress,
    required bool hasGrammar,
    bool isCheckingGrammar = false,
    VoidCallback? onStartLearning,
    VoidCallback? onViewProgress,
    VoidCallback? onViewGrammar,
    VoidCallback? onCheckGrammar,
    bool showAnimation = true,
  }) {
    return ModuleDetailFabSection(
      hasProgress: hasProgress,
      hasGrammar: hasGrammar,
      isCheckingGrammar: isCheckingGrammar,
      onStartLearning: onStartLearning,
      onViewProgress: onViewProgress,
      onViewGrammar: onViewGrammar,
      showAnimation: showAnimation,
    );
  }
}
