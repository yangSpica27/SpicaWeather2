package me.spica.spicaweather2.ui.manager_city

import android.graphics.Canvas
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
      if (viewHolder == null) return
      if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
        // 被拖动项放大
        viewHolder.itemView.animate().scaleX(1.025f).scaleY(1.025f).start()
      }
    }

    override fun onChildDraw(
      c: Canvas,
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      dX: Float,
      dY: Float,
      actionState: Int,
      isCurrentlyActive: Boolean
    ) {
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      for (i in 0..<recyclerView.childCount) {
        val childView = recyclerView.getChildAt(i)
        val holder = recyclerView.getChildViewHolder(childView)
        if (holder !== viewHolder) {
          childView.animate().scaleX(1f).scaleY(1f).start()
        }
      }
    }



    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
      super.clearView(recyclerView, viewHolder)
//      removeScale(viewHolder)
      viewHolder.itemView.animate().scaleX(1f).scaleY(1f).start()
    }

    override fun onSwiped(
      viewHolder: RecyclerView.ViewHolder,
      direction: Int,
    ) = Unit
  },
)
