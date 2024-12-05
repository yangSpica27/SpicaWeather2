package me.spica.spicaweather2.ui.manager_city

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class CityItemTouchHelper(
  var onMove: ((RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Unit)? = null,
  var isDrag: Boolean = false,
) : ItemTouchHelper(
  object : Callback() {
    override fun getMovementFlags(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
    ): Int {
      val dragFlags = UP or DOWN
      val swipeFlags = 0
      return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder,
    ): Boolean {
//        if (!isDrag) return false

//        if (viewHolder.itemViewType != target.itemViewType) {
//            return false
//        }
      if (viewHolder.itemViewType == 1 || target.itemViewType == 1) {
        Timber.tag("CityItemTouchHelper").d("onMove: return false")
        return false
      }

      onMove?.invoke(viewHolder, target)

      return true
    }

    override fun onSelectedChanged(
      viewHolder: RecyclerView.ViewHolder?,
      actionState: Int,
    ) {
      super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onSwiped(
      viewHolder: RecyclerView.ViewHolder,
      direction: Int,
    ) = Unit
  },
)
