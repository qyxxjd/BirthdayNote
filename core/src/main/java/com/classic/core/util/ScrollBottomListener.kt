@file:Suppress("unused")

package com.classic.core.util


import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * RecyclerView 滑动到底部监听
 *
 * @author Classic
 * @date 2020/6/26 5:01 PM
 */
abstract class ScrollBottomListener : RecyclerView.OnScrollListener() {

    /**
     * 滑动到底部
     */
    abstract fun onScrollBottom()

    /**
     * 滑动偏移量，
     */
    open fun offset(): Int = 0

    /**
     * 最后一个显示的Item改变
     */
    open fun onLastVisiblePositionChanged(position: Int) {}

    open fun filter(recyclerView: RecyclerView, dx: Int, dy: Int): Boolean = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (filter(recyclerView, dx, dy)) {
            // L.e("onScrolled: dx=$dx    dy=$dy")
            recyclerView.layoutManager?.apply {
                findLastVisibleItemPosition(this)
                if (lastVisiblePosition < itemCount - 1 - offset()) {
                    onLastVisiblePositionChanged(lastVisiblePosition)
                } else onScrollBottom()
            }
        }
    }

    private var lastVisiblePosition: Int = 0
    private fun findLastVisibleItemPosition(lm: RecyclerView.LayoutManager) {
        when (lm) {
            is GridLayoutManager -> {
                lastVisiblePosition = lm.findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisiblePosition = lm.findLastVisibleItemPosition()
            }
            is StaggeredGridLayoutManager -> {
                val lastPositionArray = IntArray(lm.spanCount)
                lm.findLastVisibleItemPositions(lastPositionArray)
                lastVisiblePosition = lastPositionArray.maxOrNull() ?:  lastPositionArray[lastPositionArray.size - 1]
            }
        }
    }
}
