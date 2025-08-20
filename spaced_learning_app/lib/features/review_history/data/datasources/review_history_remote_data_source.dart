import 'package:dio/dio.dart';
import 'package:retrofit/retrofit.dart';
import 'package:spaced_learning_app/core/pagination/page_dto.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_create_request_dto.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_dto.dart';
import 'package:spaced_learning_app/features/review_history/data/models/review_history_update_request_dto.dart';

part 'review_history_remote_data_source.g.dart';

@RestApi()
abstract class ReviewHistoryRemoteDataSource {
  factory ReviewHistoryRemoteDataSource(Dio dio, {String baseUrl}) =
      _ReviewHistoryRemoteDataSource;

  @POST('/reviews')
  Future<ReviewHistoryDto> create(
    @Body() ReviewHistoryCreateRequestDto body,
  );

  @GET('/reviews/{id}')
  Future<ReviewHistoryDto> getById(@Path('id') String id);

  @PUT('/reviews/{id}')
  Future<ReviewHistoryDto> update(
    @Path('id') String id,
    @Body() ReviewHistoryUpdateRequestDto body,
  );

  @GET('/reviews/set/{setId}')
  Future<PageDto<ReviewHistoryDto>> getBySet(
    @Path('setId') String setId,
    @Query('page') int page,
    @Query('size') int size,
  );
}
