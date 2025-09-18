package com.example.e_wholesaler.main.users.owner.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.main.users.owner.clients.OwnerClient
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.Details
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ShopsState
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.SortType
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.TotalShopRevenue
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

    private var ownerDetails = MutableStateFlow<OwnerDetails?>(null)
    private var homeDetails = MutableStateFlow<HomeScreenDetails?>(null)
    val detailsFlow = combine(homeDetails, ownerDetails) { homeDetails, ownerDetails ->
        Details(homeDetails, ownerDetails)
    }

    private var shopRevenueSortType = MutableStateFlow(SortType.REVENUE)
    private var revenueDetails = MutableStateFlow(
        TotalShopRevenue(
            emptyList(), 0.0, SortType.REVENUE
        )
    )
    val totalRevenue = combine(revenueDetails, shopRevenueSortType) { revenueDetails, sortType ->
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

    private var shopSortType = MutableStateFlow(SortType.NAME)
    private var shopList = MutableStateFlow(emptyList<Shop>())

    val shopsState = combine(shopList, shopSortType) { shopList, shopSortType ->
        val sortedList = when (shopSortType) {
            SortType.CITY -> shopList.sortedBy { it.city }
            else -> shopList.sortedBy { it.name }
        }
        ShopsState(shops = sortedList, sortType = shopSortType)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ShopsState())


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

    fun getDailyRevenue() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val dailyShopRevenueList = ownerClient.getDailyRevenue(it)
                dailyShopRevenueList?.let {
                    revenueDetails.value = TotalShopRevenue(
                        dailyShopRevenueList = dailyShopRevenueList,
                        totalRevenue = dailyShopRevenueList.getTotalRevenue(),
                        sortType = shopRevenueSortType.value
                    )
                }
            }
        }
    }

    fun updateShopRevenueSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Main) {
            shopRevenueSortType.value = sortType
        }
    }

    fun getOwnerShops() {
        viewModelScope.launch(Dispatchers.IO) {
            ownerId.value?.let {
                val shopList = ownerClient.getOwnerShops(it)
                shopList?.let { shopList ->
                    this@OwnerViewModel.shopList.value = shopList
                }
            }
        }
    }

    fun updateShopSortType(sortType: SortType) {
        viewModelScope.launch(Dispatchers.Main) {
            shopSortType.value = sortType
        }
    }

}


private fun List<DailyShopRevenue>.getTotalRevenue(): Double {
    var totalRevenue = 0.0
    this.forEach { totalRevenue += it.dailyRevenue }
    return totalRevenue
}