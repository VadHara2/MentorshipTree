package com.vadhara7.mentorship_tree

import android.app.Application
import com.vadhara7.mentorship_tree.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(appModule)
        }
    }
}