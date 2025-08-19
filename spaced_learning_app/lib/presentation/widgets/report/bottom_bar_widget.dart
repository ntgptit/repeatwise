// lib/presentation/widgets/report/bottom_bar_widget.dart
import 'package:flutter/material.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';

class BottomBarWidget extends StatelessWidget {
  final VoidCallback onPerformManualCheck;
  final bool isManualCheckInProgress;

  const BottomBarWidget({
    super.key,
    required this.onPerformManualCheck,
    required this.isManualCheckInProgress,
  });

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Row(
          children: [
            Expanded(
              child: SLButton(
                text: 'Check Now',
                prefixIcon: Icons.refresh,
                type: SLButtonType.primary,
                isLoading: isManualCheckInProgress,
                onPressed: onPerformManualCheck,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
