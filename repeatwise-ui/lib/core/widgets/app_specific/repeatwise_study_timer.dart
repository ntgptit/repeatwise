import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../layout/repeatwise_card.dart';

/// RepeatWise study session timer widget
class RepeatWiseStudyTimer extends ConsumerStatefulWidget {
  final int durationMinutes;
  final VoidCallback? onComplete;
  final VoidCallback? onPause;
  final VoidCallback? onResume;

  const RepeatWiseStudyTimer({
    super.key,
    required this.durationMinutes,
    this.onComplete,
    this.onPause,
    this.onResume,
  });

  @override
  ConsumerState<RepeatWiseStudyTimer> createState() => _RepeatWiseStudyTimerState();
}

class _RepeatWiseStudyTimerState extends ConsumerState<RepeatWiseStudyTimer>
    with TickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _animation;
  bool _isPaused = false;
  int _remainingSeconds = 0;

  @override
  void initState() {
    super.initState();
    _remainingSeconds = widget.durationMinutes * 60;
    
    _animationController = AnimationController(
      duration: Duration(seconds: _remainingSeconds),
      vsync: this,
    );
    
    _animation = Tween<double>(
      begin: 1.0,
      end: 0.0,
    ).animate(_animationController);

    _animationController.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        widget.onComplete?.call();
      }
    });

    _startTimer();
  }

  void _startTimer() {
    _animationController.forward();
    _startCountdown();
  }

  void _startCountdown() {
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted && !_isPaused) {
        setState(() {
          _remainingSeconds--;
        });
        if (_remainingSeconds > 0) {
          _startCountdown();
        }
      }
    });
  }

  void _togglePause() {
    setState(() {
      _isPaused = !_isPaused;
    });

    if (_isPaused) {
      _animationController.stop();
      widget.onPause?.call();
    } else {
      _animationController.forward();
      _startCountdown();
      widget.onResume?.call();
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final minutes = _remainingSeconds ~/ 60;
    final seconds = _remainingSeconds % 60;
    
    return RepeatWiseCard(
      child: Column(
        children: [
          SizedBox(
            height: 120,
            width: 120,
            child: AnimatedBuilder(
              animation: _animation,
              builder: (context, child) {
                return CircularProgressIndicator(
                  value: _animation.value,
                  strokeWidth: 8,
                  valueColor: AlwaysStoppedAnimation<Color>(
                    theme.colorScheme.primary,
                  ),
                  backgroundColor: theme.colorScheme.surfaceVariant,
                );
              },
            ),
          ),
          const SizedBox(height: 16),
          Text(
            '${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}',
            style: theme.textTheme.headlineLarge?.copyWith(
              fontWeight: FontWeight.bold,
              color: theme.colorScheme.primary,
            ),
          ),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              FilledButton.icon(
                onPressed: _togglePause,
                icon: Icon(_isPaused ? Icons.play_arrow : Icons.pause),
                label: Text(_isPaused ? 'Resume' : 'Pause'),
              ),
              OutlinedButton.icon(
                onPressed: () {
                  _animationController.reset();
                  setState(() {
                    _remainingSeconds = widget.durationMinutes * 60;
                    _isPaused = false;
                  });
                  _startTimer();
                },
                icon: const Icon(Icons.refresh),
                label: const Text('Reset'),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
