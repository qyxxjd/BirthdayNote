@file:Suppress("unused")

package com.classic.birthday.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * DB
 *
 * @author Classic
 * @version 2023/4/21 15:07
 */
@Database(entities = [(User::class)], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun dao(): UserDao

    companion object {
        private const val DB_NAME = "user.db"
        private var INSTANCE: UserDatabase? = null

        fun get(context: Context): UserDatabase {
            if (INSTANCE == null) {
                synchronized(UserDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java, DB_NAME
                    ).build()
                }
            }
            return INSTANCE!!
        }

        fun destroy() {
            INSTANCE = null
        }
    }
}
