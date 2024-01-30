package gem.poc.tvapp.player

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import gem.poc.tvapp.R
import gem.poc.tvapp.model.DetailResponse
import kotlin.math.log

class PlaybackActivity : FragmentActivity() ,PlaybackActivityProvider{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("movie_detail", DetailResponse::class.java)
        } else {
            intent.getParcelableExtra("movie_detail")
        }

        val fragment = MyVideoFragment(this)
        val bundle = Bundle()
        bundle.putParcelable("movie_detail", data)
        fragment.arguments = bundle
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }
    }

    override fun getPlaybackActivity(): PlaybackActivity? {
        return this
    }
}