import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:spaced_learning_app/core/navigation/navigation_error_handler.dart';
import 'package:spaced_learning_app/core/navigation/route_constants.dart';
import 'package:spaced_learning_app/core/navigation/route_parameters.dart';
import 'package:spaced_learning_app/presentation/screens/auth/login_screen.dart';
import 'package:spaced_learning_app/presentation/screens/help/spaced_repetition_info_screen.dart';
import 'package:spaced_learning_app/presentation/screens/home/home_screen.dart';
import 'package:spaced_learning_app/presentation/screens/profile/profile_screen.dart';
import 'package:spaced_learning_app/presentation/screens/report/daily_task_report_screen.dart';
import 'package:spaced_learning_app/presentation/screens/settings/reminder_settings_screen.dart';
import 'package:spaced_learning_app/features/learning_set/presentation/views/learning_set_list_screen.dart';
import 'package:spaced_learning_app/features/review_history/presentation/views/review_history_list_screen.dart';

/// Lớp chứa các route builders để tách biệt logic tạo routes
class RouteBuilders {
  const RouteBuilders._();

  /// Auth routes
  static List<RouteBase> get authRoutes => [
    GoRoute(
      path: RouteConstants.login,
      name: RouteConstants.loginName,
      builder: (context, state) => const LoginScreen(),
    ),
  ];

  /// Modal routes (outside shell)
  static List<RouteBase> get modalRoutes => [
    GoRoute(
      path: RouteConstants.progressDetail,
      name: RouteConstants.progressDetailName,
      builder: (context, state) {
        final progressId = state.pathParameters.getProgressId();

        if (!state.pathParameters.hasValidProgressId()) {
          return NavigationErrorHandler.handleInvalidProgressId(context);
        }

        return _buildPlaceholderScreen(
          'Progress Detail',
          'Progress ID: $progressId',
        );
      },
    ),
  ];

  /// Book routes
  static List<RouteBase> get bookRoutes => [
    GoRoute(
      path: RouteConstants.books,
      name: RouteConstants.booksName,
      builder: (context, state) =>
          _buildPlaceholderScreen('Books', 'Books screen coming soon'),
      routes: [
        GoRoute(
          path: ':${RouteParameters.bookId}',
          name: RouteConstants.bookDetailName,
          builder: (context, state) {
            final bookId = state.pathParameters.getBookId();

            if (!state.pathParameters.hasValidBookId()) {
              return NavigationErrorHandler.handleInvalidBookId(context);
            }

            return _buildPlaceholderScreen('Book Detail', 'Book ID: $bookId');
          },
          routes: [
            GoRoute(
              path: 'modules/:${RouteParameters.moduleId}',
              name: RouteConstants.moduleDetailName,
              builder: (context, state) {
                final moduleId = state.pathParameters.getModuleId();
                final bookId = state.pathParameters.getBookId();

                if (!state.pathParameters.hasValidModuleId()) {
                  return NavigationErrorHandler.handleInvalidModuleId(
                    context,
                    bookId: bookId,
                  );
                }

                return _buildPlaceholderScreen(
                  'Module Detail',
                  'Module ID: $moduleId, Book ID: $bookId',
                );
              },
              routes: [
                GoRoute(
                  path: 'grammar',
                  name: RouteConstants.moduleGrammarName,
                  builder: (context, state) {
                    final moduleId = state.pathParameters.getModuleId();
                    final bookId = state.pathParameters.getBookId();

                    if (!state.pathParameters.hasValidModuleId()) {
                      return NavigationErrorHandler.handleInvalidModuleId(
                        context,
                        bookId: bookId,
                      );
                    }

                    return _buildPlaceholderScreen(
                      'Module Grammar',
                      'Module ID: $moduleId',
                    );
                  },
                ),
                GoRoute(
                  path: 'grammars/:${RouteParameters.grammarId}',
                  name: RouteConstants.grammarDetailName,
                  builder: (context, state) {
                    final grammarId = state.pathParameters.getGrammarId();
                    final moduleId = state.pathParameters.getModuleId();
                    final bookId = state.pathParameters.getBookId();

                    if (!state.pathParameters.hasValidGrammarId()) {
                      return NavigationErrorHandler.handleInvalidGrammarId(
                        context,
                        bookId: bookId,
                        moduleId: moduleId,
                      );
                    }

                    return _buildPlaceholderScreen(
                      'Grammar Detail',
                      'Grammar ID: $grammarId, Module ID: $moduleId',
                    );
                  },
                ),
              ],
            ),
          ],
        ),
      ],
    ),
  ];

