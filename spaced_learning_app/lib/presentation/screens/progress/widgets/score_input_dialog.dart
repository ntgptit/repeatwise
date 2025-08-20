import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/core/theme/theme_extensions.dart';

class ScoreInputDialog {
  static Future<double?> show(BuildContext context) async {
    final scoreNotifier = ValueNotifier<double>(80.0);

    final confirmed = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => ProviderScope(
        child: AlertDialog(
          insetPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 16,
          ),
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 8,
          ),
          titlePadding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(AppDimens.radiusL),
          ),
          title: const Text('Enter Test Score'),
          content: ConstrainedBox(
            constraints: const BoxConstraints(minWidth: 320, maxWidth: 440),
            child: ScoreInputDialogContent(scoreNotifier: scoreNotifier),
          ),
          actionsPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 8,
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(dialogContext, false),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () => Navigator.pop(dialogContext, true),
              child: const Text('Confirm'),
            ),
          ],
        ),
      ),
    );

    final finalScore = confirmed == true ? scoreNotifier.value : null;
    scoreNotifier.dispose();
    return finalScore;
  }
}

class ScoreInputDialogContent extends ConsumerStatefulWidget {
  final ValueNotifier<double> scoreNotifier;

  const ScoreInputDialogContent({super.key, required this.scoreNotifier});

  @override
  ConsumerState<ScoreInputDialogContent> createState() =>
      _ScoreInputDialogContentState();
}

