import 'package:dio/dio.dart';
import 'package:retrofit/retrofit.dart';
import 'package:spaced_learning_app/core/pagination/page_dto.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_create_request_dto.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_dto.dart';
import 'package:spaced_learning_app/features/learning_set/data/models/learning_set_update_request_dto.dart';

part 'learning_set_remote_data_source.g.dart';

@RestApi()
abstract class LearningSetRemoteDataSource {
  factory LearningSetRemoteDataSource(Dio dio, {String baseUrl}) =
      _LearningSetRemoteDataSource;

  @POST('/learning-sets')
  Future<LearningSetDto> create(
    @Body() LearningSetCreateRequestDto body,
  );

  @GET('/learning-sets/{id}')
  Future<LearningSetDto> getById(@Path('id') String id);

  @PUT('/learning-sets/{id}')
  Future<LearningSetDto> update(
    @Path('id') String id,
    @Body() LearningSetUpdateRequestDto body,
  );

  @DELETE('/learning-sets/{id}')
  Future<void> delete(@Path('id') String id);

  @GET('/learning-sets')
  Future<PageDto<LearningSetDto>> getUserSets(
    @Query('page') int page,
    @Query('size') int size,
  );
}
