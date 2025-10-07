package com.example.e_wholesaler.dependency_injection.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import io.ktor.client.engine.okhttp.OkHttp
import org.example.project.ktor_client.createHttpClientForAuth
import org.example.project.ktor_client.createHttpClientMain
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.parimal.auth.AuthClient
import org.parimal.auth.TokenManager


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "datastore_pref")

val appModule: Module = module {

    single() {
        androidContext().datastore
    }

    single(qualifier = named("auth-http-client")) {
        createHttpClientForAuth(OkHttp.create())
    }

    single(qualifier = named("main-http-client")) {
        createHttpClientMain(OkHttp.create(), get())
    }

    single() {
        TokenManager(get())
    }

    single() {
        AuthClient(get(named("auth-http-client")), get())
    }

    viewModel() {
        NavigationViewModel()
    }
}