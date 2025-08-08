import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class GetSetsUseCase {
  final SetRepository _repository;

  GetSetsUseCase(this._repository);

  Future<ApiResponse<List<Set>>> execute(String userId) {
    return _repository.getSetsByUser(userId);
  }
}
