import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';

class SLTextField extends StatefulWidget {
  final String? label;
  final String? hint;
  final String? errorText;
  final String? helperText;
  final TextEditingController? controller;
  final TextInputType keyboardType;
  final bool obscureText;
  final FocusNode? focusNode;
  final ValueChanged<String>? onChanged;
  final VoidCallback? onTap;
  final bool readOnly;
  final bool enabled;
  final IconData? prefixIcon;
  final Widget? prefix;
  final IconData? suffixIcon;
  final Widget? suffix;
  final VoidCallback? onSuffixIconTap;
  final int? maxLength;
  final int? maxLines;
  final int? minLines;
  final List<TextInputFormatter>? inputFormatters;
  final bool showCounter;
  final bool autofocus;
  final TextAlign textAlign;
  final TextCapitalization textCapitalization;
  final String? Function(String?)? validator;
  final AutovalidateMode autovalidateMode;
  final Color? fillColor;
  final Color? labelColor;
  final Color? hintColor;
  final Color? errorColor;
  final Color? borderColor;
  final Color? focusedBorderColor;
  final Color? backgroundColor;
  final EdgeInsetsGeometry? contentPadding;
  final TextInputAction? textInputAction;
  final VoidCallback? onEditingComplete;
  final ValueChanged<String>? onSubmitted;

  const SLTextField({
    super.key,
    this.label,
    this.hint,
    this.errorText,
    this.helperText,
    this.controller,
    this.keyboardType = TextInputType.text,
    this.obscureText = false,
    this.focusNode,
    this.onChanged,
    this.onTap,
    this.readOnly = false,
    this.enabled = true,
    this.prefixIcon,
    this.prefix,
    this.suffixIcon,
    this.suffix,
    this.onSuffixIconTap,
    this.maxLength,
    this.maxLines = 1,
    this.minLines,
    this.inputFormatters,
    this.showCounter = false,
    this.autofocus = false,
    this.textAlign = TextAlign.start,
    this.textCapitalization = TextCapitalization.none,
    this.validator,
    this.autovalidateMode = AutovalidateMode.onUserInteraction,
    this.fillColor,
    this.labelColor,
    this.hintColor,
    this.errorColor,
    this.borderColor,
    this.focusedBorderColor,
    this.backgroundColor,
    this.contentPadding,
    this.textInputAction,
    this.onEditingComplete,
    this.onSubmitted,
  });

  @override
  State<SLTextField> createState() => _SLTextFieldState();
}

class _SLTextFieldState extends State<SLTextField> {
  bool _passwordVisible = false;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;

