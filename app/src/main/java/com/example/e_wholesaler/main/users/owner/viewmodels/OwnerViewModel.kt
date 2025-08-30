package com.example.e_wholesaler.main.users.owner.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.dtos.Details
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OwnerViewModel(
    private val ownerClient: OwnerClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val OWNER_ID_KEY = longPreferencesKey("user_type_id")

    private var ownerId = MutableStateFlow<Long?>(null)
    private var _ownerDetails = MutableStateFlow<OwnerDetails?>(null)
    private var _homeDetails = MutableStateFlow<HomeScreenDetails?>(null)
    private val _details = MutableStateFlow(Details(_homeDetails.value, _ownerDetails.value))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val detailsFlow = combine(_homeDetails, _ownerDetails, _details) { homeDetails, ownerDetails, details ->
        details?.copy(homeDetails, ownerDetails)
    }


    init {
        viewModelScope.launch {
            // .first() gives the current snapshot of preferences instead of continuous updates.
            val pref = dataStore.data.first()
            ownerId.value = pref[OWNER_ID_KEY]
            println("OwnerId: ${ownerId.value}")
            getHomeScreenDetails()
            getOwnerDetails()
        }
    }

    fun getHomeScreenDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val homeScreenDetails = ownerClient.getHomeScreenDetails(it)
                _homeDetails.value = homeScreenDetails
            }
        }
    }

    fun getOwnerDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val ownerDetails = ownerClient.getOwnerDetails(it)
                _ownerDetails.value = ownerDetails
            }
        }
    }

}