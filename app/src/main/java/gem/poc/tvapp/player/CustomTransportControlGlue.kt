package gem.poc.tvapp.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import gem.poc.tvapp.model.DetailResponse


class CustomTransportControlGlue(
    context: Context,
    playerAdapter: BasicMediaPlayerAdapter,
    playbackActivityProvider: PlaybackActivityProvider
) : PlaybackTransportControlGlue<BasicMediaPlayerAdapter>(context, playerAdapter){

    // Primary Actions
    private val forwardAction = PlaybackControlsRow.FastForwardAction(context)
    private val rewindAction = PlaybackControlsRow.RewindAction(context)
    private val nextAction = PlaybackControlsRow.SkipNextAction(context)
    private val previousAction = PlaybackControlsRow.SkipPreviousAction(context)
    private val pip = PlaybackControlsRow.PictureInPictureAction(context)
    private val playbackActivityProvider = playbackActivityProvider

    init {
        isSeekEnabled = true
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        primaryActionsAdapter?.add(previousAction)
        primaryActionsAdapter?.add(rewindAction)
        super.onCreatePrimaryActions(primaryActionsAdapter)
        primaryActionsAdapter?.add(forwardAction)
        primaryActionsAdapter?.add(nextAction)
        primaryActionsAdapter?.add(pip)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActionClicked(action: Action?) {
        Log.d("", "====================CustomTransportControlGlue============action = $action")
        when (action) {
            forwardAction -> playerAdapter.fastForward()
            rewindAction -> playerAdapter.rewind()
            pip -> playbackActivityProvider.getPlaybackActivity()?.enterPictureInPictureMode()
            else -> super.onActionClicked(action)
        }
        onUpdateProgress()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (host.isControlsOverlayVisible || event?.repeatCount!! > 0) {
            return super.onKey(v, keyCode, event)
        }
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT ->
                if (event.action != KeyEvent.ACTION_DOWN) false else {
                    onActionClicked(forwardAction)
                    true
                }
            KeyEvent.KEYCODE_DPAD_LEFT ->
                if (event.action != KeyEvent.ACTION_DOWN) false else {
                    onActionClicked(rewindAction)
                    true
                }
            else -> super.onKey(v, keyCode, event)
        }
    }

    fun loadMovieInfo(detailResponse: DetailResponse?) {
        subtitle = getSubtitle(detailResponse)
        title = detailResponse?.title
        val uriPath = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        playerAdapter.setDataSource(Uri.parse(uriPath))

        val path = "https://www.themoviedb.org/t/p/w780" + (detailResponse?.backdrop_path ?: "")
        Glide.with(context)
            .asBitmap()
            .load(path)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    controlsRow.setImageBitmap(context, resource)
                    host.notifyPlaybackRowChanged()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    controlsRow.setImageBitmap(context, null)
                    host.notifyPlaybackRowChanged()
                }

            })
        playWhenPrepared()
    }

    fun getSubtitle(response: DetailResponse?): String {
        val rating = if (response!!.adult) {
            "18+"
        } else {
            "13+"
        }

        val genres = response.genres.joinToString(
            prefix = " ",
            postfix = " • ",
            separator = " • "
        ) { it.name }

        val hours: Int = response.runtime / 60
        val min: Int = response.runtime % 60

        return rating + genres + hours + "h " + min + "m"

    }

}