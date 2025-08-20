// lib/presentation/screens/profile/widgets/profile_edit_form.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/viewmodels/user_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_text_field.dart';

class ProfileEditForm extends ConsumerWidget {
  final TextEditingController controller;
  final GlobalKey<FormState> formKey;
  final VoidCallback onCancel;
  final VoidCallback onSave;

  const ProfileEditForm({
    super.key,
    required this.controller,
    required this.formKey,
    required this.onCancel,
    required this.onSave,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 0,
      color: colorScheme.surfaceContainerHighest.withValues(alpha: 0.5),
      child: Padding(
        padding: const EdgeInsets.all(AppDimens.paddingL),
        child: Form(
          key: formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Edit Profile',
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: AppDimens.spaceL),
              SLTextField(
                label: 'Display Name',
                hint: 'Enter your display name',
                controller: controller,
                prefixIcon: Icons.person,
                backgroundColor: colorScheme.surface,
                borderColor: colorScheme.outline.withValues(alpha: 0.3),
                focusedBorderColor: colorScheme.primary,
                validator: (v) {
                  if (v == null || v.trim().isEmpty) return 'Cannot be empty';
                  if (v.length > 50) return 'Maximum 50 characters';
                  return null;
                },
              ),
              const SizedBox(height: AppDimens.spaceXL),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  SLButton(
                    text: 'Cancel',
                    type: SLButtonType.text,
                    onPressed: onCancel,
                    textColor: colorScheme.error,
                  ),
                  const SizedBox(width: AppDimens.spaceL),
                  Consumer(
                    builder: (context, ref, _) {
                      final isLoading = ref.watch(userStateProvider).isLoading;
                      return SLButton(
                        text: 'Save Changes',
                        type: SLButtonType.primary,
                        onPressed: onSave,
                        isLoading: isLoading,
                      );
                    },
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
