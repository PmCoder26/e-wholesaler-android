package com.example.e_wholesaler.main.users.owner.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnerViewModel(
    private val ownerClient: OwnerClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val OWNER_ID_KEY = longPreferencesKey("owner_id")

    private var ownerId = MutableStateFlow<Long?>(null)
    private var ownerDetails = MutableStateFlow<OwnerDetails?>(null)
    private var homeDetails = MutableStateFlow<HomeScreenDetails?>(null)


    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.collectLatest { pref ->
                ownerId.value = pref[OWNER_ID_KEY]
                cancel()
            }
        }
    }

    fun getHomeScreenDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val homeScreenDetails = ownerClient.getHomeScreenDetails(it)
                homeDetails.value = homeScreenDetails
            }
        }
    }

    fun getOwnerDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val ownerDetails = ownerClient.getOwnerDetails(it)
                this@OwnerViewModel.ownerDetails.value = ownerDetails
            }
        }
    }

}