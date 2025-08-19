// lib/presentation/widgets/modules/grammar/detail/grammar_detail_content.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/grammar.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';

class GrammarDetailContent extends StatefulWidget {
  final GrammarDetail grammar;
  final bool showAnimation;
  final Duration animationDelay;

  const GrammarDetailContent({
    super.key,
    required this.grammar,
    this.showAnimation = true,
    this.animationDelay = Duration.zero,
  });

  @override
  State<GrammarDetailContent> createState() => _GrammarDetailContentState();
}

class _GrammarDetailContentState extends State<GrammarDetailContent>
    with TickerProviderStateMixin {
  late List<AnimationController> _sectionControllers;
  late List<Animation<Offset>> _slideAnimations;
  late List<Animation<double>> _fadeAnimations;

  final List<_GrammarSection> _sections = [];

  @override
  void initState() {
    super.initState();
    _buildSections();
    if (widget.showAnimation) {
      _initializeAnimations();
      _startAnimations();
    }
  }

  @override
  void dispose() {
    if (widget.showAnimation) {
      for (final controller in _sectionControllers) {
        controller.dispose();
      }
    }
    super.dispose();
  }

  void _buildSections() {
    _sections.clear();

    // Definition Section
    if (widget.grammar.definition?.isNotEmpty == true) {
      _sections.add(
        _GrammarSection(
          title: 'Definition',
          content: widget.grammar.definition!,
          icon: Icons.article_outlined,
          color: _SectionColor.primary,
        ),
      );
    }

    // Structure Section
    if (widget.grammar.structure?.isNotEmpty == true) {
      _sections.add(
        _GrammarSection(
          title: 'Structure',
          content: widget.grammar.structure!,
          icon: Icons.architecture_outlined,
          color: _SectionColor.secondary,
          isStructure: true,
        ),
      );
    }

    // Conjugation Section
    if (widget.grammar.conjugation?.isNotEmpty == true) {
      _sections.add(
        _GrammarSection(
          title: 'Conjugation',
          content: widget.grammar.conjugation!,
          icon: Icons.compare_arrows_rounded,
          color: _SectionColor.tertiary,
          isConjugation: true,
        ),
      );
    }

    // Examples Section
    if (widget.grammar.examples?.isNotEmpty == true) {
      _sections.add(
        _GrammarSection(
          title: 'Examples',
          content: widget.grammar.examples!,
          icon: Icons.format_quote_rounded,
          color: _SectionColor.primary,
          isList: true,
        ),
      );
    }

    // Common Phrases Section
    if (widget.grammar.commonPhrases?.isNotEmpty == true) {
      _sections.add(
        _GrammarSection(
          title: 'Common Phrases',
          content: widget.grammar.commonPhrases!,
          icon: Icons.chat_bubble_outline_rounded,
          color: _SectionColor.tertiary,
          isList: true,
        ),
      );
    }

    // Notes Section
    if (widget.grammar.notes?.isNotEmpty == true) {
      _sections.add(
        _GrammarSection(
          title: 'Usage Notes',
          content: widget.grammar.notes!,
          icon: Icons.lightbulb_outline_rounded,
          color: _SectionColor.secondary,
        ),
      );
    }
  }

  void _initializeAnimations() {
    _sectionControllers = List.generate(
      _sections.length,
      (index) => AnimationController(
        vsync: this,
        duration: const Duration(milliseconds: 600),
      ),
    );

    _slideAnimations = _sectionControllers.map((controller) {
      return Tween<Offset>(
        begin: const Offset(0.3, 0.0),
        end: Offset.zero,
      ).animate(
        CurvedAnimation(parent: controller, curve: Curves.easeOutCubic),
      );
    }).toList();

    _fadeAnimations = _sectionControllers.map((controller) {
      return Tween<double>(
        begin: 0.0,
        end: 1.0,
      ).animate(CurvedAnimation(parent: controller, curve: Curves.easeOut));
    }).toList();
  }

  void _startAnimations() {
    for (int i = 0; i < _sectionControllers.length; i++) {
      Future.delayed(
        widget.animationDelay + Duration(milliseconds: i * 150),
        () {
          if (mounted) {
            _sectionControllers[i].forward();
          }
        },
      );
    }
  }

  Color _getSectionColor(ColorScheme colorScheme, _SectionColor color) {
    switch (color) {
      case _SectionColor.primary:
        return colorScheme.primary;
      case _SectionColor.secondary:
        return colorScheme.secondary;
      case _SectionColor.tertiary:
        return colorScheme.tertiary;
    }
  }

  Widget _buildSectionHeader(
    _GrammarSection section,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final sectionColor = _getSectionColor(colorScheme, section.color);

    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingM),
          decoration: BoxDecoration(
            color: sectionColor.withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
          ),
          child: Icon(section.icon, size: AppDimens.iconL, color: sectionColor),
        ),
        const SizedBox(width: AppDimens.spaceL),
        Expanded(
          child: Text(
            section.title,
            style: theme.textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: sectionColor,
              letterSpacing: -0.3,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildSectionContent(
    _GrammarSection section,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    if (section.isStructure) {
      return _buildStructureContent(section.content, theme, colorScheme);
    }

    if (section.isConjugation) {
      return _buildConjugationContent(section.content, theme, colorScheme);
    }

    if (section.isList) {
      return _buildListContent(section.content, theme, colorScheme);
    }

    return _buildTextContent(section.content, theme, colorScheme);
  }

  Widget _buildTextContent(
    String content,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return Text(
      content,
      style: theme.textTheme.bodyLarge?.copyWith(
        color: colorScheme.onSurfaceVariant,
        height: 1.6,
        letterSpacing: 0.1,
      ),
    );
  }

  Widget _buildListContent(
    String content,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final items = content
        .split('\n')
        .map((line) => line.trim())
        .where((line) => line.isNotEmpty)
        .toList();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: items.map((item) {
        // Remove bullet indicators if present
        String cleanItem = item;
        if (cleanItem.startsWith('• ') ||
            cleanItem.startsWith('* ') ||
            cleanItem.startsWith('- ')) {
          cleanItem = cleanItem.substring(2);
        }

        return Padding(
          padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                width: AppDimens.iconXS,
                height: AppDimens.iconXS,
                margin: const EdgeInsets.only(top: AppDimens.spaceS),
                decoration: BoxDecoration(
                  color: colorScheme.primary,
                  shape: BoxShape.circle,
                ),
              ),
              const SizedBox(width: AppDimens.spaceM),
              Expanded(
                child: Text(
                  cleanItem,
                  style: theme.textTheme.bodyLarge?.copyWith(
                    color: colorScheme.onSurfaceVariant,
                    height: 1.5,
                  ),
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }

  Widget _buildStructureContent(
    String content,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final lines = content
        .split('\n')
        .map((line) => line.trim())
        .where((line) => line.isNotEmpty)
        .toList();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: lines.map((line) {
        // Check if line contains a pattern definition
        if (line.contains(':') || line.contains('=')) {
          final parts = line.contains(':') ? line.split(':') : line.split('=');

          if (parts.length >= 2) {
            final label = parts[0].trim();
            final value = parts.sublist(1).join(':').trim();

            return Container(
              margin: const EdgeInsets.only(bottom: AppDimens.spaceM),
              padding: const EdgeInsets.all(AppDimens.paddingM),
              decoration: BoxDecoration(
                color: colorScheme.secondaryContainer.withValues(alpha: 0.3),
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                border: Border.all(
                  color: colorScheme.secondary.withValues(alpha: 0.3),
                ),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 2,
                    child: Text(
                      label,
                      style: theme.textTheme.titleMedium?.copyWith(
                        color: colorScheme.secondary,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  const SizedBox(width: AppDimens.spaceM),
                  Expanded(
                    flex: 3,
                    child: Text(
                      value,
                      style: theme.textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ),
                ],
              ),
            );
          }
        }

        // Regular line
        return Padding(
          padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
          child: Text(
            line,
            style: theme.textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurfaceVariant,
              height: 1.5,
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildConjugationContent(
    String content,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final lines = content
        .split('\n')
        .map((line) => line.trim())
        .where((line) => line.isNotEmpty)
        .toList();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: lines.map((line) {
        List<String> parts = [];
        if (line.contains(':')) {
          parts = line.split(':');
        } else if (line.contains(' - ')) {
          parts = line.split(' - ');
        } else if (line.contains('=')) {
          parts = line.split('=');
        }

        if (parts.length >= 2) {
          return Container(
            margin: const EdgeInsets.only(bottom: AppDimens.spaceM),
            padding: const EdgeInsets.all(AppDimens.paddingM),
            decoration: BoxDecoration(
              color: colorScheme.tertiaryContainer.withValues(alpha: 0.3),
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
              border: Border.all(
                color: colorScheme.tertiary.withValues(alpha: 0.3),
              ),
            ),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  flex: 1,
                  child: Text(
                    parts[0].trim(),
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: colorScheme.tertiary,
                    ),
                  ),
                ),
                const SizedBox(width: AppDimens.spaceM),
                Expanded(
                  flex: 2,
                  child: Text(
                    parts.sublist(1).join(':').trim(),
                    style: theme.textTheme.bodyLarge?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                    ),
                  ),
                ),
              ],
            ),
          );
        }

        return Padding(
          padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
          child: Text(
            line,
            style: theme.textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildSection(
    _GrammarSection section,
    int index,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    final sectionColor = _getSectionColor(colorScheme, section.color);

    final Widget sectionWidget = Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionHeader(section, theme, colorScheme),
        const SizedBox(height: AppDimens.spaceL),
        SLCard(
          type: SLCardType.outlined,
          padding: const EdgeInsets.all(AppDimens.paddingL),
          backgroundColor: colorScheme.surfaceContainerLowest,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
            side: BorderSide(color: sectionColor.withValues(alpha: 0.2)),
          ),
          child: _buildSectionContent(section, theme, colorScheme),
        ),
        const SizedBox(height: AppDimens.spaceXXL),
      ],
    );

    if (!widget.showAnimation) {
      return sectionWidget;
    }

    return AnimatedBuilder(
      animation: Listenable.merge([_sectionControllers[index]]),
      builder: (context, child) {
        return SlideTransition(
          position: _slideAnimations[index],
          child: FadeTransition(
            opacity: _fadeAnimations[index],
            child: sectionWidget,
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    if (_sections.isEmpty) {
      return const SizedBox.shrink();
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: _sections.asMap().entries.map((entry) {
        return _buildSection(entry.value, entry.key, theme, colorScheme);
      }).toList(),
    );
  }
}

// Helper classes
class _GrammarSection {
  final String title;
  final String content;
  final IconData icon;
  final _SectionColor color;
  final bool isList;
  final bool isStructure;
  final bool isConjugation;

  const _GrammarSection({
    required this.title,
    required this.content,
    required this.icon,
    required this.color,
    this.isList = false,
    this.isStructure = false,
    this.isConjugation = false,
  });
}

enum _SectionColor { primary, secondary, tertiary }

// Extension for content variations
extension GrammarDetailContentVariations on GrammarDetailContent {
  /// Creates a static version without animations
  static Widget static(GrammarDetail grammar) {
    return GrammarDetailContent(grammar: grammar, showAnimation: false);
  }

  /// Creates an animated version with custom delay
  static Widget animated(
    GrammarDetail grammar, {
    Duration animationDelay = Duration.zero,
  }) {
    return GrammarDetailContent(
      grammar: grammar,
      showAnimation: true,
      animationDelay: animationDelay,
    );
  }
}
