
package com.classic.birthday

import android.app.Application
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
        // LocalApi.get().init(this)
        Timber.plant(Timber.DebugTree())
    }
}
