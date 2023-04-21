package com.classic.core.ui.adapter

/**
 * Adapter数据操作接口规范
 *
 * @author Classic
 * @date 2021/6/21 8:59 上午
 */
interface AdapterData<T> {
    /**
     * 根据下标获取数据
     */
    fun getItem(position: Int): T

    /**
     * 获取当前数据列表
     */
    fun data(): List<T>

    /**
     * 数据是否为空
     */
    fun isEmpty(): Boolean

    /**
     * 获取真实的数据条数(不包含：空视图、头部视图)
     */
    fun size(): Int

    /**
     * 获取真实的视图总数(包含：空视图、头部视图)
     */
    fun viewSize(): Int

    /**
     * 添加一条数据
     */
    fun add(item: T)

    /**
     * 添加多条数据
     */
    fun addAll(newList: List<T>?)

    /**
     * 替换一条数据
     */
    fun replace(oldItem: T, newItem: T)

    /**
     * 根据下标替换一条数据
     */
    fun replace(index: Int, item: T)

    /**
     * 删除一条数据
     */
    fun remove(item: T): Boolean

    /**
     * 根据下标删除一条数据
     */
    fun remove(index: Int): Boolean

    /**
     * 替换所有数据
     */
    fun replaceAll(newList: List<T>?)

    /**
     * 替换所有数据
     */
    fun replaceAll(newList: List<T>?, notifyDataSetChanged: Boolean)

    /**
     * 是否存在某个对象
     */
    fun contains(item: T): Boolean

    /**
     * 清空数据
     */
    fun clear()
}