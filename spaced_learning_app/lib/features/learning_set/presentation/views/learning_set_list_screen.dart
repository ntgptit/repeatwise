import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/navigation/route_constants.dart';
import 'package:spaced_learning_app/features/learning_set/presentation/notifiers/learning_set_list_notifier.dart';

class LearningSetListScreen extends ConsumerStatefulWidget {
  const LearningSetListScreen({super.key});

  @override
  ConsumerState<LearningSetListScreen> createState() => _LearningSetListScreenState();
}

class _LearningSetListScreenState extends ConsumerState<LearningSetListScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(
      () => ref.read(learningSetListNotifierProvider.notifier).fetch(),
    );
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(learningSetListNotifierProvider);
    return Scaffold(
      appBar: AppBar(title: const Text('Learning Sets')),
      body: state.when(
        initial: () => const SizedBox.shrink(),
        loading: () => const Center(child: CircularProgressIndicator()),
        empty: () => const Center(child: Text('No sets found')),
        error: (message) => Center(child: Text(message)),
        data: (page) => ListView.builder(
          itemCount: page.content.length,
          itemBuilder: (context, index) {
            final set = page.content[index];
            return ListTile(
              title: Text(set.name),
              subtitle: Text('Words: ${set.wordCount}'),
              onTap: () => context.go(
                RouteConstants.learningSetReviewsRoute(set.id),
              ),
            );
          },
        ),
      ),
    );
  }
}

