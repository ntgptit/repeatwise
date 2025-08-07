import 'package:flutter/material.dart';

/// RepeatWise avatar with Material 3 design
class RepeatWiseAvatar extends StatelessWidget {
  final String? imageUrl;
  final String? name;
  final double size;
  final Color? backgroundColor;

  const RepeatWiseAvatar({
    super.key,
    this.imageUrl,
    this.name,
    this.size = 40,
    this.backgroundColor,
  });

  @override
  Widget build(BuildContext context) {
    if (imageUrl != null && imageUrl!.isNotEmpty) {
      return CircleAvatar(
        radius: size / 2,
        backgroundImage: NetworkImage(imageUrl!),
        backgroundColor: backgroundColor ?? Theme.of(context).colorScheme.surfaceVariant,
      );
    }

    return CircleAvatar(
      radius: size / 2,
      backgroundColor: backgroundColor ?? Theme.of(context).colorScheme.primary,
      child: Text(
        _getInitials(name ?? ''),
        style: TextStyle(
          color: Theme.of(context).colorScheme.onPrimary,
          fontSize: size * 0.4,
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }

  String _getInitials(String name) {
    if (name.isEmpty) return '?';
    
    final parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return '${parts[0][0]}${parts[1][0]}'.toUpperCase();
    }
    return name[0].toUpperCase();
  }
}
