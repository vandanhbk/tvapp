package gem.poc.tvapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gem.poc.tvapp.DataModel
import gem.poc.tvapp.api.Response
import gem.poc.tvapp.api.TmdbRepo
import gem.poc.tvapp.model.CastResponse
import gem.poc.tvapp.model.DetailResponse
import gem.poc.tvapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(val repo: TmdbRepo) : ViewModel() {
    var currentPageNowPlaying = 1
    var currentPageTopRated = 1
    var startIndexNowPlaying = 0
    var endIndexNowPlaying = -1
    var startIndexTopRated = 0
    var endIndexTopRated = -1

    var nowPlayingList: List<DataModel.Result?> = emptyList()
    var topRatedList: List<DataModel.Result?> = emptyList()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getNowPlayMoviesByPage(currentPageNowPlaying.toString())
            repo.getTopRatedMoviesByPage(currentPageTopRated.toString())
        }
    }

    val nowPlaying: LiveData<Response<DataModel>>
        get() = repo.nowPlayMovies

    val topRated: LiveData<Response<DataModel>>
        get() = repo.topRatedMovies

    private suspend fun loadNowPlayingMovies() {
        repo.getNowPlayMoviesByPage(currentPageNowPlaying.toString())
    }

    private suspend fun loadTopRatedMovies() {
        repo.getTopRatedMoviesByPage(currentPageTopRated.toString())
    }


    suspend fun loadMoreNowPlayingMovies() {
        Log.d("", "=======loadMoreNowPlayingMovies=======")
        if (currentPageNowPlaying >= Constants.MAX_PAGE) return
        else {
            currentPageNowPlaying++
            loadNowPlayingMovies()
        }
    }

    suspend fun loadMoreTopRatedMovies() {
        if (currentPageTopRated >= Constants.MAX_PAGE) return
        else {
            currentPageTopRated++
            loadTopRatedMovies()
        }
    }

    fun loadNowPlaying() {
        Log.d("", "=======loadMoreMovies=======")
        viewModelScope.launch {
            loadMoreNowPlayingMovies()
        }
    }

    fun loadTopRated() {
        Log.d("", "=======loadTopRated=======")
        viewModelScope.launch {
            loadMoreTopRatedMovies()
        }
    }

    fun updateNowPlayingList(list: List<DataModel.Result?>) {
        nowPlayingList = nowPlayingList + list
    }

    fun updateTopRatedList(list: List<DataModel.Result?>) {
        topRatedList = topRatedList + list
    }

}