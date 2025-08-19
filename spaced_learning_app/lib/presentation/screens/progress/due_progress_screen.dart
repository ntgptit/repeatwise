// lib/presentation/screens/progress/due_progress_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/theme/app_dimens.dart';
import 'package:spaced_learning_app/presentation/viewmodels/auth_viewmodel.dart';
import 'package:spaced_learning_app/presentation/viewmodels/progress_viewmodel.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/due_progress_filter_card.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/due_progress_list.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/due_progress_login_prompt.dart';
import 'package:spaced_learning_app/presentation/widgets/progress/due_progress_summary.dart';

class DueProgressScreen extends ConsumerStatefulWidget {
  const DueProgressScreen({super.key});

  @override
  ConsumerState<DueProgressScreen> createState() => _DueProgressScreenState();
}

class _DueProgressScreenState extends ConsumerState<DueProgressScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;

  DateTime? _selectedDate;
  bool _isLoading = false;
  final ScrollController _scrollController = ScrollController();
  bool _isScrolled = false;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    _fadeAnimation = CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    );
    _animationController.forward();

    _scrollController.addListener(_onScroll);

    WidgetsBinding.instance.addPostFrameCallback((_) => _loadData());
  }

  void _onScroll() {
    final isScrolled = _scrollController.offset > 10;
    if (isScrolled != _isScrolled) {
      setState(() => _isScrolled = isScrolled);
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    _scrollController.removeListener(_onScroll);
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    if (!mounted) return;

    final authState = ref.read(authStateProvider);
    final isAuthorized = authState.valueOrNull ?? false;
    final currentUser = ref.read(currentUserProvider);

    if (!isAuthorized || currentUser == null) {
      return;
    }

    setState(() => _isLoading = true);

    await ref
        .read(progressStateProvider.notifier)
        .loadDueProgress(currentUser.id, studyDate: _selectedDate);

    if (mounted) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _selectDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2101),
      builder: (context, child) => Theme(
        data: Theme.of(context).copyWith(
          colorScheme: Theme.of(context).colorScheme,
          dialogTheme: DialogThemeData(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(AppDimens.radiusL),
            ),
          ),
        ),
        child: child!,
      ),
    );

    if (picked == null || picked == _selectedDate) return;

    setState(() => _selectedDate = picked);
    _animationController.reset();
    _animationController.forward();
    await _loadData();
  }

  void _clearDateFilter() {
    setState(() => _selectedDate = null);
    _animationController.reset();
    _animationController.forward();
    _loadData();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authStateProvider);
    final isAuthorized = authState.valueOrNull ?? false;
    final currentUser = ref.watch(currentUserProvider);

    if (!isAuthorized || currentUser == null) {
      return const DueProgressLoginPrompt();
    }

    return Scaffold(
      body: NestedScrollView(
        controller: _scrollController,
        headerSliverBuilder: (context, innerBoxIsScrolled) => [
          SliverAppBar(
            leading: IconButton(
              icon: const Icon(Icons.arrow_back),
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
            title: const Text('Due Today'),
            pinned: true,
            floating: true,
            elevation: _isScrolled ? 2.0 : 0.0,
            forceElevated: _isScrolled || innerBoxIsScrolled,
            actions: [
              IconButton(
                icon: const Icon(Icons.refresh),
                onPressed: _loadData,
                tooltip: 'Refresh data',
              ),
            ],
            bottom: PreferredSize(
              preferredSize: const Size.fromHeight(80),
              child: DueProgressFilterCard(
                selectedDate: _selectedDate,
                onSelectDate: _selectDate,
                onClearDate: _clearDateFilter,
              ),
            ),
          ),
        ],
        body: _buildBody(),
      ),
    );
  }

  Widget _buildBody() => Padding(
    padding: const EdgeInsets.symmetric(horizontal: 16.0),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 8),
        DueProgressSummary(onRefresh: _loadData),
        Expanded(
          child: DueProgressList(
            selectedDate: _selectedDate,
            isLoading: _isLoading,
            animationController: _animationController,
            fadeAnimation: _fadeAnimation,
            onRefresh: _loadData,
          ),
        ),
      ],
    ),
  );
}
