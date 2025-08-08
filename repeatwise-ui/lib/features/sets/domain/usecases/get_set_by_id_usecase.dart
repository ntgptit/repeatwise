import '../../../../core/models/set.dart';
import '../../../../core/models/api_response.dart';
import '../repositories/set_repository.dart';

class GetSetByIdUseCase {
  final SetRepository _repository;

  GetSetByIdUseCase(this._repository);

  Future<ApiResponse<Set>> execute(String setId) {
    return _repository.getSetById(setId);
  }
}
