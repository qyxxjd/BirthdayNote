
package com.classic.birthday

import android.app.Application
import com.classic.birthday.data.local.LocalApi
import timber.log.Timber

/**
 * Birthday application
 *
 * @author Classic
 * @version 2023/4/21 15:07
 */
class BirthdayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocalApi.get().init(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
