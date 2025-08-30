package com.example.e_wholesaler

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.e_wholesaler.auth.TokenRefreshWorker
import com.example.e_wholesaler.dependency_injection.di.appModule
import com.example.e_wholesaler.dependency_injection.di.ownerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule, ownerModule)
        }

        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                uniqueWorkName = "TokenWorker",
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE,
                request = workRequest
            )

    }

}