package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable


@Serializable
data class DailyShopRevenue(
    val shopName: String,
    val city: String,
    val dailyRevenue: Double,
    val dailyTransactions: Long
)
