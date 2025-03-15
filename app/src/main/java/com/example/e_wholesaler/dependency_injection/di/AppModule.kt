package com.example.e_wholesaler.dependency_injection.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.example.project.ktor_client.createHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.parimal.auth.AuthClient
import org.parimal.auth.TokenManager


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "datastore_pref")

val appModule: Module = module {

    single(named("datastore")) {
        androidContext().datastore
    }

    single(named("http-client")) {
        createHttpClient(OkHttp.create())
    }

    single(named("token-manager")) {
        val datastore: DataStore<Preferences> by inject(named("datastore"))
        val httpClient: HttpClient by inject(named("http-client"))
        TokenManager(datastore, httpClient)
    }

    single(named("auth-client")) {
        val httpClient: HttpClient by inject(named("http-client"))
        val tokenManager: TokenManager by inject(named("token-manager"))
        AuthClient(httpClient, tokenManager)
    }



}