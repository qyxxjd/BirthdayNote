@file:Suppress("unused")

package com.classic.core.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.classic.core.databinding.CommonEmptyListBinding

/**
 * 多视图 adapter
 *
 * @author LiuBin
 * @date 2021/11/29 14:59
 */
abstract class MultipleViewAdapter<D> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterData<D> {
    companion object {
        /** 空视图 */
        const val VIEW_TYPE_EMPTY = 0
        /** 内容视图 */
        const val VIEW_TYPE_CONTENT = 1
        /** 头部视图 */
        const val VIEW_TYPE_HEADER = 2
    }
    /** 是否使用空视图 */
    open var useEmptyView = false
    /** 是否使用头部视图 */
    open var useHeaderView = false
    private val list = mutableListOf<D>()

    fun label(): String = this::class.java.simpleName

    /** 创建空视图 */
    open fun createEmptyHolder(parent: ViewGroup): EmptyHolder {
        val emptyBinding = CommonEmptyListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return EmptyHolder(emptyBinding)
    }
    /** 创建内容视图 */
    abstract fun createHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_EMPTY -> createEmptyHolder(parent)
            else -> createHolder(parent, viewType)
        }
    }

    fun isEmptyView(position: Int): Boolean = useEmptyView && list.isEmpty() && position == 0

    override fun getItemViewType(position: Int): Int {
        return when {
            isEmptyView(position) -> VIEW_TYPE_EMPTY
            useHeaderView && 0 == position -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_CONTENT
        }
    }

    override fun getItemCount(): Int {
        val size = size()
        return  when {
            // 使用空视图时，如果数据为空，这里需要返回1
            useEmptyView && size == 0 -> 1
            // 使用头部视图时，数据条数 + 1
            useHeaderView -> size + 1
            else -> size
        }
    }

    /**
     * 根据下标获取数据
     */
    override fun getItem(position: Int): D = if (useHeaderView) list[position - 1] else list[position]

    /**
     * 获取当前数据列表
     */
    override fun data(): MutableList<D> = list

    /**
     * 数据是否为空
     */
    override fun isEmpty(): Boolean = size() == 0

    /**
     * 获取真实的数据条数(不包含：空视图、头部视图)
     */
    override fun size(): Int = list.size

    /**
     * 获取真实的视图总数(包含：空视图、头部视图)
     */
    override fun viewSize(): Int = if (useHeaderView) size() + 1 else size()

    /**
     * 添加一条数据
     */
    override fun add(item: D) {
        list.add(item)
        notifyItemInserted(viewSize())
    }

    /**
     * 添加多条数据
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun addAll(newList: List<D>?) {
        if (newList.isNullOrEmpty()) return
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * 替换一条数据
     */
    override fun replace(oldItem: D, newItem: D) {
        val index = list.indexOf(oldItem)
        if (useHeaderView) {
            replace(index + 1, newItem)
        } else {
            replace(index, newItem)
        }
    }

    /**
     * 根据下标替换一条数据
     */
    override fun replace(index: Int, item: D) {
        if (index >= 0 && index < viewSize()) {
            list[index] = item
            notifyItemChanged(index)
        }
    }

    /**
     * 删除一条数据
     */
    override fun remove(item: D): Boolean {
        val index = list.indexOf(item)
        return if (useHeaderView) {
            remove(index + 1)
        } else {
            remove(index)
        }
    }

    /**
     * 根据下标删除一条数据
     */
    override fun remove(index: Int): Boolean {
        var result = false
        if (index >= 0 && index < viewSize()) {
            result = list.removeAt(index) != null
            if (result) notifyItemRemoved(index)
        }
        return result
    }

    /**
     * 替换所有数据
     */
    override fun replaceAll(newList: List<D>?) {
        replaceAll(newList, true)
    }
    /**
     * 替换所有数据
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun replaceAll(newList: List<D>?, notifyDataSetChanged: Boolean) {
        this.list.clear()
        if (!newList.isNullOrEmpty()) this.list.addAll(newList)
        if (notifyDataSetChanged) notifyDataSetChanged()
    }

    /**
     * 是否存在某个对象
     */
    override fun contains(item: D): Boolean = list.contains(item)

    /**
     * 清空数据
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

}