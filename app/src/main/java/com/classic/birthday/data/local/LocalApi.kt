@file:Suppress("unused")

package com.classic.birthday.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "classic_birthday_note")
/**
 * 持久化参数存储类
 *
 * @author Classic
 * @date 2021/12/30 15:45
 */
class LocalApi private constructor() {
    companion object {
        private const val EMPTY = ""
        private val IS_FIRST = booleanPreferencesKey("isFirst")
        private var INSTANCE: LocalApi? = null
        fun get() = INSTANCE ?: LocalApi().also { INSTANCE = it }
    }

    private lateinit var dataStore: DataStore<Preferences>
    fun init(context: Context) {
        dataStore = context.dataStore
    }

    suspend fun saveIsFirst(value: Boolean) {
        saveBoolean(IS_FIRST, value)
    }
    suspend fun isFirst(defaultValue: Boolean = true) =
        boolean(IS_FIRST, defaultValue)

    private suspend fun saveString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { it[key] = value }
    }
    private suspend fun string(key: Preferences.Key<String>): String =
        dataStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: EMPTY }.first()

    private suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { it[key] = value }
    }
    private suspend fun boolean(key: Preferences.Key<Boolean>, defaultValue: Boolean): Boolean =
        dataStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: defaultValue }.first()

    private suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { it[key] = value }
    }
    private suspend fun int(key: Preferences.Key<Int>, defaultValue: Int = 0): Int =
        dataStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: defaultValue }.first()

    private suspend fun saveLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { it[key] = value }
    }
    private suspend fun long(key: Preferences.Key<Long>, defaultValue: Long = 0L): Long =
        dataStore.data.catch { e ->
            e.printStackTrace()
        }.map { it[key] ?: defaultValue }.first()

}