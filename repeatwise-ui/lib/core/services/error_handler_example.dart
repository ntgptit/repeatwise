import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../models/api_response.dart';
import '../models/api_exception.dart';
import '../models/user.dart';
import 'error_handler.dart';

/// Example of using ErrorHandler in a use case with "Return Early" philosophy
class UserServiceExample {
  
  /// Example: Get user profile with error handling
  Future<ApiResponse<User>> getUserProfile(String userId) async {
    try {
      // Simulate API call that might throw DioException
      final user = await _fetchUserFromApi(userId);
      return ApiResponse.success(user);
    } on ApiException catch (e) {
      // Early return for authentication errors
      if (ErrorHandler.isAuthenticationError(e)) {
        return ApiResponse.error('Please log in again');
      }
      
      // Early return for network errors
      if (ErrorHandler.isNetworkError(e)) {
        return ApiResponse.error('No internet connection');
      }
      
      // Early return for server errors
      if (ErrorHandler.isServerError(e)) {
        return ApiResponse.error('Server is temporarily unavailable');
      }
      
      // Default error handling
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error occurred');
    }
  }

  /// Example: Update user profile with validation error handling
  Future<ApiResponse<User>> updateUserProfile(String userId, Map<String, dynamic> data) async {
    try {
      // Simulate API call
      final user = await _updateUserInApi(userId, data);
      return ApiResponse.success(user);
    } on ApiException catch (e) {
      // Early return for validation errors (422)
      if (e.statusCode == 422) {
        final details = e.details;
        if (details != null && details['errors'] != null) {
          final errors = details['errors'] as Map<String, dynamic>;
          final firstError = errors.values.first.toString();
          return ApiResponse.error(firstError);
        }
        return ApiResponse.error('Validation failed');
      }
      
      // Early return for authentication errors
      if (ErrorHandler.isAuthenticationError(e)) {
        return ApiResponse.error('Please log in again');
      }
      
      // Default error handling
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error occurred');
    }
  }

  /// Example: Delete user account with confirmation
  Future<ApiResponse<void>> deleteUserAccount(String userId, String password) async {
    try {
      // Simulate API call
      await _deleteUserFromApi(userId, password);
      return ApiResponse.success(null);
    } on ApiException catch (e) {
      // Early return for authentication errors
      if (ErrorHandler.isAuthenticationError(e)) {
        return ApiResponse.error('Please log in again');
      }
      
      // Early return for forbidden errors (403)
      if (e.statusCode == 403) {
        return ApiResponse.error('You do not have permission to delete this account');
      }
      
      // Early return for not found errors (404)
      if (e.statusCode == 404) {
        return ApiResponse.error('User not found');
      }
      
      // Default error handling
      return ApiResponse.error(e.message);
    } catch (e) {
      return ApiResponse.error('Unexpected error occurred');
    }
  }

  /// Example: Batch operation with error aggregation
  Future<ApiResponse<List<User>>> getMultipleUsers(List<String> userIds) async {
    final results = <User>[];
    final errors = <String>[];
    
    for (final userId in userIds) {
      try {
        final user = await _fetchUserFromApi(userId);
        results.add(user);
      } on ApiException catch (e) {
        // Early return for authentication errors (affects all requests)
        if (ErrorHandler.isAuthenticationError(e)) {
          return ApiResponse.error('Please log in again');
        }
        
        // Early return for network errors (affects all requests)
        if (ErrorHandler.isNetworkError(e)) {
          return ApiResponse.error('No internet connection');
        }
        
        // Collect individual errors for partial failures
        errors.add('Failed to load user $userId: ${e.message}');
      } catch (e) {
        errors.add('Failed to load user $userId: Unexpected error');
      }
    }
    
    // Return partial success if some users were loaded
    if (results.isNotEmpty) {
      return ApiResponse.success(results);
    }
    
    // Return error if all requests failed
    return ApiResponse.error(errors.join(', '));
  }

  // Mock API methods (in real app, these would be actual API calls)
  Future<User> _fetchUserFromApi(String userId) async {
    // Simulate API call
    await Future.delayed(const Duration(milliseconds: 100));
    return User(
      id: userId,
      name: 'John Doe',
      username: 'johndoe',
      email: 'john@example.com',
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
  }

  Future<User> _updateUserInApi(String userId, Map<String, dynamic> data) async {
    // Simulate API call
    await Future.delayed(const Duration(milliseconds: 100));
    return User(
      id: userId,
      name: (data['name'] as String?) ?? 'John Doe',
      username: (data['username'] as String?) ?? 'johndoe',
      email: (data['email'] as String?) ?? 'john@example.com',
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
  }

  Future<void> _deleteUserFromApi(String userId, String password) async {
    // Simulate API call
    await Future.delayed(const Duration(milliseconds: 100));
  }
}

// Provider for the example service
@riverpod
UserServiceExample userServiceExample(Ref ref) {
  return UserServiceExample();
}
