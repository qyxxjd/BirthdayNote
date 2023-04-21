@file:Suppress("unused")

package com.classic.birthday.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE

/**
 * 消费信息数据操作
 *
 * @author Classic
 * @version 2023/4/21 15:07
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM t_user ORDER BY id DESC")
    fun queryAll(): MutableList<User>

    @Insert(onConflict = REPLACE)
    fun insert(info: User): Long?

    @Update
    fun update(info: User): Int

    @Delete
    fun delete(info: User): Int
}
