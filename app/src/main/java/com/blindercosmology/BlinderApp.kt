package com.blindercosmology

import android.app.Application
import com.blindercosmology.data.AppDatabase
import com.blindercosmology.data.ProfileRepository

class BlinderApp : Application() {
    lateinit var repo: ProfileRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repo = ProfileRepository(AppDatabase.get(this).profileDao())
    }
}
