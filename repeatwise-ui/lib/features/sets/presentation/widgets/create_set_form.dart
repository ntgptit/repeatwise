import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/models/set.dart';
import '../../../../core/theme/app_theme.dart';
import '../../providers/set_providers.dart';

class CreateSetForm extends ConsumerStatefulWidget {
  final VoidCallback? onSuccess;

  const CreateSetForm({super.key, this.onSuccess});

  @override
  ConsumerState<CreateSetForm> createState() => _CreateSetFormState();
}

class _CreateSetFormState extends ConsumerState<CreateSetForm> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _wordCountController = TextEditingController();

  bool _isLoading = false;
  String? _errorMessage;

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    _wordCountController.dispose();
    super.dispose();
  }

  Future<void> _submitForm() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final request = SetCreateRequest(
        name: _nameController.text.trim(),
        description: _descriptionController.text.trim(),
      );

      await ref.read(setsNotifierProvider.notifier).createSet(request);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Set created successfully!'),
            backgroundColor: AppTheme.successColor,
          ),
        );
        widget.onSuccess?.call();
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _errorMessage = e.toString();
        });
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          TextFormField(
            controller: _nameController,
            decoration: const InputDecoration(
              labelText: 'Set Name *',
              hintText: 'Enter set name',
              border: OutlineInputBorder(),
            ),
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Set name is required';
              }
              if (value.trim().length < 3) {
                return 'Set name must be at least 3 characters';
              }
              if (value.trim().length > 128) {
                return 'Set name must not exceed 128 characters';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: _descriptionController,
            decoration: const InputDecoration(
              labelText: 'Description',
              hintText: 'Enter set description (optional)',
              border: OutlineInputBorder(),
            ),
            maxLines: 3,
            validator: (value) {
              if (value != null && value.trim().length > 1000) {
                return 'Description must not exceed 1000 characters';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: _wordCountController,
            decoration: const InputDecoration(
              labelText: 'Word Count *',
              hintText: 'Enter number of words',
              border: OutlineInputBorder(),
            ),
            keyboardType: TextInputType.number,
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Word count is required';
              }
              final wordCount = int.tryParse(value.trim());
              if (wordCount == null) {
                return 'Please enter a valid number';
              }
              if (wordCount < 1) {
                return 'Word count must be at least 1';
              }
              if (wordCount > 10000) {
                return 'Word count must not exceed 10000';
              }
              return null;
            },
          ),
          const SizedBox(height: 24),
          if (_errorMessage != null)
            Container(
              padding: const EdgeInsets.all(12),
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                color: AppTheme.errorColor.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: AppTheme.errorColor),
              ),
              child: Text(
                _errorMessage!,
                style: TextStyle(color: AppTheme.errorColor),
              ),
            ),
          ElevatedButton(
            onPressed: _isLoading ? null : _submitForm,
            style: ElevatedButton.styleFrom(
              backgroundColor: AppTheme.primaryColor,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 16),
            ),
            child: _isLoading
                ? const SizedBox(
                    height: 20,
                    width: 20,
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                    ),
                  )
                : const Text('Create Set'),
          ),
        ],
      ),
    );
  }
}
