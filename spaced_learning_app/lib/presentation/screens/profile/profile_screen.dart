// lib/presentation/screens/profile/profile_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/presentation/viewmodels/user_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_text_field.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _firstNameController = TextEditingController();
  final TextEditingController _lastNameController = TextEditingController();
  final TextEditingController _displayNameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  bool _isEditing = false;
  bool _isChangingPassword = false;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _emailController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    _displayNameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _loadUserData() {
    final userState = ref.read(userStateProvider);
    if (userState.value != null) {
      final user = userState.value!;
      _usernameController.text = user.username;
      _emailController.text = user.email;
      _firstNameController.text = user.firstName ?? '';
      _lastNameController.text = user.lastName ?? '';
      _displayNameController.text = user.displayName ?? '';
    }
  }

  Future<void> _updateProfile() async {
    if (!_formKey.currentState!.validate()) return;

    final userNotifier = ref.read(userStateProvider.notifier);
    final success = await userNotifier.updateProfile(
      username: _usernameController.text.trim(),
      email: _emailController.text.trim(),
      firstName: _firstNameController.text.trim(),
      lastName: _lastNameController.text.trim(),
      displayName: _displayNameController.text.trim(),
      password: _passwordController.text.isNotEmpty
          ? _passwordController.text
          : null,
    );

    if (success && mounted) {
      setState(() {
        _isEditing = false;
        _isChangingPassword = false;
        _passwordController.clear();
      });

      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Profile updated successfully')),
        );
      }
    }
  }

  Future<void> _deleteProfile() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Account'),
        content: const Text(
          'Are you sure you want to delete your account? This action cannot be undone.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Delete'),
          ),
        ],
      ),
    );

    if (confirmed == true && mounted) {
      final userNotifier = ref.read(userStateProvider.notifier);
      final success = await userNotifier.deleteProfile();

      if (success && mounted) {
        // Navigate to login screen
        if (context.mounted) {
          GoRouter.of(context).go('/login');
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final userState = ref.watch(userStateProvider);
    final userError = ref.watch(userErrorProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        actions: [
          if (!_isEditing)
            IconButton(
              onPressed: () => setState(() => _isEditing = true),
              icon: const Icon(Icons.edit),
            ),
        ],
      ),
      body: LoadingOverlay(
        isLoading: userState.isLoading,
        child: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  _buildHeader(theme, userState.value),
                  _buildErrorView(userError, theme),
                  _buildProfileForm(theme),
                  if (_isEditing) _buildActions(theme),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(ThemeData theme, user) {
    return Column(
      children: [
        CircleAvatar(
          radius: 50,
          backgroundColor: theme.colorScheme.primary,
          child: Text(
            user?.displayName?.substring(0, 1).toUpperCase() ??
                user?.username?.substring(0, 1).toUpperCase() ??
                'U',
            style: theme.textTheme.headlineMedium?.copyWith(
              color: theme.colorScheme.onPrimary,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        const SizedBox(height: 16),
        Text(
          user?.displayName ?? user?.username ?? 'User',
          style: theme.textTheme.headlineSmall?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
        if (user?.email != null) ...[
          const SizedBox(height: 8),
          Text(
            user.email,
            style: theme.textTheme.bodyMedium?.copyWith(
              color: theme.colorScheme.onSurfaceVariant,
            ),
          ),
        ],
        const SizedBox(height: 32),
      ],
    );
  }

  Widget _buildErrorView(String? errorMessage, ThemeData theme) {
    return errorMessage != null
        ? Column(
            children: [
              SLErrorView(
                message: errorMessage,
                compact: true,
                onRetry: () =>
                    ref.read(userErrorProvider.notifier).clearError(),
              ),
              const SizedBox(height: 16),
            ],
          )
        : const SizedBox.shrink();
  }

  Widget _buildProfileForm(ThemeData theme) {
    return Column(
      children: [
        SLTextField(
          label: 'Username',
          hint: 'Enter your username',
          controller: _usernameController,
          enabled: _isEditing,
          keyboardType: TextInputType.text,
          errorText: _isEditing && _usernameController.text.trim().isEmpty
              ? 'Username is required'
              : null,
          prefixIcon: Icons.account_circle,
        ),
        const SizedBox(height: 16),
        SLTextField(
          label: 'Email',
          hint: 'Enter your email',
          controller: _emailController,
          enabled: _isEditing,
          keyboardType: TextInputType.emailAddress,
          errorText: _isEditing && _emailController.text.trim().isEmpty
              ? 'Email is required'
              : null,
          prefixIcon: Icons.email,
        ),
        const SizedBox(height: 16),
        SLTextField(
          label: 'First Name',
          hint: 'Enter your first name',
          controller: _firstNameController,
          enabled: _isEditing,
          keyboardType: TextInputType.name,
          prefixIcon: Icons.person,
        ),
        const SizedBox(height: 16),
        SLTextField(
          label: 'Last Name',
          hint: 'Enter your last name',
          controller: _lastNameController,
          enabled: _isEditing,
          keyboardType: TextInputType.name,
          prefixIcon: Icons.person,
        ),
        const SizedBox(height: 16),
        SLTextField(
          label: 'Display Name',
          hint: 'Enter your display name',
          controller: _displayNameController,
          enabled: _isEditing,
          keyboardType: TextInputType.text,
          prefixIcon: Icons.badge,
        ),
        if (_isEditing) ...[
          const SizedBox(height: 24),
          _buildPasswordSection(theme),
        ],
      ],
    );
  }

  Widget _buildPasswordSection(ThemeData theme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Text('Change Password', style: theme.textTheme.titleMedium),
            const SizedBox(width: 8),
            Switch(
              value: _isChangingPassword,
              onChanged: (value) => setState(() => _isChangingPassword = value),
            ),
          ],
        ),
        if (_isChangingPassword) ...[
          const SizedBox(height: 16),
          SLPasswordField(
            label: 'New Password',
            hint: 'Enter new password',
            controller: _passwordController,
            errorText:
                _passwordController.text.isNotEmpty &&
                    _passwordController.text.length < 8
                ? 'Password must be at least 8 characters'
                : null,
            prefixIcon: Icon(Icons.lock, color: theme.iconTheme.color),
          ),
        ],
      ],
    );
  }

  Widget _buildActions(ThemeData theme) {
    return Column(
      children: [
        const SizedBox(height: 32),
        SLButton(
          text: 'Save Changes',
          onPressed: _updateProfile,
          isLoading: ref.watch(userStateProvider).isLoading,
          isFullWidth: true,
        ),
        const SizedBox(height: 16),
        SLButton(
          text: 'Cancel',
          onPressed: () {
            setState(() {
              _isEditing = false;
              _isChangingPassword = false;
              _passwordController.clear();
            });
            _loadUserData(); // Reset to original values
          },
          isFullWidth: true,
          type: SLButtonType.outline,
        ),
        const SizedBox(height: 32),
        Divider(color: theme.colorScheme.outline),
        const SizedBox(height: 16),
        SLButton(
          text: 'Delete Account',
          onPressed: _deleteProfile,
          isFullWidth: true,
          type: SLButtonType.error,
        ),
      ],
    );
  }
}
