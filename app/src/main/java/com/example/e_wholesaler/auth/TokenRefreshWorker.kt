package com.example.e_wholesaler.auth

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TokenRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("Worker: ", "CoroutineWorker")
        return Result.success()
    }
}