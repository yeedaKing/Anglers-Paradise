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
        Log.d("MainApplication", "App started")
        // TODO: In later steps, initialize any app-wide singletons (e.g., TankStore, FishRepository)
        // TODO: Swap placeholder sprite provider when real assets arrive
    }
}
