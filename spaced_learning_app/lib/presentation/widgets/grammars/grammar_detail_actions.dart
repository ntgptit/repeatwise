import 'package:flutter/material.dart';

import '../../../core/theme/app_dimens.dart';
import '../common/app_button.dart';
import '../common/app_card.dart';

class GrammarDetailActions extends StatefulWidget {
  final String moduleId;
  final VoidCallback? onBackToGrammarList;
  final VoidCallback? onBackToModule;
  final VoidCallback? onBookmark;
  final VoidCallback? onShare;
  final bool isBookmarked;
  final bool showAnimation;

  const GrammarDetailActions({
    super.key,
    required this.moduleId,
    this.onBackToGrammarList,
    this.onBackToModule,
    this.onBookmark,
    this.onShare,
    this.isBookmarked = false,
    this.showAnimation = true,
  });

  @override
  State<GrammarDetailActions> createState() => _GrammarDetailActionsState();
}

class _GrammarDetailActionsState extends State<GrammarDetailActions>
    with TickerProviderStateMixin {
  late AnimationController _fadeController;
  late Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    // Only initialize animations if showAnimation is true
    if (widget.showAnimation) {
      _initializeAnimations();
      _startAnimations();
    }
  }

  @override
  void dispose() {
    // Only dispose controller if it was initialized
    if (widget.showAnimation) {
      _fadeController.dispose();
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _fadeController, curve: Curves.easeOut));
  }

  void _startAnimations() {
    // Wait a bit before starting the animation for a smoother visual effect
    Future.delayed(const Duration(milliseconds: 300), () {
      // Check if the widget is still mounted before interacting with the controller
      if (mounted) {
        _fadeController.forward();
      }
    });
  }

  // Helper widget/method to build the header for each section
  Widget _buildSectionHeader({
    required IconData icon,
    required String title,
    required Color color,
    required ThemeData theme,
  }) {
    return Row(
      children: [
        Icon(icon, color: color, size: AppDimens.iconM),
        const SizedBox(width: AppDimens.spaceM),
        Text(
          title,
          style: theme.textTheme.titleMedium?.copyWith(
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
      ],
    );
  }

  Widget _buildQuickActionsSection(ThemeData theme, ColorScheme colorScheme) {
    final actions = [
      _QuickAction(
        icon: widget.isBookmarked
            ? Icons.bookmark_rounded
            : Icons.bookmark_border_rounded,
        label: widget.isBookmarked ? 'Bookmarked' : 'Bookmark',
        onPressed: widget.onBookmark,
        color: colorScheme.secondary,
        // Main color for the button
        isActive: widget.isBookmarked,
      ),
      _QuickAction(
        icon: Icons.share_rounded,
        label: 'Share',
        onPressed: widget.onShare,
        color: colorScheme.tertiary, // Main color for the button
      ),
    ];

    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide =
            constraints.maxWidth > 600; // Breakpoint for wide screens

        // Logic for wide screens
        if (isWide) {
          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildSectionHeader(
                icon: Icons.flash_on_rounded,
                title: 'Quick Actions',
                color: colorScheme.primary,
                theme: theme,
              ),
              const SizedBox(height: AppDimens.spaceL),
              Row(
                // Use Row and Expanded for wide screens to distribute buttons evenly
                children: actions
                    .asMap()
                    .entries
                    .map(
                      (entry) => Expanded(
                        child: Padding(
                          padding: EdgeInsets.only(
                            // No horizontal padding for the first and last items for even alignment
                            left: entry.key == 0 ? 0 : AppDimens.spaceS / 2,
                            right: entry.key == actions.length - 1
                                ? 0
                                : AppDimens.spaceS / 2,
                          ),
                          child: _buildQuickActionButton(
                            entry.value,
                            theme,
                            colorScheme,
                          ),
                        ),
                      ),
                    )
                    .toList(),
              ),
            ],
          );
        }

        // Default logic for narrow screens (if not wide)
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSectionHeader(
              icon: Icons.flash_on_rounded,
              title: 'Quick Actions',
              color: colorScheme.primary,
              theme: theme,
            ),
            const SizedBox(height: AppDimens.spaceL),
            Wrap(
              // Use Wrap for narrow screens so buttons flow to the next line
              spacing: AppDimens.spaceM,
              // Horizontal space between buttons
              runSpacing: AppDimens.spaceM,
              // Vertical space between lines of buttons
              alignment: WrapAlignment.start,
              children: actions
                  .map(
                    (action) => SizedBox(
                      // Calculate width for two buttons per row (if space allows)
                      // Subtract spacing for more accurate calculation
                      width:
                          (constraints.maxWidth - AppDimens.spaceM * (2 - 1)) /
                              2 -
                          1,
                      // (totalWidth - totalSpacing) / itemsPerRow - precisionFix
                      child: _buildQuickActionButton(
                        action,
                        theme,
                        colorScheme,
                      ),
                    ),
                  )
                  .toList(),
            ),
          ],
        );
      },
    );
  }

  Widget _buildQuickActionButton(
    _QuickAction action,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    // Determine button type without else
    SLButtonType buttonType = SLButtonType.outline;
    if (action.isActive) {
      buttonType = SLButtonType.primary;
    }

    // Determine background color without else
    Color? buttonBackgroundColor; // Default is null
    if (action.isActive) {
      buttonBackgroundColor = action.color.withOpacity(0.15);
    }

    return SLButton(
      text: action.label,
      type: buttonType,
      prefixIcon: action.icon,
      onPressed: action.onPressed,
      size: SLButtonSize.medium,
      backgroundColor: buttonBackgroundColor,
      // Your SLButton might need further conditional styling for text color
      // depending on its type (primary vs outline) if not handled internally.
      // For example:
      // textColor: action.isActive ? action.color : (buttonType == SLButtonType.outline ? action.color : colorScheme.onPrimary),
    );
  }

  Widget _buildNavigationSection(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionHeader(
          icon: Icons.navigation_rounded,
          title: 'Continue Learning',
          color: colorScheme.secondary,
          theme: theme,
        ),
        const SizedBox(height: AppDimens.spaceS),
        Text(
          'Explore more grammar rules or return to your module.',
          style: theme.textTheme.bodySmall?.copyWith(
            color: colorScheme.onSurfaceVariant,
          ),
        ),
        const SizedBox(height: AppDimens.spaceL),
        _buildNavigationButtons(theme, colorScheme),
      ],
    );
  }

  Widget _buildNavigationButtons(ThemeData theme, ColorScheme colorScheme) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide = constraints.maxWidth > 600;
        // Navigation buttons
        final moreGrammarButton = SLButton(
          text: 'More Grammar',
          type: SLButtonType.outline,
          prefixIcon: Icons.list_alt_rounded,
          onPressed: widget.onBackToGrammarList,
          size: SLButtonSize.medium,
        );
        final backToModuleButton = SLButton(
          text: 'Back to Module',
          type: SLButtonType.secondary,
          // Could be primary or secondary based on design
          prefixIcon: Icons.arrow_back_rounded,
          onPressed: widget.onBackToModule,
          size: SLButtonSize.medium,
        );

        // Layout for wide screens
        if (isWide) {
          return Row(
            children: [
              Expanded(child: moreGrammarButton),
              const SizedBox(width: AppDimens.spaceM),
              Expanded(child: backToModuleButton),
            ],
          );
        }

        // Default layout for narrow screens (if not wide)
        return Column(
          // Make buttons take full width on narrow screens
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            moreGrammarButton,
            const SizedBox(height: AppDimens.spaceM),
            backToModuleButton,
          ],
        );
      },
    );
  }

  Widget _buildContent(ThemeData theme, ColorScheme colorScheme) {
    return SingleChildScrollView(
      // Allows scrolling if content overflows
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingM),
        child: Column(
          children: [
            SLCard(
              // Card for Quick Actions
              type: SLCardType.filled,
              padding: const EdgeInsets.all(AppDimens.paddingL),
              backgroundColor: colorScheme.surfaceContainer,
              elevation: AppDimens.elevationS,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                side: BorderSide(
                  color: colorScheme.outlineVariant.withOpacity(0.5),
                ),
              ),
              child: _buildQuickActionsSection(theme, colorScheme),
            ),
            const SizedBox(height: AppDimens.spaceL), // Spacing between cards
            SLCard(
              // Card for Navigation
              type: SLCardType.outlined,
              // Or filled, depending on design
              padding: const EdgeInsets.all(AppDimens.paddingL),
              backgroundColor: colorScheme.surfaceContainerLowest,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                side: BorderSide(color: colorScheme.secondary.withOpacity(0.3)),
              ),
              child: _buildNavigationSection(theme, colorScheme),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    // Build the main content first
    Widget content = _buildContent(theme, colorScheme);

    // If animation is enabled, wrap the content in FadeTransition
    if (widget.showAnimation) {
      return FadeTransition(opacity: _fadeAnimation, child: content);
    }

    // If animation is not enabled, return the content directly
    return content;
  }
}

// Helper class for Quick Actions, remains good
class _QuickAction {
  final IconData icon;
  final String label;
  final VoidCallback? onPressed;
  final Color color; // This color can be used to theme the button when active
  final bool isActive;

  const _QuickAction({
    required this.icon,
    required this.label,
    this.onPressed,
    required this.color,
    this.isActive = false,
  });
}
