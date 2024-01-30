package gem.poc.tvapp.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gem.poc.tvapp.DataModel
import gem.poc.tvapp.model.CastResponse
import gem.poc.tvapp.model.DetailResponse
import gem.poc.tvapp.utils.Constants.API_KEY

class TmdbRepo(val service: ApiService) {

    val detailData = MutableLiveData<Response<DetailResponse>>()
    val castData = MutableLiveData<Response<CastResponse>>()

    val topRatedMovies = MutableLiveData<Response<DataModel>>()
    val nowPlayMovies = MutableLiveData<Response<DataModel>>()

    val movieDetail: LiveData<Response<DetailResponse>>
        get() = detailData

    val castDetail: LiveData<Response<CastResponse>>
        get() = castData

    suspend fun getMovieDetails(id: Int) {
        Log.d("", "========getMovieDetails========id = $id")

        try {
            val result = service.getMovieDetails(id, API_KEY)

            if (result.body() != null) {
                detailData.postValue(Response.Success(result.body()))
            } else {
                detailData.postValue(Response.Error(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            detailData.postValue(Response.Error(e.message.toString()))
        }
    }

    suspend fun getMovieCast(id: Int) {

        try {
            val result = service.getMovieCast(id, API_KEY)

            if (result.body() != null) {
                castData.postValue(Response.Success(result.body()))
            } else {
                castData.postValue(Response.Error(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            castData.postValue(Response.Error(e.message.toString()))
        }
    }

    suspend fun getTopRatedMovies() {
        Log.d("", "================getTopRatedMovies=================")
        try {
            val result = service.getTopRatedList(API_KEY)
            Log.d("", "================getTopRatedMovies=================result = $result")
            if (result.body() != null) {
                topRatedMovies.postValue(Response.Success(result.body()))
            } else {
                topRatedMovies.postValue(Response.Error(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            topRatedMovies.postValue(Response.Error(e.message.toString()))
        }
    }

    suspend fun getTopRatedMoviesByPage(page: String) {
        Log.d("", "================getTopRatedMovies=================")
        try {
            val result = service.getTopRatedListByPage(API_KEY, page)
            Log.d("", "================getTopRatedMovies=================result = $result")
            if (result.body() != null) {
                topRatedMovies.postValue(Response.Success(result.body()))
            } else {
                topRatedMovies.postValue(Response.Error(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            topRatedMovies.postValue(Response.Error(e.message.toString()))
        }
    }


    suspend fun getNowPlayMovies() {

        try {
            val result = service.getNowPlayingList(API_KEY)

            if (result.body() != null) {
                nowPlayMovies.postValue(Response.Success(result.body()))
            } else {
                nowPlayMovies.postValue(Response.Error(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            nowPlayMovies.postValue(Response.Error(e.message.toString()))
        }
    }

    suspend fun getNowPlayMoviesByPage(page: String) {
        Log.d("", "=======getNowPlayMoviesByPage=======")
        try {
            val result = service.getNowPlayingListByPage(API_KEY, page)
            Log.d("", "=======getNowPlayMoviesByPage=======result = $result")

            if (result.body() != null) {
                nowPlayMovies.postValue(Response.Success(result.body()))
            } else {
                nowPlayMovies.postValue(Response.Error(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            nowPlayMovies.postValue(Response.Error(e.message.toString()))
        }
    }


}