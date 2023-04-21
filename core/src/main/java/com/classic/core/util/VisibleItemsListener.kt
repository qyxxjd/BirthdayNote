@file:Suppress("unused")

package com.classic.core.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 监听滑动停止时，界面可见的Item列表
 */
abstract class VisibleItemsListener : RecyclerView.OnScrollListener() {

    /**
     * SCROLL_STATE_IDLE:停止滚动
     * SCROLL_STATE_DRAGGING: 用户慢慢拖动
     * SCROLL_STATE_SETTLING：惯性滚动
     */
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        // 停止滚动时
        if (RecyclerView.SCROLL_STATE_IDLE == newState) {
            recyclerView.layoutManager?.apply {
                findVisibleItemPosition(this)
                onItemsVisible(firstVisiblePosition, lastVisiblePosition)
            }
        }
    }

    /**
     * 当前可见的Item
     *
     * @param firstVisiblePosition 第一个可见的Item下标
     * @param lastVisiblePosition 最后一个可见的Item下标
     */
    abstract fun onItemsVisible(firstVisiblePosition: Int, lastVisiblePosition: Int)

    private var firstVisiblePosition: Int = 0
    private var lastVisiblePosition: Int = 0
    private fun findVisibleItemPosition(lm: RecyclerView.LayoutManager) {
        when (lm) {
            is GridLayoutManager -> {
                firstVisiblePosition = lm.findFirstVisibleItemPosition()
                lastVisiblePosition = lm.findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                firstVisiblePosition = lm.findFirstVisibleItemPosition()
                lastVisiblePosition = lm.findLastVisibleItemPosition()
            }
            is StaggeredGridLayoutManager -> {
                val spanCount = lm.spanCount
                val firstPositionArray = IntArray(spanCount)
                val lastPositionArray = IntArray(spanCount)
                lm.findFirstVisibleItemPositions(firstPositionArray)
                lm.findLastVisibleItemPositions(lastPositionArray)
                firstVisiblePosition = firstPositionArray.minOrNull() ?: firstPositionArray[0]
                lastVisiblePosition = lastPositionArray.maxOrNull() ?:  lastPositionArray[lastPositionArray.size - 1]
            }
        }
    }
}