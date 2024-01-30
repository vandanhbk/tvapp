package gem.poc.tvapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.gson.Gson
import gem.poc.tvapp.api.Response
import gem.poc.tvapp.viewmodel.HomeViewModel
import gem.poc.tvapp.viewmodel.HomeViewModelFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

open class HomeFragment : Fragment() {

    lateinit var txtTitle: TextView
    lateinit var txtSubTitle: TextView
    lateinit var txtDescription: TextView

    lateinit var imgBanner: ImageView
    lateinit var listFragment: ListFragment

    lateinit var viewmodel : HomeViewModel

    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = (requireActivity().application as MyApplication).tmdbRepo
        viewmodel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        playerView = view.findViewById(R.id.player_view)
        initializePlayer()
    }

     fun init(view: View) {

        imgBanner = view.findViewById(R.id.img_banner)
        txtTitle = view.findViewById(R.id.title)
        txtSubTitle = view.findViewById(R.id.subtitle)
        txtDescription = view.findViewById(R.id.description)


        listFragment = ListFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.list_fragment, listFragment)
        transaction.commit()
         listFragment.setViewModel(viewmodel)


        /*val gson = Gson()
        val i: InputStream = requireContext().assets.open("movies.json")
        val br = BufferedReader(InputStreamReader(i))
        val dataList: DataModel = gson.fromJson(br, DataModel::class.java)
                listFragment.bindData(dataList)
        */
         viewmodel.nowPlaying.observe(viewLifecycleOwner) {
             Log.d("","===============viewmodel.nowPlaying.observe===========")
             when (it) {
                 is Response.Loading -> {}
                 is Response.Success -> {
                     it.data?.results.let {
                         Log.d("","===============viewmodel.nowPlaying.observe===========it = $it")
                         viewmodel.updateNowPlayingList(it!!)
                         listFragment.bindData(it!!, "Now Playing")

                     }
                 }
                 is Response.Error -> {}
             }
         }

         viewmodel.topRated.observe(viewLifecycleOwner) {
             when (it) {
                 is Response.Loading -> {}
                 is Response.Success -> {
                     it.data?.results.let {
                         viewmodel.updateTopRatedList(it!!)
                         listFragment.bindData(it!!, "Top Rated")
                     }
                 }
                 is Response.Error -> {}
             }
         }

        listFragment.setOnContentSelectedListener {
            updateBanner(it)
            handler.removeCallbacks(playTrailerRunnable)
            playerView.visibility = View.GONE
            handler.postDelayed(playTrailerRunnable, 3000)
        }

         listFragment.setOnItemClickListener {
             val intent  = Intent(requireContext(), DetailActivity::class.java)
             intent.putExtra("id", it.id)
             startActivity(intent)
         }
    }

    fun updateBanner(dataList: DataModel.Result) {
        txtTitle.text = dataList.title
        txtDescription.text = dataList.overview


        val url = "https://www.themoviedb.org/t/p/w780" + dataList.backdrop_path
        Glide.with(this).load(url).into(imgBanner)
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        playerView.player = player
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
    }

    private fun playTrailerForFocusedItem() {
        // Example URL - replace with your actual trailer URL
        val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        playerView.visibility = View.VISIBLE
    }

    private val handler = Handler(Looper.getMainLooper())
    private val playTrailerRunnable = Runnable {
        playTrailerForFocusedItem()
    }

}