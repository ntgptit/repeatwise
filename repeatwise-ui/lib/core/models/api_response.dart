class ApiResponse<T> {
  final T? data;
  final String? error;
  final bool isSuccess;

  const ApiResponse._({
    this.data,
    this.error,
    required this.isSuccess,
  });

  factory ApiResponse.success(T data) {
    return ApiResponse._(
      data: data,
      isSuccess: true,
    );
  }

  factory ApiResponse.error(String error) {
    return ApiResponse._(
      error: error,
      isSuccess: false,
    );
  }

  // Getters for computed properties
  bool get isError => !isSuccess;
  bool get hasData => data != null;
  bool get hasError => error != null && error!.isNotEmpty;
  String get errorMessage => error ?? 'Unknown error occurred';

  // Utility methods
  R fold<R>({
    required R Function(T data) onSuccess,
    required R Function(String error) onError,
  }) {
    if (isSuccess && data != null) {
      return onSuccess(data as T);
    } else {
      return onError(errorMessage);
    }
  }

  ApiResponse<R> map<R>(R Function(T data) transform) {
    if (isSuccess && data != null) {
      return ApiResponse.success(transform(data as T));
    } else {
      return ApiResponse.error(errorMessage);
    }
  }

  ApiResponse<T> onSuccess(void Function(T data) callback) {
    if (isSuccess && data != null) {
      callback(data as T);
    }
    return this;
  }

  ApiResponse<T> onError(void Function(String error) callback) {
    if (isError) {
      callback(errorMessage);
    }
    return this;
  }

  T? getOrNull() => isSuccess ? data : null;

  T? getOrElse(T defaultValue) => isSuccess && data != null ? data : defaultValue;

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is ApiResponse<T> &&
        other.isSuccess == isSuccess &&
        other.data == data &&
        other.error == error;
  }

  @override
  int get hashCode => Object.hash(isSuccess, data, error);

  @override
  String toString() {
    if (isSuccess) {
      return 'ApiResponse.success($data)';
    } else {
      return 'ApiResponse.error($error)';
    }
  }
}
