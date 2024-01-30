package gem.poc.tvapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide

class ItemPresenter : Presenter() {
    private lateinit var borderView: View
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {


        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.item_view, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent!!.context, 12F)
        params.height = getHeightInPercent(parent!!.context, 32)

        val border = view.findViewById<View>(R.id.item_border1)
        border.visibility = View.INVISIBLE


        // Set the focus change listener
        view.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->

            borderView = view.rootView.findViewById(R.id.item_border)
            if (hasFocus) {
                // Increase padding to shrink the ImageView, making the border appear thicker
                params.width = getWidthInPercent(parent!!.context, 12F+0.5F)
                params.height = getHeightInPercent(parent!!.context, 32+1)
            } else {
                // Reset padding
                params.width = getWidthInPercent(parent!!.context, 12F)
                params.height = getHeightInPercent(parent!!.context, 32)
            }
            view.requestLayout()

            border.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
            //if (hasFocus) positionBorderAroundView(view)
        }

        return ViewHolder(view)

    }

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
            borderView.requestLayout()
            Log.d("", "==========positionBorderAroundView========location[0] = ${location[0]}")
            Log.d("", "==========positionBorderAroundView========location[1] = ${location[1]}")

            Log.d("", "==========positionBorderAroundView========itemView.width = ${itemView.width}, itemView.height = ${itemView.height}")


            /*val verticalGridView = verticalGridView
            val selectedRowIndex = verticalGridView?.selectedPosition ?: -1
            val selectedRow = adapter?.get(selectedRowIndex) as? ListRow
            val rowViewHolder = getRowViewHolder(selectedRowIndex)
            if (rowViewHolder is ListRowPresenter.ViewHolder) {
                val horizontalGridView = rowViewHolder.gridView
                val selectedItemIndex = horizontalGridView.selectedPosition

                // Now get the selected item from your data source
                val selectedItemView : View = horizontalGridView.getChildAt(selectedItemIndex)
                val location2 = IntArray(2)
                itemView.getLocationInWindow(location2)
                Log.d("", "==========positionBorderAroundView2========location[0] = ${location2[0]}")
                Log.d("", "==========positionBorderAroundView2========location[1] = ${location2[1]}")
                Log.d("", "==========positionBorderAroundView22========itemView.width = ${selectedItemView.width}, itemView.height = ${selectedItemView.height}")

            }*/


        }, 200)
    }

    fun getWidthInPercent(context: Context, percent: Float): Int {
        val width = context.resources.displayMetrics.widthPixels ?: 0
        return ((width * percent) / 100).toInt()
    }

    fun getHeightInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.heightPixels ?: 0
        return (width * percent) / 100
    }


    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {

        val content = item as? DataModel.Result

        val imageview = viewHolder?.view?.findViewById<ImageView>(R.id.poster_image)

        val url = "https://www.themoviedb.org/t/p/w500" + content?.poster_path
        Glide.with(viewHolder?.view?.context!!)
            .load(url)
            .into(imageview!!)

    }


    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
    }
}