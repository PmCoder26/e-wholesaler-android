package com.example.e_wholesaler.main.users.owner.viewmodels.utils

import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue


data class TotalShopRevenue(
    val dailyShopRevenueList: List<DailyShopRevenue>,
    val totalRevenue: Double,
    val sortType: SortType
)
