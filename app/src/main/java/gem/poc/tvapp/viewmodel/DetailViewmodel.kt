package gem.poc.tvapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gem.poc.tvapp.api.Response
import gem.poc.tvapp.api.TmdbRepo
import gem.poc.tvapp.model.CastResponse
import gem.poc.tvapp.model.DetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewmodel(val repo: TmdbRepo, id: Int) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getMovieDetails(id)
            repo.getMovieCast(id)
        }
    }

    val movieDetails: LiveData<Response<DetailResponse>>
        get() = repo.movieDetail

    val castDetails: LiveData<Response<CastResponse>>
        get() = repo.castDetail
}