import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/theme/app_colors.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/models/set.dart';
import '../../../sets/providers/set_providers.dart';

class LearningSets extends ConsumerWidget {
  const LearningSets({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final setsAsync = ref.watch(setsNotifierProvider);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Currently Learning',
              style: Theme.of(
                context,
              ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
            ),
            TextButton(
              onPressed: () {
                context.go('/sets');
              },
              child: const Text('View All'),
            ),
          ],
        ),
        const SizedBox(height: 16),
        setsAsync.when(
          data: (sets) {
            final learningSets = sets.where((set) => set.isLearning).toList();

            if (learningSets.isEmpty) {
              return Card(
                child: Padding(
                  padding: const EdgeInsets.all(24.0),
                  child: Column(
                    children: [
                      Icon(Icons.school, size: 48, color: Colors.grey[400]),
                      const SizedBox(height: 16),
                      Text(
                        'No active learning sets',
                        style: Theme.of(context).textTheme.titleMedium
                            ?.copyWith(color: Colors.grey[600]),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Start learning a set to see it here',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: Colors.grey[500],
                        ),
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 16),
                      ElevatedButton.icon(
                        onPressed: () {
                          context.go('/sets');
                        },
                        icon: const Icon(Icons.add),
                        label: const Text('Browse Sets'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppColors.primary,
                          foregroundColor: AppColors.onPrimary,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }

            return Column(
              children: learningSets.take(3).map((set) {
                return Padding(
                  padding: const EdgeInsets.only(bottom: 12.0),
                  child: RepeatWiseSetCard(
                    set: set,
                    onTap: () {
                      context.go('/sets/${set.id}');
                    },
                  ),
                );
              }).toList(),
            );
          },
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, stack) => Card(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Text('Error loading sets: $error'),
            ),
          ),
        ),
      ],
    );
  }
}
