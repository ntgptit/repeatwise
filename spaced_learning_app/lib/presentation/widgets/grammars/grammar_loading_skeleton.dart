// lib/presentation/widgets/modules/grammar/detail/grammar_loading_skeleton.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class GrammarLoadingSkeleton extends StatefulWidget {
  final bool showHeaderSkeleton;
  final bool showContentSkeleton;
  final bool showActionsSkeleton;
  final int contentSections;

  const GrammarLoadingSkeleton({
    super.key,
    this.showHeaderSkeleton = true,
    this.showContentSkeleton = true,
    this.showActionsSkeleton = true,
    this.contentSections = 3,
  });

  @override
  State<GrammarLoadingSkeleton> createState() => _GrammarLoadingSkeletonState();
}

class _GrammarLoadingSkeletonState extends State<GrammarLoadingSkeleton>
    with SingleTickerProviderStateMixin {
  late AnimationController _shimmerController;
  late Animation<double> _shimmerAnimation;

  @override
  void initState() {
    super.initState();
    _initializeShimmerAnimation();
  }

  @override
  void dispose() {
    _shimmerController.dispose();
    super.dispose();
  }

  void _initializeShimmerAnimation() {
    _shimmerController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    );

    _shimmerAnimation = Tween<double>(begin: -1.0, end: 2.0).animate(
      CurvedAnimation(parent: _shimmerController, curve: Curves.easeInOut),
    );

    _shimmerController.repeat();
  }

  Widget _buildShimmerContainer({
    required double width,
    required double height,
    BorderRadius? borderRadius,
  }) {
    return AnimatedBuilder(
      animation: _shimmerAnimation,
      builder: (context, child) {
        return Container(
          width: width,
          height: height,
          decoration: BoxDecoration(
            borderRadius:
                borderRadius ?? BorderRadius.circular(AppDimens.radiusS),
            gradient: LinearGradient(
              begin: Alignment.centerLeft,
              end: Alignment.centerRight,
              stops: [
                (_shimmerAnimation.value - 1).clamp(0.0, 1.0),
                _shimmerAnimation.value.clamp(0.0, 1.0),
                (_shimmerAnimation.value + 1).clamp(0.0, 1.0),
              ],
              colors: [
                Theme.of(context).colorScheme.surfaceContainerHighest,
                Theme.of(context).colorScheme.surfaceContainerHigh,
                Theme.of(context).colorScheme.surfaceContainerHighest,
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildHeaderSkeleton(ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Grammar Icon Skeleton
            _buildShimmerContainer(
              width: AppDimens.avatarSizeXL + 8,
              height: AppDimens.avatarSizeXL + 8,
              borderRadius: BorderRadius.circular(AppDimens.radiusXL),
            ),
            const SizedBox(width: AppDimens.spaceL),
            // Title and Module Info Skeleton
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Main title skeleton
                  _buildShimmerContainer(
                    width: double.infinity,
                    height: 32,
                    borderRadius: BorderRadius.circular(AppDimens.radiusS),
                  ),
                  const SizedBox(height: AppDimens.spaceS),
                  // Subtitle skeleton
                  _buildShimmerContainer(
                    width: MediaQuery.of(context).size.width * 0.6,
                    height: 20,
                    borderRadius: BorderRadius.circular(AppDimens.radiusS),
                  ),
                  const SizedBox(height: AppDimens.spaceM),
                  // Module badge skeleton
                  _buildShimmerContainer(
                    width: 120,
                    height: 24,
                    borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
                  ),
                ],
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceXL),
        // Info card skeleton
        _buildShimmerContainer(
          width: double.infinity,
          height: 100,
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
      ],
    );
  }

  Widget _buildContentSectionSkeleton(ColorScheme colorScheme, int index) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Section header skeleton
        Row(
          children: [
            // Icon container skeleton
            _buildShimmerContainer(
              width: AppDimens.avatarSizeM,
              height: AppDimens.avatarSizeM,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
            const SizedBox(width: AppDimens.spaceL),
            // Section title skeleton
            _buildShimmerContainer(
              width: 150 + (index * 20.0),
              height: 28,
              borderRadius: BorderRadius.circular(AppDimens.radiusS),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceL),
        // Content card skeleton
        _buildShimmerContainer(
          width: double.infinity,
          height: 120 + (index * 40.0),
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
      ],
    );
  }

  Widget _buildContentSkeleton(ColorScheme colorScheme) {
    return Column(
      children: List.generate(widget.contentSections, (index) {
        return Column(
          children: [
            _buildContentSectionSkeleton(colorScheme, index),
            if (index < widget.contentSections - 1)
              const SizedBox(height: AppDimens.spaceXXL),
          ],
        );
      }),
    );
  }

  Widget _buildActionsSkeleton(ColorScheme colorScheme) {
    return Column(
      children: [
        // Quick actions skeleton
        _buildShimmerContainer(
          width: double.infinity,
          height: 120,
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
        const SizedBox(height: AppDimens.spaceL),
        // Navigation section skeleton
        _buildShimmerContainer(
          width: double.infinity,
          height: 150,
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
        const SizedBox(height: AppDimens.spaceL),
        // Practice button skeleton
        _buildShimmerContainer(
          width: double.infinity,
          height: AppDimens.buttonHeightL,
          borderRadius: BorderRadius.circular(AppDimens.radiusM),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.all(AppDimens.paddingL),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (widget.showHeaderSkeleton) ...[
            _buildHeaderSkeleton(colorScheme),
            const SizedBox(height: AppDimens.spaceXXL),
          ],
          if (widget.showContentSkeleton) ...[
            _buildContentSkeleton(colorScheme),
            const SizedBox(height: AppDimens.spaceXXL),
          ],
          if (widget.showActionsSkeleton) ...[
            _buildActionsSkeleton(colorScheme),
          ],
        ],
      ),
    );
  }
}

// List Loading Skeleton for Grammar List
class GrammarListLoadingSkeleton extends StatefulWidget {
  final int itemCount;
  final bool showHeader;

  const GrammarListLoadingSkeleton({
    super.key,
    this.itemCount = 5,
    this.showHeader = true,
  });

  @override
  State<GrammarListLoadingSkeleton> createState() =>
      _GrammarListLoadingSkeletonState();
}

class _GrammarListLoadingSkeletonState extends State<GrammarListLoadingSkeleton>
    with SingleTickerProviderStateMixin {
  late AnimationController _shimmerController;
  late Animation<double> _shimmerAnimation;

  @override
  void initState() {
    super.initState();
    _initializeShimmerAnimation();
  }

  @override
  void dispose() {
    _shimmerController.dispose();
    super.dispose();
  }

  void _initializeShimmerAnimation() {
    _shimmerController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );

    _shimmerAnimation = Tween<double>(begin: -1.0, end: 2.0).animate(
      CurvedAnimation(parent: _shimmerController, curve: Curves.easeInOut),
    );

    _shimmerController.repeat();
  }

  Widget _buildShimmerContainer({
    required double width,
    required double height,
    BorderRadius? borderRadius,
  }) {
    return AnimatedBuilder(
      animation: _shimmerAnimation,
      builder: (context, child) {
        return Container(
          width: width,
          height: height,
          decoration: BoxDecoration(
            borderRadius:
                borderRadius ?? BorderRadius.circular(AppDimens.radiusS),
            gradient: LinearGradient(
              begin: Alignment.centerLeft,
              end: Alignment.centerRight,
              stops: [
                (_shimmerAnimation.value - 1).clamp(0.0, 1.0),
                _shimmerAnimation.value.clamp(0.0, 1.0),
                (_shimmerAnimation.value + 1).clamp(0.0, 1.0),
              ],
              colors: [
                Theme.of(context).colorScheme.surfaceContainerHighest,
                Theme.of(context).colorScheme.surfaceContainerHigh,
                Theme.of(context).colorScheme.surfaceContainerHighest,
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildHeaderSkeleton() {
    return Padding(
      padding: const EdgeInsets.all(AppDimens.paddingL),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              // Header icon skeleton
              _buildShimmerContainer(
                width: AppDimens.avatarSizeXL,
                height: AppDimens.avatarSizeXL,
                borderRadius: BorderRadius.circular(AppDimens.radiusL),
              ),
              const SizedBox(width: AppDimens.spaceL),
              // Header content skeleton
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildShimmerContainer(width: double.infinity, height: 32),
                    const SizedBox(height: AppDimens.spaceS),
                    _buildShimmerContainer(
                      width: MediaQuery.of(context).size.width * 0.6,
                      height: 20,
                    ),
                  ],
                ),
              ),
              // Action button skeleton
              _buildShimmerContainer(
                width: AppDimens.avatarSizeM,
                height: AppDimens.avatarSizeM,
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ],
          ),
          const SizedBox(height: AppDimens.spaceL),
          // Stats info skeleton
          _buildShimmerContainer(
            width: double.infinity,
            height: 60,
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
          ),
        ],
      ),
    );
  }

  Widget _buildSearchBarSkeleton() {
    return Padding(
      padding: const EdgeInsets.all(AppDimens.paddingL),
      child: _buildShimmerContainer(
        width: double.infinity,
        height: AppDimens.textFieldHeight,
        borderRadius: BorderRadius.circular(AppDimens.radiusXL),
      ),
    );
  }

  Widget _buildListItemSkeleton(int index) {
    return Padding(
      padding: EdgeInsets.fromLTRB(
        AppDimens.paddingL,
        index == 0 ? 0 : AppDimens.spaceS,
        AppDimens.paddingL,
        AppDimens.spaceS,
      ),
      child: Container(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        decoration: BoxDecoration(
          color: Theme.of(context).colorScheme.surfaceContainerLowest,
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
          border: Border.all(
            color: Theme.of(
              context,
            ).colorScheme.outlineVariant.withValues(alpha: 0.5),
          ),
        ),
        child: Row(
          children: [
            // Avatar skeleton
            _buildShimmerContainer(
              width: AppDimens.avatarSizeM,
              height: AppDimens.avatarSizeM,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
            const SizedBox(width: AppDimens.spaceL),
            // Content skeleton
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildShimmerContainer(width: double.infinity, height: 20),
                  const SizedBox(height: AppDimens.spaceXS),
                  _buildShimmerContainer(
                    width: MediaQuery.of(context).size.width * 0.4,
                    height: 16,
                  ),
                ],
              ),
            ),
            // Action indicator skeleton
            _buildShimmerContainer(
              width: AppDimens.iconL,
              height: AppDimens.iconL,
              borderRadius: BorderRadius.circular(AppDimens.radiusS),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        if (widget.showHeader) _buildHeaderSkeleton(),
        _buildSearchBarSkeleton(),
        Expanded(
          child: ListView.builder(
            itemCount: widget.itemCount,
            itemBuilder: (context, index) => _buildListItemSkeleton(index),
          ),
        ),
      ],
    );
  }
}

// Extension for skeleton variations
extension GrammarLoadingSkeletonVariations on GrammarLoadingSkeleton {
  /// Creates a header-only skeleton
  static Widget headerOnly() {
    return const GrammarLoadingSkeleton(
      showHeaderSkeleton: true,
      showContentSkeleton: false,
      showActionsSkeleton: false,
    );
  }

  /// Creates a content-only skeleton
  static Widget contentOnly({int sections = 3}) {
    return GrammarLoadingSkeleton(
      showHeaderSkeleton: false,
      showContentSkeleton: true,
      showActionsSkeleton: false,
      contentSections: sections,
    );
  }

  /// Creates a minimal skeleton for faster loading
  static Widget minimal() {
    return const GrammarLoadingSkeleton(
      showHeaderSkeleton: true,
      showContentSkeleton: true,
      showActionsSkeleton: false,
      contentSections: 2,
    );
  }
}
