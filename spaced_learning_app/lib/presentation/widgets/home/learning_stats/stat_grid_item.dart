// lib/presentation/widgets/home/learning_stats/stat_grid_item.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class StatGridItem extends StatelessWidget {
  final ThemeData theme;
  final String value;
  final String label;
  final IconData iconData;
  final Color color;
  final String? additionalInfo;
  final bool showStar;
  final Color? starColor;
  final Color? onStarColor;

  const StatGridItem({
    super.key,
    required this.theme,
    required this.value,
    required this.label,
    required this.iconData,
    required this.color,
    this.additionalInfo,
    this.showStar = false,
    this.starColor,
    this.onStarColor,
  });

  @override
  Widget build(BuildContext context) {
    final effectiveStarColor =
        starColor ??
        (theme.brightness == Brightness.light
            ? Colors.amber
            : Colors.amberAccent);

    final effectiveOnStarColor =
        onStarColor ??
        (theme.brightness == Brightness.light
            ? Colors.white
            : theme.colorScheme.surface);

    return LayoutBuilder(
      builder: (context, constraints) {
        return SingleChildScrollView(
          physics: const NeverScrollableScrollPhysics(),
          child: ConstrainedBox(
            constraints: BoxConstraints(minHeight: constraints.maxHeight),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Stack(
                  clipBehavior: Clip.none,
                  children: [
                    Icon(iconData, color: color, size: AppDimens.iconL),
                    if (showStar)
                      Positioned(
                        top: -AppDimens.paddingXS - 1,
                        right: -AppDimens.paddingXS - 1,
                        child: Container(
                          padding: const EdgeInsets.all(AppDimens.paddingXXS),
                          decoration: BoxDecoration(
                            color: effectiveStarColor,
                            shape: BoxShape.circle,
                          ),
                          child: Icon(
                            Icons.star,
                            color: effectiveOnStarColor,
                            size: AppDimens.iconXXS,
                          ),
                        ),
                      ),
                  ],
                ),
                const SizedBox(height: AppDimens.spaceXS),
                Text(
                  value,
                  style: theme.textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: color,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                if (additionalInfo != null)
                  Text(
                    additionalInfo!,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: color.withValues(alpha: AppDimens.opacityHigh),
                      fontSize: AppDimens.fontXS,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                Text(
                  label,
                  style: theme.textTheme.bodySmall,
                  textAlign: TextAlign.center,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
