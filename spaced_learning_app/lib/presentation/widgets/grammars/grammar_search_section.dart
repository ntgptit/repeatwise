// lib/presentation/widgets/grammars/grammar_search_section.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class GrammarSearchSection extends StatefulWidget {
  final TextEditingController controller;
  final FocusNode focusNode;
  final ValueChanged<String> onChanged;
  final VoidCallback onClear;
  final bool isSearching;
  final String hintText;
  final bool enabled;

  const GrammarSearchSection({
    super.key,
    required this.controller,
    required this.focusNode,
    required this.onChanged,
    required this.onClear,
    this.isSearching = false,
    this.hintText = 'Search grammar rules...',
    this.enabled = true,
  });

  @override
  State<GrammarSearchSection> createState() => _GrammarSearchSectionState();
}

class _GrammarSearchSectionState extends State<GrammarSearchSection>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _scaleAnimation;

  bool _hasFocus = false;

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    widget.focusNode.addListener(_handleFocusChange);
  }

  @override
  void dispose() {
    widget.focusNode.removeListener(_handleFocusChange);
    _animationController.dispose();
    super.dispose();
  }

  void _initializeAnimations() {
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );

    _scaleAnimation = Tween<double>(begin: 1.0, end: 1.02).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.easeInOut),
    );
  }

  void _handleFocusChange() {
    if (!mounted) return;

    final currentFocus = widget.focusNode.hasFocus;
    if (_hasFocus == currentFocus) return;

    setState(() => _hasFocus = currentFocus);

    if (currentFocus) {
      _animationController.forward();
      return;
    }

    _animationController.reverse();
  }

  void _handleClear() {
    widget.controller.clear();
    widget.onChanged('');
    widget.onClear();
  }

  Widget _buildPrefixIcon(ColorScheme colorScheme) {
    return AnimatedSwitcher(
      duration: const Duration(milliseconds: 250),
      child: widget.isSearching
          ? Container(
              key: const ValueKey('loading'),
              width: AppDimens.iconM,
              height: AppDimens.iconM,
              margin: const EdgeInsets.all(AppDimens.paddingS),
              child: CircularProgressIndicator(
                strokeWidth: 2.0,
                valueColor: AlwaysStoppedAnimation<Color>(colorScheme.primary),
              ),
            )
          : Icon(
              key: const ValueKey('search'),
              Icons.search_rounded,
              color: _hasFocus
                  ? colorScheme.primary
                  : colorScheme.onSurfaceVariant,
              size: AppDimens.iconM,
            ),
    );
  }

  Widget? _buildSuffixIcon(ColorScheme colorScheme) {
    final hasText = widget.controller.text.isNotEmpty;

    if (!hasText) return null;

    return AnimatedScale(
      scale: hasText ? 1.0 : 0.0,
      duration: const Duration(milliseconds: 200),
      child: IconButton(
        icon: Icon(
          Icons.clear_rounded,
          color: colorScheme.onSurfaceVariant,
          size: AppDimens.iconM,
        ),
        onPressed: widget.enabled ? _handleClear : null,
        tooltip: 'Clear search',
        splashRadius: AppDimens.iconM,
      ),
    );
  }

  InputDecoration _buildInputDecoration(
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return InputDecoration(
      hintText: widget.hintText,
      hintStyle: theme.textTheme.bodyLarge?.copyWith(
        color: colorScheme.onSurfaceVariant.withValues(
          alpha: AppDimens.opacityHigh,
        ),
      ),
      prefixIcon: _buildPrefixIcon(colorScheme),
      suffixIcon: _buildSuffixIcon(colorScheme),
      filled: true,
      fillColor: _hasFocus
          ? colorScheme.surfaceContainerLowest
          : colorScheme.surfaceContainer,
      contentPadding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingL,
        vertical: AppDimens.paddingM,
      ),
      border: _buildBorder(colorScheme, isEnabled: false),
      enabledBorder: _buildBorder(colorScheme, isEnabled: true),
      focusedBorder: _buildBorder(colorScheme, isFocused: true),
      disabledBorder: _buildBorder(colorScheme, isDisabled: true),
    );
  }

  OutlineInputBorder _buildBorder(
    ColorScheme colorScheme, {
    bool isEnabled = false,
    bool isFocused = false,
    bool isDisabled = false,
  }) {
    Color borderColor;
    double borderWidth;

    if (isDisabled) {
      borderColor = colorScheme.onSurface.withValues(
        alpha: AppDimens.opacityDisabled,
      );
      borderWidth = 1.0;
    } else if (isFocused) {
      borderColor = colorScheme.primary;
      borderWidth = 2.0;
    } else if (isEnabled) {
      borderColor = colorScheme.outlineVariant.withValues(
        alpha: AppDimens.opacityHigh,
      );
      borderWidth = 1.0;
    } else {
      borderColor = Colors.transparent;
      borderWidth = 0.0;
    }

    return OutlineInputBorder(
      borderRadius: BorderRadius.circular(AppDimens.radiusXL),
      borderSide: BorderSide(color: borderColor, width: borderWidth),
    );
  }

  Widget _buildSearchField(ThemeData theme, ColorScheme colorScheme) {
    return AnimatedBuilder(
      animation: _animationController,
      builder: (context, child) {
        return Transform.scale(
          scale: _scaleAnimation.value,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(AppDimens.radiusXL),
              boxShadow: _hasFocus
                  ? [
                      BoxShadow(
                        color: colorScheme.primary.withValues(alpha: 0.1),
                        blurRadius: AppDimens.shadowRadiusL,
                        offset: const Offset(0, AppDimens.shadowOffsetS),
                      ),
                    ]
                  : null,
            ),
            child: TextField(
              controller: widget.controller,
              focusNode: widget.focusNode,
              enabled: widget.enabled,
              onChanged: widget.onChanged,
              style: theme.textTheme.bodyLarge?.copyWith(
                color: colorScheme.onSurface,
              ),
              decoration: _buildInputDecoration(theme, colorScheme),
              textInputAction: TextInputAction.search,
              keyboardType: TextInputType.text,
              textCapitalization: TextCapitalization.none,
            ),
          ),
        );
      },
    );
  }

  Widget _buildSearchStats(ThemeData theme, ColorScheme colorScheme) {
    if (!widget.isSearching || widget.controller.text.isEmpty) {
      return const SizedBox.shrink();
    }

    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingL,
        vertical: AppDimens.paddingS,
      ),
      child: Row(
        children: [
          Icon(
            Icons.search_rounded,
            size: AppDimens.iconS,
            color: colorScheme.onSurfaceVariant,
          ),
          const SizedBox(width: AppDimens.spaceS),
          Text(
            'Searching for "${widget.controller.text}"',
            style: theme.textTheme.bodySmall?.copyWith(
              color: colorScheme.onSurfaceVariant,
              fontStyle: FontStyle.italic,
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Container(
      decoration: BoxDecoration(
        color: colorScheme.surface,
        border: Border(
          bottom: BorderSide(
            color: colorScheme.outlineVariant.withValues(alpha: 0.5),
            width: 1.0,
          ),
        ),
      ),
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(AppDimens.paddingL),
            child: _buildSearchField(theme, colorScheme),
          ),
          _buildSearchStats(theme, colorScheme),
        ],
      ),
    );
  }
}
