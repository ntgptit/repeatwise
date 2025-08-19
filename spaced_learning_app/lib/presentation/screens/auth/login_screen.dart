import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/presentation/screens/auth/register_screen.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_text_field.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final TextEditingController _usernameOrEmailController =
      TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  String? _usernameOrEmailError;
  String? _passwordError;

  @override
  void dispose() {
    _usernameOrEmailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _validateUsernameOrEmail() {
    setState(() {
      _usernameOrEmailError = _usernameOrEmailController.text.isEmpty
          ? 'Username or email is required'
          : null;
    });
  }

  void _validatePassword() {
    setState(() {
      _passwordError = _passwordController.text.isEmpty
          ? 'Password is required'
          : _passwordController.text.length < 8
          ? 'Password must be at least 8 characters'
          : null;
    });
  }

  Future<void> _login() async {
    _validateUsernameOrEmail();
    _validatePassword();

    if (_usernameOrEmailError == null && _passwordError == null) {
      // Clear any previous errors
      ref.read(authErrorProvider.notifier).clearError();

      final authNotifier = ref.read(authStateProvider.notifier);
      final success = await authNotifier.login(
        _usernameOrEmailController.text.trim(),
        _passwordController.text,
      );

      if (success &&
          mounted &&
          ref.read(authStateProvider).valueOrNull == true) {
        // Navigate to home screen
        if (context.mounted) {
          GoRouter.of(context).go('/');
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final authState = ref.watch(authStateProvider);
    final authError = ref.watch(authErrorProvider);

    return Scaffold(
      backgroundColor: theme.colorScheme.surface,
      body: LoadingOverlay(
        isLoading: authState.isLoading,
        color: theme.colorScheme.surfaceContainerHighest,
        child: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24.0),
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    _buildHeader(theme),
                    _buildErrorView(authError, theme),
                    _buildFormFields(theme),
                    _buildActions(authState.isLoading, theme),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(ThemeData theme) {
    return Column(
      children: [
        Text(
          AppConstants.appName,
          style: theme.textTheme.headlineMedium?.copyWith(
            fontWeight: FontWeight.bold,
            color: theme.colorScheme.primary,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        Text(
          'Login to your account',
          style: theme.textTheme.titleMedium?.copyWith(
            color: theme.colorScheme.onSurfaceVariant,
          ),
          textAlign: TextAlign.center,
        ),
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
                    ref.read(authErrorProvider.notifier).clearError(),
              ),
              const SizedBox(height: 16),
            ],
          )
        : const SizedBox.shrink();
  }

  Widget _buildFormFields(ThemeData theme) {
    return Column(
      children: [
        SLTextField(
          label: 'Username or Email',
          hint: 'Enter your username or email',
          controller: _usernameOrEmailController,
          keyboardType: TextInputType.text,
          textInputAction: TextInputAction.next,
          errorText: _usernameOrEmailError,
          prefixIcon: Icons.person,
          onChanged: (_) {
            setState(() => _usernameOrEmailError = null);
            ref.read(authErrorProvider.notifier).clearError();
          },
          onEditingComplete: _validateUsernameOrEmail,
        ),
        const SizedBox(height: 16),
        SLPasswordField(
          label: 'Password',
          hint: 'Enter your password',
          controller: _passwordController,
          textInputAction: TextInputAction.done,
          errorText: _passwordError,
          prefixIcon: Icon(Icons.lock, color: theme.iconTheme.color),
          onChanged: (_) {
            setState(() => _passwordError = null);
            ref.read(authErrorProvider.notifier).clearError();
          },
          onEditingComplete: _validatePassword,
        ),
      ],
    );
  }

  Widget _buildActions(bool isLoading, ThemeData theme) {
    return Column(
      children: [
        const SizedBox(height: 24),
        SLButton(
          text: 'Login',
          onPressed: isLoading ? null : _login,
          isLoading: isLoading,
          isFullWidth: true,
        ),
        const SizedBox(height: 16),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              "Don't have an account? ",
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
            TextButton(
              onPressed: isLoading
                  ? null
                  : () => Navigator.of(context).push(
                      MaterialPageRoute(builder: (_) => const RegisterScreen()),
                    ),
              child: const Text('Register'),
            ),
          ],
        ),
      ],
    );
  }
}
