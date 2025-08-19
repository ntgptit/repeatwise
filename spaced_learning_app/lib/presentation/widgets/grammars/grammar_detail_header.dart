// lib/presentation/widgets/grammars/grammar_detail_header.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';

class GrammarDetailHeader extends StatefulWidget {
  final GrammarDetail grammar;
  final bool showAnimation;

  const GrammarDetailHeader({
    super.key,
    required this.grammar,
    this.showAnimation = true,
  });

  @override
  State<GrammarDetailHeader> createState() => _GrammarDetailHeaderState();
}

class _GrammarDetailHeaderState extends State<GrammarDetailHeader>
    with TickerProviderStateMixin {
  late AnimationController _slideController;
  late AnimationController _fadeController;
  late AnimationController _scaleController;
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
      _scaleController.dispose();
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _slideController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    );

    _fadeController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _scaleController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0.0, -0.3), end: Offset.zero).animate(
          CurvedAnimation(parent: _slideController, curve: Curves.elasticOut),
        );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _fadeController, curve: Curves.easeOut));

    _scaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _scaleController, curve: Curves.elasticOut),
    );
  }

  void _startAnimations() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;

      _fadeController.forward();

      Future.delayed(const Duration(milliseconds: 100), () {
        if (mounted) {
          _slideController.forward();
        }
      });

      Future.delayed(const Duration(milliseconds: 200), () {
        if (mounted) {
          _scaleController.forward();
        }
      });
    });
  }

  Widget _buildGrammarIcon(ColorScheme colorScheme) {
    return Container(
      width: AppDimens.avatarSizeXXL,
      height: AppDimens.avatarSizeXXL,
      decoration: BoxDecoration(
        gradient: RadialGradient(
          colors: [
            colorScheme.primary,
            colorScheme.secondary.withValues(alpha: 0.8),
            colorScheme.tertiary.withValues(alpha: 0.6),
          ],
          stops: const [0.0, 0.6, 1.0],
        ),
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        boxShadow: [
          BoxShadow(
            color: colorScheme.primary.withValues(alpha: 0.4),
            blurRadius: AppDimens.shadowRadiusL * 1.5,
            offset: const Offset(0, AppDimens.shadowOffsetM),
          ),
          BoxShadow(
            color: colorScheme.secondary.withValues(alpha: 0.2),
            blurRadius: AppDimens.shadowRadiusL * 2,
            offset: const Offset(0, AppDimens.shadowOffsetM * 1.5),
            spreadRadius: 2,
          ),
        ],
      ),
      child: Icon(
        Icons.auto_stories_rounded,
        color: colorScheme.onPrimary,
        size: AppDimens.iconXXXL,
      ),
    );
  }

  Widget _buildTitleSection(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Main Grammar Pattern
        Text(
          widget.grammar.grammarPattern,
          style: theme.textTheme.headlineLarge?.copyWith(
            fontWeight: FontWeight.w900,
            color: colorScheme.onSurface,
            letterSpacing: -0.8,
            height: 1.1,
          ),
          maxLines: 3,
          overflow: TextOverflow.ellipsis,
        ),

        if (widget.grammar.moduleName?.isNotEmpty == true) ...[
          const SizedBox(height: AppDimens.spaceM),
          _buildModuleChip(theme, colorScheme),
        ],

        const SizedBox(height: AppDimens.spaceL),
        _buildGrammarTypeIndicator(theme, colorScheme),
      ],
    );
  }

  Widget _buildModuleChip(ThemeData theme, ColorScheme colorScheme) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingL,
        vertical: AppDimens.paddingS,
      ),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            colorScheme.secondaryContainer,
            colorScheme.secondaryContainer.withValues(alpha: 0.8),
          ],
        ),
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        border: Border.all(
          color: colorScheme.secondary.withValues(alpha: 0.3),
          width: 1.5,
        ),
        boxShadow: [
          BoxShadow(
            color: colorScheme.secondary.withValues(alpha: 0.1),
            blurRadius: AppDimens.shadowRadiusS,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            Icons.school_rounded,
            size: AppDimens.iconS,
            color: colorScheme.onSecondaryContainer,
          ),
          const SizedBox(width: AppDimens.spaceS),
          Text(
            'Module: ${widget.grammar.moduleName}',
            style: theme.textTheme.labelLarge?.copyWith(
              color: colorScheme.onSecondaryContainer,
              fontWeight: FontWeight.w700,
              letterSpacing: 0.5,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGrammarTypeIndicator(ThemeData theme, ColorScheme colorScheme) {
    final hasDefinition = widget.grammar.definition?.isNotEmpty == true;
    final hasStructure = widget.grammar.structure?.isNotEmpty == true;
    final hasExamples = widget.grammar.examples?.isNotEmpty == true;

    final indicators = <_GrammarIndicator>[];

    if (hasDefinition) {
      indicators.add(
        _GrammarIndicator(
          icon: Icons.article_outlined,
          label: 'Definition',
          color: colorScheme.primary,
        ),
      );
    }

    if (hasStructure) {
      indicators.add(
        _GrammarIndicator(
          icon: Icons.architecture_outlined,
          label: 'Structure',
          color: colorScheme.secondary,
        ),
      );
    }

    if (hasExamples) {
      indicators.add(
        _GrammarIndicator(
          icon: Icons.format_quote_rounded,
          label: 'Examples',
          color: colorScheme.tertiary,
        ),
      );
    }

    if (indicators.isEmpty) return const SizedBox.shrink();

    return Wrap(
      spacing: AppDimens.spaceM,
      runSpacing: AppDimens.spaceS,
      children: indicators
          .map((indicator) => _buildIndicatorChip(indicator, theme))
          .toList(),
    );
  }

  Widget _buildIndicatorChip(_GrammarIndicator indicator, ThemeData theme) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingM,
        vertical: AppDimens.paddingXS,
      ),
      decoration: BoxDecoration(
        color: indicator.color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        border: Border.all(color: indicator.color.withValues(alpha: 0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(indicator.icon, size: AppDimens.iconXS, color: indicator.color),
          const SizedBox(width: AppDimens.spaceXS),
          Text(
            indicator.label,
            style: theme.textTheme.labelSmall?.copyWith(
              color: indicator.color,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoCard(ThemeData theme, ColorScheme colorScheme) {
    return SLCard(
      type: SLCardType.filled,
      padding: const EdgeInsets.all(AppDimens.paddingL),
      backgroundColor: colorScheme.primaryContainer.withValues(alpha: 0.7),
      elevation: AppDimens.elevationNone,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        side: BorderSide(
          color: colorScheme.primary.withValues(alpha: 0.2),
          width: 1.5,
        ),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(AppDimens.paddingM),
            decoration: BoxDecoration(
              color: colorScheme.primary.withValues(alpha: 0.15),
              shape: BoxShape.circle,
            ),
            child: Icon(
              Icons.lightbulb_outline_rounded,
              color: colorScheme.onPrimaryContainer,
              size: AppDimens.iconL,
            ),
          ),
          const SizedBox(width: AppDimens.spaceL),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Grammar Learning Insight',
                  style: theme.textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w700,
                    color: colorScheme.onPrimaryContainer,
                  ),
                ),
                const SizedBox(height: AppDimens.spaceS),
                Text(
                  'Understanding this grammar pattern will enhance your communication skills and language comprehension. Study the structure, practice with examples, and apply in real conversations.',
                  style: theme.textTheme.bodyMedium?.copyWith(
                    color: colorScheme.onPrimaryContainer,
                    height: 1.6,
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
            if (widget.showAnimation)
              AnimatedBuilder(
                animation: _scaleController,
                builder: (context, child) {
                  return Transform.scale(
                    scale: _scaleAnimation.value,
                    child: _buildGrammarIcon(colorScheme),
                  );
                },
              )
            else
              _buildGrammarIcon(colorScheme),
            const SizedBox(width: AppDimens.spaceXL),
            Expanded(child: _buildTitleSection(theme, colorScheme)),
          ],
        ),
        const SizedBox(height: AppDimens.spaceXXL),
        _buildInfoCard(theme, colorScheme),
      ],
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
      animation: Listenable.merge([_slideController, _fadeController]),
      builder: (context, child) {
        return SlideTransition(
          position: _slideAnimation,
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: _buildContent(theme, colorScheme),
          ),
        );
      },
    );
  }
}

// Helper class for grammar indicators
class _GrammarIndicator {
  final IconData icon;
  final String label;
  final Color color;

  const _GrammarIndicator({
    required this.icon,
    required this.label,
    required this.color,
  });
}
