package com.classic.core.interfaces

/**
 * 搜索接口
 *
 * @author Classic
 * @date 2020-01-29 17:18
 */
interface SearchInterface {

    /**
     * 搜索数据
     *
     * @param keyword 搜索关键字
     */
    fun onSearch(keyword: String)
}