package gem.poc.tvapp

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.BaseGridView.OnKeyInterceptListener

import androidx.recyclerview.widget.RecyclerView
import gem.poc.tvapp.model.CastResponse
import gem.poc.tvapp.utils.Constants
import gem.poc.tvapp.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SecondListFragment : RowsSupportFragment() {

    private var itemSelectedListener: ((DataModel.Result) -> Unit)? = null
    private var itemClickListener: ((DataModel.Result) -> Unit)? = null
    private lateinit var borderView: View
    private lateinit var viewModel: HomeViewModel
    private var scrollSpeedFactor = 0.1 // Default speed
    var arrayObjectAdapter = ArrayObjectAdapter(ItemPresenter())
    private var firstVisibleItemWatchNow: Int = 0
    private var lastVisibleItemWatchNow: Int = 0
    private var countItemWatchNow: Int = 0

    private var firstVisibleItemTopRated: Int = 0
    private var lastVisibleItemTopRated: Int = 0
    private var countItemTopRated: Int = 0

    private val listRowPresenter = object : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM) {


        override fun initializeRowViewHolder(holder: RowPresenter.ViewHolder?) {
            super.initializeRowViewHolder(holder)
            (holder as? ListRowPresenter.ViewHolder)?.gridView?.setOnKeyInterceptListener(object : OnKeyInterceptListener {
                override fun onInterceptKeyEvent(event: KeyEvent?): Boolean {
                    event?.let {
                        val keyCode = it.keyCode
                        val actionType = it.action
                        if (actionType == KeyEvent.ACTION_DOWN) {
                            val t = (holder.gridView as RecyclerView).scrollState
                            val recyclerView = (holder.gridView as RecyclerView)
                            Log.d("", "=============onInterceptKeyEvent======$t")
                            if ((holder.gridView as RecyclerView).scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

                                    return true
                                }
                            } else {
                                val childCount = recyclerView.childCount
                                val itemCount = recyclerView.adapter?.itemCount ?: 0
                                val firstVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0))
                                val lastVisibleItem = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(childCount - 1))
                                Log.d("", "=================onInterceptKeyEvent================firstVisibleItem = $firstVisibleItem")
                                Log.d("", "=================onInterceptKeyEvent================lastVisibleItem = $lastVisibleItem")
                                Log.d("", "=================onInterceptKeyEvent================itemCount = $itemCount")
                                val threshold = 10 // Threshold items to trigger loading more
                                val headerTitle = getCurrentRowHeaderTitle()
                                if (itemCount <= (lastVisibleItem + threshold)) {
                                    if (headerTitle == Constants.WATCH_NOW_TITLE)  viewModel.loadNowPlaying()
                                    if (headerTitle == Constants.TOP_RATED_TITLE)  viewModel.loadTopRated()
                                }

                                when (headerTitle) {
                                    Constants.WATCH_NOW_TITLE -> {
                                        firstVisibleItemWatchNow = firstVisibleItem
                                        lastVisibleItemWatchNow = lastVisibleItem
                                        countItemWatchNow = itemCount
                                    }
                                    Constants.TOP_RATED_TITLE -> {
                                        firstVisibleItemTopRated = firstVisibleItem
                                        lastVisibleItemTopRated = lastVisibleItem
                                        countItemTopRated = itemCount
                                    }
                                }
                                var scrollDirection: String = ""
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_RIGHT -> scrollDirection = "RIGHT"
                                    KeyEvent.KEYCODE_DPAD_LEFT -> scrollDirection = "LEFT"
                                }
                            }
                        }
                        return false


                    }
                    return false
                }
            })
        }

        override fun isUsingDefaultListSelectEffect(): Boolean {
            return false
        }

        override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
            val viewHolder = super.createRowViewHolder(parent)

            with((viewHolder.view as ListRowView).gridView) {
                windowAlignment = BaseGridView.WINDOW_ALIGN_BOTH_EDGE
                windowAlignmentOffsetPercent = 0f
                windowAlignmentOffset = parent.resources.getDimensionPixelSize(androidx.leanback.R.dimen.lb_browse_padding_start)
                itemAlignmentOffsetPercent = 0f
            }

            return viewHolder
        }


    }.apply {
        shadowEnabled = false
    }

    private var rootAdapter: ArrayObjectAdapter = ArrayObjectAdapter(listRowPresenter)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("", "================= ListFragment onViewCreated view = ${view.rootView}")
        adapter = rootAdapter
        borderView = view.rootView.findViewById(R.id.item_border)
        //initBorderView()

        onItemViewSelectedListener = ItemViewSelectedListener()
        onItemViewClickedListener = ItemViewClickListener()
    }

    fun getCurrentRowHeaderTitle(): String? {
        val verticalGridView = verticalGridView
        val selectedRowIndex = verticalGridView?.selectedPosition ?: -1
        val selectedRow = adapter?.get(selectedRowIndex) as? ListRow
        return selectedRow?.headerItem?.name
    }

    fun bindData(results: List<DataModel.Result?>, title: String, position: String = "END") {
        Log.d("", "=============bindData==========results = $results")
        /*results?.forEach {
            arrayObjectAdapter.add(it)
        }

        val headerItem = HeaderItem(title)
        val listRow = ListRow(headerItem, arrayObjectAdapter)
        rootAdapter.add(listRow)*/


        // Check if the list row with this title already exists
        val existingRow = rootAdapter.unmodifiableList<ListRow>().find {
            it is ListRow && it.headerItem.name == title
        } as? ListRow

        if (existingRow != null) {
            // If the row exists, add new items to its adapter
            //(existingRow.adapter as? ArrayObjectAdapter)?.addAll(existingRow.adapter.size(), results)
            Log.d(this.tag, "==========existingRow=============results.size = ${results.size}")
            //val size = existingRow.adapter.size()
            when (position) {
                "START" -> (existingRow.adapter as? ArrayObjectAdapter)?.addAll(0, results)
                "END" -> (existingRow.adapter as? ArrayObjectAdapter)?.addAll(existingRow.adapter.size(), results)
            }

        } else {
            Log.d(this.tag, "==========new Row=============")
            // If the row doesn't exist, create a new row and add it to the root adapter
            val arrayObjectAdapter = ArrayObjectAdapter(ItemPresenter()).apply {
                addAll(0, results)
            }
            val headerItem = HeaderItem(title)
            val listRow = ListRow(headerItem, arrayObjectAdapter)
            rootAdapter.add(listRow)
        }

        when (title) {
            Constants.WATCH_NOW_TITLE -> viewModel.endIndexNowPlaying += (results.size)
            Constants.TOP_RATED_TITLE -> viewModel.endIndexTopRated += (results.size)
        }
    }

    fun setViewModel(homeViewModel: HomeViewModel) {
        viewModel = homeViewModel
    }

    /*fun bindData(dataList: DataModel) {

        dataList.result.forEachIndexed { index, result ->
            val arrayObjectAdapter = ArrayObjectAdapter(ItemPresenter())

            result.details.forEach {
                arrayObjectAdapter.add(it)
            }

            val headerItem = HeaderItem(result.title)
            val listRow = ListRow(headerItem, arrayObjectAdapter)
            rootAdapter.add(listRow)

        }

    }*/

    fun bindCastData(list: List<CastResponse.Cast>) {
        val arrayObjectAdapter = ArrayObjectAdapter(CastItemPresenter())

        list.forEach { content ->
            arrayObjectAdapter.add(content)
        }

        val headerItem = HeaderItem("Cast & Crew")
        val listRow = ListRow(headerItem, arrayObjectAdapter)
        rootAdapter.add(listRow)
    }

    fun setOnContentSelectedListener(listener: (DataModel.Result) -> Unit) {
        this.itemSelectedListener = listener
    }

    fun setOnItemClickListener(listener: (DataModel.Result) -> Unit) {
        this.itemClickListener = listener
    }

    inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is DataModel.Result) {
                itemSelectedListener?.invoke(item)
            }

            //itemViewHolder?.view?.setBackgroundResource(R.drawable.border_drawable2)
            itemViewHolder?.view?.let { itemView ->
                //positionBorderAroundView(itemView)
            }

        }
    }

    inner class ItemViewClickListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is DataModel.Result) {
                itemClickListener?.invoke(item)
            }
        }

    }

    fun requestFocus(): View {
        val view = view
        view?.requestFocus()
        return view!!
    }

    /*private fun initBorderView() {
        borderView = View(context).apply {
            // Set the background to be a border
            setBackgroundResource(R.drawable.border_drawable) // Create this drawable as a border
        }

        // Add the border view to your fragment's root view
        (view as ViewGroup).addView(borderView)
    }*/

    private fun positionBorderAroundView(itemView: View) {
        itemView.postDelayed({
            Log.d("", "==========positionBorderAroundView========itemView = $itemView")

            val location = IntArray(2)
            itemView.getLocationInWindow(location)


            borderView.layoutParams = FrameLayout.LayoutParams(
                itemView.width, itemView.height
            ).apply {
                setMargins(location[0], location[1], 0, 0)
            }
            Log.d("", "==========positionBorderAroundView========location[0] = ${location[0]}")
            Log.d("", "==========positionBorderAroundView========location[1] = ${location[1]}")

            Log.d("", "==========positionBorderAroundView========itemView.width = ${itemView.width}, itemView.height = ${itemView.height}")

        }, 300)
    }


}