package com.classic.birthday.data.local

import android.content.Context

/**
 * 本地数据源
 *
 * @author Classic
 * @version 2023/4/23 9:37
 */
class LocalDataSource(context: Context) {
    companion object {
        private var INSTANCE: LocalDataSource? = null
        fun get(context: Context): LocalDataSource {
            if (INSTANCE == null) {
                synchronized(LocalDataSource::class) {
                    INSTANCE = LocalDataSource(context)
                }
            }
            return INSTANCE!!
        }
    }

    private val db: UserDatabase by lazy { UserDatabase.get(context) }
    private val dao: UserDao by lazy { db.dao() }

    fun add(info: User): Long? = dao.insert(info)

    fun delete(info: User): Int = dao.delete(info)

    fun modify(info: User): Int = dao.update(info)

    fun queryAll(): MutableList<User> {
        return dao.queryAll()
    }
}
