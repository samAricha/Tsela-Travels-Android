package com.teka.tsela.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TselaApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}