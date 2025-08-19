// lib/presentation/widgets/modules/detail/module_detail_content.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/domain/models/module.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_card.dart';

class ModuleDetailContent extends StatefulWidget {
  final ModuleDetail module;
  final bool showAnimation;
  final VoidCallback? onViewGrammar;
  final VoidCallback? onViewProgress;
  final bool hasGrammar;

  const ModuleDetailContent({
    super.key,
    required this.module,
    this.showAnimation = true,
    this.onViewGrammar,
    this.onViewProgress,
    this.hasGrammar = false,
  });

  @override
  State<ModuleDetailContent> createState() => _ModuleDetailContentState();
}

class _ModuleDetailContentState extends State<ModuleDetailContent>
    with TickerProviderStateMixin {
  late List<AnimationController> _sectionControllers;
  late List<Animation<Offset>> _slideAnimations;
  late List<Animation<double>> _fadeAnimations;

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
      for (final controller in _sectionControllers) {
        controller.dispose();
      }
    }
    super.dispose();
  }

  void _initializeAnimations() {
    _sectionControllers = List.generate(
      3, // Number of sections
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
      Future.delayed(Duration(milliseconds: i * 150), () {
        if (mounted) {
          _sectionControllers[i].forward();
        }
      });
    }
  }

  Widget _buildContentOverview(ThemeData theme, ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionHeader(
          'Content Overview',
          Icons.article_outlined,
          colorScheme.primary,
          theme,
        ),
        const SizedBox(height: AppDimens.spaceL),
        SLCard(
          type: SLCardType.outlined,
          padding: const EdgeInsets.all(AppDimens.paddingL),
          backgroundColor: colorScheme.surfaceContainerLowest,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
            side: BorderSide(color: colorScheme.primary.withValues(alpha: 0.2)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (widget.module.wordCount != null &&
                  widget.module.wordCount! > 0)
                _buildContentMetric(
                  'Word Count',
                  '${widget.module.wordCount} words',
                  Icons.text_fields_rounded,
                  colorScheme.secondary,
                  theme,
                ),

              _buildContentMetric(
                'Estimated Reading Time',
                _calculateReadingTime(widget.module.wordCount),
                Icons.schedule_rounded,
                colorScheme.tertiary,
                theme,
              ),

              const SizedBox(height: AppDimens.spaceL),
              Divider(color: colorScheme.outlineVariant.withValues(alpha: 0.5)),
              const SizedBox(height: AppDimens.spaceL),

              Text(
                _getContentDescription(),
                style: theme.textTheme.bodyLarge?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                  height: 1.6,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildLearningFeatures(ThemeData theme, ColorScheme colorScheme) {
    final features = [
      _FeatureItem(
        icon: Icons.school_rounded,
        title: 'Structured Learning',
        description: 'Follow a systematic approach to mastering new concepts',
        color: colorScheme.primary,
      ),
      _FeatureItem(
        icon: Icons.repeat_rounded,
        title: 'Spaced Repetition',
        description: 'Review content at optimal intervals for better retention',
        color: colorScheme.secondary,
      ),
      if (widget.hasGrammar)
        _FeatureItem(
          icon: Icons.auto_stories_rounded,
          title: 'Grammar Rules',
          description: 'Learn essential grammar patterns and structures',
          color: colorScheme.tertiary,
        ),
      _FeatureItem(
        icon: Icons.track_changes_rounded,
        title: 'Progress Tracking',
        description: 'Monitor your learning journey and achievements',
        color: colorScheme.primary,
      ),
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionHeader(
          'Learning Features',
          Icons.lightbulb_outline_rounded,
          colorScheme.secondary,
          theme,
        ),
        const SizedBox(height: AppDimens.spaceL),
        ...features.map(
          (feature) => _buildFeatureCard(feature, theme, colorScheme),
        ),
      ],
    );
  }

  Widget _buildStudyTips(ThemeData theme, ColorScheme colorScheme) {
    final tips = [
      'Review this module according to your spaced repetition schedule',
      'Take active notes during your study sessions',
      'Practice recall before checking answers or materials',
      'Connect new concepts to your existing knowledge',
      'Use multiple senses when studying (visual, auditory, kinesthetic)',
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionHeader(
          'Study Tips',
          Icons.tips_and_updates_outlined,
          colorScheme.tertiary,
          theme,
        ),
        const SizedBox(height: AppDimens.spaceL),
        SLCard(
          type: SLCardType.filled,
          padding: const EdgeInsets.all(AppDimens.paddingL),
          backgroundColor: colorScheme.tertiaryContainer.withValues(alpha: 0.7),
          elevation: AppDimens.elevationNone,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
            side: BorderSide(
              color: colorScheme.tertiary.withValues(alpha: 0.3),
            ),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(AppDimens.paddingS),
                    decoration: BoxDecoration(
                      color: colorScheme.tertiary.withValues(alpha: 0.2),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(
                      Icons.psychology_rounded,
                      color: colorScheme.onTertiaryContainer,
                      size: AppDimens.iconM,
                    ),
                  ),
                  const SizedBox(width: AppDimens.spaceM),
                  Text(
                    'Effective Learning Strategies',
                    style: theme.textTheme.titleMedium?.copyWith(
                      color: colorScheme.onTertiaryContainer,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: AppDimens.spaceL),
              ...tips.map((tip) => _buildTipItem(tip, theme, colorScheme)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildSectionHeader(
    String title,
    IconData icon,
    Color color,
    ThemeData theme,
  ) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(AppDimens.paddingM),
          decoration: BoxDecoration(
            color: color.withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
          ),
          child: Icon(icon, size: AppDimens.iconL, color: color),
        ),
        const SizedBox(width: AppDimens.spaceL),
        Expanded(
          child: Text(
            title,
            style: theme.textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: color,
              letterSpacing: -0.3,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildContentMetric(
    String label,
    String value,
    IconData icon,
    Color color,
    ThemeData theme,
  ) {
    return Padding(
      padding: const EdgeInsets.only(bottom: AppDimens.spaceM),
      child: Row(
        children: [
          Icon(icon, size: AppDimens.iconM, color: color),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: theme.textTheme.labelMedium?.copyWith(
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                Text(
                  value,
                  style: theme.textTheme.titleMedium?.copyWith(
                    color: color,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFeatureCard(
    _FeatureItem feature,
    ThemeData theme,
    ColorScheme colorScheme,
  ) {
    return Container(
      margin: const EdgeInsets.only(bottom: AppDimens.spaceM),
      child: SLCard(
        type: SLCardType.outlined,
        padding: const EdgeInsets.all(AppDimens.paddingL),
        backgroundColor: colorScheme.surfaceContainerLowest,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
          side: BorderSide(color: feature.color.withValues(alpha: 0.2)),
        ),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(AppDimens.paddingM),
              decoration: BoxDecoration(
                color: feature.color.withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
              child: Icon(
                feature.icon,
                color: feature.color,
                size: AppDimens.iconL,
              ),
            ),
            const SizedBox(width: AppDimens.spaceL),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    feature.title,
                    style: theme.textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                      color: colorScheme.onSurface,
                    ),
                  ),
                  const SizedBox(height: AppDimens.spaceXS),
                  Text(
                    feature.description,
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                      height: 1.4,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTipItem(String tip, ThemeData theme, ColorScheme colorScheme) {
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
              color: colorScheme.onTertiaryContainer,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Text(
              tip,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: colorScheme.onTertiaryContainer,
                height: 1.5,
              ),
            ),
          ),
        ],
      ),
    );
  }

  String _calculateReadingTime(int? wordCount) {
    if (wordCount == null || wordCount <= 0) {
      return 'Unknown';
    }

    final minutes = (wordCount / 200).ceil();
    if (minutes < 1) return 'Less than 1 minute';
    if (minutes == 1) return '1 minute';
    return '$minutes minutes';
  }

  String _getContentDescription() {
    if (widget.module.url != null && widget.module.url!.isNotEmpty) {
      return 'This module contains external learning materials accessible through the provided link. Click "Start Learning" to access the content and begin your study session.';
    }

    return 'This module provides comprehensive learning materials designed to help you master new concepts. The content includes structured lessons, examples, and practice opportunities to enhance your understanding.';
  }

  Widget _buildSection(Widget section, int index) {
    if (!widget.showAnimation) {
      return Column(
        children: [
          section,
          const SizedBox(height: AppDimens.spaceXXL),
        ],
      );
    }

    return AnimatedBuilder(
      animation: Listenable.merge([_sectionControllers[index]]),
      builder: (context, child) {
        return SlideTransition(
          position: _slideAnimations[index],
          child: FadeTransition(
            opacity: _fadeAnimations[index],
            child: Column(
              children: [
                section,
                const SizedBox(height: AppDimens.spaceXXL),
              ],
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    final sections = [
      _buildContentOverview(theme, colorScheme),
      _buildLearningFeatures(theme, colorScheme),
      _buildStudyTips(theme, colorScheme),
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: sections.asMap().entries.map((entry) {
        return _buildSection(entry.value, entry.key);
      }).toList(),
    );
  }
}

// Helper class for feature items
class _FeatureItem {
  final IconData icon;
  final String title;
  final String description;
  final Color color;

  const _FeatureItem({
    required this.icon,
    required this.title,
    required this.description,
    required this.color,
  });
}

// Extension for content variations
extension ModuleDetailContentVariations on ModuleDetailContent {
  /// Creates a static version without animations
  static Widget static({
    required ModuleDetail module,
    VoidCallback? onViewGrammar,
    VoidCallback? onViewProgress,
    bool hasGrammar = false,
  }) {
    return ModuleDetailContent(
      module: module,
      showAnimation: false,
      onViewGrammar: onViewGrammar,
      onViewProgress: onViewProgress,
      hasGrammar: hasGrammar,
    );
  }

  /// Creates an animated version
  static Widget animated({
    required ModuleDetail module,
    VoidCallback? onViewGrammar,
    VoidCallback? onViewProgress,
    bool hasGrammar = false,
  }) {
    return ModuleDetailContent(
      module: module,
      showAnimation: true,
      onViewGrammar: onViewGrammar,
      onViewProgress: onViewProgress,
      hasGrammar: hasGrammar,
    );
  }
}
