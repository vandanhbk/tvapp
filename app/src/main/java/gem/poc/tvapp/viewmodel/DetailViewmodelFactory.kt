package gem.poc.tvapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gem.poc.tvapp.api.TmdbRepo

class DetailViewmodelFactory(val repo: TmdbRepo, val movieId: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewmodel(repo, movieId) as T
    }
}