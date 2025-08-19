import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/constants/app_constants.dart';
import 'package:spaced_learning_app/core/navigation/navigation_helper.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_button.dart';
import 'package:spaced_learning_app/presentation/widgets/common/app_text_field.dart';
import 'package:spaced_learning_app/presentation/widgets/common/error_display.dart';
import 'package:spaced_learning_app/presentation/widgets/common/loading_indicator.dart';

class RegisterScreen extends ConsumerStatefulWidget {
  const RegisterScreen({super.key});

  @override
  ConsumerState<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends ConsumerState<RegisterScreen> {
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController =
      TextEditingController();
  final TextEditingController _firstNameController = TextEditingController();
  final TextEditingController _lastNameController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  String? _usernameError;
  String? _emailError;
  String? _passwordError;
  String? _confirmPasswordError;
  String? _firstNameError;
  String? _lastNameError;

  @override
  void dispose() {
    _usernameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _firstNameController.dispose();
    _lastNameController.dispose();
    super.dispose();
  }

  void _validateUsername() {
    setState(() {
      _usernameError = _usernameController.text.isEmpty
          ? 'Username is required'
          : _usernameController.text.length < 3
          ? 'Username must be at least 3 characters'
          : !RegExp(r'^[a-zA-Z0-9._-]+$').hasMatch(_usernameController.text)
          ? 'Username can only contain letters, numbers, dots, underscores and hyphens'
          : null;
    });
  }

  void _validateEmail() {
    setState(() {
      _emailError = _emailController.text.isEmpty
          ? 'Email is required'
          : !RegExp(
              r'^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$',
            ).hasMatch(_emailController.text)
          ? 'Enter a valid email address'
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

  void _validateConfirmPassword() {
    setState(() {
      _confirmPasswordError = _confirmPasswordController.text.isEmpty
          ? 'Please confirm your password'
          : _confirmPasswordController.text != _passwordController.text
          ? 'Passwords do not match'
          : null;
    });
  }

  void _validateFirstName() {
    setState(() {
      _firstNameError = _firstNameController.text.isEmpty
          ? 'First name is required'
          : null;
    });
  }

  void _validateLastName() {
    setState(() {
      _lastNameError = _lastNameController.text.isEmpty
          ? 'Last name is required'
          : null;
    });
  }

  Future<void> _register() async {
    _validateUsername();
    _validateEmail();
    _validatePassword();
    _validateConfirmPassword();
    _validateFirstName();
    _validateLastName();

    if (_usernameError == null &&
        _emailError == null &&
        _passwordError == null &&
        _confirmPasswordError == null &&
        _firstNameError == null &&
        _lastNameError == null) {
      final authNotifier = ref.read(authStateProvider.notifier);
      final success = await authNotifier.register(
        _usernameController.text,
        _emailController.text,
        _passwordController.text,
        _firstNameController.text,
        _lastNameController.text,
      );

      if (success &&
          mounted &&
          ref.read(authStateProvider).valueOrNull == true) {
        // Sử dụng NavigationHelper để clear stack và đi đến home
        NavigationHelper.clearStackAndGo(context, '/');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final authState = ref.watch(authStateProvider);
    final authError = ref.watch(authErrorProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Register')),
      body: LoadingOverlay(
        isLoading: authState.isLoading,
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
                    _buildSLErrorView(authError, theme),
                    _buildNameFields(),
                    const SizedBox(height: 16),
                    _buildAuthFields(theme),
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
          'Create your account',
          style: theme.textTheme.titleMedium?.copyWith(
            color: theme.colorScheme.onSurfaceVariant,
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 32),
      ],
    );
  }

  Widget _buildSLErrorView(String? errorMessage, ThemeData theme) {
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

  Widget _buildNameFields() {
    return Column(
      children: [
        SLTextField(
          label: 'First Name',
          hint: 'Enter your first name',
          controller: _firstNameController,
          keyboardType: TextInputType.name,
          errorText: _firstNameError,
          prefixIcon: Icons.person,
          onChanged: (_) => _firstNameError = null,
          onEditingComplete: _validateFirstName,
        ),
        const SizedBox(height: 16),
        SLTextField(
          label: 'Last Name',
          hint: 'Enter your last name',
          controller: _lastNameController,
          keyboardType: TextInputType.name,
          errorText: _lastNameError,
          prefixIcon: Icons.person,
          onChanged: (_) => _lastNameError = null,
          onEditingComplete: _validateLastName,
        ),
      ],
    );
  }

  Widget _buildAuthFields(ThemeData theme) {
    return Column(
      children: [
        SLTextField(
          label: 'Username',
          hint: 'Enter your username',
          controller: _usernameController,
          keyboardType: TextInputType.text,
          errorText: _usernameError,
          prefixIcon: Icons.account_circle,
          onChanged: (_) => _usernameError = null,
          onEditingComplete: _validateUsername,
        ),
        const SizedBox(height: 16),
        SLTextField(
          label: 'Email',
          hint: 'Enter your email',
          controller: _emailController,
          keyboardType: TextInputType.emailAddress,
          errorText: _emailError,
          prefixIcon: Icons.email,
          onChanged: (_) => _emailError = null,
          onEditingComplete: _validateEmail,
        ),
        const SizedBox(height: 16),
        SLPasswordField(
          label: 'Password',
          hint: 'Enter your password',
          controller: _passwordController,
          textInputAction: TextInputAction.next,
          errorText: _passwordError,
          prefixIcon: Icon(Icons.lock, color: theme.iconTheme.color),
          onChanged: (_) {
            _passwordError = null;
            if (_confirmPasswordController.text.isNotEmpty) {
              _validateConfirmPassword();
            }
          },
          onEditingComplete: _validatePassword,
        ),
        const SizedBox(height: 16),
        SLPasswordField(
          label: 'Confirm Password',
          hint: 'Confirm your password',
          controller: _confirmPasswordController,
          errorText: _confirmPasswordError,
          prefixIcon: Icon(Icons.lock_outline, color: theme.iconTheme.color),
          onChanged: (_) => _confirmPasswordError = null,
          onEditingComplete: _validateConfirmPassword,
        ),
      ],
    );
  }

  Widget _buildActions(bool isLoading, ThemeData theme) {
    return Column(
      children: [
        const SizedBox(height: 24),
        SLButton(
          text: 'Register',
          onPressed: _register,
          isLoading: isLoading,
          isFullWidth: true,
        ),
        const SizedBox(height: 16),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Already have an account? ',
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: Text(
                'Login',
                style: TextStyle(color: theme.colorScheme.primary),
              ),
            ),
          ],
        ),
      ],
    );
  }
}