class _ScoreInputDialogContentState
    extends ConsumerState<ScoreInputDialogContent>
    with SingleTickerProviderStateMixin {
  late double _currentScore;
  late TextEditingController _controller;
  late AnimationController _animationController;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _currentScore = widget.scoreNotifier.value.clamp(0.0, 100.0);
    _controller = TextEditingController(text: _currentScore.toInt().toString());
    _controller.addListener(_onTextChanged);

    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: AppDimens.durationM),
    );

    _animation = CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeOut,
    );

    _animationController.forward();
  }

  @override
  void dispose() {
    _controller.removeListener(_onTextChanged);
    _controller.dispose();
    _animationController.dispose();
    super.dispose();
  }

  void _onTextChanged() {
    final value = double.tryParse(_controller.text);
    if (value != null && value != _currentScore) {
      _updateScore(value);
    }
  }

  void _updateScore(double newScore) {
    final clampedScore = newScore.clamp(0.0, 100.0);
    if (_currentScore != clampedScore) {
      setState(() {
        _currentScore = clampedScore;
        widget.scoreNotifier.value = _currentScore;

        final scoreText = clampedScore.toInt().toString();
        if (_controller.text != scoreText) {
          final selection = _controller.selection;
          _controller.text = scoreText;

          if (selection.isValid) {
            try {
              final newOffset = selection.baseOffset.clamp(0, scoreText.length);
              _controller.selection = TextSelection.collapsed(
                offset: newOffset,
              );
            } catch (_) {
              _controller.selection = TextSelection.collapsed(
                offset: scoreText.length,
              );
            }
          }
        }
      });
    }
  }

  Color _getContrastTextColor(Color bg) =>
      bg.computeLuminance() > 0.5 ? Colors.black : Colors.white;

  Color _getScoreColor(ColorScheme scheme, double score) {
    if (score >= 90) return Colors.green.shade700;
    if (score >= 75) return scheme.primary;
    if (score >= 60) return scheme.secondary;
    if (score >= 40) return Colors.orange.shade700;
    return scheme.error;
  }

  Widget _buildSlider(ColorScheme colorScheme) {
    final sliderColor = _getScoreColor(colorScheme, _currentScore);

    return SliderTheme(
      data: SliderThemeData(
        activeTrackColor: sliderColor,
        thumbColor: sliderColor,
        overlayColor: sliderColor.withValues(alpha: AppDimens.opacityMedium),
        trackHeight: 8,
        thumbShape: const RoundSliderThumbShape(enabledThumbRadius: 12),
        overlayShape: const RoundSliderOverlayShape(overlayRadius: 24),
        inactiveTrackColor: colorScheme.surfaceContainerHighest,
      ),
      child: Slider(
        value: _currentScore,
        min: 0,
        max: 100,
        divisions: 100,
        label: '${_currentScore.toInt()}%',
        onChanged: _updateScore,
      ),
    );
  }

  Widget _buildScoreDisplay(TextStyle? style, Color color) {
    final textColor = _getContrastTextColor(color);
    return Container(
      height: AppDimens.iconXXL * 2,
      decoration: BoxDecoration(
        color: color.withValues(alpha: AppDimens.opacitySemi),
        borderRadius: BorderRadius.circular(AppDimens.radiusL),
        border: Border.all(
          color: color.withValues(alpha: AppDimens.opacityHigh),
          width: 1.5,
        ),
      ),
      child: Center(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              '${_currentScore.toInt()}',
              style: style?.copyWith(color: textColor, fontSize: 48),
            ),
            Text('%', style: style?.copyWith(color: textColor, fontSize: 36)),
          ],
        ),
      ),
    );
  }

  Widget _buildExactScore(ThemeData theme, ColorScheme scheme) {
    final scoreColor = _getScoreColor(scheme, _currentScore);
    final bgColor = scoreColor.withValues(
      alpha: scoreColor.computeLuminance() > 0.5 ? 0.2 : 0.1,
    );

    return Padding(
      padding: const EdgeInsets.symmetric(
        horizontal: AppDimens.paddingS,
        vertical: AppDimens.paddingM,
      ),
      child: Row(
        children: [
          Text(
            'Exact score: ',
            style: theme.textTheme.bodyMedium?.copyWith(
              color: scheme.onSurface,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(width: AppDimens.spaceS),
          Expanded(
            child: Container(
              padding: const EdgeInsets.symmetric(
                horizontal: AppDimens.paddingS,
              ),
              decoration: BoxDecoration(
                color: bgColor,
                borderRadius: BorderRadius.circular(AppDimens.radiusM),
                border: Border.all(color: scoreColor, width: 2),
              ),
              child: IntrinsicWidth(
                child: TextField(
                  controller: _controller,
                  keyboardType: const TextInputType.numberWithOptions(
                    decimal: false,
                  ),
                  textAlign: TextAlign.center,
                  maxLength: 3,
                  decoration: InputDecoration(
                    filled: true,
                    fillColor: Colors.transparent,
                    suffixText: '%',
                    counterText: '',
                    border: InputBorder.none,
                    suffixStyle: TextStyle(
                      color: scoreColor,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  style: TextStyle(
                    color: scoreColor,
                    fontWeight: FontWeight.bold,
                    fontSize: 18,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickOptions(ColorScheme scheme, TextTheme textTheme) {
    final options = [0, 25, 50, 75, 100];
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: options.map((score) {
        final selected = _currentScore.round() == score;
        return _ScoreButton(
          score: score,
          isSelected: selected,
          onTap: () => _updateScore(score.toDouble()),
          colorScheme: scheme,
          textTheme: textTheme,
        );
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final scheme = theme.colorScheme;
    final scoreColor = theme.getScoreColor(_currentScore);

    return FadeTransition(
      opacity: _animation,
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _buildHeader(theme, scheme),
            const SizedBox(height: AppDimens.spaceL),
            _buildScoreDisplay(
              theme.textTheme.displaySmall?.copyWith(
                fontWeight: FontWeight.bold,
              ),
              scoreColor,
            ),
            const SizedBox(height: AppDimens.spaceS),
            _buildSlider(scheme),
            _buildExactScore(theme, scheme),
            const SizedBox(height: AppDimens.spaceL),
            _buildQuickOptions(scheme, theme.textTheme),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(ThemeData theme, ColorScheme scheme) {
    return Container(
      padding: const EdgeInsets.all(AppDimens.paddingM),
      decoration: BoxDecoration(
        color: scheme.surfaceContainerLow,
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
      ),
      child: Row(
        children: [
          Icon(Icons.info_outline, color: scheme.primary),
          const SizedBox(width: AppDimens.spaceM),
          Expanded(
            child: Text(
              'Enter the score from your test on Quizlet or another tool:',
              style: theme.textTheme.bodyMedium?.copyWith(
                color: scheme.onSurface,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _ScoreButton extends StatelessWidget {
  final int score;
  final bool isSelected;
  final VoidCallback onTap;
  final ColorScheme colorScheme;
  final TextTheme textTheme;

  const _ScoreButton({
    required this.score,
    required this.isSelected,
    required this.onTap,
    required this.colorScheme,
    required this.textTheme,
  });

  Color get _color {
    if (score >= 90) return Colors.green.shade700;
    if (score >= 75) return colorScheme.primary;
    if (score >= 60) return colorScheme.secondary;
    if (score >= 40) return Colors.orange.shade700;
    return colorScheme.error;
  }

  Color _getContrastTextColor(Color color) =>
      color.computeLuminance() > 0.5 ? Colors.black : Colors.white;

  @override
  Widget build(BuildContext context) {
    final bgColor = isSelected ? _color : colorScheme.surfaceContainerHighest;
    final fgColor = _getContrastTextColor(bgColor);

    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(AppDimens.radiusM),
        child: Container(
          width: AppDimens.iconXL + AppDimens.spaceS,
          height: AppDimens.iconXL + AppDimens.spaceS,
          decoration: BoxDecoration(
            color: bgColor,
            borderRadius: BorderRadius.circular(AppDimens.radiusM),
            border: isSelected ? null : Border.all(color: _color, width: 2),
            boxShadow: isSelected
                ? [
                    BoxShadow(
                      color: _color.withValues(alpha: AppDimens.opacityHigh),
                      blurRadius: 4,
                      offset: const Offset(0, 2),
                    ),
                  ]
                : null,
          ),
          child: Center(
            child: Text(
              '$score%',
              style: textTheme.labelLarge?.copyWith(
                color: fgColor,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.w600,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
