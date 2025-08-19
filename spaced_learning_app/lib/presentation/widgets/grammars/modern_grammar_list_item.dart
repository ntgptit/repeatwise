// lib/presentation/widgets/grammars/modern_grammar_list_item.dart
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';

class ModernGrammarListItem extends StatefulWidget {
  final GrammarSummary grammar;
  final VoidCallback onTap;
  final Duration animationDelay;
  final bool showModuleName;
  final bool isCompact;
  final bool showAnimation;

  const ModernGrammarListItem({
    super.key,
    required this.grammar,
    required this.onTap,
    this.animationDelay = Duration.zero,
    this.showModuleName = true,
    this.isCompact = false,
    this.showAnimation = true,
  });

  @override
  State<ModernGrammarListItem> createState() => _ModernGrammarListItemState();
}

class _ModernGrammarListItemState extends State<ModernGrammarListItem>
    with TickerProviderStateMixin {
  late AnimationController _slideController;
  late AnimationController _pressController;
  late AnimationController _hoverController;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;
  late Animation<double> _hoverAnimation;

  bool _isPressed = false;
  bool _isHovered = false;

  @override
  void initState() {
    super.initState();
    if (widget.showAnimation) {
      _initializeAnimations();
      _startEntryAnimation();
    }
  }

  @override
  void dispose() {
    if (widget.showAnimation) {
      _slideController.dispose();
      _pressController.dispose();
      _hoverController.dispose();
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _slideController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );

    _pressController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 100),
    );

    _hoverController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 200),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0.0, 0.3), end: Offset.zero).animate(
          CurvedAnimation(parent: _slideController, curve: Curves.easeOutCubic),
        );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _slideController, curve: Curves.easeOut));

    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.96).animate(
      CurvedAnimation(parent: _pressController, curve: Curves.easeInOut),
    );

    _hoverAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _hoverController, curve: Curves.easeInOut),
    );
  }

  void _startEntryAnimation() {
    Future.delayed(widget.animationDelay, () {
      if (mounted) {
        _slideController.forward();
      }
    });
  }

  void _handleTapDown(TapDownDetails details) {
    setState(() => _isPressed = true);
    if (widget.showAnimation) {
      _pressController.forward();
    }
  }

  void _handleTapUp(TapUpDetails details) {
    _handleTapEnd();
  }

  void _handleTapCancel() {
    _handleTapEnd();
  }

  void _handleTapEnd() {
    setState(() => _isPressed = false);
    if (widget.showAnimation) {
      _pressController.reverse();
    }
  }

  void _handleHoverEnter(PointerEnterEvent event) {
    setState(() => _isHovered = true);
    if (widget.showAnimation) {
      _hoverController.forward();
    }
  }

  void _handleHoverExit(PointerExitEvent event) {
    setState(() => _isHovered = false);
    if (widget.showAnimation) {
      _hoverController.reverse();
    }
  }

  Widget _buildGrammarIcon(ColorScheme colorScheme) {
    final iconColor = _isPressed
        ? colorScheme.onSecondaryContainer
        : colorScheme.onSecondaryContainer;

    final backgroundColor = _isPressed
        ? colorScheme.secondaryContainer.withValues(alpha: 0.9)
        : colorScheme.secondaryContainer.withValues(alpha: 0.8);

    return AnimatedContainer(
      duration: const Duration(milliseconds: 150),
      width: widget.isCompact ? AppDimens.avatarSizeM : AppDimens.avatarSizeL,
      height: widget.isCompact ? AppDimens.avatarSizeM : AppDimens.avatarSizeL,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            backgroundColor,
            colorScheme.tertiaryContainer.withValues(alpha: 0.8),
          ],
        ),
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        boxShadow: _isPressed
            ? []
            : [
                BoxShadow(
                  color: colorScheme.shadow.withValues(alpha: 0.08),
                  blurRadius: AppDimens.shadowRadiusM,
                  offset: const Offset(0, AppDimens.shadowOffsetS),
                ),
              ],
      ),
      child: Icon(
        Icons.auto_stories_rounded,
        color: iconColor,
        size: widget.isCompact ? AppDimens.iconM : AppDimens.iconL,
      ),
    );
  }

  Widget _buildGrammarContent(ThemeData theme, ColorScheme colorScheme) {
    return Expanded(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            widget.grammar.grammarPattern,
            style:
                (widget.isCompact
                        ? theme.textTheme.titleSmall
                        : theme.textTheme.titleMedium)
                    ?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: colorScheme.onSurface,
                      letterSpacing: -0.2,
                    ),
            maxLines: widget.isCompact ? 1 : 2,
            overflow: TextOverflow.ellipsis,
          ),

          if (widget.showModuleName &&
              widget.grammar.moduleName != null &&
              widget.grammar.moduleName!.isNotEmpty) ...[
            SizedBox(
              height: widget.isCompact ? AppDimens.spaceXXS : AppDimens.spaceXS,
            ),
            Row(
              children: [
                Icon(
                  Icons.book_outlined,
                  size: AppDimens.iconXS,
                  color: colorScheme.primary,
                ),
                const SizedBox(width: AppDimens.spaceXS),
                Expanded(
                  child: Text(
                    widget.grammar.moduleName!,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: colorScheme.primary,
                      fontWeight: FontWeight.w500,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
          ],

          if (!widget.isCompact) ...[
            const SizedBox(height: AppDimens.spaceXS),
            _buildGrammarMetadata(theme, colorScheme),
          ],
        ],
      ),
    );
  }

  Widget _buildGrammarMetadata(ThemeData theme, ColorScheme colorScheme) {
    return Row(
      children: [
        Icon(
          Icons.rule_rounded,
          size: AppDimens.iconXS,
          color: colorScheme.onSurfaceVariant,
        ),
        const SizedBox(width: AppDimens.spaceXS),
        Text(
          'Grammar Rule',
          style: theme.textTheme.bodySmall?.copyWith(
            color: colorScheme.onSurfaceVariant,
            fontWeight: FontWeight.w400,
          ),
        ),
        const Spacer(),
        if (widget.grammar.createdAt != null) ...[
          Icon(
            Icons.access_time_rounded,
            size: AppDimens.iconXS,
            color: colorScheme.onSurfaceVariant,
          ),
          const SizedBox(width: AppDimens.spaceXS),
          Text(
            _formatDate(widget.grammar.createdAt!),
            style: theme.textTheme.bodySmall?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildActionIndicator(ColorScheme colorScheme) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      curve: Curves.easeInOut,
      padding: EdgeInsets.all(
        widget.isCompact ? AppDimens.paddingXS : AppDimens.paddingS,
      ),
      decoration: BoxDecoration(
        color: _isPressed || _isHovered
            ? colorScheme.primary.withValues(alpha: 0.12)
            : colorScheme.surfaceContainerHighest,
        shape: BoxShape.circle,
      ),
      child: Icon(
        Icons.arrow_forward_ios_rounded,
        size: widget.isCompact ? AppDimens.iconXS : AppDimens.iconS,
        color: _isPressed || _isHovered
            ? colorScheme.primary
            : colorScheme.onSurfaceVariant,
      ),
    );
  }

  Widget _buildContent(ThemeData theme, ColorScheme colorScheme) {
    return MouseRegion(
      onEnter: _handleHoverEnter,
      onExit: _handleHoverExit,
      child: GestureDetector(
        onTapDown: _handleTapDown,
        onTapUp: _handleTapUp,
        onTapCancel: _handleTapCancel,
        onTap: widget.onTap,
        child: SLCard(
          type: SLCardType.filled,
          padding: EdgeInsets.all(
            widget.isCompact ? AppDimens.paddingM : AppDimens.paddingL,
          ),
          margin: EdgeInsets.zero,
          backgroundColor: colorScheme.surfaceContainerLowest,
          elevation: _isPressed
              ? AppDimens.elevationM
              : (_isHovered ? AppDimens.elevationS : AppDimens.elevationXS),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
            side: BorderSide(
              color: _isPressed
                  ? colorScheme.primary.withValues(alpha: 0.4)
                  : (_isHovered
                        ? colorScheme.primary.withValues(alpha: 0.2)
                        : colorScheme.outlineVariant.withValues(alpha: 0.5)),
              width: _isPressed ? 1.5 : 1.0,
            ),
          ),
          child: Row(
            children: [
              _buildGrammarIcon(colorScheme),
              SizedBox(
                width: widget.isCompact ? AppDimens.spaceM : AppDimens.spaceL,
              ),
              _buildGrammarContent(theme, colorScheme),
              const SizedBox(width: AppDimens.spaceM),
              _buildActionIndicator(colorScheme),
            ],
          ),
        ),
      ),
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
      animation: Listenable.merge([
        _slideController,
        _pressController,
        _hoverController,
      ]),
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

// Extension for grammar list item variations
extension ModernGrammarListItemVariations on ModernGrammarListItem {
  /// Creates a compact version for dense lists
  static Widget compact({
    required GrammarSummary grammar,
    required VoidCallback onTap,
    bool showModuleName = false,
  }) {
    return ModernGrammarListItem(
      grammar: grammar,
      onTap: onTap,
      showModuleName: showModuleName,
      isCompact: true,
      showAnimation: false,
    );
  }

  /// Creates a detailed version with full information
  static Widget detailed({
    required GrammarSummary grammar,
    required VoidCallback onTap,
    Duration animationDelay = Duration.zero,
    bool showModuleName = true,
  }) {
    return ModernGrammarListItem(
      grammar: grammar,
      onTap: onTap,
      animationDelay: animationDelay,
      showModuleName: showModuleName,
      isCompact: false,
      showAnimation: true,
    );
  }

  /// Creates a static version without animations for performance
  static Widget static({
    required GrammarSummary grammar,
    required VoidCallback onTap,
    bool showModuleName = true,
    bool isCompact = false,
  }) {
    return ModernGrammarListItem(
      grammar: grammar,
      onTap: onTap,
      showModuleName: showModuleName,
      isCompact: isCompact,
      showAnimation: false,
    );
  }
}
