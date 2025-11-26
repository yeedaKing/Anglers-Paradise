// app/src/main/java/com/angersparadise/MainApplication.kt

package com.anglersparadise

import android.app.Application
import android.util.Log

/**
 * App-level entry point.
 * Keep this minimal now; we’ll wire repositories/stores here later.
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        com.anglersparadise.data.local.TankStore.init(this)
    }

}
