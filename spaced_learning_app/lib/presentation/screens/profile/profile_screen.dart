// lib/presentation/screens/profile/profile_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/navigation/navigation_helper.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/utils/snackbar_utils.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/user_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

import '../../widgets/profile/login_prompt.dart';
import '../../widgets/profile/profile_edit_form.dart';
import '../../widgets/profile/profile_header.dart';
import '../../widgets/profile/profile_info_card.dart';
import '../../widgets/profile/theme_toggle_card.dart';

class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
  final _displayNameController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  bool _isEditing = false;

  @override
  void initState() {
    super.initState();
    _runSafe(_loadUserData);
  }

  @override
  void dispose() {
    _displayNameController.dispose();
    super.dispose();
  }

  Future<void> _loadUserData() async {
    await ref.read(userStateProvider.notifier).loadCurrentUser();
    if (mounted) {
      final currentUser = ref.read(userStateProvider).valueOrNull;
      if (currentUser != null) {
        _displayNameController.text = currentUser.displayName ?? '';
      }
    }
  }

  Future<void> _updateProfile() async {
    if (!(_formKey.currentState?.validate() ?? false)) return;

    final success = await ref
        .read(userStateProvider.notifier)
        .updateProfile(displayName: _displayNameController.text.trim());

    if (!mounted) return;

    if (success) {
      await _loadUserData();
      setState(() => _isEditing = false);
      _showSnackBar('Profile updated successfully', Colors.green);
    } else {
      _showSnackBar('Failed to update profile', Colors.red);
    }
  }

  Future<void> _logout() async {
    await ref.read(authStateProvider.notifier).logout();

    if (!mounted) return;

    if (!(ref.read(authStateProvider).valueOrNull ?? false)) {
      NavigationHelper.clearStackAndGo(context, '/login');
    }
  }

  void _toggleEditing() {
    setState(() {
      _isEditing = !_isEditing;
      if (!_isEditing) _resetForm();
    });
  }

  void _resetForm() {
    final currentUser = ref.read(userStateProvider).valueOrNull;
    if (currentUser != null) {
      _displayNameController.text = currentUser.displayName ?? '';
    }
  }

  void _showSnackBar(String msg, Color color) {
    if (!mounted) return;
    SnackBarUtils.show(context, msg, backgroundColor: color);
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return Scaffold(
      backgroundColor: colorScheme.surface,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.transparent,
        centerTitle: true,
        title: Text(
          'User Profile',
          style: theme.textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: colorScheme.onSurface),
          onPressed: () {
            final router = GoRouter.of(context);
            if (router.canPop()) {
              router.pop();
              return;
            }
            router.go('/');
          },
          tooltip: 'Back',
        ),
      ),
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: _loadUserData,
          child: _buildContent(),
        ),
      ),
    );
  }

  Widget _buildContent() {
    final authState = ref.watch(authStateProvider);
    final user = authState.valueOrNull == true
        ? ref.watch(currentUserProvider)
        : null;

    if (user == null) {
      return const ProfileLoginPrompt();
    }

    final userStateAsync = ref.watch(userStateProvider);

    return userStateAsync.when(
      data: (currentUser) {
        final effectiveUser = currentUser ?? user;
        return _buildProfileContent(effectiveUser);
      },
      loading: () => const Center(child: SLLoadingIndicator()),
      error: (error, stack) => SLErrorView(
        message: error.toString(),
        onRetry: () {
          ref.read(userStateProvider.notifier).clearError();
          _runSafe(_loadUserData);
        },
        compact: true,
      ),
    );
  }

  Widget _buildProfileContent(user) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    return ListView(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingL,
        vertical: AppDimens.spaceL,
      ),
      children: [
        ProfileHeader(user: user),
        const SizedBox(height: AppDimens.spaceXL),

        _isEditing
            ? ProfileEditForm(
                controller: _displayNameController,
                formKey: _formKey,
                onCancel: _toggleEditing,
                onSave: _updateProfile,
              )
            : ProfileInfoCard(user: user, onEditPressed: _toggleEditing),

        const SizedBox(height: AppDimens.spaceXL),
        const ThemeToggleCard(),
        const SizedBox(height: AppDimens.spaceXL),

        SLButton(
          text: 'Logout',
          type: SLButtonType.outline,
          prefixIcon: Icons.logout,
          isFullWidth: true,
          backgroundColor: colorScheme.surfaceContainerHighest,
          textColor: colorScheme.error,
          borderColor: colorScheme.error.withValues(alpha: 0.3),
          onPressed: _logout,
        ),

        const SizedBox(height: AppDimens.spaceXXL),
      ],
    );
  }

  void _runSafe(VoidCallback cb) {
    if (!mounted) return;
    Future.microtask(cb);
  }
}
