import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class AboutScreen extends StatelessWidget {
  const AboutScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      appBar: AppBar(title: const Text('About')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'About Spaced Learning App',
              style: theme.textTheme.headlineSmall,
            ),
            const SizedBox(height: AppDimens.spaceL),
            const Text(
              'Spaced Learning App helps you learn more effectively using '
              'scientifically proven spaced repetition techniques.',
            ),
            const SizedBox(height: AppDimens.spaceXL),
            const Text('Version: 1.0.0'),
            const SizedBox(height: AppDimens.spaceM),
            const Text('© 2023 Your Company'),
            const SizedBox(height: AppDimens.spaceXXL),

            // Deep Link Demo Section
            Text('Deep Link Demo', style: theme.textTheme.titleMedium),
            const SizedBox(height: AppDimens.spaceM),
            const Text('Here are some examples of deep links to try:'),
            const SizedBox(height: AppDimens.spaceM),
            _buildDeepLinkCard(
              context,
              'Open Books Screen',
              '/books',
              colorScheme.primaryContainer,
            ),
            _buildDeepLinkCard(
              context,
              'Open Learning Screen',
              '/learning',
              colorScheme.secondaryContainer,
            ),
            _buildDeepLinkCard(
              context,
              'Open Profile Screen',
              '/profile',
              colorScheme.tertiaryContainer,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDeepLinkCard(
    BuildContext context,
    String title,
    String route,
    Color backgroundColor,
  ) {
    return Card(
      margin: const EdgeInsets.only(bottom: AppDimens.spaceM),
      color: backgroundColor,
      child: InkWell(
        onTap: () => GoRouter.of(context).go(route),
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingL),
          child: Row(
            children: [
              Expanded(child: Text(title)),
              const Icon(Icons.arrow_forward),
            ],
          ),
        ),
      ),
    );
  }
}
