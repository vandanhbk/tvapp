package gem.poc.tvapp.player

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackSeekDataProvider
import gem.poc.tvapp.R
import gem.poc.tvapp.model.DetailResponse

interface PlaybackActivityProvider {
    fun getPlaybackActivity(): PlaybackActivity?
}
class MyVideoFragment(playbackActivityProvider: PlaybackActivityProvider) : VideoSupportFragment() {

    private lateinit var transportGlue : CustomTransportControlGlue
    private lateinit var fastForwardIndicatorView: View
    private lateinit var rewindIndicatorView: View
    private var playbackActivityProvider: PlaybackActivityProvider = playbackActivityProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val detailResponse = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("movie_detail", DetailResponse::class.java)
        } else {
            arguments?.getParcelable("movie_detail")
        }

        transportGlue = CustomTransportControlGlue(
            context = requireContext(),
            playerAdapter = BasicMediaPlayerAdapter(requireContext()),
            playbackActivityProvider
        )
        transportGlue.host = VideoSupportFragmentGlueHost(this)

        transportGlue.loadMovieInfo(detailResponse)

        setOnKeyInterceptListener { view, keyCode, event ->
            if (isControlsOverlayVisible || event.repeatCount > 0) {
                //isShowOrHideControlOverlayOnUserInteraction = true
            } else when (keyCode) {
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    //isShowOrHideControlOverlayOnUserInteraction = event.action != KeyEvent.ACTION_DOWN
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        animateIndicator(fastForwardIndicatorView)
                    }
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    //isShowOrHideControlOverlayOnUserInteraction = event.action != KeyEvent.ACTION_DOWN
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        animateIndicator(rewindIndicatorView)
                    }
                }
            }
            transportGlue.onKey(view, keyCode, event)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        fastForwardIndicatorView = inflater.inflate(R.layout.view_forward, view, false)
        view.addView(fastForwardIndicatorView)

        rewindIndicatorView = inflater.inflate(R.layout.view_rewind, view, false)
        view.addView(rewindIndicatorView)

        return view
    }

    fun animateIndicator(indicator : View) {
        indicator.animate()
            .withEndAction {
                indicator.isVisible = false
                indicator.alpha = 1F
                indicator.scaleX = 1F
                indicator.scaleY = 1F
            }
            .withStartAction {
                indicator.isVisible = true
            }
            .alpha(0.2F)
            .scaleX(2f)
            .scaleY(2f)
            .setDuration(400)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

    }
}