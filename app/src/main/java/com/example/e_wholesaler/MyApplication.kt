package com.example.e_wholesaler

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.e_wholesaler.dependency_injection.di.appModule
import com.example.e_wholesaler.dependency_injection.di.ownerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule, ownerModule)
        }
    }

}