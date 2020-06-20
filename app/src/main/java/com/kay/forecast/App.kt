package com.kay.forecast

import android.app.Application
import timber.log.Timber

class App : Application() {

    lateinit var dependenciesContainer: DependenciesContainer
        private set

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        dependenciesContainer = DependenciesContainer(this)
    }
}