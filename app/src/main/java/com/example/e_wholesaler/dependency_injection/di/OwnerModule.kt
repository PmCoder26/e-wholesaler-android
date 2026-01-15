package com.example.e_wholesaler.dependency_injection.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


@RequiresApi(Build.VERSION_CODES.O)
val ownerModule = module {

    single() {
        OwnerClient(get(named("main-http-client")), androidContext())
    }

    viewModel() {
        OwnerViewModel(get(), get())
    }

}