@file:Suppress("unused")

package com.classic.core.ext

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.classic.core.decoration.GridDividerItemDecoration
import com.classic.core.decoration.GridSpaceItemDecoration
import com.classic.core.decoration.HorizontalDividerItemDecoration
import com.classic.core.decoration.VerticalDividerItemDecoration

/**
 * RecyclerView extensions
 *
 * @author Classic
 * @date 2019-05-23 15:48
 */
fun RecyclerView?.clearAnim() {
    this?.itemAnimator?.let {
        if (it is SimpleItemAnimator) it.supportsChangeAnimations = false
        it.changeDuration = 0L
    }
}
fun RecyclerView?.clearItemDecoration() {
    this?.let {
        while (it.itemDecorationCount > 0) {
            removeItemDecorationAt(0)
        }
    }
}
fun RecyclerView?.destroy() {
    clearItemDecoration()
    this?.adapter = null
}
/** 根据偏移量，滑动到指定索引 */
fun RecyclerView.scrollToPositionWithOffset(position: Int, offset: Int = 0) {
    try {
        when (val lm = layoutManager) {
            is StaggeredGridLayoutManager -> lm.scrollToPositionWithOffset(position, offset)
            is LinearLayoutManager -> lm.scrollToPositionWithOffset(position, offset)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
/** 滑动到顶部 */
fun RecyclerView.scrollToTop() {
    scrollToPositionWithOffset(0)
}
/** 滑动到底部 */
fun RecyclerView.scrollToBottom(offset: Int = Int.MIN_VALUE) {
    adapter?.let {
        val size = it.itemCount
        if (size <= 0) return
        scrollToPositionWithOffset(it.itemCount - 1, offset)
    }
}
/** 应用垂直方向的列表配置 */
fun RecyclerView.applyLinearConfig(
    layoutManager: RecyclerView.LayoutManager = WrapLinearLayoutManager(context),
    hasFixedSize: Boolean = true,
    supportsChangeAnimations: Boolean = false,
    decoration: ItemDecoration? = null,
    adapter: RecyclerView.Adapter<*>? = null
) {
    applyCustomConfig(layoutManager, hasFixedSize, supportsChangeAnimations, decoration, adapter)
}
/** 应用水平方向的列表配置 */
fun RecyclerView.applyHorizontalLinearConfig(
    hasFixedSize: Boolean = true,
    supportsChangeAnimations: Boolean = false,
    decoration: ItemDecoration? = null,
    adapter: RecyclerView.Adapter<*>? = null
) {
    applyCustomConfig(
        WrapLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false),
        hasFixedSize, supportsChangeAnimations, decoration, adapter
    )
}
/** 应用网格列表配置 */
fun RecyclerView.applyGridConfig(
    spanCount: Int,
    orientation: Int = RecyclerView.VERTICAL,
    hasFixedSize: Boolean = true,
    supportsChangeAnimations: Boolean = false,
    decoration: ItemDecoration? = null,
    adapter: RecyclerView.Adapter<*>? = null
) {
    val lm = GridLayoutManager(context, spanCount).also { it.orientation = orientation }
    applyCustomConfig(lm, hasFixedSize, supportsChangeAnimations, decoration, adapter)
}
/** 应用瀑布流列表配置 */
fun RecyclerView.applyStaggeredGridConfig(
    spanCount: Int,
    supportsChangeAnimations: Boolean = false,
    decoration: ItemDecoration? = null,
    adapter: RecyclerView.Adapter<*>? = null
) {
    applyCustomConfig(
        StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL),
        false, supportsChangeAnimations, decoration, adapter
    )
}
/** 应用自定义配置 */
fun RecyclerView.applyCustomConfig(
    layoutManager: RecyclerView.LayoutManager,
    hasFixedSize: Boolean = true,
    supportsChangeAnimations: Boolean = false,
    decoration: ItemDecoration? = null,
    adapter: RecyclerView.Adapter<*>? = null
) {
    setHasFixedSize(hasFixedSize)
    this.layoutManager = layoutManager
    if (!supportsChangeAnimations) clearAnim()
    if (null != decoration) {
        clearItemDecoration()
        addItemDecoration(decoration)
    }
    if (null != adapter) this.adapter = adapter
}

/** 通用水平分割线 */
fun Context.divider(
    color: Int = Color.parseColor("#EAEAEA"),
    size: Int = 1,
    horizontalMargin: Int = 0,
    showLastDivider: Boolean = true
): ItemDecoration {
    val builder = HorizontalDividerItemDecoration.Builder(this).color(color).size(size)
        .margin(horizontalMargin)
    if (showLastDivider) builder.showLastDivider()
    return builder.build()
}
/** 通用垂直分割线 */
fun Context.dividerVertical(
    color: Int = Color.parseColor("#EAEAEA"),
    size: Int = 1,
    verticalMargin: Int = 0,
    showLastDivider: Boolean = true
): ItemDecoration {
    val builder =
        VerticalDividerItemDecoration.Builder(this).color(color).size(size).margin(verticalMargin)
    if (showLastDivider) builder.showLastDivider()
    return builder.build()
}
/**
 * 通用网格分割线
 *
 * @param heightResId 分割线高度
 * @param colorResId 分割线颜色
 */
fun Context.commonGridDividerRes(heightResId: Int, colorResId: Int) =
    GridDividerItemDecoration(px(heightResId), color(colorResId))
/**
 * 通用网格分割线
 *
 * @param height 分割线高度
 * @param color 分割线颜色
 */
fun Context.commonGridDivider(height: Int, color: Int) =
    GridDividerItemDecoration(height, color)
/**
 * 通用网格/瀑布流间距分割线
 *
 * @param spacing 间距
 */
fun commonGridSpaceDivider(spacing: Int, startFromSize: Int = 0, endFromSize: Int = 0): ItemDecoration {
    val divide = GridSpaceItemDecoration(spacing, true)
    divide.setStartFrom(startFromSize)
    divide.setEndFromSize(endFromSize)
    return divide
}

/** Item移除事件 */
interface ItemRemoveListener {
    fun onItemRemove(view: View, position: Int)
}
/** Item点击事件 */
interface ItemClickListener {
    fun onItemClick(view: View, position: Int)
}
/** Item长按事件 */
interface ItemLongClickListener {
    fun onItemLongClick(view: View, position: Int): Boolean
}
/** 设置点击事件 */
fun RecyclerView?.setOnItemClickListener(listener: (View, Int) -> Unit) {
    this?.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
        //'构造手势探测器，用于解析单击事件'
        val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onShowPress(e: MotionEvent) {}
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                //'当单击事件发生时，寻找单击坐标下的子控件，并回调监听器'
                findChildViewUnder(e.x, e.y)?.let { child ->
                    listener(child, getChildAdapterPosition(child))
                }
                return false
            }
            override fun onDown(e: MotionEvent): Boolean {
                return false
            }
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                return false
            }
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                return false
            }
            override fun onLongPress(e: MotionEvent) {}
        })
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        //'在拦截触摸事件时，解析触摸事件'
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(e)
            return false
        }
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    })
}

