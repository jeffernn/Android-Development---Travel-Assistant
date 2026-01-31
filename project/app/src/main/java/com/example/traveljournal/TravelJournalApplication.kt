package com.example.traveljournal

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TravelJournalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}