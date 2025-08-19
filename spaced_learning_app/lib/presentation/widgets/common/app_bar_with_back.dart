import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

/// Tiện ích AppBar với nút back, sử dụng GoRouter
class AppBarWithBack extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final List<Widget>? actions;
  final Color? backgroundColor;
  final Color? foregroundColor;
  final bool canPop;
  final String? fallbackRoute;
  final VoidCallback? onBackPressed;
  final bool implyLeading;
  final Widget? titleWidget;
  final bool centerTitle;

  const AppBarWithBack({
    super.key,
    this.title = '',
    this.actions,
    this.backgroundColor,
    this.foregroundColor,
    this.canPop = true,
    this.fallbackRoute,
    this.onBackPressed,
    this.implyLeading = true,
    this.titleWidget,
    this.centerTitle = true,
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: titleWidget ?? Text(title),
      actions: actions,
      backgroundColor: backgroundColor,
      foregroundColor: foregroundColor,
      automaticallyImplyLeading: implyLeading,
      centerTitle: centerTitle,
      leading: canPop && implyLeading
          ? IconButton(
              icon: const Icon(Icons.arrow_back),
              onPressed: onBackPressed ?? () => _handleBack(context),
            )
          : null,
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  void _handleBack(BuildContext context) {
    final router = GoRouter.of(context);

    if (router.canPop()) {
      router.pop();
    } else if (fallbackRoute != null) {
      router.go(fallbackRoute!);
    }
  }
}