  /// Learning routes
  static List<RouteBase> get learningRoutes => [
    GoRoute(
      path: RouteConstants.learning,
      name: RouteConstants.learningName,
      builder: (context, state) => _buildPlaceholderScreen(
        'Learning Progress',
        'Learning progress screen coming soon',
      ),
      routes: [
        GoRoute(
          path: 'progress/:${RouteParameters.progressId}',
          name: RouteConstants.learningProgressName,
          builder: (context, state) {
            final progressId = state.pathParameters.getProgressId();

            if (!state.pathParameters.hasValidProgressId()) {
              return NavigationErrorHandler.handleInvalidProgressId(context);
            }

            return _buildPlaceholderScreen(
              'Progress Detail',
              'Progress ID: $progressId',
            );
          },
        ),
        GoRoute(
          path: 'modules/:${RouteParameters.moduleId}',
          name: RouteConstants.learningModuleName,
          builder: (context, state) {
            final moduleId = state.pathParameters.getModuleId();

            if (!state.pathParameters.hasValidModuleId()) {
              return NavigationErrorHandler.handleInvalidModuleId(context);
            }

            return _buildPlaceholderScreen(
              'Learning Module',
              'Module ID: $moduleId',
            );
          },
        ),
      ],
    ),
  ];

  /// Profile routes
  static List<RouteBase> get profileRoutes => [
    GoRoute(
      path: RouteConstants.profile,
      name: RouteConstants.profileName,
      builder: (context, state) => const ProfileScreen(),
    ),
  ];

  /// Due progress routes
  static List<RouteBase> get dueProgressRoutes => [
    GoRoute(
      path: RouteConstants.dueProgress,
      name: RouteConstants.dueProgressName,
      builder: (context, state) => _buildPlaceholderScreen(
        'Due Progress',
        'Due progress screen coming soon',
      ),
    ),
  ];

  /// Settings routes
  static List<RouteBase> get settingsRoutes => [
    GoRoute(
      path: RouteConstants.reminderSettings,
      name: RouteConstants.reminderSettingsName,
      builder: (context, state) => const ReminderSettingsScreen(),
    ),
  ];

  /// Help routes
  static List<RouteBase> get helpRoutes => [
    GoRoute(
      path: RouteConstants.help,
      name: RouteConstants.helpName,
      builder: (context, state) =>
          const Scaffold(body: Center(child: Text('Help & Support'))),
      routes: [
        GoRoute(
          path: 'spaced-repetition',
          name: RouteConstants.spacedRepetitionName,
          builder: (context, state) => const SpacedRepetitionInfoScreen(),
        ),
      ],
    ),
  ];

  /// Report routes
  static List<RouteBase> get reportRoutes => [
    GoRoute(
      path: RouteConstants.taskReport,
      name: RouteConstants.taskReportName,
      builder: (context, state) => const DailyTaskReportScreen(),
    ),
  ];

  /// Home route
  static List<RouteBase> get homeRoutes => [
    GoRoute(
      path: RouteConstants.home,
      name: RouteConstants.homeName,
      builder: (context, state) => const HomeScreen(),
    ),
  ];

  /// Learning set routes
  static List<RouteBase> get learningSetRoutes => [
    GoRoute(
      path: RouteConstants.learningSets,
      name: RouteConstants.learningSetsName,
      builder: (context, state) => const LearningSetListScreen(),
      routes: [
        GoRoute(
          path: ':${RouteParameters.setId}/reviews',
          name: RouteConstants.learningSetReviewsName,
          builder: (context, state) {
            final setId = state.pathParameters.getSetId();
            if (!state.pathParameters.hasValidSetId()) {
              return NavigationErrorHandler.handleInvalidParameter(
                context: context,
                parameterName: RouteParameters.setId,
                errorMessage: RouteParameters.invalidSetIdMessage,
                fallbackRoute: RouteConstants.learningSets,
              );
            }
            return ReviewHistoryListScreen(setId: setId!);
          },
        ),
      ],
    ),
  ];

  /// Helper method to build placeholder screens
  static Widget _buildPlaceholderScreen(String title, String message) {
    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.construction, size: 64, color: Colors.orange[400]),
            const SizedBox(height: 16),
            Text(
              message,
              style: const TextStyle(fontSize: 18),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              'This screen is under development',
              style: TextStyle(fontSize: 14, color: Colors.grey[600]),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}
