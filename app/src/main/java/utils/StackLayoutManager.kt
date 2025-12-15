package utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StackLayoutManager(context: Context) : LinearLayoutManager(context) {

    init {
        orientation = VERTICAL
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        if (itemCount > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child?.translationY = (i * 20).toFloat()
            }
        }
    }
}