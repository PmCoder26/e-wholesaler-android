package com.example.e_wholesaler.main.users.owner.dtos


data class TotalShopRevenue(
    val dailyShopRevenueList: List<DailyShopRevenue>,
    val totalRevenue: Double,
    val sortType: SortType
)
