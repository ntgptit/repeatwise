import 'package:spaced_learning_app/domain/models/module.dart';

abstract class ModuleRepository {
  Future<List<ModuleSummary>> getAllModules({int page = 0, int size = 20});

  Future<ModuleDetail> getModuleById(String id);

  Future<List<ModuleSummary>> getModulesByBookId(
    String bookId, {
    int page = 0,
    int size = 20,
  });





}
