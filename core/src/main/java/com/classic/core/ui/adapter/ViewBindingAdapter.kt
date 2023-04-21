@file:Suppress("unused")

package com.classic.core.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.classic.core.databinding.CommonEmptyListBinding
import com.classic.core.ext.IPartialRefreshAdapter
import com.classic.core.ext.ItemClickListener
import com.classic.core.ext.ItemLongClickListener
import com.classic.core.ext.onClick

/**
 * ViewBinding adapter
 *
 * @author Classic
 * @date 2021/6/21 8:59 上午
 */
abstract class ViewBindingAdapter<D, VB : ViewBinding> : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    AdapterData<D>, IPartialRefreshAdapter {
    companion object {
        /** 空视图 */
        const val VIEW_TYPE_EMPTY = 0
        /** 内容视图 */
        const val VIEW_TYPE_CONTENT = 1
    }
    /** 有效的点击间隔 */
    open var clickInterval = 1000L
    /** 是否使用空视图 */
    open var useEmptyView = false
    /** 点击事件 */
    open var itemClickListener: ItemClickListener? = null
    /** 长按事件 */
    open var itemLongClickListener: ItemLongClickListener? = null
    private val list = mutableListOf<D>()
    protected var binding: VB? = null

    fun label(): String = this::class.java.simpleName

    /** 创建空视图 */
    open fun createEmptyHolder(parent: ViewGroup): EmptyHolder {
        val emptyBinding = CommonEmptyListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return EmptyHolder(emptyBinding)
    }
    /** 创建内容视图 */
    abstract fun createHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<VB>
    /** 绑定数据 */
    abstract fun onBind(binding: VB, item: D, position: Int)

    /**
     * 局部刷新处理
     */
    open fun onPartialRefresh(vb: VB?, item: D, position: Int) {}
    override fun onRefreshItem(position: Int, holder: RecyclerView.ViewHolder) {
        if (holder is ViewBindingHolder<*>) {
            val vb = holder.viewBinding
            try {
                @Suppress("UNCHECKED_CAST")
                onPartialRefresh(vb as VB?, getItem(position), position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_EMPTY -> createEmptyHolder(parent)
            else -> createHolder(parent, viewType)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewBindingHolder<*> && position >= 0) {
            (holder as ViewBindingHolder<VB>).viewBinding?.let { vb ->
                val view = vb.root
                view.onClick(clickInterval) { itemClickListener?.onItemClick(view, position) }
                view.setOnLongClickListener {
                    itemLongClickListener?.onItemLongClick(view, position) ?: false
                }
                if (position < size()) onBind(vb, list[position], position)
            }
        }
    }

    private fun isEmptyView(position: Int): Boolean = useEmptyView && list.isEmpty() && position == 0

    override fun getItemViewType(position: Int): Int {
        return if (isEmptyView(position)) VIEW_TYPE_EMPTY else VIEW_TYPE_CONTENT
    }

    override fun getItemCount(): Int {
        val size = size()
        // 使用空视图时，如果数据为空，这里需要返回1
        return if (useEmptyView && size == 0) 1 else size
    }

    /**
     * 根据下标获取数据
     */
    override fun getItem(position: Int): D = list[position]

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
    override fun viewSize(): Int = size()

    /**
     * 添加一条数据
     */
    override fun add(item: D) {
        list.add(item)
        notifyItemInserted(list.size)
    }

    /**
     * 添加多条数据
     */
    override fun addAll(newList: List<D>?) {
        if (newList.isNullOrEmpty()) return
        this.list.addAll(newList)
        val newSize = newList.size
        notifyItemRangeInserted(size() - newSize, newSize)
    }

    /**
     * 替换一条数据
     */
    override fun replace(oldItem: D, newItem: D) {
        replace(list.indexOf(oldItem), newItem)
    }

    /**
     * 根据下标替换一条数据
     */
    override fun replace(index: Int, item: D) {
        if (index >= 0 && index < size()) {
            list[index] = item
            notifyItemChanged(index)
        }
    }

    /**
     * 删除一条数据
     */
    override fun remove(item: D): Boolean = remove(list.indexOf(item))

    /**
     * 根据下标删除一条数据
     */
    override fun remove(index: Int): Boolean {
        var result = false
        if (index >= 0 && index < list.size) {
            result = list.removeAt(index) != null
            if (result) notifyItemRemoved(index)
        }
        return result
    }

    /**
     * 替换所有数据
     */
    override fun replaceAll(newList: List<D>?) {
        replaceAll(newList, false)
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

    // /** 数据解绑，释放资源 */
    // open fun onUnbind(holder: RecyclerView.ViewHolder) {
    //     if (holder is ViewBindingHolder<*>) holder.viewBinding = null
    // }
    // override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    //     super.onViewRecycled(holder)
    //     onUnbind(holder)
    // }
    // override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
    //     super.onViewDetachedFromWindow(holder)
    //     onUnbind(holder)
    // }
}

