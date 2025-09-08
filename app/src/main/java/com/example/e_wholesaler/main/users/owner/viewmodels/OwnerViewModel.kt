package com.example.e_wholesaler.main.users.owner.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.Details
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.SortType
import com.example.e_wholesaler.main.users.owner.dtos.TotalShopRevenue
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
    private var _revenueDetails = MutableStateFlow(
        TotalShopRevenue(
            emptyList(), 0.0, SortType.REVENUE
        )
    )
    private var _sortType = MutableStateFlow(SortType.REVENUE)

    private val _details = MutableStateFlow(Details(_homeDetails.value, _ownerDetails.value))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val detailsFlow = combine(_homeDetails, _ownerDetails, _details) { homeDetails, ownerDetails, details ->
        details?.copy(homeDetails, ownerDetails)
    }

    val totalRevenue = combine(_revenueDetails, _sortType) { revenueDetails, sortType ->
        val sortedList = when (sortType) {
            SortType.REVENUE -> revenueDetails.dailyShopRevenueList.sortedByDescending { it -> it.dailyRevenue }
            SortType.NAME -> revenueDetails.dailyShopRevenueList.sortedBy { it -> it.shopName }
            SortType.CITY -> revenueDetails.dailyShopRevenueList.sortedBy { it -> it.city }
        }
        TotalShopRevenue(
            totalRevenue = revenueDetails.totalRevenue,
            dailyShopRevenueList = sortedList,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

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

    fun getDailyRevenue() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val dailyShopRevenueList = ownerClient.getDailyRevenue(it)
                dailyShopRevenueList?.let {
                    _revenueDetails.value = TotalShopRevenue(
                        dailyShopRevenueList = dailyShopRevenueList,
                        totalRevenue = dailyShopRevenueList.getTotalRevenue(),
                        sortType = _sortType.value
                    )
                }
            }
        }
    }

    fun updateSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Main) {
            _sortType.value = sortType
        }
    }

}


private fun List<DailyShopRevenue>.getTotalRevenue(): Double {
    var totalRevenue = 0.0
    this.forEach { totalRevenue += it.dailyRevenue }
    return totalRevenue
}