    Widget? suffixIconWidget;
    if (widget.suffixIcon != null) {
      suffixIconWidget = InkWell(
        onTap: widget.onSuffixIconTap,
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingS),
          child: Icon(
            widget.suffixIcon,
            color: widget.errorText != null
                ? colorScheme.error
                : colorScheme.primary,
            size: AppDimens.iconM,
          ),
        ),
      );
    } else if (widget.obscureText) {
      suffixIconWidget = InkWell(
        onTap: () {
          setState(() {
            _passwordVisible = !_passwordVisible;
          });
        },
        borderRadius: BorderRadius.circular(AppDimens.radiusXXL),
        child: Padding(
          padding: const EdgeInsets.all(AppDimens.paddingS),
          child: Icon(
            _passwordVisible
                ? Icons.visibility_off_outlined
                : Icons.visibility_outlined,
            color: colorScheme.secondary,
            size: AppDimens.iconM,
          ),
        ),
      );
    }

    return Container(
      color: widget.backgroundColor ?? Colors.transparent,
      child: TextFormField(
        controller: widget.controller,
        focusNode: widget.focusNode,
        keyboardType: widget.keyboardType,
        textInputAction: widget.textInputAction,
        textCapitalization: widget.textCapitalization,
        textAlign: widget.textAlign,
        readOnly: widget.readOnly,
        enabled: widget.enabled,
        obscureText: widget.obscureText && !_passwordVisible,
        maxLength: widget.maxLength,
        maxLines: widget.obscureText ? 1 : widget.maxLines,
        minLines: widget.minLines,
        autofocus: widget.autofocus,
        onChanged: widget.onChanged,
        onTap: widget.onTap,
        inputFormatters: widget.inputFormatters,
        onEditingComplete: widget.onEditingComplete,
        onFieldSubmitted: widget.onSubmitted,
        autovalidateMode: widget.autovalidateMode,
        validator: widget.validator,
        style: theme.textTheme.bodyLarge!.copyWith(
          color: widget.enabled
              ? colorScheme.onSurface
              : colorScheme.onSurface.withValues(
                  alpha: AppDimens.opacityDisabled,
                ),
        ),
        decoration: InputDecoration(
          labelText: widget.label,
          hintText: widget.hint,
          errorText: widget.errorText,
          helperText: widget.helperText,
          filled: true,
          fillColor: widget.fillColor ?? colorScheme.surfaceContainerLowest,
          contentPadding:
              widget.contentPadding ??
              const EdgeInsets.symmetric(
                horizontal: AppDimens.paddingL,
                vertical: AppDimens.paddingL,
              ),
          prefixIcon: widget.prefixIcon != null
              ? Icon(
                  widget.prefixIcon,
                  color: widget.errorText != null
                      ? colorScheme.error
                      : colorScheme.primary,
                  size: AppDimens.iconM,
                )
              : widget.prefix,
          suffixIcon: suffixIconWidget ?? widget.suffix,
          counterText: widget.showCounter ? null : '',
          labelStyle: TextStyle(
            color: widget.labelColor ?? colorScheme.onSurface,
          ),
          hintStyle: TextStyle(
            color: widget.hintColor ?? colorScheme.onSurfaceVariant,
          ),
          errorStyle: TextStyle(color: widget.errorColor ?? colorScheme.error),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            borderSide: BorderSide(color: colorScheme.outline),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            borderSide: BorderSide(
              color: widget.borderColor ?? colorScheme.outline,
            ),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            borderSide: BorderSide(
              color: widget.focusedBorderColor ?? colorScheme.primary,
              width: 2,
            ),
          ),
          errorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            borderSide: BorderSide(color: colorScheme.error),
          ),
          focusedErrorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            borderSide: BorderSide(color: colorScheme.error, width: 2),
          ),
          disabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            borderSide: BorderSide(
              color: colorScheme.onSurface.withValues(
                alpha: AppDimens.opacityDisabled,
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class SLPasswordField extends StatefulWidget {
  final String? label;
  final String? hint;
  final String? errorText;
  final TextEditingController? controller;
  final Widget? prefixIcon;
  final ValueChanged<String>? onChanged;
  final VoidCallback? onEditingComplete;
  final TextInputAction? textInputAction;
  final FocusNode? focusNode;
  final String? Function(String?)? validator;
  final Color? labelColor;
  final Color? hintColor;
  final Color? errorColor;
  final Color? borderColor;
  final Color? focusedBorderColor;
  final Color? backgroundColor;

  const SLPasswordField({
    super.key,
    this.label,
    this.hint,
    this.errorText,
    this.controller,
    this.prefixIcon,
    this.onChanged,
    this.onEditingComplete,
    this.textInputAction = TextInputAction.done,
    this.focusNode,
    this.validator,
    this.labelColor,
    this.hintColor,
    this.errorColor,
    this.borderColor,
    this.focusedBorderColor,
    this.backgroundColor,
  });

  @override
  State<SLPasswordField> createState() => _SLPasswordFieldState();
}

class _SLPasswordFieldState extends State<SLPasswordField> {
  @override
  Widget build(BuildContext context) {
    return SLTextField(
      label: widget.label,
      hint: widget.hint,
      controller: widget.controller,
      errorText: widget.errorText,
      prefix: widget.prefixIcon,
      obscureText: true,
      keyboardType: TextInputType.visiblePassword,
      onChanged: widget.onChanged,
      onEditingComplete: widget.onEditingComplete,
      textInputAction: widget.textInputAction,
      focusNode: widget.focusNode,
      validator: widget.validator,
      labelColor: widget.labelColor,
      hintColor: widget.hintColor,
      errorColor: widget.errorColor,
      borderColor: widget.borderColor,
      focusedBorderColor: widget.focusedBorderColor,
      backgroundColor: widget.backgroundColor,
    );
  }
}
