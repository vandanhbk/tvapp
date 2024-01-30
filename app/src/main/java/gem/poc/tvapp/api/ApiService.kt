package gem.poc.tvapp.api

import gem.poc.tvapp.DataModel
import gem.poc.tvapp.model.CastResponse
import gem.poc.tvapp.model.DetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @GET("movie/now_playing?language=en-US&page=1")
    suspend fun getNowPlayingList(@Query("api_key")apiKey: String) : Response<DataModel>

    @GET("movie/now_playing?language=en-US")
    suspend fun getNowPlayingListByPage(@Query("api_key")apiKey: String, @Query("page")page: String) : Response<DataModel>

    @GET("movie/top_rated?language=en-US&page=1")
    suspend fun getTopRatedList(@Query("api_key")apiKey: String) : Response<DataModel>

    @GET("movie/top_rated?language=en-US")
    suspend fun getTopRatedListByPage(@Query("api_key")apiKey: String, @Query("page")page: String) : Response<DataModel>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id")id: Int, @Query("api_key")apiKey: String
    ):Response<DetailResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCast(
        @Path("movie_id")id: Int, @Query("api_key")apiKey: String
    ):Response<CastResponse>
}