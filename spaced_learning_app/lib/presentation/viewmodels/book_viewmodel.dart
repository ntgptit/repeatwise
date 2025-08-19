import 'package:riverpod_annotation/riverpod_annotation.dart';
import 'package:spaced_learning_app/domain/models/book.dart';

import '../../core/di/providers.dart';

part 'book_viewmodel.g.dart';

@riverpod
class BooksState extends _$BooksState {
  @override
  Future<List<BookSummary>> build() => _loadBooks();

  Future<List<BookSummary>> _loadBooks({int page = 0, int size = 20}) async {
    try {
      final books = await ref
          .read(bookRepositoryProvider)
          .getAllBooks(page: page, size: size);
      return books;
    } catch (e, st) {
      throw AsyncError(e, st);
    }
  }

  Future<void> loadBooks({int page = 0, int size = 20}) async {
    state = const AsyncValue.loading();
    try {
      final books = await ref
          .read(bookRepositoryProvider)
          .getAllBooks(page: page, size: size);
      state = AsyncValue.data(books);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> searchBooks(String query, {int page = 0, int size = 20}) async {
    state = const AsyncValue.loading();
    try {
      final books = query.isEmpty
          ? await ref
                .read(bookRepositoryProvider)
                .getAllBooks(page: page, size: size)
          : await ref
                .read(bookRepositoryProvider)
                .searchBooks(query, page: page, size: size);
      state = AsyncValue.data(books);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> filterBooks({
    BookStatus? status,
    DifficultyLevel? difficultyLevel,
    String? category,
    int page = 0,
    int size = 20,
  }) async {
    state = const AsyncValue.loading();
    try {
      final books = await ref
          .read(bookRepositoryProvider)
          .filterBooks(
            status: status,
            difficultyLevel: difficultyLevel,
            category: category,
            page: page,
            size: size,
          );
      state = AsyncValue.data(books);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<bool> deleteBook(String id) async {
    try {
      await ref.read(bookRepositoryProvider).deleteBook(id);

      final books = state.valueOrNull ?? [];
      state = AsyncValue.data(books.where((book) => book.id != id).toList());

      final selectedBook = ref.read(selectedBookProvider).valueOrNull;
      if (selectedBook?.id == id) {
        ref.read(selectedBookProvider.notifier).clearSelectedBook();
      }

      return true;
    } catch (_) {
      return false;
    }
  }
}

@riverpod
class SelectedBook extends _$SelectedBook {
  @override
  Future<BookDetail?> build() async => null;

  Future<void> loadBookDetails(String id) async {
    if (id.isEmpty) {
      state = const AsyncValue.data(null);
      return;
    }

    state = const AsyncValue.loading();
    try {
      final book = await ref.read(bookRepositoryProvider).getBookById(id);
      state = AsyncValue.data(book);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  void clearSelectedBook() {
    state = const AsyncValue.data(null);
  }
}

@Riverpod(keepAlive: true)
class Categories extends _$Categories {
  @override
  Future<List<String>> build() => _loadCategories();

  Future<List<String>> _loadCategories() async {
    try {
      final categories = await ref
          .read(bookRepositoryProvider)
          .getAllCategories();
      return categories;
    } catch (e, st) {
      throw AsyncError(e, st);
    }
  }

  Future<void> reloadCategories() async {
    state = const AsyncValue.loading();
    try {
      final categories = await ref
          .read(bookRepositoryProvider)
          .getAllCategories();
      state = AsyncValue.data(categories);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }
}
