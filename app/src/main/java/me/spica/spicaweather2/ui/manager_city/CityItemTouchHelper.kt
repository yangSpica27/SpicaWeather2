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
      if (viewHolder == null) return
//      if (actionState == ACTION_STATE_DRAG) {
//        doBiggerScale(viewHolder)
//      }
//      else {
//        doSmallScale(viewHolder)
//      }
    }

//    // 缩小动画
//    private fun doSmallScale(viewHolder: RecyclerView.ViewHolder) {
//      viewHolder.itemView.let {
//        val anim = ScaleAnimation(
//          it.scaleX,
//          0.95f,
//          it.scaleY,
//          0.95f,
//          it.width / 2f,
//          it.height / 2f
//        )
//        it.startAnimation(anim)
//      }
//    }
//
//    // 变得更大
//    private fun doBiggerScale(viewHolder: RecyclerView.ViewHolder) {
//      viewHolder.itemView.let {
//        val anim = ScaleAnimation(
//          it.scaleX,
//          1.05f,
//          it.scaleY,
//          1.05f,
//          it.width / 2f,
//          it.height / 2f
//        )
//        it.startAnimation(anim)
//      }
//    }
//
//    // 删除缩放
//    private fun removeScale(viewHolder: RecyclerView.ViewHolder) {
//      viewHolder.itemView.let {
//        val anim = ScaleAnimation(
//          it.scaleX,
//          1.00f,
//          it.scaleY,
//          1.00f,
//          it.width / 2f,
//          it.height / 2f
//        )
//        it.startAnimation(anim)
//      }
//    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
      super.clearView(recyclerView, viewHolder)
//      removeScale(viewHolder)
    }

    override fun onSwiped(
      viewHolder: RecyclerView.ViewHolder,
      direction: Int,
    ) = Unit
  },
)
