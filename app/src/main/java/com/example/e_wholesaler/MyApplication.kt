package com.example.e_wholesaler

import android.app.Application
import com.example.e_wholesaler.dependency_injection.di.appModule
import com.example.e_wholesaler.dependency_injection.di.ownerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule, ownerModule)
        }

//        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
//            repeatInterval = 15,
//            repeatIntervalTimeUnit = TimeUnit.MINUTES
//        )
//            .build()

//        WorkManager.getInstance(this)
//            .enqueueUniquePeriodicWork(
//                uniqueWorkName = "TokenWorker",
//                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
//                request = workRequest
//            )

    }

}