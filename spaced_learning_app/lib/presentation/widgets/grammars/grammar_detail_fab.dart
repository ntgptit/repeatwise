// lib/presentation/widgets/modules/grammar/detail/grammar_detail_fab.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';

class GrammarDetailFAB extends StatefulWidget {
  final GrammarDetail grammar;

  // final VoidCallback? onPracticePressed; // Removed
  final VoidCallback? onBookmarkPressed;
  final bool isBookmarked;
  final bool showAnimation;
  final bool isExtended;

  const GrammarDetailFAB({
    super.key,
    required this.grammar,
    // this.onPracticePressed, // Removed
    this.onBookmarkPressed,
    this.isBookmarked = false,
    this.showAnimation = true,
    this.isExtended =
        true, // Set to true if you want a single FAB, false for speed dial like
  });

  @override
  State<GrammarDetailFAB> createState() => _GrammarDetailFABState();
}

class _GrammarDetailFABState extends State<GrammarDetailFAB>
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
    // If not extended, default to expanded state to show secondary FABs if any are present
    if (!widget.isExtended && widget.onBookmarkPressed != null) {
      _isExpanded = false; // Start collapsed, user needs to tap to expand
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
      duration: const Duration(milliseconds: 600),
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

    _rotationAnimation = Tween<double>(begin: 0.0, end: 0.5).animate(
      CurvedAnimation(parent: _rotationController, curve: Curves.easeInOut),
    );

    _expandAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _expandController, curve: Curves.easeOutCubic),
    );
  }

  void _startEntryAnimation() {
    Future.delayed(const Duration(milliseconds: 800), () {
      if (mounted) {
        _slideController.forward();
      }
    });
  }

  void _toggleExpansion() {
    // Only toggle if there are secondary actions to show
    if (widget.onBookmarkPressed == null && widget.isExtended) return;

    setState(() {
      _isExpanded = !_isExpanded;
    });

    if (_isExpanded) {
      _rotationController.forward();
      _expandController.forward();
    } else {
      _rotationController.reverse();
      _expandController.reverse();
    }
  }

  Widget _buildMainFAB(ColorScheme colorScheme) {
    // If isExtended is true, it means we want a single action FAB (e.g., only bookmark)
    // If onBookmarkPressed is null, then there's no action for the main FAB in extended mode.
    if (widget.isExtended) {
      if (widget.onBookmarkPressed == null) {
        return const SizedBox.shrink(); // No action if bookmark is not available
      }
      return FloatingActionButton.extended(
        onPressed: widget.onBookmarkPressed,
        heroTag: 'grammar_bookmark_fab_extended',
        backgroundColor: widget.isBookmarked
            ? colorScheme.primaryContainer
            : colorScheme.secondaryContainer,
        foregroundColor: widget.isBookmarked
            ? colorScheme.onPrimaryContainer
            : colorScheme.onSecondaryContainer,
        elevation: AppDimens.elevationL,
        icon: Icon(
          widget.isBookmarked
              ? Icons.bookmark_rounded
              : Icons.bookmark_border_rounded,
        ),
        label: Text(widget.isBookmarked ? 'Bookmarked' : 'Bookmark'),
      );
    }

    // If not extended, this FAB acts as a toggle for other actions
    return FloatingActionButton(
      onPressed: _toggleExpansion,
      heroTag: 'grammar_main_fab_toggle',
      backgroundColor: colorScheme.primary,
      foregroundColor: colorScheme.onPrimary,
      elevation: AppDimens.elevationL,
      child: AnimatedBuilder(
        animation: _rotationAnimation,
        builder: (context, child) {
          return Transform.rotate(
            angle: _rotationAnimation.value * 3.14159, // 180 deg rotation
            child: Icon(
              _isExpanded ? Icons.close_rounded : Icons.more_vert_rounded,
            ),
          );
        },
      ),
    );
  }

  Widget _buildSecondaryFAB({
    required IconData icon,
    required String tooltip,
    required VoidCallback? onPressed,
    required ColorScheme colorScheme,
    bool isActive = false,
  }) {
    return FloatingActionButton(
      onPressed: onPressed,
      heroTag: 'grammar_${tooltip.toLowerCase().replaceAll(' ', '_')}_fab',
      backgroundColor: isActive
          ? colorScheme.primaryContainer
          : colorScheme.secondaryContainer,
      foregroundColor: isActive
          ? colorScheme.onPrimaryContainer
          : colorScheme.onSecondaryContainer,
      elevation: AppDimens.elevationM,
      mini: true,
      tooltip: tooltip,
      child: Icon(icon),
    );
  }

  Widget _buildExpandedActions(ColorScheme colorScheme) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.end, // Align to the right
      children: [
        // Bookmark FAB
        if (widget.onBookmarkPressed != null) ...[
          AnimatedBuilder(
            animation: _expandAnimation,
            builder: (context, child) {
              return Transform.scale(
                scale: _expandAnimation.value,
                child: Opacity(
                  opacity: _expandAnimation.value,
                  child: _buildSecondaryFAB(
                    icon: widget.isBookmarked
                        ? Icons.bookmark_rounded
                        : Icons.bookmark_border_rounded,
                    tooltip: widget.isBookmarked
                        ? 'Remove Bookmark'
                        : 'Bookmark',
                    onPressed: widget.onBookmarkPressed,
                    colorScheme: colorScheme,
                    isActive: widget.isBookmarked,
                  ),
                ),
              );
            },
          ),
          const SizedBox(height: AppDimens.spaceM),
        ],
      ],
    );
  }

  Widget _buildFABContent(ColorScheme colorScheme) {
    // If isExtended is true, only show the main FAB (which will be bookmark or nothing)
    if (widget.isExtended) {
      return _buildMainFAB(colorScheme);
    }

    // If not extended, show the toggle FAB and potentially expanded actions
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        if (_isExpanded) _buildExpandedActions(colorScheme),
        _buildMainFAB(colorScheme), // This is the toggle FAB
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    // If there are no actions at all, return an empty widget
    if (widget.onBookmarkPressed == null && widget.isExtended) {
      return const SizedBox.shrink();
    }

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

// Grammar FAB Action Model - Kept for potential future use but not directly used by GrammarDetailFAB anymore
class GrammarFABAction {
  final IconData icon;
  final String label;
  final VoidCallback onPressed;
  final Color? backgroundColor;
  final Color? foregroundColor;

  const GrammarFABAction({
    required this.icon,
    required this.label,
    required this.onPressed,
    this.backgroundColor,
    this.foregroundColor,
  });
}

// Extension for FAB variations
extension GrammarDetailFABVariations on GrammarDetailFAB {
  /// Creates a simple bookmark-only FAB (if bookmark action is provided)
  static Widget bookmarkOnly({
    required GrammarDetail grammar,
    VoidCallback? onBookmarkPressed,
    bool isBookmarked = false,
    bool showAnimation = true,
  }) {
    return GrammarDetailFAB(
      grammar: grammar,
      onBookmarkPressed: onBookmarkPressed,
      isBookmarked: isBookmarked,
      showAnimation: showAnimation,
      isExtended: true, // Ensures it behaves as a single action FAB
    );
  }

  /// Creates a multi-action FAB (toggle with bookmark)
  static Widget multiAction({
    required GrammarDetail grammar,
    VoidCallback? onBookmarkPressed,
    bool isBookmarked = false,
    bool showAnimation = true,
  }) {
    return GrammarDetailFAB(
      grammar: grammar,
      onBookmarkPressed: onBookmarkPressed,
      isBookmarked: isBookmarked,
      showAnimation: showAnimation,
      isExtended: false, // Ensures it behaves as a speed-dial like FAB
    );
  }
}
