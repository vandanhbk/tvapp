package gem.poc.tvapp.player

import android.content.Context
import android.media.session.PlaybackState.ACTION_FAST_FORWARD
import android.media.session.PlaybackState.ACTION_PLAY_PAUSE
import android.media.session.PlaybackState.ACTION_REWIND
import android.media.session.PlaybackState.ACTION_SKIP_TO_NEXT
import android.media.session.PlaybackState.ACTION_SKIP_TO_PREVIOUS
import androidx.leanback.media.MediaPlayerAdapter

class BasicMediaPlayerAdapter(context : Context) : MediaPlayerAdapter(context) {
    val playlist = ArrayList<String>()
    var playlistPosition = 0

    override fun next() {
        super.next()
    }

    override fun previous() {
        super.previous()
    }

    override fun fastForward() {
        seekTo(currentPosition + 10_000)
    }

    override fun rewind() {
        seekTo(currentPosition - 10_000)
    }

    override fun getSupportedActions(): Long {
        return (ACTION_SKIP_TO_PREVIOUS xor
                ACTION_REWIND xor
                ACTION_PLAY_PAUSE xor
                ACTION_FAST_FORWARD xor
                ACTION_SKIP_TO_NEXT).toLong()
    }

    fun loadMovies() {

    }
}