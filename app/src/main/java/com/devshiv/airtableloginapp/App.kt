package com.devshiv.airtableloginapp

import android.app.Application
import com.androidnetworking.AndroidNetworking

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(this)
    }
}