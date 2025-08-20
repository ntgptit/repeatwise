import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/features/review_history/presentation/notifiers/review_history_list_notifier.dart';

class ReviewHistoryListScreen extends ConsumerStatefulWidget {
  final String setId;
  const ReviewHistoryListScreen({required this.setId, super.key});

  @override
  ConsumerState<ReviewHistoryListScreen> createState() => _ReviewHistoryListScreenState();
}

class _ReviewHistoryListScreenState extends ConsumerState<ReviewHistoryListScreen> {
  @override
  void initState() {
    super.initState();
    Future.microtask(
      () => ref
          .read(reviewHistoryListNotifierProvider(widget.setId).notifier)
          .fetch(),
    );
  }

  @override
  Widget build(BuildContext context) {
    final state = ref.watch(reviewHistoryListNotifierProvider(widget.setId));
    return Scaffold(
      appBar: AppBar(title: const Text('Review History')),
      body: state.when(
        initial: () => const SizedBox.shrink(),
        loading: () => const Center(child: CircularProgressIndicator()),
        empty: () => const Center(child: Text('No reviews found')),
        error: (message) => Center(child: Text(message)),
        data: (page) => ListView.builder(
          itemCount: page.content.length,
          itemBuilder: (context, index) {
            final review = page.content[index];
            return ListTile(
              title: Text('Cycle ${review.cycleNo} - Review ${review.reviewNo}'),
              subtitle: Text('Status: ${review.status.name}'),
            );
          },
        ),
      ),
    );
  }
}

