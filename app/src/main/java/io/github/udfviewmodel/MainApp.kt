package io.github.udfviewmodel

import android.app.Application

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val anrMonitor = AnrMonitor(this)
        anrMonitor.start()
    }
}