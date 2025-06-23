package com.vadhara7.mentorship_tree

import android.app.Application
import com.vadhara7.mentorship_tree.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AndroidApp : Application() {
    companion object {
        lateinit var INSTNACE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTNACE = this
        startKoin {
            androidContext(this@AndroidApp)
            androidLogger()
            modules(appModule)
        }
    }
}