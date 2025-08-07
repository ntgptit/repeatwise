import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_dimens.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/utils/utils.dart';

class RecentSets extends ConsumerWidget {
  const RecentSets({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        UiUtils.createSectionHeader(
          title: 'Recent Sets',
          actionText: 'View All',
          onActionPressed: () {
            // TODO: Navigate to all sets
          },
        ),
        const SizedBox(height: 16),
        // TODO: Replace with actual data from provider
        ListView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: 3,
          itemBuilder: (context, index) {
            return RepeatWiseCard(
              onTap: () {
                // TODO: Navigate to set detail
              },
              child: RepeatWiseListTile(
                leading: CircleAvatar(
                  backgroundColor: Theme.of(context).primaryColor.withOpacity(0.1),
                  child: Icon(
                    Icons.list,
                    color: Theme.of(context).primaryColor,
                  ),
                ),
                title: Text(
                  'Set ${index + 1}',
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                subtitle: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: 4),
                    Text('Description for set ${index + 1}'),
                    const SizedBox(height: 8),
                    UiUtils.createProgressIndicator(
                      value: (index + 1) * 0.3,
                      percentageText: '${((index + 1) * 0.3 * 100).toInt()}%',
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Last reviewed: ${DateUtils.formatRelativeDate(DateTime.now().subtract(Duration(days: index)))}',
                      style: const TextStyle(
                        fontSize: AppDimens.textSize12,
                        color: AppColors.gray500,
                      ),
                    ),
                  ],
                ),
                trailing: const Icon(Icons.arrow_forward_ios),
              ),
            );
          },
        ),
      ],
    );
  }


}
