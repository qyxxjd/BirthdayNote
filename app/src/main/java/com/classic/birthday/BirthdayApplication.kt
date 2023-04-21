
package com.classic.birthday

import android.app.Application

/**
 * Birthday application
 *
 * @author Classic
 * @version 2023-04-21 13:12
 */
class BirthdayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // LocalApi.get().init(this)
    }
}