/**
 * 支持局部刷新的适配器
 */
interface IPartialRefreshAdapter {
    /**
     * 刷新Item回调
     */
    fun onRefreshItem(position: Int, holder: RecyclerView.ViewHolder)
}

/**
 * 查找可见的Item列表开始、结束下标
 */
fun RecyclerView.findVisibleItems(): IntArray {
    return when (val lm = layoutManager) {
        is GridLayoutManager -> {
            intArrayOf(lm.findFirstVisibleItemPosition(), lm.findLastVisibleItemPosition())
        }
        is LinearLayoutManager -> {
            intArrayOf(lm.findFirstVisibleItemPosition(), lm.findLastVisibleItemPosition())
        }
        is StaggeredGridLayoutManager -> {
            val spanCount = lm.spanCount
            val firstPositionArray = IntArray(spanCount)
            val lastPositionArray = IntArray(spanCount)
            lm.findFirstVisibleItemPositions(firstPositionArray)
            lm.findLastVisibleItemPositions(lastPositionArray)
            val firstVisiblePosition = firstPositionArray.minOrNull() ?: firstPositionArray[0]
            val lastVisiblePosition = lastPositionArray.maxOrNull() ?:  lastPositionArray[lastPositionArray.size - 1]
            intArrayOf(firstVisiblePosition, lastVisiblePosition)
        }
        else -> intArrayOf(0, 0)
    }
}

/**
 * 刷新水平列表当前可见的Item列表
 *
 * @param hasHeaderView 是否有添加头部或尾部视图，会影响下标的顺序
 */
fun RecyclerView?.refreshVisibleItems(hasHeaderView: Boolean = false) {
    if (null == this || null == layoutManager || null == adapter || adapter !is IPartialRefreshAdapter) return
    val size = adapter?.itemCount ?: 0
    val offset = if (hasHeaderView) 1 else 0
    if (size <= offset) return
    try {
        val visibleRange = findVisibleItems()
        val first = visibleRange[0]
        val last = visibleRange[1]
        for (i in 0 until size) {
            if (i in first..last) {
                var holder = findViewHolderForAdapterPosition(i)
                if (null == holder) holder = findViewHolderForLayoutPosition(i)
                holder?.let {
                    (adapter as IPartialRefreshAdapter).onRefreshItem(i, it)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/** 刷新水平列表当前可见的Item列表 */
fun RecyclerView?.refreshVisibleItems(
    hasHeaderView: Boolean,
    task: (position: Int, holder: RecyclerView.ViewHolder) -> Unit) {
    if (null == this || null == layoutManager || null == adapter) return
    val size = adapter?.itemCount ?: 0
    val offset = if (hasHeaderView) 1 else 0
    if (size <= offset) return
    try {
        val visibleRange = findVisibleItems()
        val first = visibleRange[0]
        val last = visibleRange[1]
        for (i in 0 until size) {
            if (i in first..last) {
                var holder = findViewHolderForAdapterPosition(i)
                if (null == holder) holder = findViewHolderForLayoutPosition(i)
                holder?.let { task.invoke(i, it) }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Fix:
 *
 * > java.lang.IndexOutOfBoundsException: Inconsistency detected.
 * > Invalid view holder adapter positionViewHolder
 *
 * - [RecyclerView IndexOutOfBoundsException: Inconsistency detected](https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in)
 */
class WrapLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean):
        super(context, orientation, reverseLayout)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int):
        super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}