package com.example.e_wholesaler.dependency_injection.di

import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val ownerModule = module {

    single() {
        OwnerClient(get(), get())
    }

    viewModel() {
        OwnerViewModel(get(), get())
    }

}