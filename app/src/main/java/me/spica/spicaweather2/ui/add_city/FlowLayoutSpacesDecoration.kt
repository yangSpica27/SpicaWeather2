package me.spica.spicaweather2.ui.add_city

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

/**
 * @author xiayiye5
 * @data 2022/10/23
 * @describe RecyclerView Google FlexboxLayoutManager流式布局间距分割线
 * */
class FlowLayoutSpacesDecoration(private val horizontalSpace: Int, private val verticalSpace: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 拿到每个item的position
        val itemPosition = parent.getChildAdapterPosition(view)
        val flexboxLayoutManager = parent.layoutManager as FlexboxLayoutManager
        // 拿到排满每行的集合
        val flexLineCount = flexboxLayoutManager.flexLines
        // 设置第一个item左间距为0
        if (itemPosition == 0) {
            outRect.left = 0
        } else {
            // 设置其它item左间距为horizontalSpace
            outRect.left = horizontalSpace
        }
        // 每次都需要初始化为0
        var rowCount = 0
        flexLineCount.forEach {
            // 开始循环每行，拿到排满每行的个数，并进行累加，累加的值就是每行的第一个item
            it.let { flexLine ->
                rowCount += flexLine.itemCount
                if (rowCount == itemPosition) {
                    // 判断累加的item值是不是当前item的position，如果是则代表此item为每行的第一个需要设置左间距为0
                    outRect.left = 0
                } else {
                    // 不是第一个测设置左间距为horizontalSpace
                    outRect.left = horizontalSpace
                }
            }
        }
        // 每个item都设置底部间距为verticalSpace即可
        outRect.bottom = verticalSpace
    }
}
