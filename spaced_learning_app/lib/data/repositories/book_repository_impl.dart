import 'package:spaced_learning_app/core/constants/api_endpoints.dart';
import 'package:spaced_learning_app/core/exceptions/app_exceptions.dart';
import 'package:spaced_learning_app/core/network/api_client.dart';
import 'package:spaced_learning_app/domain/models/book.dart';
import 'package:spaced_learning_app/domain/repositories/book_repository.dart';

class BookRepositoryImpl implements BookRepository {
  final ApiClient _apiClient;

  BookRepositoryImpl(this._apiClient);

  @override
  Future<List<BookSummary>> getAllBooks({int page = 0, int size = 20}) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.books,
        queryParameters: {'page': page, 'size': size},
      );

      final content = response['content'];
      if (content == null || content is! List) {
        return [];
      }

      return content.map((item) => BookSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get books: $e');
    }
  }

  @override
  Future<BookDetail> getBookById(String id) async {
    try {
      final response = await _apiClient.get('${ApiEndpoints.books}/$id');

      if (response['success'] != true || response['data'] == null) {
        throw NotFoundException('Book not found: ${response['message']}');
      }

      return BookDetail.fromJson(response['data']);
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get book: $e');
    }
  }

  @override
  Future<List<BookSummary>> searchBooks(
    String query, {
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _apiClient.get(
        ApiEndpoints.bookSearch,
        queryParameters: {'query': query, 'page': page, 'size': size},
      );

      if (response['success'] != true || response['content'] == null) {
        return [];
      }

      final List<dynamic> bookList = response['content'];
      return bookList.map((item) => BookSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to search books: $e');
    }
  }

  @override
  Future<List<String>> getAllCategories() async {
    try {
      final response = await _apiClient.get(ApiEndpoints.bookCategories);

      if (response['success'] != true || response['data'] == null) {
        return [];
      }

      final List<dynamic> categoryList = response['data'];
      return categoryList.map((item) => item.toString()).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to get categories: $e');
    }
  }

  @override
  Future<List<BookSummary>> filterBooks({
    BookStatus? status,
    DifficultyLevel? difficultyLevel,
    String? category,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final Map<String, dynamic> queryParams = {'page': page, 'size': size};

      if (status != null) {
        queryParams['status'] = status.toString().split('.').last.toUpperCase();
      }

      if (difficultyLevel != null) {
        queryParams['difficultyLevel'] = difficultyLevel
            .toString()
            .split('.')
            .last
            .toUpperCase();
      }

      if (category != null) {
        queryParams['category'] = category;
      }

      final response = await _apiClient.get(
        ApiEndpoints.bookFilter,
        queryParameters: queryParams,
      );

      if (response['success'] != true || response['content'] == null) {
        return [];
      }

      final List<dynamic> bookList = response['content'];
      return bookList.map((item) => BookSummary.fromJson(item)).toList();
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to filter books: $e');
    }
  }

  @override
  Future<void> deleteBook(String id) async {
    try {
      final response = await _apiClient.delete('${ApiEndpoints.books}/$id');

      if (response == null || response['success'] != true) {
        throw BadRequestException(
          'Failed to delete book: ${response?['message']}',
        );
      }
    } on AppException {
      rethrow;
    } catch (e) {
      throw UnexpectedException('Failed to delete book: $e');
    }
  }
}
