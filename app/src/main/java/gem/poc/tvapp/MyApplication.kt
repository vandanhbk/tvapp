package gem.poc.tvapp

import android.app.Application
import gem.poc.tvapp.api.ApiService
import gem.poc.tvapp.api.RetrofitHelper
import gem.poc.tvapp.api.TmdbRepo

class MyApplication : Application() {

    lateinit var tmdbRepo : TmdbRepo

    override fun onCreate() {
        super.onCreate()

        init()
    }

    private fun init(){
        val service = RetrofitHelper.getInstance().create(ApiService::class.java)
        tmdbRepo = TmdbRepo(service)
    }
}