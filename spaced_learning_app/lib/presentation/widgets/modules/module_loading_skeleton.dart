// lib/presentation/widgets/modules/module_loading_skeleton.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class ModuleLoadingSkeleton extends StatefulWidget {
  final bool showHeaderSkeleton;
  final bool showProgressSkeleton;
  final bool showContentSkeleton;
  final int contentSections;

  const ModuleLoadingSkeleton({
    super.key,
    this.showHeaderSkeleton = true,
    this.showProgressSkeleton = true,
    this.showContentSkeleton = true,
    this.contentSections = 3,
  });

  @override
  State<ModuleLoadingSkeleton> createState() => _ModuleLoadingSkeletonState();
}

class _ModuleLoadingSkeletonState extends State<ModuleLoadingSkeleton>
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
      mainAxisSize: MainAxisSize.min,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Module Icon Skeleton
            _buildShimmerContainer(
              width: AppDimens.avatarSizeL, // Reduced from XXL
              height: AppDimens.avatarSizeL,
              borderRadius: BorderRadius.circular(AppDimens.radiusL),
            ),
            const SizedBox(width: AppDimens.spaceM), // Reduced spacing
            // Title and Info Skeleton
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  // Main title skeleton
                  _buildShimmerContainer(
                    width: double.infinity,
                    height: 24, // Reduced from 32
                    borderRadius: BorderRadius.circular(AppDimens.radiusS),
                  ),
                  const SizedBox(height: AppDimens.spaceXS), // Reduced spacing
                  // Book info skeleton
                  _buildShimmerContainer(
                    width: 150, // Reduced from 180
                    height: 20, // Reduced from 24
                    borderRadius: BorderRadius.circular(AppDimens.radiusM),
                  ),
                  const SizedBox(height: AppDimens.spaceS), // Reduced spacing
                  // Stats chips skeleton
                  Row(
                    children: [
                      _buildShimmerContainer(
                        width: 70, // Reduced from 80
                        height: 20, // Reduced from 24
                        borderRadius: BorderRadius.circular(AppDimens.radiusM),
                      ),
                      const SizedBox(width: AppDimens.spaceS),
                      // Reduced spacing
                      _buildShimmerContainer(
                        width: 90, // Reduced from 100
                        height: 20, // Reduced from 24
                        borderRadius: BorderRadius.circular(AppDimens.radiusM),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            // Action button skeleton
            _buildShimmerContainer(
              width: AppDimens.avatarSizeM, // Reduced from L
              height: AppDimens.avatarSizeM,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceL), // Reduced spacing
        // Info card skeleton
        _buildShimmerContainer(
          width: double.infinity,
          height: 80, // Reduced from 120
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
      ],
    );
  }

  Widget _buildProgressSkeleton(ColorScheme colorScheme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        // Progress header skeleton
        Row(
          children: [
            _buildShimmerContainer(
              width: AppDimens.avatarSizeM, // Reduced from L
              height: AppDimens.avatarSizeM,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
            const SizedBox(width: AppDimens.spaceM), // Reduced spacing
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  _buildShimmerContainer(
                    width: 120, // Reduced from 150
                    height: 20, // Reduced from 24
                    borderRadius: BorderRadius.circular(AppDimens.radiusS),
                  ),
                  const SizedBox(height: AppDimens.spaceXS),
                  _buildShimmerContainer(
                    width: 160, // Reduced from 200
                    height: 14, // Reduced from 16
                    borderRadius: BorderRadius.circular(AppDimens.radiusS),
                  ),
                ],
              ),
            ),
            _buildShimmerContainer(
              width: 70, // Reduced from 80
              height: 28, // Reduced from 32
              borderRadius: BorderRadius.circular(AppDimens.radiusXL),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceL), // Reduced spacing
        // Progress bar skeleton
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _buildShimmerContainer(
                  width: 70, // Reduced from 80
                  height: 12, // Reduced from 14
                  borderRadius: BorderRadius.circular(AppDimens.radiusS),
                ),
                _buildShimmerContainer(
                  width: 35, // Reduced from 40
                  height: 14, // Reduced from 16
                  borderRadius: BorderRadius.circular(AppDimens.radiusS),
                ),
              ],
            ),
            const SizedBox(height: AppDimens.spaceXS), // Reduced spacing
            _buildShimmerContainer(
              width: double.infinity,
              height: AppDimens.lineProgressHeight, // Reduced thickness
              borderRadius: BorderRadius.circular(AppDimens.radiusS),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceM), // Reduced spacing
        // Info items skeleton
        Row(
          children: [
            Expanded(
              child: _buildShimmerContainer(
                width: double.infinity,
                height: 60, // Reduced from 80
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ),
            const SizedBox(width: AppDimens.spaceM), // Reduced spacing
            Expanded(
              child: _buildShimmerContainer(
                width: double.infinity,
                height: 60, // Reduced from 80
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceM), // Reduced spacing
        // Action buttons skeleton
        Row(
          children: [
            Expanded(
              child: _buildShimmerContainer(
                width: double.infinity,
                height: AppDimens.buttonHeightM, // Reduced from L
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ),
            const SizedBox(width: AppDimens.spaceS), // Reduced spacing
            Expanded(
              child: _buildShimmerContainer(
                width: double.infinity,
                height: AppDimens.buttonHeightM, // Reduced from L
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildContentSectionSkeleton(ColorScheme colorScheme, int index) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        // Section header skeleton
        Row(
          children: [
            _buildShimmerContainer(
              width: AppDimens.avatarSizeM, // Reduced from L
              height: AppDimens.avatarSizeM,
              borderRadius: BorderRadius.circular(AppDimens.radiusM),
            ),
            const SizedBox(width: AppDimens.spaceM), // Reduced spacing
            _buildShimmerContainer(
              width: 140 + (index * 15.0), // Reduced base and increment
              height: 24, // Reduced from 28
              borderRadius: BorderRadius.circular(AppDimens.radiusS),
            ),
          ],
        ),
        const SizedBox(height: AppDimens.spaceM), // Reduced spacing
        // Content skeleton - Dynamic height based on available space
        _buildShimmerContainer(
          width: double.infinity,
          height: 120 + (index * 30.0), // Reduced significantly
          borderRadius: BorderRadius.circular(AppDimens.radiusL),
        ),
      ],
    );
  }

  Widget _buildContentSkeleton(ColorScheme colorScheme) {
    return Flexible(
      // Use Flexible instead of fixed height
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: List.generate(
          widget.contentSections.clamp(1, 2),
          // Limit sections to prevent overflow
          (index) {
            return Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                _buildContentSectionSkeleton(colorScheme, index),
                if (index < widget.contentSections - 1)
                  const SizedBox(height: AppDimens.spaceL), // Reduced spacing
              ],
            );
          },
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return SingleChildScrollView(
      // Wrap in SingleChildScrollView
      physics: const NeverScrollableScrollPhysics(),
      // Disable scrolling for skeleton
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min, // Important: Use min size
          children: [
            if (widget.showHeaderSkeleton) ...[
              _buildHeaderSkeleton(colorScheme),
              const SizedBox(height: AppDimens.spaceL), // Reduced spacing
            ],
            if (widget.showProgressSkeleton) ...[
              _buildProgressSkeleton(colorScheme),
              const SizedBox(height: AppDimens.spaceL), // Reduced spacing
            ],
            if (widget.showContentSkeleton) ...[
              _buildContentSkeleton(colorScheme),
            ],
            // Add safe bottom padding
            SizedBox(
              height: MediaQuery.of(context).padding.bottom + AppDimens.spaceL,
            ),
          ],
        ),
      ),
    );
  }
}

// Extension for skeleton variations - Updated for better space management
extension ModuleLoadingSkeletonVariations on ModuleLoadingSkeleton {
  /// Creates a header-only skeleton
  static Widget headerOnly() {
    return const ModuleLoadingSkeleton(
      showHeaderSkeleton: true,
      showProgressSkeleton: false,
      showContentSkeleton: false,
    );
  }

  /// Creates a progress-only skeleton
  static Widget progressOnly() {
    return const ModuleLoadingSkeleton(
      showHeaderSkeleton: false,
      showProgressSkeleton: true,
      showContentSkeleton: false,
    );
  }

  /// Creates a content-only skeleton with limited sections
  static Widget contentOnly({int sections = 2}) {
    // Reduced default
    return ModuleLoadingSkeleton(
      showHeaderSkeleton: false,
      showProgressSkeleton: false,
      showContentSkeleton: true,
      contentSections: sections.clamp(1, 2), // Limit to prevent overflow
    );
  }

  /// Creates a minimal skeleton for faster loading
  static Widget minimal() {
    return const ModuleLoadingSkeleton(
      showHeaderSkeleton: true,
      showProgressSkeleton: false,
      showContentSkeleton: true,
      contentSections: 1, // Reduced from 2
    );
  }

  /// Creates a complete skeleton with all sections but optimized
  static Widget complete() {
    return const ModuleLoadingSkeleton(
      showHeaderSkeleton: true,
      showProgressSkeleton: true,
      showContentSkeleton: true,
      contentSections: 2, // Reduced from 3
    );
  }
}
