import 'package:spaced_learning_app/domain/models/book.dart';

abstract class BookRepository {
  Future<List<BookSummary>> getAllBooks({int page = 0, int size = 20});

  Future<BookDetail> getBookById(String id);

  Future<List<BookSummary>> searchBooks(
    String query, {
    int page = 0,
    int size = 20,
  });

  Future<List<String>> getAllCategories();

  Future<List<BookSummary>> filterBooks({
    BookStatus? status,
    DifficultyLevel? difficultyLevel,
    String? category,
    int page = 0,
    int size = 20,
  });



  Future<void> deleteBook(String id);
}
